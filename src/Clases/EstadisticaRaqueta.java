package Clases;

public class EstadisticaRaqueta extends EstadisticaPractica {

	private int gamesGanados;
	private int aces;
	private int erroresNoForzados;
	
	public EstadisticaRaqueta(int setsGanados, Socio jugador, int gamesGanados, int aces, int erroresNoForzados) {
		super(setsGanados, jugador);
		this.gamesGanados = gamesGanados;
		this.aces = aces;
		this.erroresNoForzados = erroresNoForzados;
	}

	public int getGamesGanados() {
		return gamesGanados;
	}

	public int getAces() {
		return aces;
	}

	public int getErroresNoForzados() {
		return erroresNoForzados;
	}
	
	
	
}
