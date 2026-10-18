package Tienda_Cafeteria_Inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import Instalaciones.Instalacion;

public class Inventario {

	private Instalacion instalacion;

	private List<Articulo> articulos;

	private Ubicacion ubicacion;

	public Inventario(Instalacion instalacion, Ubicacion ubicacion) {
		super();
		this.instalacion = instalacion;
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

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public List<Articulo> getArticulos() {
		return articulos;
	}

	public Ubicacion getUbicacion() {
		return ubicacion;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("instalacion", instalacion.toJSON());
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

		JSONObject jsonInstalacion = json.getJSONObject("instalacion");
		String nombreInstalacion = jsonInstalacion.getString("nombre");

		Instalacion instalacion = mapaInstalaciones.get(nombreInstalacion);
		if (instalacion == null) {
			instalacion = Instalacion.fromJSON(jsonInstalacion, mapaInstalaciones);
		}

		Ubicacion ubicacion = Ubicacion.fromJSON(json.getJSONObject("ubicacion"));
		Inventario inventario = new Inventario(instalacion, ubicacion);

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
