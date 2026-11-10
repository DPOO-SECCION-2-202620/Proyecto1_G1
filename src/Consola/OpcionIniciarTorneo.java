package Consola;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.Torneo;

public class OpcionIniciarTorneo extends OpcionTorneo {

	public OpcionIniciarTorneo(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Iniciar un torneo (cierra inscripciones)";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		Torneo torneo = elegirTorneo(torneosEn(EstadoTorneo.INSCRIPCIONES_ABIERTAS),
				"No hay torneos con inscripciones abiertas.");
		if (torneo == null) {
			return;
		}
		System.out.println("Inscritos: " + torneo.getInscripciones().size() + " de " + torneo.getCupos());
		if (!lector.confirmar("¿Iniciar \"" + torneo.getNombre() + "\"? Nadie podrá inscribirse ni retirarse")) {
			System.out.println("Operación cancelada.");
			return;
		}
		club.iniciarTorneo(torneo);
		System.out.println("Torneo iniciado. Encuentros de la primera ronda:");
		for (Encuentro encuentro : torneo.getEncuentrosDeRonda(torneo.getRondaActual())) {
			System.out.println("  " + encuentro);
		}
	}
}
