package PruebasJson;

import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;

public class PruebaLeer {

	public static void main(String[] args) throws Exception {
		Path archivo = Path.of("datos", "prueba.json");

		if (!Files.exists(archivo)) {
			System.out.println("No existe el archivo. Ejecuta PruebaGuardar primero.");
			return;
		}

		JSONObject club = new JSONObject(Files.readString(archivo));
		System.out.println("Club: " + club.getString("nombre"));

		JSONArray usuarios = club.getJSONArray("usuarios");
		for (int i = 0; i < usuarios.length(); i++) {
			JSONObject u = usuarios.getJSONObject(i);
			System.out.println("Usuario: " + u.getString("tipo") + " - " + u.getString("nombre")
					+ " (" + u.getString("login") + "), edad " + u.getInt("edad"));
		}

		// Resolver una referencia: la venta guarda el login, se busca el usuario
		JSONObject venta = club.getJSONArray("ventas").getJSONObject(0);
		String loginComprador = venta.getString("comprador");
		for (int i = 0; i < usuarios.length(); i++) {
			JSONObject u = usuarios.getJSONObject(i);
			if (u.getString("login").equals(loginComprador)) {
				System.out.println("Venta de " + venta.getDouble("total") + " comprada por " + u.getString("nombre"));
			}
		}

		System.out.println("LECTURA OK");
	}
}