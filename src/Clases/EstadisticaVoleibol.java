package Clases;

public class EstadisticaVoleibol extends EstadisticaConjunto{
	
	private int aces;
	private int bloqueos;
	
	public EstadisticaVoleibol(InscripcionEquipo jugador, int aces, int bloqueos) {
		super(jugador);
		this.aces = aces;
		this.bloqueos = bloqueos;
	}
	public int getAces() {
		return aces;
	}
	public int getBloqueos() {
		return bloqueos;
	}
	@Override
	public int calcularAporte() {
		return aces + bloqueos;
	}
	
	

}
