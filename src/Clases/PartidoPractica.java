package Clases;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

public class PartidoPractica {

	private LocalDate fecha;
	private boolean finalizado;
	private DisciplinaIndividual disciplina;
	private List<Socio> jugadores;
	private InstalacionIndividual instalacion;
	private List<EstadisticaPractica> estadisticas;
	
	
	public PartidoPractica(LocalDate fecha, DisciplinaIndividual disciplina, List<Socio> jugadores,
			InstalacionIndividual instalacion) {
		if (jugadores.size() != 2 && jugadores.size() != 4) {
			throw new IllegalArgumentException("Un partido de práctica tiene 2 o 4 jugadores");
		}
		this.fecha = fecha;
		this.finalizado = false;
		this.disciplina = disciplina;
		this.jugadores = new ArrayList <>(jugadores);
		this.instalacion = instalacion;
		this.estadisticas = new ArrayList <>();
	}


	public LocalDate getFecha() {
		return fecha;
	}


	public boolean isFinalizado() {
		return finalizado;
	}


	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}


	public List<Socio> getJugadores() {
		return jugadores;
	}


	public InstalacionIndividual getInstalacion() {
		return instalacion;
	}


	public List<EstadisticaPractica> getEstadisticas() {
		return estadisticas;
	}
	
	public boolean validarNiveles() {
		for (Socio jugador : jugadores) {
			if(jugador.esEntrenador()) {
				return true;
			}
		}
		for (Socio a: jugadores) {
			for(Socio b : jugadores) {
				NivelJugador nivelA = a.getNivelEn(disciplina);
				NivelJugador nivelB = b.getNivelEn(disciplina);
				if (!nivelA.esCompatibleCon(nivelB)) {
					return false;
				}
			}
		}
		return true;
	}

	public void finalizar(List<EstadisticaPractica> estadisticas) throws ReglaNegocioException {
		if (finalizado == true) {
	        throw new ReglaNegocioException("El partido ya está finalizado");
	    }
	    if (estadisticas.size() != jugadores.size()) {
	        throw new ReglaNegocioException("Debe haber una estadística por jugador");
	    }
	    for (EstadisticaPractica estadistica : estadisticas) {
	        if (!jugadores.contains(estadistica.getJugador())) {
	            throw new ReglaNegocioException(estadistica.getJugador().getNombre() + " no juega este partido");
	        }
	    }
	    this.estadisticas.addAll(estadisticas);
	    this.finalizado = true;
	}

	public Socio getGanador() {
		EstadisticaPractica mejor = null;
	    for (EstadisticaPractica estadistica : estadisticas) {
	        if (mejor == null || estadistica.getSetsGanados() > mejor.getSetsGanados()) {
	            mejor = estadistica;
	        }
	    }
	    if (mejor == null) {
	        return null;
	    }
	    return mejor.getJugador();
	}
	
	
	
}
