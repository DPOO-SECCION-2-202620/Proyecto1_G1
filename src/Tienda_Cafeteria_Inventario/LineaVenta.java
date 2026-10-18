package Tienda_Cafeteria_Inventario;

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
	
}
