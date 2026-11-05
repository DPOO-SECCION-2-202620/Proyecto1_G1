package TorneosInternos;

import java.util.List;

import Excepciones.ReglaNegocioException;

public class PremiacionCompetitiva implements Premiacion{

	public static final double PORC_PRIMERO = 0.5;
	public static final double PORC_SEGUNDO = 0.2;
	private double tarifa;
	
	public PremiacionCompetitiva(double tarifa) throws ReglaNegocioException {
		this.tarifa = tarifa;
	}
	
	public double getTarifaBase() {
		return tarifa;
	}
	
	@Override
	public double getTarifa(InscripcionTorneo inscripcion) {
		if (inscripcion instanceof InscripcionTorneoEquipo) {
	        return tarifa;
	    }
	    double total = 0;
	    for (JugadorTorneo j : inscripcion.getJugadores()) {
	        if (!j.esEmpleado()) {
	            total += tarifa;
	        }
	    }
	    return total;
	}
	
	
	@Override
	public List<PremioEntregado> entregarPremios(InscripcionTorneo primero, InscripcionTorneo segundo, double recaudado,
			String nombreTorneo) {
		List<PremioEntregado> premios = new ArrayList<>();
	    repartir(premios, 1, primero, recaudado * PORC_PRIMERO);
	    repartir(premios, 2, segundo, recaudado * PORC_SEGUNDO);
	    return premios;
	}
	
	private void repartir(List<PremioEntregado> premios, int puesto, InscripcionTorneo inscripcion, double premio) {
		List<JugadorTorneo> jugadores = inscripcion.getJugadores();
	    double parte = premio / jugadores.size();
	    for (JugadorTorneo j : jugadores) {
	        double monto;
	        if (j.esEmpleado()) {
	            monto = 0;
	        } else {
	            monto = parte;
	        }
	        premios.add(new PremioEntregado(puesto, j, monto, null));
	    }
	}
	
}
