package Tienda_Cafeteria_Inventario;

import java.util.List;

import Instalaciones.Instalacion;

public class Inventario {

	private Instalacion instalacion;
	
	private List<Articulo> articulos;
	
	private Ubicacion ubicacion;
	
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
}
