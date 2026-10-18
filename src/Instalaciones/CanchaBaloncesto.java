package Instalaciones;

import org.json.JSONObject;

public class CanchaBaloncesto extends InstalacionConjunto{

	public CanchaBaloncesto(String nombre, int capacidadMaxima) {
		super(nombre, 10);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("tipo", "CanchaBaloncesto");
		return json;
	}
}
