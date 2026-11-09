package DeportesIndividuales;

import java.util.Map;

import org.json.JSONObject;

import Usuarios.Socio;
import Usuarios.Usuario;

public abstract class EstadisticaPractica {

	private int setsGanados;
	private Socio jugador;
	
	public EstadisticaPractica(int setsGanados, Socio jugador) {
		this.setsGanados = setsGanados;
		this.jugador = jugador;
	}

	public int getSetsGanados() {
		return setsGanados;
	}

	public Socio getJugador() {
		return jugador;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("setsGanados", setsGanados);
	    json.put("jugador", jugador.getLogin());
		return json;
	}

	public static EstadisticaPractica fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios) {
		String tipo = json.getString("tipo");

		switch (tipo) {
			case "EstadisticaRaqueta":
				return EstadisticaRaqueta.fromJSON(json, mapaUsuarios);
			case "EstadisticaTenisDeMesa":
				return EstadisticaTenisDeMesa.fromJSON(json, mapaUsuarios);
			default:
				throw new IllegalArgumentException("Tipo de estadística de práctica desconocido: " + tipo);
		}
	}
}