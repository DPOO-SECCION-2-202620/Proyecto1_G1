package TurnosEmpleados;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import Usuarios.Empleado;
import Usuarios.Usuario;

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
		json.put("turnoActual", turnoActual.toJSON());
		json.put("turnoNuevo", turnoNuevo.toJSON());
		json.put("solicitante", solicitante.getLogin());
		return json;
	}

	public static SolicitudCambioTurno fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios) {
		String loginSolicitante = json.getString("solicitante");
		Usuario usuario = mapaUsuarios.get(loginSolicitante);
		if (!(usuario instanceof Empleado)) {
			throw new IllegalArgumentException("El solicitante no es un empleado válido: " + loginSolicitante);
		}
		Empleado solicitante = (Empleado) usuario;

		Turno turnoActual = Turno.fromJSON(json.getJSONObject("turnoActual"));
		for (Turno turno : solicitante.getTurnos()) {
			boolean mismoDia = turno.getDiaSemana().equals(turnoActual.getDiaSemana());
			boolean mismaInicio = turno.getHoraInicio().equals(turnoActual.getHoraInicio());
			boolean mismaFinal = turno.getHoraFinal().equals(turnoActual.getHoraFinal());
			if (mismoDia && mismaInicio && mismaFinal) {
				turnoActual = turno;
				break;
			}
		}

		Turno turnoNuevo = Turno.fromJSON(json.getJSONObject("turnoNuevo"));

		SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turnoActual, turnoNuevo, solicitante);
		solicitud.estado = EstadoSolicitud.valueOf(json.getString("estado"));
		return solicitud;
	}
}
