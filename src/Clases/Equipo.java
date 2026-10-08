package Clases;

import java.util.List;
import java.util.ArrayList;

public class Equipo {
	
private String nombre;
private int cupoMaximo;
private List<InscripcionEquipo> inscripciones;
private DeporteConjunto deporte;
private Categoria categoria;

public Equipo(String nombre, int cupoMaximo, List<InscripcionEquipo> inscripciones, DeporteConjunto deporte,
		Categoria categoria) {
	super();
	this.nombre = nombre;
	this.cupoMaximo = cupoMaximo;
	this.inscripciones = inscripciones;
	this.deporte = deporte;
	this.categoria = categoria;
}

public boolean agregarJugador(Socio jugador, String posicion, int numeroCamiseta) {
	if (inscripciones.size() >= cupoMaximo) {
		return false;
	}
	if (categoria.getEdadMinima() > jugador.getEdad() || jugador.getEdad() > categoria.getEdadMaxima()) {
		return false;
	}
	if (jugador.tieneEquipoEn(deporte)) {
		return false;	
	}
	
	if (camisetaOcupada(numeroCamiseta)) {
		return false;
	}
	
	InscripcionEquipo inscripcion = new InscripcionEquipo(jugador, this, posicion, numeroCamiseta);
	inscripciones.add(inscripcion);
	jugador.agregarInscripcionEquipo(inscripcion);
	return true;
	
}

public boolean camisetaOcupada(int numeroCamiseta) {
	for (InscripcionEquipo inscripcion : inscripciones) {
		if (inscripcion.getNumeroCamiseta() == numeroCamiseta) {
			return true;
		}
	}
	return false;
}

public String getNombre() {
    return nombre;
}

public int getCupoMaximo() {
    return cupoMaximo;
}

public DeporteConjunto getDeporte() {
    return deporte;
}

public Categoria getCategoria() {
    return categoria;
}

public List<InscripcionEquipo> getInscripciones() {
    return inscripciones;
}
	
}

