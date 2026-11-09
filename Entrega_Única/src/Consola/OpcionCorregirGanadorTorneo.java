package Consola;

import java.util.List;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.Torneo;

public class OpcionCorregirGanadorTorneo extends OpcionTorneo {

	public OpcionCorregirGanadorTorneo(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Corregir el ganador de un encuentro";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		Torneo torneo = elegirTorneo(torneosEn(EstadoTorneo.EN_CURSO), "No hay torneos en curso.");
		if (torneo == null) {
			return;
		}
		List<Encuentro> jugados = torneo.getEncuentrosJugados();
		if (jugados.isEmpty()) {
			System.out.println("Este torneo todavía no tiene encuentros con ganador.");
			return;
		}
		System.out.println("\nEncuentros jugados:");
		for (int i = 0; i < jugados.size(); i++) {
			System.out.println((i + 1) + ". " + jugados.get(i));
		}
		Encuentro encuentro = jugados.get(lector.leerIndice("\nElija el encuentro a corregir", jugados.size()));
		InscripcionTorneo nuevoGanador = encuentro.getPerdedor();
		if (!lector.confirmar("¿Cambiar el ganador de " + encuentro.getGanador().getNombre() + " a "
				+ nuevoGanador.getNombre() + "?")) {
			System.out.println("Operación cancelada.");
			return;
		}
		club.corregirGanadorTorneo(torneo, encuentro, nuevoGanador);
		System.out.println("Ganador corregido: " + nuevoGanador.getNombre()
				+ ". Si ya estaba en la ronda siguiente, ocupa el lugar del ganador anterior.");
	}
}
