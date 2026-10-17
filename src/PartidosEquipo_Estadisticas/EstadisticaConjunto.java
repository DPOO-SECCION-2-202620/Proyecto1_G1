package PartidosEquipo_Estadisticas;

import DeportesConjunto_Competencias.InscripcionEquipo;

public abstract class EstadisticaConjunto {

	private InscripcionEquipo jugador;

	public EstadisticaConjunto(InscripcionEquipo jugador) {
		this.jugador = jugador;
	}
	
	public abstract int calcularAporte();
	
	public InscripcionEquipo getJugador() {
		return jugador;
	}
}
