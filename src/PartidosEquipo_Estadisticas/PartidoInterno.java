package PartidosEquipo_Estadisticas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;

public class PartidoInterno extends Partido{

	private int marcadorLocal;
	private int marcadorVisitante;
	private Equipo equipoVisitante;
	private Equipo equipoLocal;
	
	public PartidoInterno(LocalDate fecha, Equipo equipoLocal, Equipo equipoVisitante, int marcadorLocal,
			int marcadorVisitante) {
		super(fecha);
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
		this.equipoVisitante = equipoVisitante;
		this.equipoLocal = equipoLocal;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}

	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}

	public Equipo getEquipoVisitante() {
		return equipoVisitante;
	}

	public Equipo getEquipoLocal() {
		return equipoLocal;
	}
	
	public void registrarMarcador(int marcadorLocal, int marcadorVisitante) {
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
	}
	
	public Equipo getGanador() {
	    if (marcadorLocal > marcadorVisitante) {
	        return equipoLocal;
	    }
	    if (marcadorVisitante > marcadorLocal) {
	        return equipoVisitante;
	    }
	    return null;
	}
	
	
	@Override
	public boolean puedeJugarse() {
	    return equipoLocal.puedeJugar() && equipoVisitante.puedeJugar();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "PartidoInterno");
		json.put("marcadorLocal", marcadorLocal);
		json.put("marcadorVisitante", marcadorVisitante);
		json.put("equipoVisitante", equipoVisitante.getNombre());
		json.put("equipoLocal", equipoLocal.getNombre());
		return json;
	}

	public static PartidoInterno fromJSON(JSONObject json, Map<String, Equipo> mapaEquipos) {
		String nombreLocal = json.getString("equipoLocal");
		String nombreVisitante = json.getString("equipoVisitante");
		Equipo equipoLocal = mapaEquipos.get(nombreLocal);
		Equipo equipoVisitante = mapaEquipos.get(nombreVisitante);
		if (equipoLocal == null || equipoVisitante == null) {
			throw new IllegalArgumentException(
					"Equipo desconocido en el partido interno: " + nombreLocal + " / " + nombreVisitante);
		}

		PartidoInterno partido = new PartidoInterno(
				LocalDate.parse(json.getString("fecha")),
				equipoLocal,
				equipoVisitante,
				json.getInt("marcadorLocal"),
				json.getInt("marcadorVisitante"));

		List<InscripcionEquipo> candidatas = new ArrayList<>(equipoLocal.getInscripciones());
		candidatas.addAll(equipoVisitante.getInscripciones());

		JSONArray jsonEstadisticas = json.optJSONArray("estadisticasConjunto");
		if (jsonEstadisticas != null) {
			for (int indice = 0; indice < jsonEstadisticas.length(); indice++) {
				partido.agregarEstadistica(EstadisticaConjunto.fromJSON(
						jsonEstadisticas.getJSONObject(indice), candidatas));
			}
		}
		return partido;
	}
	
}
