package DeportesIndividuales;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Instalaciones.InstalacionIndividual;

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
	
	public boolean hayCupo(List<Reserva> reservas, LocalDate fecha, LocalTime horaInicio, LocalTime horaFinal) {
		int ocupadas = 0;
		for (Reserva reserva : reservas) {
			if (instalaciones.contains(reserva.getInstalacion())
					&& reserva.seSolapaCon(fecha, horaInicio, horaFinal)) {
				ocupadas++;
			}
		}
		return ocupadas < maxInstalacionesSimultaneas;
	}

	public void agregarInstalacion(InstalacionIndividual instalacion) {
		if (!instalaciones.contains(instalacion)) {
			instalaciones.add(instalacion);
		}
	}
	
	public int getMaxInstalacionesSimultaneas() {
		return maxInstalacionesSimultaneas;
	}

	public String getNombre() {
		return nombre;
	}

	public List<InstalacionIndividual> getInstalaciones() {
		return instalaciones;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("maxInstalacionesSimultaneas", maxInstalacionesSimultaneas);
	    JSONArray jsonInstalaciones = new JSONArray();
	    for (InstalacionIndividual instalacion : instalaciones) {
	    	jsonInstalaciones.put(instalacion.toJSON());
	    }
	    json.put("turnos", jsonInstalaciones);
		return json;
	}
}
