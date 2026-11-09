package PartidosEquipo_Estadisticas;

import java.util.List;

import org.json.JSONObject;

import DeportesConjunto_Competencias.InscripcionEquipo;

public abstract class EstadisticaConjunto {

	private InscripcionEquipo jugador;

	public EstadisticaConjunto(InscripcionEquipo jugador) {
		this.jugador = jugador;
	}
	
	public abstract int calcularAporte();
	
	public InscripcionEquipo getJugador() {
		return jugador;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("jugador", jugador.getJugador().getLogin());
		return json;
	}
	
	public static EstadisticaConjunto fromJSON(JSONObject json, List<InscripcionEquipo> Inscripciones) {
	    String tipo = json.getString("tipo");

	    switch (tipo) {
	        case "EstadisticaFutbol":
	            return EstadisticaFutbol.fromJSON(json, Inscripciones);
	        case "EstadisticaBaloncesto":
	            return EstadisticaBaloncesto.fromJSON(json, Inscripciones);
	        case "EstadisticaVoleibol":
	            return EstadisticaVoleibol.fromJSON(json, Inscripciones);
	        default:
	            throw new IllegalArgumentException("Tipo de estadística desconocido: " + tipo);
	    }
	}
}