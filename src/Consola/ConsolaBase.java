package Consola;

import java.util.List;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Usuarios.Administrador;
import Usuarios.Usuario;

public abstract class ConsolaBase {

	private static final int MAX_INTENTOS = 3;

	protected final ClubSeneca club;
	protected final LectorConsola lector;

	protected ConsolaBase(ClubSeneca club, LectorConsola lector) {
		this.club = club;
		this.lector = lector;
	}

	protected abstract boolean aceptaUsuario(Usuario usuario);

	protected abstract String getNombreConsola();

	protected abstract List<OpcionMenu> crearOpciones(Usuario usuario);

	public static ClubSeneca cargarClub() {
		ClubSeneca club = new ClubSeneca("Club Seneca");
		club.cargarDatos();
		return club;
	}

	public void iniciar() {
		try {
			Usuario usuario = autenticar();
			if (usuario == null) {
				System.out.println("Demasiados intentos fallidos. Hasta luego.");
				return;
			}
			System.out.println("\nBienvenido, " + usuario.getNombre() + ".");
			ejecutarMenu(usuario);
		} catch (EntradaCerradaException excepcion) {
			System.out.println("\nSe cerró la entrada de datos. Saliendo.");
		} finally {
			club.guardarDatos();
		}
	}

	private Usuario autenticar() {
		System.out.println("=== " + getNombreConsola() + " ===");
		for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
			String login = lector.leerTexto("Usuario");
			String password = lector.leerTexto("Contraseña");
			Usuario usuario = club.iniciarSesion(login, password);
			if (usuario == null) {
				System.out.println("Usuario o contraseña incorrectos.");
				continue;
			}
			if (!aceptaUsuario(usuario)) {
				System.out.println("Este usuario no tiene acceso a la consola de " + getNombreConsola() + ".");
				continue;
			}
			if (usuario instanceof Administrador) {
				((Administrador) usuario).setClub(club);
			}
			return usuario;
		}
		return null;
	}

	private void ejecutarMenu(Usuario usuario) {
		List<OpcionMenu> opciones = crearOpciones(usuario);
		while (true) {
			System.out.println("\n--- Menú: " + getNombreConsola() + " ---");
			for (int i = 0; i < opciones.size(); i++) {
				System.out.println((i + 1) + ". " + opciones.get(i).getTitulo());
			}
			System.out.println("0. Salir");
			int eleccion = lector.leerEntero("\nElija una opción", 0, opciones.size());
			if (eleccion == 0) {
				System.out.println("Guardando datos. Hasta luego.");
				return;
			}
			try {
				opciones.get(eleccion - 1).ejecutar();
			} catch (ReglaNegocioException excepcion) {
				System.out.println("Operación rechazada: " + excepcion.getMessage());
			} catch (EntradaCerradaException excepcion) {
				throw excepcion;
			} catch (RuntimeException excepcion) {
				System.out.println("Ocurrió un error inesperado: " + excepcion.getMessage());
			}
		}
	}
}