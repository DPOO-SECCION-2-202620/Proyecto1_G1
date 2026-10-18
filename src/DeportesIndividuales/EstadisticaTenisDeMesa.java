package DeportesIndividuales;

import java.util.Map;

import org.json.JSONObject;

import Usuarios.Socio;
import Usuarios.Usuario;

public class EstadisticaTenisDeMesa extends EstadisticaPractica {
	
	private int puntosTotales;

	public EstadisticaTenisDeMesa(int setsGanados, Socio jugador, int puntosTotales) {
		super(setsGanados, jugador);
		this.puntosTotales = puntosTotales;
	}

	public int getPuntosTotales() {
		return puntosTotales;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "EstadisticaTenisDeMesa");
		json.put("puntosTotales", puntosTotales);
		return json;
	}

	public static EstadisticaTenisDeMesa fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios) {
	    if (json == null) {
	        return null;
	    }

	    int setsGanados = json.getInt("setsGanados");
	    String loginSocio = json.getString("jugador");
	    
	    Socio jugador = (Socio) mapaUsuarios.get(loginSocio);

	    int puntosTotales = json.getInt("puntosTotales");

	    return new EstadisticaTenisDeMesa(setsGanados, jugador, puntosTotales);
	}
}