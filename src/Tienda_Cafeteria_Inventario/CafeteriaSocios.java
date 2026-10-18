package Tienda_Cafeteria_Inventario;

import java.util.List;

import Instalaciones.Instalacion;
import Instalaciones.InstalacionIndividual;
import Usuarios.Socio;

public class CafeteriaSocios {

	private static final double IMPUESTO_CONSUMO = 0.08;
	
	private static double propinaSugerida = 0.1;
	
	private List<Bebida> bebidas;
	
	private List<Snack> snacks;

	public CafeteriaSocios() {
		super();
	}
	
	public static double getPropinaSugerida() {
		return propinaSugerida;
	}

	public void agregarProducto(Producto producto) {
		if (producto instanceof Bebida) {
			bebidas.add((Bebida) producto);
		}
		
		else if (producto instanceof Snack) {
			snacks.add((Snack) producto);
		}
	}
	
	public void reabastecer(Producto producto, int cantidad) {
		for (int i = 0; i < cantidad; i++) {
			agregarProducto(producto);
		}
	}
	
	public boolean puedeDespachar(Bebida bebida, Instalacion instalacionActual) {
		boolean esCaliente = bebida.getCaliente();
		boolean esCanchaRestringida = instalacionActual instanceof InstalacionIndividual && ((InstalacionIndividual) instalacionActual).permiteBebidaCaliente();
		return !(esCaliente && esCanchaRestringida);
	}
	
	public Venta vender(Socio comprador, List<LineaVenta> lineas, Instalacion instalacionActual, CodigoDescuento codigoDescuento, int puntosARedimir, double propina) {
		for (LineaVenta linea : lineas) {
			Producto producto = linea.getProducto();
		    if (producto instanceof Bebida && !(puedeDespachar((Bebida)producto, instalacionActual))) {
		    	continue;
		    }
		}
		Venta venta = new Venta(propina, puntosARedimir, Rubro.Cafeteria, codigoDescuento, comprador, lineas, IMPUESTO_CONSUMO);
		return venta;
	}
}
