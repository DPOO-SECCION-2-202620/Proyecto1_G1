package Clases;
import java.time.LocalDate;

public abstract class Partido {

	private LocalDate fecha;

	public Partido(LocalDate fecha) {
		super();
		this.fecha = fecha;
	}

	public LocalDate getFecha() {
		return fecha;
	}
		
		
	

}
