package Consola;

import java.util.List;

import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.Torneo;

public class OpcionConsultarTorneos extends OpcionTorneo {

	public OpcionConsultarTorneos(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Consultar torneos (estado e historial)";
	}

	@Override
	public void ejecutar() {
		if (club.getTorneos().isEmpty()) {
			System.out.println("No hay torneos registrados.");
			return;
		}
		EstadoTorneo filtro = elegirFiltro();
		List<Torneo> torneos = club.getTorneos();
		if (torneos.isEmpty()) {
			System.out.println("No hay torneos registrados.");
			return;
		}
		while (true) {
			System.out.println("\nTorneos:");
			FormatoTorneo.imprimirLista(torneos);
			int eleccion = lector.leerEntero("Elija un torneo para ver su historial (0 = volver)", 0, torneos.size());
			if (eleccion == 0) {
				return;
			}
			FormatoTorneo.imprimirDetalle(torneos.get(eleccion - 1));
		}
	}
	
	private EstadoTorneo elegirFiltro() {
		System.out.println("Mostrar:");
		System.out.println("1. Todos");
		System.out.println("2. Inscripciones abiertas");
		System.out.println("3. En curso");
		System.out.println("4. Finalizados");
		switch (lector.leerEntero("Elija una opción", 1, 4)) {
		case 2:
			return EstadoTorneo.INSCRIPCIONES_ABIERTAS;
		case 3:
			return EstadoTorneo.EN_CURSO;
		case 4:
			return EstadoTorneo.FINALIZADO;
		default:
			return null;
		}
	}
}
