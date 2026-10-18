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
		JSONObject json = new JSONObject();
		json.put("tipo", "Bebida");
		return json;
	}
}
