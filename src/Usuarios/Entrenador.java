package Usuarios;

import org.json.JSONObject;

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
}
