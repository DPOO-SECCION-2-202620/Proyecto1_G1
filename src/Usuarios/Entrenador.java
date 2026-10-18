package Usuarios;

import java.util.Map;

import org.json.JSONObject;

import DeportesIndividuales.DisciplinaIndividual;

public class Entrenador extends Empleado{

	public Entrenador(int edad, String nombre, String login, String password, String codigoEmpleado) {
		super(edad, nombre, login, password, codigoEmpleado);
	}
	
	@Override
	public boolean esEntrenador() {
		return true;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "Entrenador");      
		return json;
	}

	public static Entrenador fromJSON(JSONObject json, Map<String, DisciplinaIndividual> mapaDisciplinas) {
		Entrenador entrenador = new Entrenador(
				json.getInt("edad"),
				json.getString("codigoEmpleado"),
				json.getString("nombre"),
				json.getString("login"),
				json.getString("password"));
		entrenador.restaurarEstadoSocio(json, mapaDisciplinas);
		entrenador.restaurarEstadoEmpleado(json);
		return entrenador;
	}
}
