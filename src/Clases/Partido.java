package Clases;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Partido {

	private LocalDate fecha;
	private List<EstadisticaConjunto> estadisticasConjunto;

	public Partido(LocalDate fecha) {
		this.fecha = fecha;
		this.estadisticasConjunto = new ArrayList<>();
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public List<EstadisticaConjunto> getEstadisticas() {
		return estadisticasConjunto;
	}
		
	public void agregarEstadistica(EstadisticaConjunto estadistica) {
		estadisticasConjunto.add(estadistica);
	}
	
	public abstract boolean puedeJugarse();

}
