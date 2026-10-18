package NucleoSistema;

import java.util.Map;
import Usuarios.Usuario;

public class Autenticador {

	public Usuario autenticar(String login, Map<String, Usuario> mapaUsuarios, String password) {
		if (login == null || mapaUsuarios == null || password == null) {
			return null;
		}

		Usuario usuario = mapaUsuarios.get(login);

		if (usuario != null && usuario.verificarPassword(password)) {
			return usuario;
		}

		return null;
	}
	
}