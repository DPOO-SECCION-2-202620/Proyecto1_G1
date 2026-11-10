package Consola;

import java.util.ArrayList;
import java.util.List;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.JugadorTorneo;
import TorneosInternos.Torneo;

public class OpcionRetirarseTorneo extends OpcionTorneo {

	private final JugadorTorneo jugador;

	public OpcionRetirarseTorneo(ClubSeneca club, JugadorTorneo jugador, LectorConsola lector) {
		super(club, lector);
		this.jugador = jugador;
	}

	@Override
	public String getTitulo() {
		return "Retirarme de un torneo";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<Torneo> inscritos = new ArrayList<>();
		for (Torneo torneo : torneosEn(EstadoTorneo.INSCRIPCIONES_ABIERTAS)) {
			if (torneo.buscarInscripcion(jugador) != null) {
				inscritos.add(torneo);
			}
		}
		Torneo torneo = elegirTorneo(inscritos, "No está inscrito en ningún torneo que aún no haya iniciado.");
		if (torneo == null) {
			return;
		}
		if (!lector.confirmar("¿Retirarse de \"" + torneo.getNombre() + "\"? (en dobles se retira toda la pareja)")) {
			System.out.println("Operación cancelada.");
			return;
		}
		club.retirarJugadorDeTorneo(torneo, jugador);
		System.out.println("Se retiró de \"" + torneo.getNombre() + "\". Los cupos quedaron liberados.");
	}
}
