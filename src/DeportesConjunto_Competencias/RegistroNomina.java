package DeportesConjunto_Competencias;

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
	
}
