package DeportesConjunto_Competencias;

import org.json.JSONObject;

public class RegistroNomina {

	private InscripcionEquipo inscripcionEquipo;
	private ControlSanciones controlSanciones;

	public RegistroNomina(InscripcionEquipo inscripcionEquipo) {
		this.inscripcionEquipo = inscripcionEquipo;
		this.controlSanciones = new ControlSanciones();
	}
	
	public InscripcionEquipo getInscripcionEquipo() {
		return inscripcionEquipo;
	}
	
	public void registrarTarjetas(int amarillas, int rojas) {
		controlSanciones.registrarTarjetas(amarillas, rojas);
	}

	public boolean estaHabilitado() {
		return controlSanciones.estaHabilitado();
	}

	public void cumplirSuspension() {
		controlSanciones.habilitar();
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("jugador", inscripcionEquipo.getJugador().getLogin());
		json.put("controlSanciones", controlSanciones.toJSON());
		return json;
	}

	public static RegistroNomina fromJSON(JSONObject json, Equipo equipo) {
		String loginJugador = json.getString("jugador");

		InscripcionEquipo inscripcionEquipo = null;
		for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
			if (inscripcion.getJugador().getLogin().equals(loginJugador)) {
				inscripcionEquipo = inscripcion;
				break;
			}
		}
		if (inscripcionEquipo == null) {
			throw new IllegalArgumentException("El jugador no pertenece al equipo: " + loginJugador);
		}

		RegistroNomina registro = new RegistroNomina(inscripcionEquipo);
		registro.controlSanciones = ControlSanciones.fromJSON(json.getJSONObject("controlSanciones"));
		return registro;
	}
	
}
