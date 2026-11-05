package TorneosInternos;

import java.util.List;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.NivelJugador;

public interface JugadorTorneo {

	String getNombre();
	String getLogin();
	NivelJugador getNivelEn(DisciplinaIndividual disciplina);
	List<BonoDescuento> getBonos();
	void agregarBono(BonoDescuento bono);
	boolean esEmpleado();
}
