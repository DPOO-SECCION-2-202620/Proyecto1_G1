package Tienda_Cafeteria_Inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import Instalaciones.Instalacion;
import Usuarios.Socio;

public class Tienda {

	private static final double IVA = 0.19;
	
	private List<Articulo> articulos;

	public Tienda() {
		super();
		this.articulos = new ArrayList<>();
	}
	
	public void agregarArticulo(Articulo articulo) {
		articulos.add(articulo);
	}
	
	public void reabastecer(Articulo articulo, int cantidad) {
		for (int i = 0; i < cantidad; i++) {
			agregarArticulo(articulo);
		}
	}
	
	public Venta vender(Socio comprador, List<LineaVenta> lineas, Instalacion instalacionActual, CodigoDescuento codigoDescuento, int puntosARedimir, double propina) {
		Venta venta = new Venta(propina, puntosARedimir, Rubro.CAFETERIA, codigoDescuento, comprador, lineas, IVA);
		return venta;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		JSONArray jsonArticulos = new JSONArray();
	    for (Articulo articulo : articulos) {
	    	jsonArticulos.put(articulo.toJSON());
	    }
	    json.put("articulos", jsonArticulos);
		return json;
	}

	public static Tienda fromJSON(JSONObject json, Map<String, Producto> mapaProductos) {
		Tienda tienda = new Tienda();
		JSONArray jsonArticulos = json.optJSONArray("articulos");
		if (jsonArticulos != null) {
			for (int indice = 0; indice < jsonArticulos.length(); indice++) {
				JSONObject jsonArticulo = jsonArticulos.getJSONObject(indice);
				String nombre = jsonArticulo.getString("nombre");

				Articulo articulo = (Articulo) mapaProductos.get(nombre);
				if (articulo == null) {
					articulo = Articulo.fromJSON(jsonArticulo);
					mapaProductos.put(nombre, articulo);
				}
				tienda.agregarArticulo(articulo);
			}
		}
		return tienda;
	}
}
