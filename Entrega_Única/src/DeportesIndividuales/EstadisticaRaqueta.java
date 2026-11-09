package DeportesIndividuales;

import java.util.Map;

import org.json.JSONObject;

import Usuarios.Socio;
import Usuarios.Usuario;

public class EstadisticaRaqueta extends EstadisticaPractica {

	private int gamesGanados;
	private int aces;
	private int erroresNoForzados;
	
	public EstadisticaRaqueta(int setsGanados, Socio jugador, int gamesGanados, int aces, int erroresNoForzados) {
		super(setsGanados, jugador);
		this.gamesGanados = gamesGanados;
		this.aces = aces;
		this.erroresNoForzados = erroresNoForzados;
	}

	public int getGamesGanados() {
		return gamesGanados;
	}

	public int getAces() {
		return aces;
	}

	public int getErroresNoForzados() {
		return erroresNoForzados;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "EstadisticaRaqueta");
		json.put("gamesGanados", gamesGanados);
		json.put("aces", aces);
		json.put("erroresNoForzados", erroresNoForzados);
		return json;
	}

	public static EstadisticaRaqueta fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios) {
	    if (json == null) {
	        return null;
	    }

	    int setsGanados = json.getInt("setsGanados");
	    String loginSocio = json.getString("jugador");
	    
	    Socio jugador = (Socio) mapaUsuarios.get(loginSocio);

	    int gamesGanados = json.getInt("gamesGanados");
	    int aces = json.getInt("aces");
	    int erroresNoForzados = json.getInt("erroresNoForzados");

	    return new EstadisticaRaqueta(setsGanados, jugador, gamesGanados, aces, erroresNoForzados);
	}
}