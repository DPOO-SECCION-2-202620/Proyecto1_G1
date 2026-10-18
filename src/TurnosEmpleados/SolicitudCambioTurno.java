package TurnosEmpleados;

import Usuarios.Empleado;

public class SolicitudCambioTurno {

	private EstadoSolicitud estado = EstadoSolicitud.Pendiente;
	
	private Turno turnoActual;
	
	private Turno turnoNuevo;
	
	private Empleado solicitante;

	public SolicitudCambioTurno(Turno turnoActual, Turno turnoNuevo, Empleado solicitante) {
		super();
		this.turnoActual = turnoActual;
		this.turnoNuevo = turnoNuevo;
		this.solicitante = solicitante;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public Turno getTurnoActual() {
		return turnoActual;
	}

	public Turno getTurnoNuevo() {
		return turnoNuevo;
	}

	public Empleado getSolicitante() {
		return solicitante;
	}

	public void aprobar() {
		this.estado = EstadoSolicitud.Aceptada;
	}
	
	public void rechazar() {
		this.estado = EstadoSolicitud.Rechazada;
	}
}
