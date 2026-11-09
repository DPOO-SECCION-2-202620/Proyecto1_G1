package TorneosInternos;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class InscripcionTorneoIndividual extends InscripcionTorneo{

	private JugadorTorneo jugador;
	private JugadorTorneo companero; 

	public InscripcionTorneoIndividual(JugadorTorneo jugador, JugadorTorneo companero) {
		super();
		this.jugador = jugador;
		this.companero = companero;
	}

	public JugadorTorneo getJugador() {
		return jugador;
	}

	public JugadorTorneo getCompanero() {
		return companero;
	}

	public boolean esPareja() {
		return companero != null;
	}
	
	@Override
	public JSONObject toJSON() {
	    JSONObject json = super.toJSON();
	    json.put("tipo", "INDIVIDUAL");
	    json.put("jugador", jugador.getLogin());
	    if (companero != null) {
	        json.put("companero", companero.getLogin());
	    }
	    return json;
	}

	@Override
	public String getNombre() {
		return esPareja() ? jugador.getNombre() + " / " + companero.getNombre() : jugador.getNombre();
	}

	@Override
	public List<JugadorTorneo> getJugadores() {
		List<JugadorTorneo> jugadores = new ArrayList<>();
		jugadores.add(jugador);
		if (companero != null) {
			jugadores.add(companero);
		}
		return jugadores;
	}
}
