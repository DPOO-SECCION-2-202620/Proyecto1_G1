package PartidosEquipo_Estadisticas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Equipo;

public abstract class Partido {

	private LocalDate fecha;
	private List<EstadisticaConjunto> estadisticasConjunto;

	public Partido(LocalDate fecha) {
		this.fecha = fecha;
		this.estadisticasConjunto = new ArrayList<>();
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public List<EstadisticaConjunto> getEstadisticas() {
		return estadisticasConjunto;
	}
		
	public void agregarEstadistica(EstadisticaConjunto estadistica) {
		estadisticasConjunto.add(estadistica);
	}
	
	public abstract boolean puedeJugarse();

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("fecha", fecha.toString());
	    JSONArray jsonEstadisticaConjunto = new JSONArray();
	    for (EstadisticaConjunto estadisticaConjunto : estadisticasConjunto) {
	    	jsonEstadisticaConjunto.put(estadisticaConjunto.toJSON());
	    }
	    json.put("estadisticasConjunto", jsonEstadisticaConjunto);
		return json;
	}
	
	public static Partido fromJSON(JSONObject json, Map<String, Equipo> mapaEquipos) {
		String tipo = json.getString("tipo");
		switch (tipo) {
			case "PartidoInterno":
				return PartidoInterno.fromJSON(json, mapaEquipos);
			case "PartidoExterno":
				return PartidoExterno.fromJSON(json, mapaEquipos);
			default:
				throw new IllegalArgumentException("Tipo de partido desconocido: " + tipo);
		}
	}
	
}
