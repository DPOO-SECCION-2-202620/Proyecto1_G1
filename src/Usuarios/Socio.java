package Usuarios;

import java.time.LocalDateTime;
import java.util.List;

import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.NivelJugador;

import java.util.ArrayList;
import java.time.Duration;

public class Socio extends Usuario{
private int edad;
private int puntosFidelidad;
public static final int MINUTOS_BEBIDA_CALIENTE = 30;
private LocalDateTime horaUltimaBebidaCaliente;
private List<InscripcionEquipo> inscripcionesEquipo;
private List<InscripcionIndividual> inscripcionesIndividuales;


public Socio(int edad, String nombre, String login, String password) {
	super(nombre, login, password);
	this.edad = edad;
	this.puntosFidelidad = 0;
	this.horaUltimaBebidaCaliente = null;
	this.inscripcionesEquipo = new ArrayList<>();
	this.inscripcionesIndividuales = new ArrayList<>();
}

public boolean tieneEquipoEn(DeporteConjunto deporte) {
	for (InscripcionEquipo inscripcion : inscripcionesEquipo) {
		Equipo equipo = inscripcion.getEquipo();
		DeporteConjunto deporteDelEquipo = equipo.getDeporte();
		if (deporteDelEquipo.equals(deporte)) {
			return true;
		}
	}
	return false;
}

public NivelJugador getNivelEn(DisciplinaIndividual disciplina) {
	for (InscripcionIndividual inscripcion : inscripcionesIndividuales) {
		if (inscripcion.getDisciplina() == disciplina) {
			return inscripcion.getNivel();
		}
	}
	return null;
}

public void redimirPuntos(int puntos){
if (puntos <= 0 || puntos > puntosFidelidad) {
	throw new IllegalArgumentException ("Los puntos no pueden ser redimidos");
}
this.puntosFidelidad -= puntos;
}

public void acumularPuntos(int puntos) {
	if (puntos > 0) {
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
	if (minutos >= 0 && minutos < MINUTOS_BEBIDA_CALIENTE) {
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


public boolean esEntrenador() {
    return false;
}

public int getEdad() {
	return edad;
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






	
}
