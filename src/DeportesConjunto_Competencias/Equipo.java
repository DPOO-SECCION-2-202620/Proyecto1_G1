package DeportesConjunto_Competencias;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import Usuarios.Socio;
import Usuarios.Usuario;

public class Equipo {
	
private String nombre;
private int cupoMaximo;
private List<InscripcionEquipo> inscripciones;
private DeporteConjunto deporte;
private Categoria categoria;

public Equipo(String nombre, int cupoMaximo, DeporteConjunto deporte,
		Categoria categoria) {
	this.nombre = nombre;
	this.cupoMaximo = cupoMaximo;
	this.inscripciones = new ArrayList<>();
	this.deporte = deporte;
	this.categoria = categoria;
}

public boolean agregarJugador(Socio jugador, String posicion, int numeroCamiseta) {
	if (inscripciones.size() >= cupoMaximo) {
		return false;
	}
	if (!categoria.admiteEdad(jugador.getEdad())) {
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

public InscripcionEquipo getInscripcionDe(Socio socio) {
	for (InscripcionEquipo inscripcion : inscripciones) {
		if (inscripcion.getJugador() == socio) {
			return inscripcion;
		}
	}
	return null;
}

public boolean estaHabilitado(Socio jugador) {
	InscripcionEquipo inscripcion = getInscripcionDe(jugador);
	if (inscripcion == null) {
		return false;
	}
	return inscripcion.estaHabilitado();
}

public int contarJugadoresHabilitados() {
	int habilitados = 0;
	for(InscripcionEquipo inscripcion : inscripciones) {
		if(inscripcion.estaHabilitado()) {
			habilitados++;
		}
	}
	return habilitados;
}

public boolean puedeJugar() {
	if (contarJugadoresHabilitados() >= deporte.getMinimoJugadores()) {
		return true;
	}
	return false;
}

public List<InscripcionEquipo> getInscripcionesSuspendidas() {
	List<InscripcionEquipo> suspendidas = new ArrayList<>();
	for (InscripcionEquipo inscripcion : inscripciones) {
		if (!inscripcion.estaHabilitado()) {
			suspendidas.add(inscripcion);
		}
	}
	return suspendidas;
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

@Override
public String toString() {
	return nombre + " (" + deporte + ", " + categoria + ")";

}

public JSONObject toJSON() {
	JSONObject json = new JSONObject();
	json.put("nombre", nombre);
	json.put("cupoMaximo", cupoMaximo);
	JSONArray jsonInscripciones = new JSONArray();
	for (InscripcionEquipo inscripcion : inscripciones) {
		jsonInscripciones.put(inscripcion.toJSON());
	}
	json.put("inscripciones", jsonInscripciones);
	json.put("deporte", deporte.toJSON());
	json.put("categoria", categoria.name());
	return json;
}

public static Equipo fromJSON(JSONObject json,
		Map<String, DeporteConjunto> mapaDeportes,
		Map<String, Usuario> mapaUsuarios) {

	DeporteConjunto deporte = DeporteConjunto.fromJSON(json.getJSONObject("deporte"), mapaDeportes);

	Equipo equipo = new Equipo(
			json.getString("nombre"),
			json.getInt("cupoMaximo"),
			deporte,
			Categoria.valueOf(json.getString("categoria")));

	JSONArray jsonInscripciones = json.optJSONArray("inscripciones");
	if (jsonInscripciones != null) {
		for (int indice = 0; indice < jsonInscripciones.length(); indice++) {
			InscripcionEquipo inscripcion = InscripcionEquipo.fromJSON(jsonInscripciones.getJSONObject(indice), equipo, mapaUsuarios);
			equipo.inscripciones.add(inscripcion);
			inscripcion.getJugador().agregarInscripcionEquipo(inscripcion);
		}
	}
	return equipo;
}
}

