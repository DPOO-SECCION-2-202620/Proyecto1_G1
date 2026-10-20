package Tienda_Cafeteria_Inventario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

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
	
	public Venta(LocalDate fecha, double propina, double total, int puntosRedimidos, int puntosGenerados,
			Rubro rubro, Socio comprador, List<LineaVenta> lineas, double tasaImpuestos) {
		super();
		this.fecha = fecha;
		this.propina = propina;
		this.total = total;
		this.puntosRedimidos = puntosRedimidos;
		this.puntosGenerados = puntosGenerados;
		this.rubro = rubro;
		this.comprador = comprador;
		this.lineas = lineas;
		this.tasaImpuestos = tasaImpuestos;
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
		
		double total = 0;
		for (LineaVenta linea : lineas) {
		    if (linea != null && linea.getProducto() != null) {
		    	total += (linea.getProducto().getPrecio() * linea.getCantidad()) * (1 + this.tasaImpuestos);
		    }
		}
		
		double descuento = 0;
	    if (codigoDescuento != null) {
	    	boolean esDueno = comprador instanceof Empleado
	    			&& codigoDescuento.getCodigo().equals(((Empleado) comprador).getCodigoDescuento().getCodigo());
	    	descuento = codigoDescuento.getPorcentaje(esDueno);
	    	total -= total * descuento;
	    }
	    
	    total += this.propina;
	    
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
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("fecha", fecha.toString());
		json.put("propina", propina);
		json.put("total", total);
		json.put("puntosRedimidos", puntosRedimidos);
		json.put("puntosGenerados", puntosGenerados);
		json.put("rubro", rubro.name());
		json.put("tasaImpuestos", tasaImpuestos);
		json.put("comprador", comprador.getLogin());
		JSONArray jsonLineas = new JSONArray();
		for (LineaVenta linea : lineas) {
			jsonLineas.put(linea.toJSON());
		}
		json.put("lineas", jsonLineas);
		return json;
	}
	
	public static Venta fromJSON(JSONObject json,
			Map<String, Usuario> mapaUsuarios,
			Map<String, Producto> mapaProductos) {

		String loginComprador = json.getString("comprador");
		Usuario usuario = mapaUsuarios.get(loginComprador);
		Socio comprador;
		if (usuario instanceof Socio) {
			comprador = (Socio) usuario;
		} else {
			comprador = new Socio(0, loginComprador, loginComprador, "");
		}

		List<LineaVenta> lineas = new ArrayList<>();
		JSONArray jsonLineas = json.optJSONArray("lineas");
		if (jsonLineas != null) {
			for (int indice = 0; indice < jsonLineas.length(); indice++) {
				lineas.add(LineaVenta.fromJSON(jsonLineas.getJSONObject(indice), mapaProductos));
			}
		}

		return new Venta(
				LocalDate.parse(json.getString("fecha")),
				json.getDouble("propina"),
				json.getDouble("total"),
				json.getInt("puntosRedimidos"),
				json.getInt("puntosGenerados"),
				Rubro.valueOf(json.getString("rubro")),
				comprador,
				lineas,
				json.getDouble("tasaImpuestos"));
	}
	
}
