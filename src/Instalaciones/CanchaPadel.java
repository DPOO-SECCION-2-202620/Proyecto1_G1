package Instalaciones;

import org.json.JSONObject;

import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Padel;

public class CanchaPadel extends InstalacionIndividual{
	
	public CanchaPadel(String nombre, boolean techado, int salon, Padel padel) {
		super(nombre, 4, techado, salon, padel);
	}

	@Override
	public boolean admiteModalidad(Modalidad modalidad, int numeroJugadores) {
		if (modalidad == Modalidad.DOBLES && numeroJugadores == 4) {
			return true;
		}
		return false;
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return false;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "CanchaPadel");
		return json;
	}
	
	public static CanchaPadel fromJSON(JSONObject json, Padel padel) {
		CanchaPadel cancha = new CanchaPadel(
				json.getString("nombre"),
				json.getBoolean("techado"),
				json.getInt("salon"),
				padel);
		return cancha;
	}
}
