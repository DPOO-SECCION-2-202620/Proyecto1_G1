package Instalaciones;

import org.json.JSONObject;

import DeportesIndividuales.Tenis;

public class CanchaTenis extends InstalacionIndividual{

	public CanchaTenis(String nombre, int capacidadMaxima, boolean techado, int salon,
			Tenis tenis) {
		super(nombre, 4, techado, salon, tenis);
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return true;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("tipo", "CanchaTenis");
		return json;
	}
	
}
