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
	
	private int puntosGenerados;

	public Venta(double propina, int puntosRedimidos, Rubro rubro, CodigoDescuento codigoDescuento, Socio comprador,
			List<LineaVenta> lineas, double tasaImpuestos) {
		super();
		this.fecha = LocalDate.now();
		this.propina = propina;
		this.puntosRedimidos = puntosRedimidos;
		this.rubro = rubro;
		this.tasaImpuestos = tasaImpuestos;
		this.comprador = comprador;
		this.lineas = lineas;
		
		this.total = calcularTotal(codigoDescuento);
		this.puntosGenerados = (int) Math.round(total * VALOR_PUNTOS);
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
	
	private double calcularTotal(CodigoDescuento codigoDescuento){
		
		// Calculo del total con las lineas.
		double total = 0;
		for (LineaVenta linea : lineas) {
		    if (linea != null && linea.getProducto() != null) {
		    	total += (linea.getProducto().getPrecio() * linea.getCantidad()) * (1 + this.tasaImpuestos);
		    }
		}
		
		// Calculo del total con el descuento de empleado.
		double descuento = 0;
	    if (codigoDescuento != null) {
	    	descuento = codigoDescuento.getPorcentaje(comprador instanceof Empleado);
	    	total -= total * descuento;
	    }
	    
	    // Calculo del total con la propina.
	    total += this.propina;
	    
	    // Calculo del total con los puntos usados.
	    int puntosNecesarios = (int) Math.ceil(total);
	    if (puntosRedimidos >= puntosNecesarios) {
	        puntosRedimidos = puntosNecesarios;
	        total = 0;
	    }
	    else {
	    	total -= puntosRedimidos;
	    }
	    
		return total;
	}

	public int getPuntosGenerados() {
		return puntosGenerados;
	}
	
}
