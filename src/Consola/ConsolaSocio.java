package Consola;

import java.util.ArrayList;
import java.util.List;

import NucleoSistema.ClubSeneca;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class ConsolaSocio extends ConsolaBase {

	public ConsolaSocio(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	protected boolean aceptaUsuario(Usuario usuario) {
		return usuario instanceof Socio && !(usuario instanceof Empleado);
	}

	@Override
	protected String getNombreConsola() {
		return "Socio";
	}

	@Override
	protected List<OpcionMenu> crearOpciones(Usuario usuario) {
		Socio socio = (Socio) usuario;
		List<OpcionMenu> opciones = new ArrayList<>();
		opciones.add(new OpcionInscribirseEnEquipo(club, socio, lector));
		return opciones;
	}

	public static void main(String[] args) {
		ClubSeneca club = cargarClub();
		new ConsolaSocio(club, new LectorConsola()).iniciar();
	}
}