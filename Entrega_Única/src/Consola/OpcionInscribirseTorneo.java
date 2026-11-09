package Consola;

import java.util.ArrayList;
import java.util.List;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;

public class OpcionInscribirseTorneo extends OpcionTorneo {

	private final Socio jugador;

	public OpcionInscribirseTorneo(ClubSeneca club, Socio jugador, LectorConsola lector) {
		super(club, lector);
		this.jugador = jugador;
	}

	@Override
	public String getTitulo() {
		return "Inscribirme en un torneo individual";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<Torneo> candidatos = new ArrayList<>();
		for (Torneo torneo : torneosEn(EstadoTorneo.INSCRIPCIONES_ABIERTAS)) {
			if (torneo instanceof TorneoIndividual) {
				candidatos.add(torneo);
			}
		}
		Torneo elegido = elegirTorneo(candidatos, "No hay torneos individuales con inscripciones abiertas.");
		if (elegido == null) {
			return;
		}
		TorneoIndividual torneo = (TorneoIndividual) elegido;

		Socio companero = null;
		if (torneo.esDobles()) {
			String login = lector.leerTexto("Usuario (login) de su compañero de dobles");
			companero = club.buscarSocio(login);
			if (companero == null) {
				throw new ReglaNegocioException("No existe un socio con el usuario " + login);
			}
			if (companero.getLogin().equals(jugador.getLogin())) {
				throw new ReglaNegocioException("No puede ser su propio compañero");
			}
		}

		InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(jugador, companero);
		club.inscribirEnTorneo(torneo, inscripcion);

		System.out.println("Inscripción registrada en \"" + torneo.getNombre() + "\": " + inscripcion.getNombre() + ".");
		if (inscripcion.esPreferencial()) {
			System.out.println("Ocupó un cupo preferencial por su ranking.");
		}
		if (torneo.esCompetitivo()) {
			System.out.println("Valor de la inscripción: " + FormatoTorneo.dinero(inscripcion.getMontoPagado())
					+ " (el pago se gestiona por un medio externo).");
		}
	}
}
