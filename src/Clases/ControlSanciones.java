package Clases;

public class ControlSanciones {

	private int amarillasAcumuladas;
	private EstadoJugador estado;
	
	public ControlSanciones() {
		this.amarillasAcumuladas = 0;
		this.estado = EstadoJugador.HABILITADO;
	}
	
	public void registrarTarjetas(int amarillas,int rojas) {
		if (amarillas < 0 || rojas < 0) {
			throw new IllegalArgumentException ("No puede haber tarjetas negativas");
		}
		this.amarillasAcumuladas += amarillas;
		
		if (rojas > 0 || amarillasAcumuladas >= 2) {
			suspender();
			this.amarillasAcumuladas = 0;
		}
	}
	
	public boolean estaHabilitado() {
		return estado == EstadoJugador.HABILITADO;
	}

	public void suspender() {
		this.estado = EstadoJugador.SUSPENDIDO;
	}

	public void habilitar() {
		this.estado = EstadoJugador.HABILITADO;
	}

	public int getAmarillasAcumuladas() {
		return amarillasAcumuladas;
	}

	public EstadoJugador getEstado() {
		return estado;
	}
	
	
}
