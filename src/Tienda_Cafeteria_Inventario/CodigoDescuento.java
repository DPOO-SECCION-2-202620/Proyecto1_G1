package Tienda_Cafeteria_Inventario;

import org.json.JSONObject;

public class CodigoDescuento {

	private String codigo;
	
	private static final Double PORCENTAJE_DUENO = 0.15;
	
	private static final Double PORCENTAJE_COMPARTIDO = 0.15;

	public CodigoDescuento(String codigo) {
		super();
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public Double getPorcentaje(boolean esDueno) {
		return esDueno ? PORCENTAJE_DUENO : PORCENTAJE_COMPARTIDO;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("codigo", codigo);
		return json;
	}
	
	public static CodigoDescuento fromJSON(JSONObject json) {
		return new CodigoDescuento(json.getString("codigo"));
	}
}
