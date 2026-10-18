package Instalaciones;

import org.json.JSONObject;

public class CanchaFutbol extends InstalacionConjunto{

	public CanchaFutbol(String nombre, int capacidadMaxima) {
		super(nombre, 22);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("tipo", "CanchaFutbol");
		return json;
	}
}
