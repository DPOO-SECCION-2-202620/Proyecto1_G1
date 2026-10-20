package Instalaciones;

import org.json.JSONObject;

public class CanchaVoleibol extends InstalacionConjunto{

	public CanchaVoleibol(String nombre) {
		super(nombre, 12);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "CanchaVoleibol");
		return json;
	}
	
	public static CanchaVoleibol fromJSON(JSONObject json) {
	    return new CanchaVoleibol(json.getString("nombre"));
	}
}
