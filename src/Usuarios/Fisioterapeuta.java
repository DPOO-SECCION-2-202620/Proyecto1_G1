package Usuarios;

import org.json.JSONObject;

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
}
