package Tienda_Cafeteria_Inventario;

public class Bebida extends Producto{

	private Boolean caliente;

	public Bebida(String nombre, String categoria, double precio, int cantidad, Boolean caliente) {
		super(nombre, categoria, precio, cantidad);
		this.caliente = caliente;
	}

	public Boolean getCaliente() {
		return caliente;
	}
	
}
