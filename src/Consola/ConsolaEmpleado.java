package Consola;

import java.util.ArrayList;
import java.util.List;

import NucleoSistema.ClubSeneca;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Usuario;
import Usuarios.Socio;

public class ConsolaEmpleado extends ConsolaBase {

	public ConsolaEmpleado(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	protected boolean aceptaUsuario(Usuario usuario) {
		return usuario instanceof Empleado;
	}

	@Override
	protected String getNombreConsola() {
		return "Empleado";
	}

	@Override
	protected List<OpcionMenu> crearOpciones(Usuario usuario) {
	    Socio empleado = (Socio) usuario;
	    List<OpcionMenu> opciones = new ArrayList<>();
	    if (usuario instanceof Entrenador) {
	        opciones.add(new OpcionVerificarMinimoJugadores(club, lector));
	        opciones.add(new OpcionInscribirEquipoTorneo(club, lector));
	        opciones.add(new OpcionRetirarEquipoTorneo(club, lector));
	    }
	    opciones.add(new OpcionInscribirseTorneo(club, empleado, lector));
	    opciones.add(new OpcionRetirarseTorneo(club, empleado, lector));
	    opciones.add(new OpcionConsultarTorneos(club, lector));
	    opciones.add(new OpcionVerBonos(empleado, lector));
	    opciones.add(new OpcionComprarConBono(club, empleado, lector));
	    return opciones;
	}

	public static void main(String[] args) {
		ClubSeneca club = cargarClub();
		new ConsolaEmpleado(club, new LectorConsola()).iniciar();
	}
}