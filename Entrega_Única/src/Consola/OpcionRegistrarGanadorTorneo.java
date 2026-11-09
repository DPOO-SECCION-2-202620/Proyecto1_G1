package Consola;

import java.util.ArrayList;
import java.util.List;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.Torneo;

public class OpcionRegistrarGanadorTorneo extends OpcionTorneo {

	public OpcionRegistrarGanadorTorneo(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Registrar el ganador de un encuentro";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		Torneo torneo = elegirTorneo(torneosEn(EstadoTorneo.EN_CURSO), "No hay torneos en curso.");
		if (torneo == null) {
			return;
		}
		List<Encuentro> pendientes = new ArrayList<>();
		for (Encuentro encuentro : torneo.getEncuentrosDeRonda(torneo.getRondaActual())) {
			if (!encuentro.tieneGanador()) {
				pendientes.add(encuentro);
			}
		}
		if (pendientes.isEmpty()) {
			System.out.println("No hay encuentros pendientes en este torneo.");
			return;
		}
		System.out.println("\nEncuentros pendientes:");
		for (int i = 0; i < pendientes.size(); i++) {
			System.out.println((i + 1) + ". " + pendientes.get(i));
		}
		Encuentro encuentro = pendientes.get(lector.leerIndice("\nElija el encuentro", pendientes.size()));

		System.out.println("1. " + encuentro.getParticipanteA().getNombre());
		System.out.println("2. " + encuentro.getParticipanteB().getNombre());
		int eleccion = lector.leerEntero("¿Quién ganó?", 1, 2);
		InscripcionTorneo ganador = eleccion == 1 ? encuentro.getParticipanteA() : encuentro.getParticipanteB();

		int rondaAntes = torneo.getRondaActual();
		club.registrarGanadorTorneo(torneo, encuentro, ganador);
		System.out.println("Ganador registrado: " + ganador.getNombre());

		if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
			System.out.println("\nEl torneo terminó. Campeón: " + ganador.getNombre());
			FormatoTorneo.imprimirPremios(torneo);
		} else if (torneo.getRondaActual() > rondaAntes) {
			System.out.println("Ronda completa. Encuentros de la ronda " + torneo.getRondaActual() + ":");
			for (Encuentro siguiente : torneo.getEncuentrosDeRonda(torneo.getRondaActual())) {
				System.out.println("  " + siguiente);
			}
		}
	}
}
