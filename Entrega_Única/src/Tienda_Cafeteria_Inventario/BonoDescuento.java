package Tienda_Cafeteria_Inventario;

import Excepciones.ReglaNegocioException;
import org.json.JSONObject;

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
	
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    json.put("porcentaje", porcentaje);
	    json.put("usado", usado);
	    json.put("torneoOrigen", torneoOrigen);
	    return json;
	}

	public static BonoDescuento fromJSON(JSONObject json) {
	    BonoDescuento bono = new BonoDescuento(json.getDouble("porcentaje"), json.getString("torneoOrigen"));
	    bono.usado = json.getBoolean("usado");
	    return bono;
	}
}
