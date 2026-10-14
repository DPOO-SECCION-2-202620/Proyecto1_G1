package Clases;

public class CanchaPadel extends InstalacionIndividual{
	
	public CanchaPadel(String nombre, boolean techado, String salon, Padel padel) {
		super(nombre, 4, techado, salon, padel);
	}

	@Override
	public boolean admiteModalidad(Modalidad modalidad, int numeroJugadores) {
		if (modalidad == Modalidad.DOBLES && numeroJugadores == 4) {
			return true;
		}
		return false;
	}

	@Override
	public boolean permiteBebidaCaliente() {
		return false;
	}

}
