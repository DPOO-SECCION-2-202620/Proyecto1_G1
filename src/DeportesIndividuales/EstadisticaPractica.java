package DeportesIndividuales;

import org.json.JSONObject;

import Usuarios.Socio;

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
}
