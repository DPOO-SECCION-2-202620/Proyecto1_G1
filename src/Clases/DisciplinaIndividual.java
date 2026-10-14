package Clases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class DisciplinaIndividual {

	private int maxInstalacionesSimultaneas;
	private String nombre;
	private List<InstalacionIndividual> instalaciones;
	
	public DisciplinaIndividual(int maxInstalacionesSimultaneas, String nombre) {
		super();
		this.maxInstalacionesSimultaneas = maxInstalacionesSimultaneas;
		this.nombre = nombre;
		this.instalaciones = new ArrayList<>();
	}
	
	public boolean hayCupo() {
		
	}

	public int getMaxInstalacionesSimultaneas() {
		return maxInstalacionesSimultaneas;
	}

	public String getNombre() {
		return nombre;
	}
	
	
	
	
	
}
