package Instalaciones;

import org.json.JSONObject;

public class CanchaFutbol extends InstalacionConjunto{

	public CanchaFutbol(String nombre) {
		super(nombre, 22);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "CanchaFutbol");
		return json;
	}
	
	public static CanchaFutbol fromJSON(JSONObject json) {
	    return new CanchaFutbol(json.getString("tipo"));
	}
}
