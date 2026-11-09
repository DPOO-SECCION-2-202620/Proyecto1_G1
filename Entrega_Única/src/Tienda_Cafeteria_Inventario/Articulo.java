package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public class Articulo extends Producto{

	public Articulo(String nombre, String categoria, double precio, int cantidad) {
		super(nombre, categoria, precio, cantidad);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "Articulo");
		return json;
	}
	
	public static Articulo fromJSON(JSONObject json) {
		return new Articulo(
				json.getString("nombre"),
				json.getString("categoria"),
				json.getDouble("precio"),
				json.getInt("cantidad"));
	}
}
