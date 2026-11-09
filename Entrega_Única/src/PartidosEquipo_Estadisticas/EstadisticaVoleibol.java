package PartidosEquipo_Estadisticas;

import java.util.List;

import org.json.JSONObject;

import DeportesConjunto_Competencias.InscripcionEquipo;

public class EstadisticaVoleibol extends EstadisticaConjunto {
	
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
		JSONObject json = super.toJSON();
		json.put("tipo", "EstadisticaVoleibol");
		json.put("aces", aces);
		json.put("bloqueos", bloqueos);
		return json;
	}

	public static EstadisticaVoleibol fromJSON(JSONObject json, List<InscripcionEquipo> inscripciones) {
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

		int aces = json.getInt("aces");
		int bloqueos = json.getInt("bloqueos");

		return new EstadisticaVoleibol(jugador, aces, bloqueos);
	}
}
