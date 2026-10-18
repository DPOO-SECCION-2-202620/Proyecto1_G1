package PruebasJson;

import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;

public class PruebaGuardar {

	public static void main(String[] args) throws Exception {
		// Un "club" de mentira con una lista de socios y una venta que referencia al socio por login
		JSONObject club = new JSONObject();
		club.put("nombre", "Club Seneca");

		JSONArray usuarios = new JSONArray();
		usuarios.put(new JSONObject()
				.put("tipo", "Socio").put("nombre", "Ana").put("login", "ana").put("edad", 20));
		usuarios.put(new JSONObject()
				.put("tipo", "Entrenador").put("nombre", "Luis").put("login", "luis").put("edad", 35));
		club.put("usuarios", usuarios);

		JSONArray ventas = new JSONArray();
		ventas.put(new JSONObject()
				.put("fecha", "2026-10-17").put("total", 12500.5).put("comprador", "ana"));
		club.put("ventas", ventas);

		// Guardar (la carpeta se crea sola)
		Path carpeta = Path.of("datos");
		Files.createDirectories(carpeta);
		Files.writeString(carpeta.resolve("prueba.json"), club.toString(2));

		System.out.println("Guardado en: " + carpeta.resolve("prueba.json").toAbsolutePath());
	}
}