package TorneosInternos;

import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import DeportesConjunto_Competencias.Equipo;
import Usuarios.Usuario;

public abstract class InscripcionTorneo {

	private boolean preferencial;
	private double montoPagado;

	public InscripcionTorneo() {
		this.preferencial = false;
		this.montoPagado = 0;
	}
		
		public abstract String getNombre();
		public abstract List<JugadorTorneo> getJugadores();
		public boolean incluye(JugadorTorneo jugador) {
			for (JugadorTorneo j : getJugadores()) {
				if (j.getLogin().equals(jugador.getLogin())) {
					return true;
				}
			}
			return false;
		}

		public boolean esPreferencial() {
			return preferencial;
		}

		public double getMontoPagado() {
			return montoPagado;
		}

		public void setPreferencial(boolean preferencial) {
			this.preferencial = preferencial;
		}

		public void setMontoPagado(double montoPagado) {
			this.montoPagado = montoPagado;
		}
		
		public JSONObject toJSON() {
		    JSONObject json = new JSONObject();
		    json.put("preferencial", preferencial);
		    json.put("montoPagado", montoPagado);
		    return json;
		}

		static JugadorTorneo jugadorPorLogin(String login, Map<String, Usuario> mapaUsuarios) {
		    Usuario usuario = mapaUsuarios.get(login);
		    if (!(usuario instanceof JugadorTorneo)) {
		        throw new IllegalStateException("Jugador inexistente en el torneo: " + login);
		    }
		    return (JugadorTorneo) usuario;
		}

		public static InscripcionTorneo fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios,
		        Map<String, Equipo> mapaEquipos) {
		    InscripcionTorneo inscripcion;
		    String tipo = json.getString("tipo");
		    if (tipo.equals("EQUIPO")) {
		        Equipo equipo = mapaEquipos.get(json.getString("equipo"));
		        if (equipo == null) {
		            throw new IllegalStateException("Equipo inexistente en el torneo: " + json.getString("equipo"));
		        }
		        inscripcion = new InscripcionTorneoEquipo(equipo);
		    } else if (tipo.equals("INDIVIDUAL")) {
		        JugadorTorneo jugador = jugadorPorLogin(json.getString("jugador"), mapaUsuarios);
		        JugadorTorneo companero = null;
		        if (json.has("companero")) {
		            companero = jugadorPorLogin(json.getString("companero"), mapaUsuarios);
		        }
		        inscripcion = new InscripcionTorneoIndividual(jugador, companero);
		    } else {
		        throw new IllegalArgumentException("Tipo de inscripción desconocido: " + tipo);
		    }
		    inscripcion.setPreferencial(json.getBoolean("preferencial"));
		    inscripcion.setMontoPagado(json.getDouble("montoPagado"));
		    return inscripcion;
		}

		@Override
		public String toString() {
			return getNombre();
		
	    }
	
}
