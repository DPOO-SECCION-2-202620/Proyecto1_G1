package Usuarios;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Equipo;
import Excepciones.ReglaNegocioException;
import Tienda_Cafeteria_Inventario.CodigoDescuento;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;

public class Empleado extends Socio{

	private String codigoEmpleado;
	private CodigoDescuento codigoDescuento;
	private List<Turno> turnos;
	
	public Empleado(int edad, String nombre, String login, String password, String codigoEmpleado) {
		super(edad, nombre, login, password);
		this.codigoEmpleado = codigoEmpleado;
		this.codigoDescuento = new CodigoDescuento("DESC-" + codigoEmpleado);
		this.turnos = new ArrayList<>();
	}

	public String getCodigoEmpleado() {
		return codigoEmpleado;
	}

	public CodigoDescuento getCodigoDescuento() {
		return codigoDescuento;
	}

	public List<Turno> getTurnos() {
		return turnos;
	}
	
	public SugerenciaArticulo sugerirArticulo(String nombrePropuesto, String categoria, double precioSugerido) {
		return new SugerenciaArticulo(nombrePropuesto, categoria, precioSugerido);
	}
	
	public SolicitudCambioTurno solicitarCambioTurno(Turno turnoActual, Turno turnoNuevo)
			throws ReglaNegocioException {
		if (!turnos.contains(turnoActual)) {
	        throw new ReglaNegocioException("Ese turno no es de " + getNombre());
	    }
		return new SolicitudCambioTurno(turnoActual, turnoNuevo, this);
	}

	public void agregarTurno(Turno turno) {
		turnos.add(turno);
	}

	public void cambiarTurno(Turno actual, Turno nuevo) {
		int posicion = turnos.indexOf(actual);
		if (posicion >= 0) {
			turnos.set(posicion, nuevo);
		}
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "Empleado");
		json.put("codigoEmpleado", codigoEmpleado);
		json.put("codigoDescuento", codigoDescuento.toJSON());
		JSONArray jsonTurnos = new JSONArray();
	    for (Turno turno : turnos) {
	    	jsonTurnos.put(turno.toJSON());
	    }
	    json.put("turnos", jsonTurnos);
		return json;
	}
}
