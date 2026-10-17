package PartidosEquipo_Estadisticas;

import DeportesConjunto_Competencias.InscripcionEquipo;

public class EstadisticaBaloncesto extends EstadisticaConjunto{
	
	private int puntos;
	private int rebotes;
	
	public EstadisticaBaloncesto(InscripcionEquipo jugador, int puntos, int rebotes) {
		super(jugador);
		this.puntos = puntos;
		this.rebotes = rebotes;
	}

	public int getPuntos() {
		return puntos;
	}

	public int getRebotes() {
		return rebotes;
	}

	@Override
	public int calcularAporte() {
		return puntos*2 + rebotes;
	}
	
	
	

}
