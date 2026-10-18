package Tienda_Cafeteria_Inventario;

public abstract class Producto {

	public String nombre;
	
	public String categoria;
	
	public double precio;
	
	public int cantidad;

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
		
}
