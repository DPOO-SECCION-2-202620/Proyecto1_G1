package DeportesIndividuales;

import org.json.JSONObject;

import Usuarios.Socio;

public class EstadisticaTenisDeMesa extends EstadisticaPractica{
	
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
		JSONObject json = new JSONObject();
	    json.put("puntosTotales", puntosTotales);
		return json;
	}
	
}
