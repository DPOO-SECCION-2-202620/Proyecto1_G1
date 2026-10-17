package Clases;

public class EstadisticaTenisDeMesa extends EstadisticaPractica{
	
	private int puntosTotales;

	public EstadisticaTenisDeMesa(int setsGanados, Socio jugador, int puntosTotales) {
		super(setsGanados, jugador);
		this.puntosTotales = puntosTotales;
	}

	public int getPuntosTotales() {
		return puntosTotales;
	}
	
	
	
}
