package PartidosEquipo_Estadisticas;

import org.json.JSONObject;

import DeportesConjunto_Competencias.InscripcionEquipo;

public class EstadisticaFutbol extends EstadisticaConjunto{

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
		JSONObject json = new JSONObject();
	    json.put("goles", goles);
	    json.put("asistencias", asistencias);
	    json.put("tarjetasAmarillas", tarjetasAmarillas);
	    json.put("tarjetasRojas", tarjetasRojas);
		return json;
	}
	
	
}
