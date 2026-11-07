package Consola;

import java.util.ArrayList;
import java.util.List;

import DeportesConjunto_Competencias.Equipo;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;

public class OpcionInscribirEquipoTorneo extends OpcionTorneo {

	public OpcionInscribirEquipoTorneo(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Inscribir un equipo en un torneo";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<Torneo> candidatos = new ArrayList<>();
		for (Torneo torneo : torneosEn(EstadoTorneo.INSCRIPCIONES_ABIERTAS)) {
			if (torneo instanceof TorneoConjunto) {
				candidatos.add(torneo);
			}
		}
		Torneo elegido = elegirTorneo(candidatos, "No hay torneos de conjunto con inscripciones abiertas.");
		if (elegido == null) {
			return;
		}
		TorneoConjunto torneo = (TorneoConjunto) elegido;

		List<Equipo> compatibles = new ArrayList<>();
		for (Equipo equipo : club.getEquipos()) {
			boolean mismoDeporte = equipo.getDeporte().getNombre().equals(torneo.getDeporte().getNombre());
			if (mismoDeporte && equipo.getCategoria() == torneo.getCategoria()) {
				compatibles.add(equipo);
			}
		}
		if (compatibles.isEmpty()) {
			System.out.println("No hay equipos de " + torneo.getDescripcion() + " en el club.");
			return;
		}
		System.out.println("\nEquipos compatibles:");
		for (int i = 0; i < compatibles.size(); i++) {
			Equipo equipo = compatibles.get(i);
			System.out.println((i + 1) + ". " + equipo.getNombre() + " - habilitados: "
					+ equipo.contarJugadoresHabilitados() + " (mínimo " + equipo.getDeporte().getMinimoJugadores() + ")");
		}
		Equipo equipo = compatibles.get(lector.leerIndice("\nElija el equipo", compatibles.size()));

		InscripcionTorneoEquipo inscripcion = new InscripcionTorneoEquipo(equipo);
		club.inscribirEnTorneo(torneo, inscripcion);

		System.out.println("Equipo " + equipo.getNombre() + " inscrito en \"" + torneo.getNombre() + "\".");
		if (inscripcion.esPreferencial()) {
			System.out.println("Ocupó un cupo preferencial por su desempeño.");
		}
		if (torneo.esCompetitivo()) {
			System.out.println("Valor de la inscripción: " + FormatoTorneo.dinero(inscripcion.getMontoPagado())
					+ " (el pago se gestiona por un medio externo).");
		}
	}
}
