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
	    json.put("inscripcionEquipo", inscripcionEquipo.getEquipo());
	    json.put("controlSanciones", controlSanciones.toJSON());
		return json;
	}
	
}
