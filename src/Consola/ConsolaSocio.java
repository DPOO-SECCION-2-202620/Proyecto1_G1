package Consola;

import java.util.ArrayList;
import java.util.List;

import NucleoSistema.ClubSeneca;
import Usuarios.Empleado;
import Usuarios.ParticipanteExterno;
import Usuarios.Socio;
import Usuarios.Usuario;

public class ConsolaSocio extends ConsolaBase {

	public ConsolaSocio(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	protected boolean aceptaUsuario(Usuario usuario) {
		return (usuario instanceof Socio && !(usuario instanceof Empleado)) || usuario instanceof ParticipanteExterno;
	}

	@Override
	protected String getNombreConsola() {
		return "Socios y jugadores externos";
	}

	@Override
	protected List<OpcionMenu> crearOpciones(Usuario usuario) {
		List<OpcionMenu> opciones = new ArrayList<>();
		if (usuario instanceof ParticipanteExterno) {
			ParticipanteExterno externo = (ParticipanteExterno) usuario;
			opciones.add(new OpcionInscribirseTorneo(club, externo, lector));
			opciones.add(new OpcionRetirarseTorneo(club, externo, lector));
			opciones.add(new OpcionConsultarTorneos(club, lector));
			opciones.add(new OpcionVerBonos(externo, lector));
			return opciones;
		}
		Socio socio = (Socio) usuario;
		opciones.add(new OpcionInscribirseEnEquipo(club, socio, lector));
		opciones.add(new OpcionInscribirseTorneo(club, socio, lector));
		opciones.add(new OpcionRetirarseTorneo(club, socio, lector));
		opciones.add(new OpcionConsultarTorneos(club, lector));
		opciones.add(new OpcionVerBonos(socio, lector));
		opciones.add(new OpcionComprarConBono(club, socio, lector));
		return opciones;
	}

	public static void main(String[] args) {
		ClubSeneca club = cargarClub();
		new ConsolaSocio(club, new LectorConsola()).iniciar();
	}
}