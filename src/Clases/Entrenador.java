package Clases;

public class Entrenador extends Empleado{

	public Entrenador(int edad, String nombre, String login, String password, String codigoEmpleado) {
		super(edad, nombre, login, password, codigoEmpleado);
	}
	
	@Override
	public boolean esEntrenador() {
		return true;
	}

}
