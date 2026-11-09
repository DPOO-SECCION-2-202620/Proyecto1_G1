package Consola;

import java.util.ArrayList;
import java.util.List;

import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.Torneo;

public abstract class OpcionTorneo implements OpcionMenu {

	protected final ClubSeneca club;
	protected final LectorConsola lector;

	protected OpcionTorneo(ClubSeneca club, LectorConsola lector) {
		this.club = club;
		this.lector = lector;
	}

	protected List<Torneo> torneosEn(EstadoTorneo estado) {
		List<Torneo> resultado = new ArrayList<>();
		for (Torneo torneo : club.getTorneos()) {
			if (torneo.getEstado() == estado) {
				resultado.add(torneo);
			}
		}
		return resultado;
	}

	protected Torneo elegirTorneo(List<Torneo> torneos, String mensajeSiVacio) {
		if (torneos.isEmpty()) {
			System.out.println(mensajeSiVacio);
			return null;
		}
		System.out.println("\nTorneos:");
		FormatoTorneo.imprimirLista(torneos);
		return torneos.get(lector.leerIndice("\nElija el torneo", torneos.size()));
	}
}
