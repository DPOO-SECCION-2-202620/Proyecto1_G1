package Tienda_Cafeteria_Inventario;

import java.util.List;

import Instalaciones.Instalacion;
import Usuarios.Socio;

public class Tienda {

	private static final double IVA = 0.19;
	
	private List<Articulo> articulos;

	public Tienda() {
		super();
	}
	
	public void agregarArticulo(Articulo articulo) {
		articulos.add(articulo);
	}
	
	public void reabastecer(Articulo articulo, int cantidad) {
		for (int i = 0; i < cantidad; i++) {
			agregarArticulo(articulo);
		}
	}
	
	public Venta vender(Socio comprador, List<LineaVenta> lineas, Instalacion instalacionActual, CodigoDescuento codigoDescuento, int puntosARedimir, double propina) {
		Venta venta = new Venta(propina, puntosARedimir, Rubro.Cafeteria, codigoDescuento, comprador, lineas, IVA);
		return venta;
	}
}
