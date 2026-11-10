package Consola;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
	
	public double leerDecimal(String mensaje, double minimo, double maximo) {
		while (true) {
			System.out.print(mensaje + " [" + formatear(minimo) + "-" + formatear(maximo) + "]: ");
			String linea = leerLinea().trim().replace(',', '.');
			try {
				double valor = Double.parseDouble(linea);
				if (valor >= minimo && valor <= maximo) {
					return valor;
				}
				System.out.println("  El valor debe estar entre " + formatear(minimo) + " y " + formatear(maximo) + ".");
			} catch (NumberFormatException excepcion) {
				System.out.println("  Escriba un número válido.");
			}
		}
	}

	public LocalDate leerFecha(String mensaje, LocalDate minima, LocalDate maxima) {
		while (true) {
			System.out.print(mensaje + " (AAAA-MM-DD, entre " + minima + " y " + maxima + "): ");
			String linea = leerLinea().trim();
			try {
				LocalDate fecha = LocalDate.parse(linea);
				if (!fecha.isBefore(minima) && !fecha.isAfter(maxima)) {
					return fecha;
				}
				System.out.println("  La fecha debe estar entre " + minima + " y " + maxima + ".");
			} catch (DateTimeParseException excepcion) {
				System.out.println("  Formato inválido. Use AAAA-MM-DD, por ejemplo 2026-12-20.");
			}
		}
	}

	private String formatear(double valor) {
		return String.valueOf((long) valor);
	}

	private String leerLinea() {
		if (!scanner.hasNextLine()) {
			throw new EntradaCerradaException();
		}
		return scanner.nextLine();
	}
}