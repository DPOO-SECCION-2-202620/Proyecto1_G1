package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public abstract class Producto {

	private String nombre;
	
	private String categoria;
	
	private double precio;
	
	private int cantidad;

	public Producto(String nombre, String categoria, double precio, int cantidad) {
		super();
		this.nombre = nombre;
		this.categoria = categoria;
		this.precio = precio;
		this.cantidad = cantidad;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCategoria() {
		return categoria;
	}

	public double getPrecio() {
		return precio;
	}

	public int getCantidad() {
		return cantidad;
	}

	public boolean reducirCantidad(int cantidad) {
	    if (this.cantidad - cantidad >= 0) {
	        this.cantidad -= cantidad;
	        return true;
	    }
	    return false;
	}
	
	public void agregarCantidad(int cantidad) {
	    this.cantidad += cantidad;
	}
		
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("categoria", categoria);
	    json.put("precio", precio);
	    json.put("cantidad", cantidad);
		return json;
	}
	
	public static Producto fromJSON(JSONObject json) {
	    String tipo = json.getString("tipo");

	    switch (tipo) {
	        case "Articulo":
	        	return Articulo.fromJSON(json);
	        case "Bebida":
	        	return Bebida.fromJSON(json);
	        case "Snack": 
	            return Snack.fromJSON(json);
	        default:throw new IllegalArgumentException("Tipo de producto desconocido");
	    }
	}
	
}
