package Tienda_Cafeteria_Inventario;

import java.time.LocalDate;
import java.util.List;

import Usuarios.Empleado;
import Usuarios.Socio;

public class Venta {
	
	private static final double VALOR_PUNTOS = 0.02;
	
	private LocalDate fecha;
	
	private double propina;
	
	private double total;
	
	private int puntosRedimidos;
	
	private Rubro rubro;
	
	private double tasaImpuestos;
	
	private Socio comprador;
	
	private List<LineaVenta> lineas;

	public Venta(double propina, int puntosRedimidos, Rubro rubro, CodigoDescuento codigoDescuento, Socio comprador,
			List<LineaVenta> lineas, double tasaImpuestos) {
		super();
		this.fecha = LocalDate.now();
		this.propina = propina;
		this.puntosRedimidos = puntosRedimidos;
		this.rubro = rubro;
		this.tasaImpuestos = tasaImpuestos;
		this.comprador = comprador;
		
		this.total = calcularTotal(lineas, codigoDescuento);
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public double getPropina() {
		return propina;
	}

	public double getTotal() {
		return total;
	}

	public int getPuntosRedimidos() {
		return puntosRedimidos;
	}

	public Rubro getRubro() {
		return rubro;
	}

	public double getTasaImpuestos() {
		return tasaImpuestos;
	}
	
	public double calcularTotal(List<LineaVenta> lineas, CodigoDescuento codigoDescuento){
		double total = 0;
		for (LineaVenta linea : lineas) {
		    if (linea != null && linea.getProducto() != null) {
		    	total += (linea.getProducto().getPrecio() * linea.getCantidad()) * (1 + this.tasaImpuestos);
		    }
		}
		double descuento = 0;
	    if (codigoDescuento != null) {
	    	descuento = codigoDescuento.getPorcentaje(comprador instanceof Empleado);
	    	total -= total * descuento;
	    }
		total += this.propina;
		return total;
	}
	
	
	
}
