package Consola;

import java.util.List;

import NucleoSistema.ClubSeneca;
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
}
