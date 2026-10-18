package TurnosEmpleados;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import Usuarios.Empleado;

public class GestorTurnos {

	private static final int MIN_ENTRENADORES = 2;
	private static final int MIN_FISIOTERAPEUTAS = 1;

	public boolean puedeAprobar(SolicitudCambioTurno solicitud, List<Empleado> empleados) {
		if (solicitud.getEstado() != EstadoSolicitud.Pendiente) {
			return false;
		}

		Turno turnoActual = solicitud.getTurnoActual();
		String dia = turnoActual.getDiaSemana();

		for (LocalTime hora : horasCambio(turnoActual, solicitud, empleados)) {
			if (!cumpleHora(dia, empleados, hora, solicitud)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean cumpleMinimo(String diaSemana, List<Empleado> empleados, LocalTime hora) {
		return cumpleHora(diaSemana, empleados, hora, null);
	}

	private boolean cumpleHora(String dia, List<Empleado> empleados, LocalTime hora, SolicitudCambioTurno solicitud) {
		int entrenadores = 0;
		int fisioterapeutas = 0;

		for (Empleado empleado : empleados) {
			boolean cubre = false;
			for (Turno turno : turnosSimulados(empleado, solicitud)) {
				if (turno.cubre(dia, hora)) {
					cubre = true;
					break;
				}
			}
			if (cubre) {
				if (empleado.esEntrenador()) entrenadores++;
				else fisioterapeutas++;
			}
		}
		return entrenadores >= MIN_ENTRENADORES && fisioterapeutas >= MIN_FISIOTERAPEUTAS;
	}

	private List<Turno> turnosSimulados(Empleado empleado, SolicitudCambioTurno solicitud) {
		List<Turno> turnos = empleado.getTurnos();
		if (solicitud == null || !empleado.equals(solicitud.getSolicitante())) {
			return turnos;
		}
		List<Turno> simulados = new ArrayList<>(turnos);
		simulados.remove(solicitud.getTurnoActual());
		simulados.add(solicitud.getTurnoNuevo());
		return simulados;
	}

	private TreeSet<LocalTime> horasCambio(Turno turnoActual, SolicitudCambioTurno solicitud, List<Empleado> empleados) {
		TreeSet<LocalTime> horas = new TreeSet<>();
		horas.add(turnoActual.getHoraInicio());

		for (Empleado empleado : empleados) {
			for (Turno turno : turnosSimulados(empleado, solicitud)) {
				if (!turno.getDiaSemana().equals(turnoActual.getDiaSemana())) {
					continue;
				}
				agregarSiCubre(horas, turno.getHoraInicio(), turnoActual);
				agregarSiCubre(horas, turno.getHoraFinal(), turnoActual);
			}
		}
		return horas;
	}

	private void agregarSiCubre(TreeSet<LocalTime> horas, LocalTime hora, Turno turnoActual) {
		if (hora.isAfter(turnoActual.getHoraInicio()) && hora.isBefore(turnoActual.getHoraFinal())) {
			horas.add(hora);
		}
	}
}