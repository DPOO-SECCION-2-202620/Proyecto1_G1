package PartidosEquipo_Estadisticas;

import java.time.LocalDate;

import org.json.JSONObject;

import DeportesConjunto_Competencias.Equipo;

public class PartidoExterno extends Partido{

	private int marcadorPropio;
	private int marcadorRival;
	private String institucionRival;
	private String escenario;
	private Equipo equipo;
	
	public PartidoExterno(LocalDate fecha, int marcadorPropio, int marcadorRival, String institucionRival,
			String escenario, Equipo equipo) {
		super(fecha);
		this.marcadorPropio = marcadorPropio;
		this.marcadorRival = marcadorRival;
		this.institucionRival = institucionRival;
		this.escenario = escenario;
		this.equipo = equipo;
	}

	public int getMarcadorPropio() {
		return marcadorPropio;
	}

	public int getMarcadorRival() {
		return marcadorRival;
	}

	public String getInstitucionRival() {
		return institucionRival;
	}

	public String getEscenario() {
		return escenario;
	}
	
	public Equipo getEquipo() {
		return equipo;
	}

	@Override
	public boolean puedeJugarse() {
		return equipo.puedeJugar();
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("marcadorPropio", marcadorPropio);
	    json.put("marcadorRival", marcadorRival);
	    json.put("institucionRival", institucionRival);
	    json.put("escenario", escenario);
	    json.put("equipo", equipo.getNombre());
		return json;
	}
}
