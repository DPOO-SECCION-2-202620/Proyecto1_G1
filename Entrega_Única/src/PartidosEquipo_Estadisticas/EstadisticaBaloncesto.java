package PartidosEquipo_Estadisticas;

import java.util.List;

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
	
	public static EstadisticaBaloncesto fromJSON(JSONObject json, List<InscripcionEquipo> inscripciones) {
		if (json == null) {
			return null;
		}

		String loginJugador = json.getString("jugador");
		InscripcionEquipo jugador = null;
		for (InscripcionEquipo inscripcion : inscripciones) {
			if (inscripcion.getJugador().getLogin().equals(loginJugador)) {
				jugador = inscripcion;
				break;
			}
		}
		if (jugador == null) {
			throw new IllegalArgumentException(
					"El jugador " + loginJugador + " no está inscrito en el equipo de la estadística");
		}

		int puntos = json.getInt("puntos");
		int rebotes = json.getInt("rebotes");

		return new EstadisticaBaloncesto(jugador, puntos, rebotes);
	}
}
