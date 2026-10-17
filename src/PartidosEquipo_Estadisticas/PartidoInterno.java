package PartidosEquipo_Estadisticas;

import java.time.LocalDate;

import DeportesConjunto_Competencias.Equipo;

public class PartidoInterno extends Partido{

	private int marcadorLocal;
	private int marcadorVisitante;
	private Equipo equipoVisitante;
	private Equipo equipoLocal;
	
	public PartidoInterno(LocalDate fecha, Equipo equipoLocal, Equipo equipoVisitante, int marcadorLocal,
			int marcadorVisitante) {
		super(fecha);
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
		this.equipoVisitante = equipoVisitante;
		this.equipoLocal = equipoLocal;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}

	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}

	public Equipo getEquipoVisitante() {
		return equipoVisitante;
	}

	public Equipo getEquipoLocal() {
		return equipoLocal;
	}
	
	public void registrarMarcador(int marcadorLocal, int marcadorVisitante) {
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
	}
	
	public Equipo getGanador() {
	    if (marcadorLocal > marcadorVisitante) {
	        return equipoLocal;
	    }
	    if (marcadorVisitante > marcadorLocal) {
	        return equipoVisitante;
	    }
	    return null;
	}
	
	
	@Override
	public boolean puedeJugarse() {
	    return equipoLocal.puedeJugar() && equipoVisitante.puedeJugar();
	}

	
	
}
