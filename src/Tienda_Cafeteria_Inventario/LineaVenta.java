package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public class LineaVenta {

	private int cantidad;
	
	private Producto producto;

	public LineaVenta(int cantidad, Producto producto) {
		super();
		this.cantidad = cantidad;
		this.producto = producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public Producto getProducto() {
		return producto;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("cantidad", cantidad);
	    json.put("login", producto.getNombre());
		return json;
	}
	
}
