package Instalaciones;

import org.json.JSONObject;

public class CanchaVoleibol extends InstalacionConjunto{

	public CanchaVoleibol(String nombre, int capacidadMaxima) {
		super(nombre, 12);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("tipo", "CanchaVoleibol");
		return json;
	}
}
