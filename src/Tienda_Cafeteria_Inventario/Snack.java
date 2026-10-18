package Tienda_Cafeteria_Inventario;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Snack extends Producto{

	private List<Alergeno> alergenos;

	public Snack(String nombre, String categoria, double precio, int cantidad, List<Alergeno> alergenos) {
		super(nombre, categoria, precio, cantidad);
		this.alergenos = alergenos;
	}
	
	public List<Alergeno> getAlergenos() {
		return alergenos;
	}

	@Override
	public JSONObject toJSON() {
	    JSONObject json = super.toJSON();
	    json.put("tipo", "SNACK");
	    JSONArray jsonAlergenos = new JSONArray();
	    for (Alergeno alergeno : alergenos) {
	    	jsonAlergenos.put(alergeno.name());
	    }
	    json.put("alergenos", jsonAlergenos);
	    return json;
	}
	
	public static Snack fromJSON(JSONObject json) {
	    List<Alergeno> alergenos = new ArrayList<>();
	    JSONArray jsonAlergenos = json.optJSONArray("alergenos");
	    if (jsonAlergenos != null) {
	        for (int indice = 0; indice < jsonAlergenos.length(); indice++) {
	            alergenos.add(Alergeno.valueOf(jsonAlergenos.getString(indice)));
	        }
	    }
	    return new Snack(
	            json.getString("nombre"),
	            json.getString("categoria"),
	            json.getDouble("precio"),
	            json.getInt("cantidad"),
	            alergenos);
	}
}
