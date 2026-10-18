package DeportesConjunto_Competencias;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Usuarios.Socio;

public class InscripcionCompetencia {

	private Equipo equipo;
	private Competencia competencia;
	private List<RegistroNomina> nomina;
	public InscripcionCompetencia(Equipo equipo, Competencia competencia) {
		this.equipo = equipo;
		this.competencia = competencia;
		this.nomina = new ArrayList<>();
	}
	public Equipo getEquipo() {
		return equipo;
	}
	public Competencia getCompetencia() {
		return competencia;
	}
	public List<RegistroNomina> getNomina() {
		return nomina;
	}
	
	public RegistroNomina getRegistroDe(Socio jugador) {
		for (RegistroNomina registro : nomina) {
			if (registro.getInscripcionEquipo().getJugador() == jugador) {
				return registro;
			}
		}
		return null;
	}
	
	public boolean agregarANomina(Socio jugador) {
		InscripcionEquipo inscripcionEquipo = equipo.getInscripcionDe(jugador);
	    if (inscripcionEquipo == null) {
	        return false;
	    }
	    if (getRegistroDe(jugador) != null) {
	        return false;
	    }
	    nomina.add(new RegistroNomina(inscripcionEquipo));
	    return true;
	}
	
	public boolean puedeAlinear(Socio jugador) {
	    RegistroNomina registro = getRegistroDe(jugador);
	    if (registro == null) {
	        return false;
	    }
	    return registro.estaHabilitado();
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("equipo", equipo.getNombre());
	    json.put("competencia", competencia.getNombre());
	    JSONArray jsonNomina = new JSONArray();
	    for (RegistroNomina nomina : nomina) {
	    	jsonNomina.put(nomina.toJSON());
	    }
	    json.put("nomina", jsonNomina);
		return json;
	}
	
}
