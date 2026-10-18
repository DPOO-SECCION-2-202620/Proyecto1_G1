package PartidosEquipo_Estadisticas;

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
	    json.put("jugador", jugador.getJugador());
		return json;
	}
}
