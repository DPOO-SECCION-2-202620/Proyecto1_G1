package Usuarios;

import java.util.List;
import java.util.ArrayList;

public class Empleado extends Socio{

	private String codigoEmpleado;
	private CodigoDescuento codigoDescuento;
	private List<Turno> listaTurnos;
	
	public Empleado(int edad, String nombre, String login, String password, String codigoEmpleado) {
		super(edad, nombre, login, password);
		this.codigoEmpleado = codigoEmpleado;
		this.codigoDescuento = new CodigoDescuento("DESC-" + codigoEmpleado);
		this.listaTurnos = new ArrayList<>();
	}

	public String getCodigoEmpleado() {
		return codigoEmpleado;
	}

	public CodigoDescuento getCodigoDescuento() {
		return codigoDescuento;
	}

	public List<Turno> getListaTurnos() {
		return listaTurnos;
	}
	
	public SugerenciaArticulo sugerirArticulo(String nombrePropuesto, String categoria, double precioSugerido) {
		return new SugerenciaArticulo(nombrePropuesto, categoria, precioSugerido);
	}
	
	public SolicitudCambioTurno solicitarCambioTurno(Turno turnoActual, Turno turnoNuevo)
			throws ReglaNegocioException {
		if (!listaTurnos.contains(turnoActual)) {
	        throw new ReglaNegocioException("Ese turno no es de " + getNombre());
	    }
		return new SolicitudCambioTurno(turnoActual, turnoNuevo, this);
	}

	public void agregarTurno(Turno turno) {
		listaTurnos.add(turno);
	}

	public void cambiarTurno(Turno actual, Turno nuevo) {
		int posicion = listaTurnos.indexOf(actual);
		if (posicion >= 0) {
			listaTurnos.set(posicion, nuevo);
		}
	}
	
	
}
