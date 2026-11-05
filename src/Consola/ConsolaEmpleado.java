package Consola;

import java.util.ArrayList;
import java.util.List;

import NucleoSistema.ClubSeneca;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Usuario;

public class ConsolaEmpleado extends ConsolaBase {

	public ConsolaEmpleado(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	protected boolean aceptaUsuario(Usuario usuario) {
		return usuario instanceof Empleado;
	}

	@Override
	protected String getNombreConsola() {
		return "Empleado";
	}

	@Override
	protected List<OpcionMenu> crearOpciones(Usuario usuario) {
		List<OpcionMenu> opciones = new ArrayList<>();
		if (usuario instanceof Entrenador) {
			opciones.add(new OpcionVerificarMinimoJugadores(club, lector));
		}
		return opciones;
	}

	public static void main(String[] args) {
		ClubSeneca club = cargarClub();
		new ConsolaEmpleado(club, new LectorConsola()).iniciar();
	}
}