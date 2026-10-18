package DeportesIndividuales;

public enum NivelJugador {

	Principiante,
	Intermedio,
	Avanzado;

	
	public boolean esCompatibleCon (NivelJugador otro) {
		int diferencia = Math.abs(this.ordinal() - otro.ordinal());
		return diferencia <= 1;
		
	}
	
}
