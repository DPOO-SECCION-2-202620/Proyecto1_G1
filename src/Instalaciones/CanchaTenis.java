package Instalaciones;

import DeportesIndividuales.Tenis;

public class CanchaTenis extends InstalacionIndividual{

	public CanchaTenis(String nombre, int capacidadMaxima, boolean techado, int salon,
			Tenis tenis) {
		super(nombre, 4, techado, salon, tenis);
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return true;
	}
	
}
