package Clases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Socio extends Usuario{
private int edad;
private int puntosFidelidad;
public static final int minutosBebidaCaliente = 30;
private LocalDateTime horaUltimaBebidaCaliente;
private List<InscripcionEquipo> inscripcionesEquipo;
private List<InscripcionIndividual> inscripcionesIndividuales;
private List<Reserva> reservas;

public Socio(int edad,  String nombre, String login, String password, int puntosFidelidad) {
	super(nombre,login,password);
	this.edad = edad;
	this.puntosFidelidad = puntosFidelidad;
	this.horaUltimaBebidaCaliente = null;
    this.inscripcionesEquipo = new ArrayList<>();
    this.inscripcionesIndividuales = new ArrayList<>();
    this.reservas = new ArrayList<>();
	
}

public boolean tieneEquipoEn (DeporteConjunto deporte) {
	for (InscripcionEquipo inscripcion : inscripcionesEquipo ) {
		Equipo equipo = inscripcion.getEquipo();
		DeporteConjunto deporteDelEquipo = equipo.getDeporte();
		if (deporteDelEquipo.equals(deporte)) {
			return true;
		}
	}
	return false;
}

public void redimirPuntos(int puntos){
if (puntos <= 0 || puntos > puntosFidelidad) {
	throw new IllegalArgumentException ("Los puntos no pueden ser redimidos");
}
else {
	this.puntosFidelidad -= puntos;
}
}

public void acumularPuntos (int puntos) {
	if (puntosFidelidad >= 0) {
	this.puntosFidelidad += puntos;	
	}
}

public void registrarBebidaCaliente(LocalDateTime momento) {
    this.horaUltimaBebidaCaliente = momento;
}


public boolean llevaBebidaCaliente(LocalDateTime momento) {
	if (horaUltimaBebidaCaliente == null) {
        return false;
    }
	long minutos = Duration.between(this.horaUltimaBebidaCaliente, momento).toMinutes();
	if (minutos >= 0 && minutos < minutosBebidaCaliente) {
		return true;
	}
	return false;
}


public void agregarInscripcionEquipo(InscripcionEquipo inscripcion) {
    inscripcionesEquipo.add(inscripcion);
}

public void agregarInscripcionIndividual(InscripcionIndividual inscripcion) {
    inscripcionesIndividuales.add(inscripcion);
}

public void agregarReserva(Reserva reserva) {
    reservas.add(reserva);
}

public boolean esEntrenador() {
    return false;
}

public int getEdad() {
	return edad;
}

public void setEdad(int edad) {
	this.edad = edad;
}

public int getPuntosFidelidad() {
	return puntosFidelidad;
}

public LocalDateTime getHoraUltimaBebidaCaliente() {
	return horaUltimaBebidaCaliente;
}

public List<InscripcionEquipo> getInscripcionesEquipo() {
	return inscripcionesEquipo;
}

public List<InscripcionIndividual> getInscripcionesIndividuales() {
	return inscripcionesIndividuales;
}

public List<Reserva> getReservas() {
	return reservas;
}




	
}
