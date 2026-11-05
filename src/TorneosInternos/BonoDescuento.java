package TorneosInternos;

import Excepciones.ReglaNegocioException;

public class BonoDescuento {

	private double porcentaje;
	private boolean usado;
	private String torneoOrigen;

	public BonoDescuento(double porcentaje, String torneoOrigen) {
		this.porcentaje = porcentaje;
		this.torneoOrigen = torneoOrigen;
		this.usado = false;
	}

	public double aplicar(double subtotal) throws ReglaNegocioException {
		if (usado) {
	        throw new ReglaNegocioException("El bono ya fue usado");
	    }
	    usado = true;
	    return subtotal * (1 - porcentaje);
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public boolean estaUsado() {
		return usado;
	}

	public String getTorneoOrigen() {
		return torneoOrigen;
	}

	public void setUsado(boolean usado) {
		this.usado = usado;
	}
}
