package Consola;

import java.util.ArrayList;
import java.util.List;

import NucleoSistema.ClubSeneca;
import Usuarios.Administrador;
import Usuarios.Usuario;

public class ConsolaAdministrador extends ConsolaBase {

	public ConsolaAdministrador(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	protected boolean aceptaUsuario(Usuario usuario) {
		return usuario instanceof Administrador;
	}

	@Override
	protected String getNombreConsola() {
		return "Administrador";
	}

	@Override
	protected List<OpcionMenu> crearOpciones(Usuario usuario) {
		Administrador administrador = (Administrador) usuario;
		List<OpcionMenu> opciones = new ArrayList<>();
		opciones.add(new OpcionRevisarSolicitudesTurno(club, administrador, lector));
		opciones.add(new OpcionRegistrarParticipanteExterno(club, lector));
		opciones.add(new OpcionCrearTorneo(club, lector));
		opciones.add(new OpcionIniciarTorneo(club, lector));
		opciones.add(new OpcionRegistrarGanadorTorneo(club, lector));
		opciones.add(new OpcionCorregirGanadorTorneo(club, lector));
		opciones.add(new OpcionConsultarTorneos(club, lector));
		return opciones;
	}

	public static void main(String[] args) {
		ClubSeneca club = cargarClub();
		new ConsolaAdministrador(club, new LectorConsola()).iniciar();
	}
}