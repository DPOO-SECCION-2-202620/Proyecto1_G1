package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public class SugerenciaArticulo {

	private String nombrePropuesto;
	
	private String categoria;
	
	private double precioSugerido;
	
	private EstadoSugerencia estado = EstadoSugerencia.PENDIENTE;

	public SugerenciaArticulo(String nombrePropuesto, String categoria, double precioSugerido) {
		super();
		this.nombrePropuesto = nombrePropuesto;
		this.categoria = categoria;
		this.precioSugerido = precioSugerido;
	}

	public String getNombrePropuesto() {
		return nombrePropuesto;
	}

	public String getCategoria() {
		return categoria;
	}

	public double getPrecioSugerido() {
		return precioSugerido;
	}

	public EstadoSugerencia getEstado() {
		return estado;
	}

	public void aprobar() {
		this.estado = EstadoSugerencia.ACEPTADA;
	}
	
	public void rechazar() {
		this.estado = EstadoSugerencia.RECHAZADA;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombrePropuesto", nombrePropuesto);
	    json.put("categoria", categoria);
	    json.put("precioSugerido", precioSugerido);
	    json.put("estado", estado.name());
		return json;
	}
	
	public static SugerenciaArticulo fromJSON(JSONObject json) {
		SugerenciaArticulo sugerencia = new SugerenciaArticulo(
				json.getString("nombrePropuesto"),
				json.getString("categoria"),
				json.getDouble("precioSugerido"));
		sugerencia.estado = EstadoSugerencia.valueOf(json.getString("estado"));
		return sugerencia;
	}
}
