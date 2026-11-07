package TorneosInternos;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;

public class InscripcionTorneoEquipo extends InscripcionTorneo {

	private Equipo equipo;
	public InscripcionTorneoEquipo(Equipo equipo) {
		super();
		this.equipo = equipo;
	}

	public Equipo getEquipo() {
		return equipo;
	}
	@Override
	public JSONObject toJSON() {
	    JSONObject json = super.toJSON();
	    json.put("tipo", "EQUIPO");
	    json.put("equipo", equipo.getNombre());
	    return json;
	}

	@Override
	public String getNombre() {
		return equipo.getNombre();
	}

	@Override
	public List<JugadorTorneo> getJugadores() {
		List<JugadorTorneo> jugadores = new ArrayList<>();
		for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
			jugadores.add(inscripcion.getJugador());
		}
		return jugadores;
	}
}
