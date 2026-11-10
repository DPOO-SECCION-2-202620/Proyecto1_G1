package PruebasJson;

import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class PruebaLeer {
	public static void main(String[] args) throws Exception {
		Path archivo = Path.of("datos", "club.json");
		if (!Files.exists(archivo)) {
			System.out.println("No existe datos/club.json. Ejecuta PruebaGuardar primero.");
			return;
		}

		JSONObject json = new JSONObject(Files.readString(archivo));
		System.out.println("Club: " + json.getString("nombre"));
		System.out.println("Usuarios: " + json.getJSONArray("usuarios").length());
		System.out.println("Disciplinas: " + json.getJSONArray("disciplinas").length());
		System.out.println("Equipos: " + json.getJSONArray("equipos").length());
		System.out.println("LECTURA DE ARCHIVO OK");
	}
}