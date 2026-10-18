package PruebasJson;

import org.json.JSONObject;

public class PruebaLibrerias {
	public static void main(String[] args) {
		JSONObject obj = new JSONObject();
		obj.put("nombre", "Ana");
		obj.put("edad", 20);

		String texto = obj.toString(2);
		System.out.println(texto);

		JSONObject leido = new JSONObject(texto);
		System.out.println(leido.getString("nombre") + ", " + leido.getInt("edad"));
		System.out.println("LIBRERIAS OK");
	}
}