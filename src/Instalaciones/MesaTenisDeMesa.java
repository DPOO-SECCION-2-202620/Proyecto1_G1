package Instalaciones;

import DeportesIndividuales.TenisDeMesa;

public class MesaTenisDeMesa extends InstalacionIndividual{

	public MesaTenisDeMesa(String nombre, int capacidadMaxima, boolean techado, int salon,
			TenisDeMesa tenisDeMesa) {
		super(nombre, 4, techado, salon, tenisDeMesa);
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return !isTechado();
	}

}
