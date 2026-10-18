package Tienda_Cafeteria_Inventario;

public class Snack extends Producto{

	private Alergenos alergenos;

	public Snack(String nombre, String categoria, double precio, int cantidad, Alergenos alergenos) {
		super(nombre, categoria, precio, cantidad);
		this.alergenos = alergenos;
	}

	public Alergenos getalergenos() {
		return alergenos;
	}
	
}
