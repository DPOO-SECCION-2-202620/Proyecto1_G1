package Instalaciones;

import org.json.JSONObject;

import DeportesIndividuales.TenisDeMesa;

public class MesaTenisDeMesa extends InstalacionIndividual{

	public MesaTenisDeMesa(String nombre, boolean techado, int salon, TenisDeMesa tenisDeMesa) {
		super(nombre, 4, techado, salon, tenisDeMesa);
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return !isTechado();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "MesaTenisDeMesa");
		return json;
	}
	
	public static MesaTenisDeMesa fromJSON(JSONObject json, TenisDeMesa TenisDeMesa) {
		MesaTenisDeMesa cancha = new MesaTenisDeMesa(
				json.getString("nombre"),
				json.getBoolean("techado"),
				json.getInt("salon"),
				TenisDeMesa);
		return cancha;
	}
}
