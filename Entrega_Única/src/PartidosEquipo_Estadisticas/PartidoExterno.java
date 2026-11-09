package PartidosEquipo_Estadisticas;

import java.time.LocalDate;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Equipo;

public class PartidoExterno extends Partido{

	private int marcadorPropio;
	private int marcadorRival;
	private String institucionRival;
	private String escenario;
	private Equipo equipo;
	
	public PartidoExterno(LocalDate fecha, int marcadorPropio, int marcadorRival, String institucionRival,
			String escenario, Equipo equipo) {
		super(fecha);
		this.marcadorPropio = marcadorPropio;
		this.marcadorRival = marcadorRival;
		this.institucionRival = institucionRival;
		this.escenario = escenario;
		this.equipo = equipo;
	}

	public int getMarcadorPropio() {
		return marcadorPropio;
	}

	public int getMarcadorRival() {
		return marcadorRival;
	}

	public String getInstitucionRival() {
		return institucionRival;
	}

	public String getEscenario() {
		return escenario;
	}
	
	public Equipo getEquipo() {
		return equipo;
	}

	@Override
	public String toString() {
		return getFecha() + ": " + equipo.getNombre() + " " + marcadorPropio + " - "
				+ marcadorRival + " " + institucionRival + " (" + escenario + ")";
	}
	
	@Override
	public boolean puedeJugarse() {
		return equipo.puedeJugar();
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "PartidoExterno");
		json.put("marcadorPropio", marcadorPropio);
		json.put("marcadorRival", marcadorRival);
		json.put("institucionRival", institucionRival);
		json.put("escenario", escenario);
		json.put("equipo", equipo.getNombre());
		return json;
	}

	public static PartidoExterno fromJSON(JSONObject json, Map<String, Equipo> mapaEquipos) {
		String nombreEquipo = json.getString("equipo");
		Equipo equipo = mapaEquipos.get(nombreEquipo);
		if (equipo == null) {
			throw new IllegalArgumentException("Equipo desconocido en el partido: " + nombreEquipo);
		}

		PartidoExterno partido = new PartidoExterno(
				LocalDate.parse(json.getString("fecha")),
				json.getInt("marcadorPropio"),
				json.getInt("marcadorRival"),
				json.getString("institucionRival"),
				json.getString("escenario"),
				equipo);

		JSONArray jsonEstadisticas = json.optJSONArray("estadisticasConjunto");
		if (jsonEstadisticas != null) {
			for (int indice = 0; indice < jsonEstadisticas.length(); indice++) {
				partido.agregarEstadistica(EstadisticaConjunto.fromJSON(jsonEstadisticas.getJSONObject(indice), equipo.getInscripciones()));
			}
		}
		return partido;
	}
}
