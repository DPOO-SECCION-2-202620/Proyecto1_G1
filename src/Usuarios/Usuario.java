package Usuarios;

import java.util.Map;

import org.json.JSONObject;

import DeportesIndividuales.DisciplinaIndividual;

public abstract class Usuario {
	
	private String nombre;
	
	private String login;
	
	private String password;
	
	public Usuario(String nombre, String login, String password) {
	    this.nombre = nombre;
	    this.login = login;
	    this.password = password;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean verificarPassword (String password) {
		return this.password.equals(password);
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("login", login);
	    json.put("password", password);
		return json;
	}

	public static Usuario fromJSON(JSONObject json, Map<String, DisciplinaIndividual> mapaDisciplinas) {
		String tipo = json.getString("tipo");

		switch (tipo) {
			case "Socio":
				return Socio.fromJSON(json, mapaDisciplinas);
			case "Entrenador":
				return Entrenador.fromJSON(json, mapaDisciplinas);
			case "Fisioterapeuta":
				return Fisioterapeuta.fromJSON(json, mapaDisciplinas);
			case "Administrador":
				return Administrador.fromJSON(json);
			default:
				throw new IllegalArgumentException("Tipo de usuario desconocido: " + tipo);
		}
	}
	
}
