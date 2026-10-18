package PartidosEquipo_Estadisticas;

import java.util.Map;
import org.json.JSONObject;
import DeportesConjunto_Competencias.InscripcionEquipo;

public class EstadisticaBaloncesto extends EstadisticaConjunto {
	
	private int puntos;
	private int rebotes;
	
	public EstadisticaBaloncesto(InscripcionEquipo jugador, int puntos, int rebotes) {
		super(jugador);
		this.puntos = puntos;
		this.rebotes = rebotes;
	}

	public int getPuntos() {
		return puntos;
	}

	public int getRebotes() {
		return rebotes;
	}

	@Override
	public int calcularAporte() {
		return (puntos * 2) + rebotes;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "EstadisticaBaloncesto");
		json.put("puntos", puntos);
		json.put("rebotes", rebotes);
		return json;
	}
	
	public static EstadisticaBaloncesto fromJSON(JSONObject json, Map<String, InscripcionEquipo> mapaInscripciones) {
		if (json == null) {
			return null;
		}

		String claveJugador = json.getString("jugador");
		InscripcionEquipo jugador = mapaInscripciones.get(claveJugador);
	
		int puntos = json.getInt("puntos");
		int rebotes = json.getInt("rebotes");
		
		return new EstadisticaBaloncesto(jugador, puntos, rebotes);
	}
}
