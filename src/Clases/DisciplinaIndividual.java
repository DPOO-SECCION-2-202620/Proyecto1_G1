package Clases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class DisciplinaIndividual {

	private int maxInstalacionesIndividuales;
	private String nombre;
	private List<InstalacionIndividual> instalaciones;
	
	public DisciplinaIndividual(int maxInstalacionesIndividuales, String nombre) {
		super();
		this.maxInstalacionesIndividuales = maxInstalacionesIndividuales;
		this.nombre = nombre;
		this.instalaciones = new ArrayList<>();
	}
	
	
	
	
	
	
}
