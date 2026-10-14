package Clases;

public abstract class Instalacion {

	private String nombre;
	private int capacidadMaxima;

	public Instalacion(String nombre, int capacidadMaxima) {
		this.nombre = nombre;
		this.capacidadMaxima = capacidadMaxima;
	}

	public String getNombre() {
		return nombre;
	}

	public int getCapacidadMaxima() {
		return capacidadMaxima;
	}

	public void setCapacidadMaxima(int capacidadMaxima) {
		this.capacidadMaxima = capacidadMaxima;
	}
}
