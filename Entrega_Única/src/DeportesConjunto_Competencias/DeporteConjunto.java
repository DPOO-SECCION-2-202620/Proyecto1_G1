package DeportesConjunto_Competencias;

import java.util.Map;

import org.json.JSONObject;

public abstract class DeporteConjunto {
	
	private String nombre;
	protected int minimoJugadores;

	public DeporteConjunto(String nombre, int minimoJugadores) {
		this.nombre = nombre;
		this.minimoJugadores = minimoJugadores;
	}
	
	public abstract int calcularPuntos(int marcadorPropio, int marcadorRival);
	
	public abstract boolean admiteEmpate();
	
	public int getMinimoJugadores() {
		return minimoJugadores;
	}

	public String getNombre() {
		return nombre;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("minimoJugadores", minimoJugadores);
		return json;
	}
	
	public static DeporteConjunto fromJSON(JSONObject json, Map<String, DeporteConjunto> mapaDeportes) {
		String nombre = json.getString("nombre");

		DeporteConjunto existente = mapaDeportes.get(nombre);
		if (existente != null) {
			return existente;
		}

		DeporteConjunto deporte;
		switch (nombre) {
			case "Futbol":
				deporte = new Futbol();
				break;
			case "Voleibol":
				deporte = new Voleibol();
				break;
			case "Baloncesto":
				deporte = new Baloncesto();
				break;
			default:
				throw new IllegalArgumentException("Deporte de conjunto desconocido: " + nombre);
		}

		mapaDeportes.put(nombre, deporte);
		return deporte;
	}
}
