package Instalaciones;

import java.util.Map;

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
	
	public static Instalacion fromJSON(JSONObject json, Map<String, Instalacion> mapaInstalaciones) {
		String tipo = json.getString("tipo");

		switch (tipo) {
			case "CanchaTenis":
				return CanchaTenis.fromJSON(json, mapaInstalaciones);
			case "CanchaPadel":
				return CanchaPadel.fromJSON(json, mapaInstalaciones);
			case "MESA_TENIS_DE_MESA":
				return MesaTenisDeMesa.fromJSON(json, mapaInstalaciones);
			case "CanchaFutbol":
				return CanchaFutbol.fromJSON(json);
			case "CanchaBaloncesto":
				return CanchaBaloncesto.fromJSON(json);
			case "CanchaVoleibol":
				return CanchaVoleibol.fromJSON(json);
			default:
				throw new IllegalArgumentException("Tipo de Instalacion desconocido: " + tipo);
		}
	}
}
