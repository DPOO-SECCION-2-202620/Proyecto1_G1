package Consola;

import Excepciones.ReglaNegocioException;

public interface OpcionMenu {
	String getTitulo();

	void ejecutar() throws ReglaNegocioException;
}