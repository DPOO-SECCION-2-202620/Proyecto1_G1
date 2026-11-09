package Consola;

import java.util.ArrayList;
import java.util.List;

import DeportesConjunto_Competencias.Equipo;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;

public class OpcionRetirarEquipoTorneo extends OpcionTorneo {

	public OpcionRetirarEquipoTorneo(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Retirar un equipo de un torneo";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<Torneo> candidatos = new ArrayList<>();
		for (Torneo torneo : torneosEn(EstadoTorneo.INSCRIPCIONES_ABIERTAS)) {
			if (torneo instanceof TorneoConjunto && !torneo.getInscripciones().isEmpty()) {
				candidatos.add(torneo);
			}
		}
		Torneo torneo = elegirTorneo(candidatos, "No hay torneos de conjunto con equipos inscritos para retirar.");
		if (torneo == null) {
			return;
		}
		List<Equipo> inscritos = new ArrayList<>();
		for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
			inscritos.add(((InscripcionTorneoEquipo) inscripcion).getEquipo());
		}
		System.out.println("\nEquipos inscritos:");
		for (int i = 0; i < inscritos.size(); i++) {
			System.out.println((i + 1) + ". " + inscritos.get(i).getNombre());
		}
		Equipo equipo = inscritos.get(lector.leerIndice("\nElija el equipo a retirar", inscritos.size()));
		club.retirarEquipoDeTorneo(torneo, equipo);
		System.out.println("Equipo " + equipo.getNombre() + " retirado de \"" + torneo.getNombre()
				+ "\". Los cupos quedaron liberados.");
	}
}
