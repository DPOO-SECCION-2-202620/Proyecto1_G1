package PruebasJson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import NucleoSistema.ClubSeneca;
import NucleoSistema.Persistencia;

public class PruebaCargaCompleta {

	public static void main(String[] args) throws Exception {
		JSONObject original = new JSONObject(Files.readString(Path.of("datos", "club.json")));

		long t0 = System.nanoTime();
		ClubSeneca club = new Persistencia("datos").cargar();
		long t1 = System.nanoTime();
		System.out.println("Carga: " + (t1 - t0) / 1_000_000 + " ms, club cargado: " + (club != null));

		JSONObject regenerado = club.toJSON();
		List<String> diferencias = new ArrayList<>();
		comparar("club", original, regenerado, diferencias);
		System.out.println("Diferencias original vs regenerado: " + diferencias.size());
		for (int i = 0; i < Math.min(30, diferencias.size()); i++) System.out.println("  " + diferencias.get(i));

		// 50 ciclos: guardar, cargar, comparar. El resultado no debe cambiar nunca.
		Persistencia temporal = new Persistencia("datos_ciclos");
		ClubSeneca actual = club;
		int ciclosConCambio = 0;
		for (int ciclo = 1; ciclo <= 50; ciclo++) {
			temporal.guardar(actual);
			actual = temporal.cargar();
			if (!regenerado.similar(actual.toJSON())) {
				ciclosConCambio++;
				System.out.println("  El ciclo " + ciclo + " cambió los datos");
			}
		}
		System.out.println("Ciclos con cambios: " + ciclosConCambio + " de 50");
		System.out.println("Tiempo total: " + (System.nanoTime() - t0) / 1_000_000 + " ms");
		System.out.println(diferencias.isEmpty() && ciclosConCambio == 0 ? "CARGA COMPLETA OK" : "REVISAR DIFERENCIAS");
	}

	private static void comparar(String ruta, Object a, Object b, List<String> salida) {
		if (a instanceof JSONObject && b instanceof JSONObject) {
			JSONObject x = (JSONObject) a, y = (JSONObject) b;
			TreeSet<String> claves = new TreeSet<>(x.keySet());
			claves.addAll(y.keySet());
			for (String clave : claves) {
				if (!x.has(clave)) salida.add(ruta + "." + clave + " solo aparece al regenerar");
				else if (!y.has(clave)) salida.add(ruta + "." + clave + " se perdió al regenerar");
				else comparar(ruta + "." + clave, x.get(clave), y.get(clave), salida);
			}
		} else if (a instanceof JSONArray && b instanceof JSONArray) {
			JSONArray x = (JSONArray) a, y = (JSONArray) b;
			if (x.length() != y.length()) salida.add(ruta + ": tamaño " + x.length() + " vs " + y.length());
			for (int i = 0; i < Math.min(x.length(), y.length()); i++) comparar(ruta + "[" + i + "]", x.get(i), y.get(i), salida);
		} else if (a instanceof Number && b instanceof Number) {
			if (((Number) a).doubleValue() != ((Number) b).doubleValue()) salida.add(ruta + ": " + a + " vs " + b);
		} else if (!a.equals(b)) {
			salida.add(ruta + ": '" + a + "' vs '" + b + "'");
		}
	}
}