package PartidosEquipo_Estadisticas;

import org.json.JSONObject;

import DeportesConjunto_Competencias.InscripcionEquipo;

public class EstadisticaVoleibol extends EstadisticaConjunto{
	
	private int aces;
	private int bloqueos;
	
	public EstadisticaVoleibol(InscripcionEquipo jugador, int aces, int bloqueos) {
		super(jugador);
		this.aces = aces;
		this.bloqueos = bloqueos;
	}
	public int getAces() {
		return aces;
	}
	public int getBloqueos() {
		return bloqueos;
	}
	@Override
	public int calcularAporte() {
		return aces + bloqueos;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("aces", aces);
	    json.put("bloqueos", bloqueos);
		return json;
	}

}
