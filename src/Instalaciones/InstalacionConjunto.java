package Instalaciones;

import org.json.JSONObject;

public abstract class InstalacionConjunto extends Instalacion{

	public InstalacionConjunto(String nombre, int capacidadMaxima) {
		super(nombre, capacidadMaxima);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("tipo", "InstalacionConjunto");
		return json;
	}
	
}
