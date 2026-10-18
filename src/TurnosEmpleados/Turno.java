package TurnosEmpleados;

import java.time.LocalTime;

import org.json.JSONObject;

public class Turno {

	private String diaSemana;
	
	private LocalTime horaInicio;
	
	private LocalTime horaFinal;

	public Turno(String diaSemana, LocalTime horaInicio, LocalTime horaFinal) {
		super();
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFinal = horaFinal;
	}

	public String getDiaSemana() {
		return diaSemana;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFinal() {
		return horaFinal;
	}
	
	public boolean cubre(String diaSemana, LocalTime hora) {
		
	    boolean mismoDia = this.diaSemana.equalsIgnoreCase(diaSemana);

	    boolean horaEnRango = (hora.isAfter(this.horaInicio) || hora.equals(this.horaInicio)) 
	                       && (hora.isBefore(this.horaFinal) || hora.equals(this.horaFinal));

	    return mismoDia && horaEnRango;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("diaSemana", diaSemana);
	    json.put("horaInicio", horaInicio);
	    json.put("horaFinal", horaFinal);
		return json;
	}
}
