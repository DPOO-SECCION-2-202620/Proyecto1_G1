package DeportesIndividuales;

import org.json.JSONObject;

public class TenisDeMesa extends DisciplinaIndividual {
	
	public TenisDeMesa(int maxInstalacionesSimultaneas, String nombre) {
		super(maxInstalacionesSimultaneas, "TenisDeMesa");
	}
	
	public static TenisDeMesa fromJSON(JSONObject json) {
		TenisDeMesa tenisDeMesa = new TenisDeMesa(
				json.getInt("maxInstalacionesSimultaneas"),
				json.getString("TenisDeMesa"));
		tenisDeMesa.restaurarEstadoDisciplinaIndividual(json, tenisDeMesa);
		return tenisDeMesa;
	}
}
