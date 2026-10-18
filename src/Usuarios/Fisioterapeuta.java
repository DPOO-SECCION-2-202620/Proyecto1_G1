package Usuarios;

import java.util.Map;

import org.json.JSONObject;

import DeportesIndividuales.DisciplinaIndividual;

public class Fisioterapeuta extends Empleado{

	public Fisioterapeuta(int edad, String nombre, String login, String password, String codigoEmpleado) {
		super(edad, nombre, login, password, codigoEmpleado);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "Fisioterapeuta");
		return json;
	}

	public static Fisioterapeuta fromJSON(JSONObject json, Map<String, DisciplinaIndividual> mapaDisciplinas) {
		Fisioterapeuta fisioterapeuta = new Fisioterapeuta(
				json.getInt("edad"),
				json.getString("codigoEmpleado"),
				json.getString("nombre"),
				json.getString("login"),
				json.getString("password"));
		fisioterapeuta.restaurarEstadoSocio(json, mapaDisciplinas);
		fisioterapeuta.restaurarEstadoEmpleado(json);
		return fisioterapeuta;
	}
}
