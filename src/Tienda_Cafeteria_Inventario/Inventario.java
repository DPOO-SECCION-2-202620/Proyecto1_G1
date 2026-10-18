package Tienda_Cafeteria_Inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import Instalaciones.Instalacion;

public class Inventario {

	private List<Instalacion> instalaciones;

	private List<Articulo> articulos;

	private Ubicacion ubicacion;

	public Inventario(List<Instalacion> instalaciones, Ubicacion ubicacion) {
		super();
		this.instalaciones = instalaciones;
		this.ubicacion = ubicacion;
		this.articulos = new ArrayList<>();
	}

	public void agregarArticulo(Articulo articulo) {
		articulos.add(articulo);
	}

	public void quitarArticulo(Articulo articulo) {
		articulos.remove(articulo);
	}

	public boolean tieneArticulo(Articulo articulo) {
		return articulos.contains(articulo);
	}

	public List<Instalacion> getInstalaciones() {
		return instalaciones;
	}
	
	public List<Articulo> getArticulos() {
		return articulos;
	}

	public Ubicacion getUbicacion() {
		return ubicacion;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		JSONArray jsonInstalaciones = new JSONArray();
		for (Instalacion instalacion : instalaciones) {
			jsonInstalaciones.put(instalacion.getNombre());
		}
		json.put("instalaciones", jsonInstalaciones);
		json.put("ubicacion", ubicacion.toJSON());
		JSONArray jsonArticulos = new JSONArray();
		for (Articulo articulo : articulos) {
			jsonArticulos.put(articulo.getNombre());
		}
		json.put("articulos", jsonArticulos);
		return json;
	}

	public static Inventario fromJSON(JSONObject json,
			Map<String, Producto> mapaProductos,
			Map<String, Instalacion> mapaInstalaciones) {

		List<Instalacion> instalaciones = new ArrayList<>();
		JSONArray jsonInstalaciones = json.getJSONArray("instalaciones");
		for (int indice = 0; indice < jsonInstalaciones.length(); indice++) {
			String nombreInstalacion = jsonInstalaciones.getString(indice);
			Instalacion instalacion = mapaInstalaciones.get(nombreInstalacion);
			if (instalacion == null) {
				throw new IllegalArgumentException(
						"Instalación no encontrada para el inventario: " + nombreInstalacion);
			}
			instalaciones.add(instalacion);
		}

		Ubicacion ubicacion = Ubicacion.fromJSON(json.getJSONObject("ubicacion"));
		Inventario inventario = new Inventario(instalaciones, ubicacion);

		JSONArray jsonArticulos = json.optJSONArray("articulos");
		if (jsonArticulos != null) {
			for (int indice = 0; indice < jsonArticulos.length(); indice++) {
				String nombreArticulo = jsonArticulos.getString(indice);
				Producto producto = mapaProductos.get(nombreArticulo);
				if (!(producto instanceof Articulo)) {
					throw new IllegalArgumentException(
							"Artículo no encontrado para el inventario: " + nombreArticulo);
				}
				inventario.agregarArticulo((Articulo) producto);
			}
		}
		return inventario;
	}
}
