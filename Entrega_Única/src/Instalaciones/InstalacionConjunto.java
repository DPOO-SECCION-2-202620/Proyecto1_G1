package Instalaciones;

import org.json.JSONObject;

public abstract class InstalacionConjunto extends Instalacion{

	public InstalacionConjunto(String nombre, int capacidadMaxima) {
		super(nombre, capacidadMaxima);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();  
		return json;
	}
	
	public static Instalacion fromJSON(JSONObject json) {
		String tipo = json.getString("tipo");

		switch (tipo) {
			case "CanchaFutbol":
				return CanchaFutbol.fromJSON(json);
			case "CanchaBaloncesto":
				return CanchaBaloncesto.fromJSON(json);
			case "CanchaVoleibol":
				return CanchaVoleibol.fromJSON(json);
			default:
				throw new IllegalArgumentException("Tipo de usuario desconocido: " + tipo);
		}
	}
}
