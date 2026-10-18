package TurnosEmpleados;

import org.json.JSONObject;

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
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("estado", estado.name());
	    if (estado == EstadoSolicitud.Pendiente) {
			json.put("turnoActual", solicitante.getTurnos().indexOf(turnoActual));
		} else {
			json.put("turnoActual", turnoActual.toJSON());
		}
	    json.put("turnoNuevo", turnoNuevo.toJSON());
	    json.put("solicitante", solicitante.getLogin());
		return json;
	}
}
