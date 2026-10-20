package NucleoSistema;

import java.util.List;

import Usuarios.Usuario;

public class Autenticador {

	public Usuario autenticar(String login, List<Usuario> Usuarios, String password) {
		if (login == null || Usuarios == null || password == null) {
			return null;
		}
		
		Usuario usuarioAutenticado = null;
		for (Usuario usuario : Usuarios) {
			if (usuario.getLogin().equals(login)) {
				usuarioAutenticado = usuario;
			}
		}
		if (usuarioAutenticado != null && usuarioAutenticado.getPassword().equals(password)) {
			return usuarioAutenticado;
		}
		return null;
	}
	
}