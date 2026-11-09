package Instalaciones;

import java.util.Map;

import org.json.JSONObject;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Tenis;

public class CanchaTenis extends InstalacionIndividual{

	public CanchaTenis(String nombre, boolean techado, int salon, Tenis tenis) {
		super(nombre, 4, techado, salon, tenis);
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return true;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "CanchaTenis");
		return json;
	}
	
	public static CanchaTenis fromJSON(JSONObject json, Map<String, DisciplinaIndividual> mapaDisciplinas) {
		CanchaTenis cancha = new CanchaTenis(
				json.getString("nombre"),
				json.getBoolean("techado"),
				json.getInt("salon"),
				(Tenis) mapaDisciplinas.get("Tenis"));
		return cancha;
	}
	
	public static CanchaTenis fromJSON(JSONObject json, Tenis tenis) {
		CanchaTenis cancha = new CanchaTenis(
				json.getString("nombre"),
				json.getBoolean("techado"),
				json.getInt("salon"),
				tenis);
		return cancha;
	}
}
