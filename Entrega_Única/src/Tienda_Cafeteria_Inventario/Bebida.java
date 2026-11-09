package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public class Bebida extends Producto{

	private Boolean caliente;

	public Bebida(String nombre, String categoria, double precio, int cantidad, Boolean caliente) {
		super(nombre, categoria, precio, cantidad);
		this.caliente = caliente;
	}

	public Boolean getCaliente() {
		return caliente;
	}
	
	@Override
	public JSONObject toJSON() {
	    JSONObject json = super.toJSON();
	    json.put("tipo", "Bebida");
	    json.put("caliente", caliente);
	    return json;
	}
	
	public static Bebida fromJSON(JSONObject json) {
	    return new Bebida(
	            json.getString("nombre"),
	            json.getString("categoria"),
	            json.getDouble("precio"),
	            json.getInt("cantidad"),
	            json.getBoolean("caliente"));
	}
}
