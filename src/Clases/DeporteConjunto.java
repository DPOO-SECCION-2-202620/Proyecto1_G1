package Clases;

public abstract class DeporteConjunto {

	protected int minimoJugadores;

	public DeporteConjunto(int minimoJugadores) {
		super();
		this.minimoJugadores = minimoJugadores;
	}
	
	public abstract int calcularPuntos(int marcadorPropio, int marcadorRival);
	
	public abstract boolean admiteEmpate();
	
	public int getMinimoJugadores() {
		return minimoJugadores;
	}
	
}
