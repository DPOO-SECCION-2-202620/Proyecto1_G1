package Instalaciones;

import org.json.JSONObject;

public class CanchaBaloncesto extends InstalacionConjunto{

	public CanchaBaloncesto(String nombre) {
		super(nombre, 10);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "CanchaBaloncesto");
		return json;
	}
	
	public static CanchaBaloncesto fromJSON(JSONObject json) {
	    return new CanchaBaloncesto(json.getString("tipo"));
	}
}
