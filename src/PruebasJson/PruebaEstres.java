package PruebasJson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class PruebaEstres {

	public static void main(String[] args) throws Exception {
		int socios = 5000, ventas = 30000, reservas = 10000;
		Random azar = new Random(42);

		long t0 = System.nanoTime();
		JSONObject club = new JSONObject().put("nombre", "Club Estres");

		JSONArray usuarios = new JSONArray();
		for (int i = 0; i < socios; i++) {
			usuarios.put(new JSONObject().put("tipo", "Socio").put("nombre", "Socio " + i)
					.put("login", "socio" + i).put("password", "pw" + i).put("edad", 15 + azar.nextInt(60))
					.put("puntosFidelidad", azar.nextInt(1000)).put("inscripcionesIndividuales", new JSONArray()));
		}
		club.put("usuarios", usuarios);

		JSONArray ventasJson = new JSONArray();
		for (int i = 0; i < ventas; i++) {
			JSONArray lineas = new JSONArray();
			for (int j = 0; j < 1 + azar.nextInt(5); j++) {
				lineas.put(new JSONObject().put("cantidad", 1 + azar.nextInt(4)).put("producto", "Producto " + azar.nextInt(200)));
			}
			ventasJson.put(new JSONObject().put("fecha", "2026-10-" + String.format("%02d", 1 + azar.nextInt(28)))
					.put("total", 1000 + azar.nextInt(300000)).put("comprador", "socio" + azar.nextInt(socios)).put("lineas", lineas));
		}
		club.put("ventas", ventasJson);

		JSONArray reservasJson = new JSONArray();
		for (int i = 0; i < reservas; i++) {
			reservasJson.put(new JSONObject().put("socio", "socio" + azar.nextInt(socios))
					.put("instalacion", "Mesa " + azar.nextInt(50)).put("fecha", "2026-11-" + String.format("%02d", 1 + azar.nextInt(28)))
					.put("horaInicio", "08:00").put("horaFinal", "09:00").put("modalidad", "SENCILLOS").put("numeroJugadores", 2));
		}
		club.put("reservas", reservasJson);
		long t1 = System.nanoTime();

		Path carpeta = Path.of("datos_estres");
		Files.createDirectories(carpeta);
		Path archivo = carpeta.resolve("club_estres.json");
		Files.writeString(archivo, club.toString(2));
		long t2 = System.nanoTime();

		JSONObject leido = new JSONObject(Files.readString(archivo));
		long t3 = System.nanoTime();

		Set<String> logins = new HashSet<>();
		for (Object o : leido.getJSONArray("usuarios")) logins.add(((JSONObject) o).getString("login"));
		int rotas = 0;
		for (Object o : leido.getJSONArray("ventas")) if (!logins.contains(((JSONObject) o).getString("comprador"))) rotas++;
		for (Object o : leido.getJSONArray("reservas")) if (!logins.contains(((JSONObject) o).getString("socio"))) rotas++;
		long t4 = System.nanoTime();

		System.out.println("Tamaño del archivo: " + Files.size(archivo) / 1024 + " KB");
		System.out.println("Generar en memoria : " + (t1 - t0) / 1_000_000 + " ms");
		System.out.println("Escribir a disco   : " + (t2 - t1) / 1_000_000 + " ms");
		System.out.println("Leer y parsear     : " + (t3 - t2) / 1_000_000 + " ms");
		System.out.println("Validar referencias: " + (t4 - t3) / 1_000_000 + " ms");
		System.out.println("Usuarios " + leido.getJSONArray("usuarios").length() + ", ventas " + leido.getJSONArray("ventas").length()
				+ ", reservas " + leido.getJSONArray("reservas").length());
		System.out.println("Idéntico tras ida y vuelta: " + club.similar(leido));
		System.out.println("Referencias rotas: " + rotas);
		System.out.println(rotas == 0 && club.similar(leido) ? "ESTRES OK" : "ESTRES CON PROBLEMAS");
	}
}