package Usuarios;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.NivelJugador;

public class Socio extends Usuario{
private int edad;
private int puntosFidelidad;
public static final int MINUTOS_BEBIDA_CALIENTE = 30;
private LocalDateTime horaUltimaBebidaCaliente;
private List<InscripcionEquipo> inscripcionesEquipos;
private List<InscripcionIndividual> inscripcionesIndividuales;


public Socio(int edad, String nombre, String login, String password) {
	super(nombre, login, password);
	this.edad = edad;
	this.puntosFidelidad = 0;
	this.horaUltimaBebidaCaliente = null;
	this.inscripcionesEquipos = new ArrayList<>();
	this.inscripcionesIndividuales = new ArrayList<>();
}

public boolean tieneEquipoEn(DeporteConjunto deporte) {
	for (InscripcionEquipo inscripcion : inscripcionesEquipos) {
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
    inscripcionesEquipos.add(inscripcion);
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
	return inscripcionesEquipos;
}

public List<InscripcionIndividual> getInscripcionesIndividuales() {
	return inscripcionesIndividuales;
}

@Override
public JSONObject toJSON() {
	JSONObject json = super.toJSON();
	json.put("tipo", "Socio");
	json.put("edad", edad);
	json.put("puntosFidelidad", puntosFidelidad);
	if (horaUltimaBebidaCaliente != null) {     
		json.put("horaUltimaBebidaCaliente", horaUltimaBebidaCaliente.toString());
	}

	JSONArray jsonInscripcionesIndividuales = new JSONArray();
	for (InscripcionIndividual inscripcion : inscripcionesIndividuales) {
		jsonInscripcionesIndividuales.put(inscripcion.toJSON());
	}
	json.put("inscripcionesIndividuales", jsonInscripcionesIndividuales);
	return json;
}

public static Socio fromJSON(JSONObject usuarioJson, Map<String, DisciplinaIndividual> mapaDisciplinas) {
	Socio socio = new Socio(
			usuarioJson.getInt("edad"),
			usuarioJson.getString("nombre"),
			usuarioJson.getString("login"),
			usuarioJson.getString("password"));
	socio.restaurarEstadoSocio(usuarioJson, mapaDisciplinas);
	return socio;
}

protected void restaurarEstadoSocio(JSONObject json, Map<String, DisciplinaIndividual> mapaDisciplinas) {
	this.puntosFidelidad = json.getInt("puntosFidelidad");

	if (json.has("horaUltimaBebidaCaliente")) {
		this.horaUltimaBebidaCaliente = LocalDateTime.parse(json.getString("horaUltimaBebidaCaliente"));
	}

	JSONArray jsonInscripciones = json.getJSONArray("inscripcionesIndividuales");
	for (int indice = 0; indice < jsonInscripciones.length(); indice++) {
		JSONObject jsonInscripcion = jsonInscripciones.getJSONObject(indice);
		DisciplinaIndividual disciplina = mapaDisciplinas.get(jsonInscripcion.getString("disciplina"));
		if (disciplina == null) {
			throw new IllegalStateException("Disciplina inexistente: " + jsonInscripcion.getString("disciplina"));
		}
		NivelJugador nivel = NivelJugador.valueOf(jsonInscripcion.getString("nivel"));
		this.inscripcionesIndividuales.add(new InscripcionIndividual(disciplina, nivel));
	}
}

}
