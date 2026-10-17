package DeportesIndividuales;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

	private int numeroJugadores;
	private LocalTime horaInicio;
	private LocalTime horaFinal;
	private LocalDate fecha;
	private Modalidad modalidad;
	private Socio jugador;
	private InstalacionIndividual instalacion;
	
	public Reserva(int numeroJugadores, LocalTime horaInicio, LocalTime horaFinal, LocalDate fecha, Modalidad modalidad,
			Socio jugador, InstalacionIndividual instalacion) {
		this.numeroJugadores = numeroJugadores;
		this.horaInicio = horaInicio;
		this.horaFinal = horaFinal;
		this.fecha = fecha;
		this.modalidad = modalidad;
		this.jugador = jugador;
		this.instalacion = instalacion;
	}

	public int getNumeroJugadores() {
		return numeroJugadores;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFinal() {
		return horaFinal;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Modalidad getModalidad() {
		return modalidad;
	}

	public InstalacionIndividual getInstalacion() {
		return instalacion;
	}
	
	public Socio getJugador() {
		return jugador;
	}
	
	
	public boolean seSolapaCon (LocalDate fecha,LocalTime horaInicio,LocalTime horaFinal) {
		
		
		return false;
	}

	

	
	
	
	
}


