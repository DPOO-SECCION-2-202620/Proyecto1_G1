package Tienda_Cafeteria_Inventario;

import java.util.Map;

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
		json.put("producto", producto.getNombre());
		return json;
	}

	public static LineaVenta fromJSON(JSONObject json, Map<String, Producto> mapaProductos) {
		String nombreProducto = json.getString("producto");
		Producto producto = mapaProductos.get(nombreProducto);
		if (producto == null) {
			throw new IllegalArgumentException("Producto desconocido en la línea de venta: " + nombreProducto);
		}
		return new LineaVenta(json.getInt("cantidad"), producto);
	}

}