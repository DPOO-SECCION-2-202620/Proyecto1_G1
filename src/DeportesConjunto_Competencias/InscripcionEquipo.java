package DeportesConjunto_Competencias;

import org.json.JSONObject;

import Usuarios.Socio;

public class InscripcionEquipo {

	private Socio jugador;
	private Equipo equipo;
	private String posicion;
	private int numeroCamiseta;
	private ControlSanciones controlSanciones;
	
	public InscripcionEquipo(Socio jugador, Equipo equipo, String posicion, int numeroCamiseta) {
		this.jugador = jugador;
		this.equipo = equipo;
		this.posicion = posicion;
		this.numeroCamiseta = numeroCamiseta;
		this.controlSanciones = new ControlSanciones();
	}
	
	public boolean estaHabilitado() {
		return controlSanciones.estaHabilitado();
	}
	
	public void registrarTarjetas(int amarillas, int rojas) {
		controlSanciones.registrarTarjetas(amarillas, rojas);
	}

	public void suspender() {
		controlSanciones.suspender();
	}

	public void cumplirSuspension() {
		controlSanciones.habilitar();
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public Socio getJugador() {
		return jugador;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public int getNumeroCamiseta() {
		return numeroCamiseta;
	}

	public ControlSanciones getControlSanciones() {
		return controlSanciones;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("jugador", jugador);
	    json.put("equipo", equipo);
	    json.put("posicion", posicion);
	    json.put("numeroCamiseta", numeroCamiseta);
	    json.put("controlSanciones", controlSanciones);
		return json;
	}

	
	
}
