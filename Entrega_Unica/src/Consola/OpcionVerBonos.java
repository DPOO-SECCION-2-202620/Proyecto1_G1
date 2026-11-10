package Consola;

import java.util.List;

import Tienda_Cafeteria_Inventario.BonoDescuento;
import TorneosInternos.JugadorTorneo;

public class OpcionVerBonos implements OpcionMenu {

	private final JugadorTorneo socio;

	public OpcionVerBonos(JugadorTorneo socio, LectorConsola lector) {
		this.socio = socio;
	}

	@Override
	public String getTitulo() {
		return "Ver mis bonos de descuento";
	}

	@Override
	public void ejecutar() {
		List<BonoDescuento> bonos = socio.getBonos();
		if (bonos.isEmpty()) {
			System.out.println("Todavía no ha ganado bonos de descuento.");
			return;
		}
		System.out.println("\nSus bonos (redimibles en la tienda o la cafetería; no acumulables con otros descuentos):");
		for (int i = 0; i < bonos.size(); i++) {
			BonoDescuento bono = bonos.get(i);
			System.out.println((i + 1) + ". " + Math.round(bono.getPorcentaje() * 100) + "% de descuento - torneo \""
					+ bono.getTorneoOrigen() + "\" - " + (bono.estaUsado() ? "usado" : "disponible"));
		}
	}
}
