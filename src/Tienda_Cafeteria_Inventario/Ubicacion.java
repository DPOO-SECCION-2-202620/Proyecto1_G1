package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public class Ubicacion {

	private String nombre;

	public Ubicacion(String nombre) {
		super();
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("nombre", nombre);
		return json;
	}
}
