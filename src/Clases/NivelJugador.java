package Clases;

public enum NivelJugador {

	PRINCIPIANTE,
	INTERMEDIO,
	AVANZADO;

	
	public boolean esCompatibleCon (NivelJugador otro) {
		int diferencia = Math.abs(this.ordinal() - otro.ordinal());
		return diferencia <= 1;
		
	}
	
}
