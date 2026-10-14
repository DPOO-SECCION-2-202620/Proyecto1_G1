package Clases;

public abstract class DeporteConjunto {
	
	private String nombre;
	protected int minimoJugadores;

	public DeporteConjunto(String nombre, int minimoJugadores) {
		super();
		this.nombre = nombre;
		this.minimoJugadores = minimoJugadores;
	}
	
	public abstract int calcularPuntos(int marcadorPropio, int marcadorRival);
	
	public abstract boolean admiteEmpate();
	
	public int getMinimoJugadores() {
		return minimoJugadores;
	}

	public String getNombre() {
		return nombre;
	}
	
	
}
