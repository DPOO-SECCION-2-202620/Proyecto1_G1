package Consola;

public class EntradaCerradaException extends RuntimeException {
	public EntradaCerradaException() {
		super("Se cerró la entrada de datos");
	}
}