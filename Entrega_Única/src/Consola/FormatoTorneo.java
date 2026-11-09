package Consola;

import java.util.List;
import java.util.Locale;

import Tienda_Cafeteria_Inventario.BonoDescuento;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.PremioEntregado;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.Torneo;

public final class FormatoTorneo {

	private FormatoTorneo() {
	}

	public static String estado(EstadoTorneo estado) {
		if (estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
			return "Inscripciones abiertas";
		}
		if (estado == EstadoTorneo.EN_CURSO) {
			return "En curso";
		}
		return "Finalizado";
	}

	public static String dinero(double valor) {
		return "$" + Math.round(valor);
	}

	public static String linea(Torneo torneo) {
		return torneo.getNombre() + " | " + torneo.getDescripcion() + " | "
				+ (torneo.esCompetitivo() ? "Competitivo" : "Recreativo") + " | " + estado(torneo.getEstado())
				+ " | inscritos: " + torneo.getInscripciones().size() + "/" + torneo.getCupos();
	}

	public static void imprimirLista(List<Torneo> torneos) {
		for (int i = 0; i < torneos.size(); i++) {
			System.out.println((i + 1) + ". " + linea(torneos.get(i)));
		}
	}

	public static void imprimirDetalle(Torneo torneo) {
		System.out.println("\n=== " + torneo.getNombre() + " ===");
		System.out.println("Deporte/disciplina: " + torneo.getDescripcion());
		if (torneo.esCompetitivo()) {
			double tarifa = ((PremiacionCompetitiva) torneo.getPremiacion()).getTarifaBase();
			System.out.println("Tipo: Competitivo (tarifa por jugador: " + dinero(tarifa) + ", premio en efectivo)");
			System.out.println("Total recaudado: " + dinero(torneo.getRecaudado()));
		} else {
			System.out.println("Tipo: Recreativo (sin costo, premio: bono de descuento)");
		}
		System.out.println("Estado: " + estado(torneo.getEstado()));
		System.out.println("Fecha de inicio: " + torneo.getFechaInicio() + " | límite cupos preferenciales: "
				+ torneo.getFechaLimitePreferencial());
		System.out.println("Cupos: " + torneo.getInscripciones().size() + " ocupados de " + torneo.getCupos()
				+ " (preferenciales libres: " + torneo.getCuposPreferencialesLibres() + " de "
				+ torneo.getCuposPreferenciales() + ")");

		System.out.println("Participantes:");
		if (torneo.getInscripciones().isEmpty()) {
			System.out.println("  (ninguno)");
		}
		int numero = 1;
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			String linea = "  " + numero + ". " + inscripcion.getNombre();
			if (inscripcion.esPreferencial()) {
				linea = linea + " [cupo preferencial]";
			}
			if (torneo.esCompetitivo()) {
				linea = linea + " - inscripción: " + dinero(inscripcion.getMontoPagado());
			}
			System.out.println(linea);
			numero++;
		}

		System.out.println("Encuentros:");
		if (torneo.getEncuentros().isEmpty()) {
			System.out.println("  (aún no se han programado)");
		}
		for (Encuentro encuentro : torneo.getEncuentros()) {
			System.out.println("  " + encuentro);
		}

		if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
			System.out.println("Posiciones finales:");
			List<InscripcionTorneo> posiciones = torneo.getPosiciones();
			for (int i = 0; i < posiciones.size(); i++) {
				System.out.println("  " + (i + 1) + ". " + posiciones.get(i).getNombre());
			}
			imprimirPremios(torneo);
		}
	}

	public static void imprimirPremios(Torneo torneo) {
		System.out.println("Premios entregados:");
		for (PremioEntregado premio : torneo.getPremios()) {
			String detalle;
			BonoDescuento bono = premio.getBono();
			if (bono != null) {
				detalle = "bono de descuento del " + Math.round(bono.getPorcentaje() * 100) + "%";
			} else if (premio.getJugador().esEmpleado()) {
				detalle = "sin premio en efectivo (empleado)";
			} else {
				detalle = dinero(premio.getMontoEfectivo()) + " en efectivo";
			}
			System.out.println("  Puesto " + premio.getPuesto() + ": " + premio.getJugador().getNombre() + " - " + detalle);
		}
	}
}
