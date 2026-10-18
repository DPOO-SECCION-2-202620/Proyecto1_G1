package DeportesConjunto_Competencias;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		JSONArray jsonNomina = new JSONArray();
		for (RegistroNomina registro : nomina) {
			jsonNomina.put(registro.toJSON());
		}
		json.put("nomina", jsonNomina);
		return json;
	}

	public static InscripcionCompetencia fromJSON(JSONObject json,
			Competencia competencia, Map<String, Equipo> mapaEquipos) {

		String nombreEquipo = json.getString("equipo");
		Equipo equipo = mapaEquipos.get(nombreEquipo);
		if (equipo == null) {
			throw new IllegalArgumentException("Equipo desconocido en la inscripción: " + nombreEquipo);
		}

		InscripcionCompetencia inscripcion = new InscripcionCompetencia(equipo, competencia);

		JSONArray jsonNomina = json.optJSONArray("nomina");
		if (jsonNomina != null) {
			for (int indice = 0; indice < jsonNomina.length(); indice++) {
				inscripcion.nomina.add(RegistroNomina.fromJSON(jsonNomina.getJSONObject(indice), equipo));
			}
		}
		return inscripcion;
	}
	
}
