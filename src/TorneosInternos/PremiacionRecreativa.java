package TorneosInternos;

import java.util.ArrayList;
import java.util.List;

import Tienda_Cafeteria_Inventario.BonoDescuento;

public class PremiacionRecreativa implements Premiacion{
	
	public static final double BONO_PRIMERO = 0.25;
	public static final double BONO_SEGUNDO = 0.1;
	
	public PremiacionRecreativa() {
		super();
	}

	@Override
	public double getTarifa(InscripcionTorneo inscripcion) {
		return 0;
	}

	@Override
	public List<PremioEntregado> entregarPremios(InscripcionTorneo primero, InscripcionTorneo segundo, double recaudado,
			String nombreTorneo) {
		List<PremioEntregado> premios = new ArrayList<>();
	    darBonos(premios, 1, primero, BONO_PRIMERO, nombreTorneo);
	    darBonos(premios, 2, segundo, BONO_SEGUNDO, nombreTorneo);
	    return premios;
	}
	private void darBonos(List<PremioEntregado> premios, int puesto, InscripcionTorneo inscripcion, double porcentaje,
	        String nombreTorneo) {
	    for (JugadorTorneo j : inscripcion.getJugadores()) {
	        BonoDescuento bono = new BonoDescuento(porcentaje, nombreTorneo);
	        j.agregarBono(bono);
	        premios.add(new PremioEntregado(puesto, j, 0, bono));
	    }
	}
	

}
