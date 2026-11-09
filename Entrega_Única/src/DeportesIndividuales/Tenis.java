package DeportesIndividuales;

import org.json.JSONObject;

public class Tenis extends DisciplinaIndividual {

	public Tenis(int maxInstalacionesSimultaneas, String nombre) {
		super(maxInstalacionesSimultaneas, "Tenis");
	
	}
	
	public static Tenis fromJSON(JSONObject json) {
		Tenis tenis = new Tenis(
				json.getInt("maxInstalacionesSimultaneas"),
				json.getString("nombre"));
		tenis.restaurarEstadoDisciplinaIndividual(json, tenis);
		return tenis;
	}
}