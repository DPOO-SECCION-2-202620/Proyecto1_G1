package TorneosInternos;

import Excepciones.ReglaNegocioException;
import java.util.List;
import org.json.JSONObject;

public class Encuentro {

	private int ronda;
	private InscripcionTorneo participanteA;
	private InscripcionTorneo participanteB;
	private InscripcionTorneo ganador;

	public Encuentro(int ronda, InscripcionTorneo participanteA, InscripcionTorneo participanteB) {
		this.ronda = ronda;
		this.participanteA = participanteA;
		this.participanteB = participanteB;
		this.ganador = null;
	}

	public void registrarGanador(InscripcionTorneo ganador) throws ReglaNegocioException {
		if (this.ganador != null) {
			throw new ReglaNegocioException("El encuentro ya se jugó");
		}
		if (ganador != participanteA && ganador != participanteB) {
			throw new ReglaNegocioException("El ganador debe ser uno de los dos participantes del encuentro");
		}
		this.ganador = ganador;
	}

	public void cambiarGanador(InscripcionTorneo nuevoGanador) throws ReglaNegocioException {
		if (nuevoGanador != participanteA && nuevoGanador != participanteB) {
	        throw new ReglaNegocioException("El ganador debe ser uno de los dos participantes del encuentro");
	    }
	    ganador = nuevoGanador;
	}

	public void reemplazarParticipante(InscripcionTorneo anterior, InscripcionTorneo nuevo) {
		if (participanteA == anterior) {
			participanteA = nuevo;
		}
		else if (participanteB == anterior) {
			participanteB = nuevo;
		}
	}
	

	public boolean tieneGanador() {
		return ganador != null;
	}

	public InscripcionTorneo getPerdedor() {
		if (ganador == null) {
			return null;
		}
		return ganador == participanteA ? participanteB : participanteA;
	}

	public boolean incluye(InscripcionTorneo inscripcion) {
		return participanteA == inscripcion || participanteB == inscripcion;
	}

	public int getRonda() {
		return ronda;
	}

	public InscripcionTorneo getParticipanteA() {
		return participanteA;
	}

	public InscripcionTorneo getParticipanteB() {
		return participanteB;
	}

	public InscripcionTorneo getGanador() {
		return ganador;
	}
	
	public JSONObject toJSON(List<InscripcionTorneo> inscripciones) {
	    JSONObject json = new JSONObject();
	    json.put("ronda", ronda);
	    json.put("participanteA", inscripciones.indexOf(participanteA));
	    json.put("participanteB", inscripciones.indexOf(participanteB));
	    if (ganador != null) {
	        json.put("ganador", inscripciones.indexOf(ganador));
	    }
	    return json;
	}

	public static Encuentro fromJSON(JSONObject json, List<InscripcionTorneo> inscripciones)
	        throws ReglaNegocioException {
	    Encuentro encuentro = new Encuentro(json.getInt("ronda"),
	            inscripciones.get(json.getInt("participanteA")),
	            inscripciones.get(json.getInt("participanteB")));
	    if (json.has("ganador")) {
	        encuentro.registrarGanador(inscripciones.get(json.getInt("ganador")));
	    }
	    return encuentro;
	}

	@Override
	public String toString() {
		String r = "Ronda " + ronda + ": " + participanteA.getNombre() + " vs " + participanteB.getNombre();
		return tieneGanador() ? r + " -> ganó " + ganador.getNombre() : r + " (pendiente)";
	}
}
