package Instalaciones;

import org.json.JSONObject;

public abstract class Instalacion {

	private String nombre;
	private int capacidadMaxima;

	public Instalacion(String nombre, int capacidadMaxima) {
		this.nombre = nombre;
		this.capacidadMaxima = capacidadMaxima;
	}

	public String getNombre() {
		return nombre;
	}

	public int getCapacidadMaxima() {
		return capacidadMaxima;
	}

	public void setCapacidadMaxima(int capacidadMaxima) {
		this.capacidadMaxima = capacidadMaxima;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("capacidadMaxima", capacidadMaxima);
		return json;
	}
}
