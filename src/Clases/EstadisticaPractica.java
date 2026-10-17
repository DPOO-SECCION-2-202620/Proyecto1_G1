package Clases;

public abstract class EstadisticaPractica {

	private int setsGanados;
	private Socio jugador;
	
	public EstadisticaPractica(int setsGanados, Socio jugador) {
		this.setsGanados = setsGanados;
		this.jugador = jugador;
	}

	public int getSetsGanados() {
		return setsGanados;
	}

	public Socio getJugador() {
		return jugador;
	}
	
	
}
