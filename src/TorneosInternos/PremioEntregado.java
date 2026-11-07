package TorneosInternos;

public class PremioEntregado {
	 
	private int puesto;
	private JugadorTorneo jugador;
	private double montoEfectivo;
	private BonoDescuento bono;
 
	public PremioEntregado(int puesto, JugadorTorneo jugador, double montoEfectivo, BonoDescuento bono) {
		this.puesto = puesto;
		this.jugador = jugador;
		this.montoEfectivo = montoEfectivo;
		this.bono = bono;
	}
 
	public int getPuesto() {
		return puesto;
	}
 
	public JugadorTorneo getJugador() {
		return jugador;
	}
 
	public double getMontoEfectivo() {
		return montoEfectivo;
	}
 
	public BonoDescuento getBono() {
		return bono;
	}
}