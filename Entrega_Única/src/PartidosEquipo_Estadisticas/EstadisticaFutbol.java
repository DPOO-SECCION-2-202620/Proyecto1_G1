package PartidosEquipo_Estadisticas;

import java.util.List;

import org.json.JSONObject;

import DeportesConjunto_Competencias.InscripcionEquipo;

public class EstadisticaFutbol extends EstadisticaConjunto {

	private int goles;
	private int asistencias;
	private int tarjetasAmarillas;
	private int tarjetasRojas;
	
	public EstadisticaFutbol(InscripcionEquipo jugador, int goles, int asistencias, int tarjetasAmarillas,
			int tarjetasRojas) {
		super(jugador);
		this.goles = goles;
		this.asistencias = asistencias;
		this.tarjetasAmarillas = tarjetasAmarillas;
		this.tarjetasRojas = tarjetasRojas;
	}

	public int getGoles() {
		return goles;
	}

	public int getAsistencias() {
		return asistencias;
	}

	public int getTarjetasAmarillas() {
		return tarjetasAmarillas;
	}

	public int getTarjetasRojas() {
		return tarjetasRojas;
	}

	@Override
	public int calcularAporte() {
		return goles * 2 + asistencias;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "EstadisticaFutbol");
		json.put("goles", goles);
		json.put("asistencias", asistencias);
		json.put("tarjetasAmarillas", tarjetasAmarillas);
		json.put("tarjetasRojas", tarjetasRojas);
		return json;
	}
	
	public static EstadisticaFutbol fromJSON(JSONObject json, List<InscripcionEquipo> inscripciones) {
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

		int goles = json.getInt("goles");
		int asistencias = json.getInt("asistencias");
		int tarjetasAmarillas = json.getInt("tarjetasAmarillas");
		int tarjetasRojas = json.getInt("tarjetasRojas");

		return new EstadisticaFutbol(jugador, goles, asistencias, tarjetasAmarillas, tarjetasRojas);
	}
}
