package Consola;

import java.util.List;

import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;
import NucleoSistema.ClubSeneca;

public class OpcionVerificarMinimoJugadores implements OpcionMenu {

	private final ClubSeneca club;
	private final LectorConsola lector;

	public OpcionVerificarMinimoJugadores(ClubSeneca club, LectorConsola lector) {
		this.club = club;
		this.lector = lector;
	}

	@Override
	public String getTitulo() {
		return "Verificar si un equipo alcanza el mínimo de jugadores habilitados";
	}

	@Override
	public void ejecutar() {
		List<Equipo> equipos = club.getEquipos();
		if (equipos.isEmpty()) {
			System.out.println("No hay equipos registrados.");
			return;
		}

		System.out.println("\nEquipos:");
		for (int i = 0; i < equipos.size(); i++) {
			Equipo equipo = equipos.get(i);
			System.out.println((i + 1) + ". " + equipo.getNombre() + " (" + equipo.getDeporte().getNombre() + ", "
					+ equipo.getCategoria() + ") - " + equipo.getInscripciones().size() + " inscritos");
		}

		Equipo equipo = equipos.get(lector.leerIndice("\nElija el equipo", equipos.size()));
		int habilitados = equipo.contarJugadoresHabilitados();
		int minimo = equipo.getDeporte().getMinimoJugadores();

		System.out.println("\n--- Disponibilidad de " + equipo.getNombre() + " ---");
		System.out.println("Jugadores habilitados: " + habilitados + " (mínimo requerido: " + minimo + ")");
		if (equipo.puedeJugar()) {
			System.out.println("El partido PUEDE disputarse.");
		} else {
			System.out.println("El partido NO puede jugarse: faltan " + (minimo - habilitados)
					+ " jugador(es) habilitado(s).");
		}

		List<InscripcionEquipo> noDisponibles = equipo.getInscripcionesSuspendidas();
		if (!noDisponibles.isEmpty()) {
			System.out.println("Jugadores no disponibles:");
			for (InscripcionEquipo inscripcion : noDisponibles) {
				System.out.println("  - " + inscripcion.getJugador().getNombre() + " (dorsal "
						+ inscripcion.getNumeroCamiseta() + "): suspendido (sanción vigente)");
			}
		}
	}
}