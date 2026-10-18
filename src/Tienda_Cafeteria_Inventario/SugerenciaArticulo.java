package Tienda_Cafeteria_Inventario;

public class SugerenciaArticulo {

	private String nombrePropuesto;
	
	private String categoria;
	
	private double precioSugerido;
	
	private EstadoSugerencia estado = EstadoSugerencia.Pendiente;

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
		this.estado = EstadoSugerencia.Aceptada;
	}
	
	public void rechazar() {
		this.estado = EstadoSugerencia.Rechazada;
	}
}
