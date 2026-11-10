package TorneosInternos;

import java.util.List;

public interface Premiacion {

	double getTarifa(InscripcionTorneo inscripcion);
	
	List<PremioEntregado> entregarPremios(InscripcionTorneo primero, InscripcionTorneo segundo, double recaudado,
			String nombreTorneo);
}
