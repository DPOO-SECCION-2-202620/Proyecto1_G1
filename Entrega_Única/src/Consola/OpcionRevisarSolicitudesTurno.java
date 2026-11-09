package Consola;

import java.util.List;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Administrador;
import Usuarios.Entrenador;

public class OpcionRevisarSolicitudesTurno implements OpcionMenu {

	private final ClubSeneca club;
	private final Administrador administrador;
	private final LectorConsola lector;

	public OpcionRevisarSolicitudesTurno(ClubSeneca club, Administrador administrador, LectorConsola lector) {
		this.club = club;
		this.administrador = administrador;
		this.lector = lector;
	}

	@Override
	public String getTitulo() {
		return "Revisar solicitudes de cambio de turno";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<SolicitudCambioTurno> pendientes = club.getSolicitudesPendientes();
		if (pendientes.isEmpty()) {
			System.out.println("No hay solicitudes pendientes.");
			return;
		}

		System.out.println("\nSolicitudes pendientes:");
		for (int i = 0; i < pendientes.size(); i++) {
			System.out.println((i + 1) + ". " + describir(pendientes.get(i)));
		}

		SolicitudCambioTurno solicitud = pendientes.get(lector.leerIndice("\nElija la solicitud a revisar", pendientes.size()));
		int decision = lector.leerEntero("Decisión (1 = aprobar, 2 = rechazar, 0 = cancelar)", 0, 2);
		if (decision == 0) {
			System.out.println("Operación cancelada.");
			return;
		}

		boolean aprobar = decision == 1;
		administrador.resolverSolicitudTurno(solicitud, aprobar);

		if (aprobar) {
			System.out.println("Solicitud aprobada. " + solicitud.getSolicitante().getNombre() + " pasa de "
					+ describirTurno(solicitud.getTurnoActual()) + " a " + describirTurno(solicitud.getTurnoNuevo()) + ".");
		} else {
			System.out.println("Solicitud rechazada.");
		}
	}

	private String describir(SolicitudCambioTurno solicitud) {
		String cargo = "Fisioterapeuta";
		if (solicitud.getSolicitante() instanceof Entrenador) {
		    cargo = "Entrenador";
		}
		return solicitud.getSolicitante().getNombre() + " (" + cargo + "): "
				+ describirTurno(solicitud.getTurnoActual()) + " -> " + describirTurno(solicitud.getTurnoNuevo());
	}

	private String describirTurno(Turno turno) {
		return turno.getDiaSemana() + " " + turno.getHoraInicio() + "-" + turno.getHoraFinal();
	}
}