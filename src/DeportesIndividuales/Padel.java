package DeportesIndividuales;

import org.json.JSONObject;

public class Padel extends DisciplinaIndividual {
	public Padel(int maxInstalacionesSimultaneas, String nombre) {
		super(maxInstalacionesSimultaneas, "Padel");
	}

	public static Padel fromJSON(JSONObject json) {
		Padel padel = new Padel(
				json.getInt("maxInstalacionesSimultaneas"),
				json.getString("Padel"));
		padel.restaurarEstadoDisciplinaIndividual(json, padel);
		return padel;
	}
	
}
