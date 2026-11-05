package Consola;

import java.io.InputStream;
import java.util.Scanner;

public class LectorConsola {

	private final Scanner scanner;

	public LectorConsola(InputStream entrada) {
		this.scanner = new Scanner(entrada);
	}

	public LectorConsola() {
		this(System.in);
	}

	public String leerTexto(String mensaje) {
		while (true) {
			System.out.print(mensaje + ": ");
			String linea = leerLinea().trim();
			if (!linea.isEmpty()) {
				return linea;
			}
			System.out.println("  La entrada no puede estar vacía.");
		}
	}

	public int leerEntero(String mensaje, int minimo, int maximo) {
		while (true) {
			System.out.print(mensaje + " [" + minimo + "-" + maximo + "]: ");
			String linea = leerLinea().trim();
			try {
				int valor = Integer.parseInt(linea);
				if (valor >= minimo && valor <= maximo) {
					return valor;
				}
				System.out.println("  El número debe estar entre " + minimo + " y " + maximo + ".");
			} catch (NumberFormatException excepcion) {
				System.out.println("  Escriba un número entero válido.");
			}
		}
	}

	/** Pide elegir un elemento de una lista numerada desde 1; devuelve el índice desde 0. */
	public int leerIndice(String mensaje, int tamanoLista) {
		return leerEntero(mensaje, 1, tamanoLista) - 1;
	}

	public boolean confirmar(String mensaje) {
		while (true) {
			System.out.print(mensaje + " (s/n): ");
			String linea = leerLinea().trim().toLowerCase();
			if (linea.equals("s") || linea.equals("si") || linea.equals("sí")) {
				return true;
			}
			if (linea.equals("n") || linea.equals("no")) {
				return false;
			}
			System.out.println("  Responda s o n.");
		}
	}

	private String leerLinea() {
		if (!scanner.hasNextLine()) {
			throw new EntradaCerradaException();
		}
		return scanner.nextLine();
	}
}