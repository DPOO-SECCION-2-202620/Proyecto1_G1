package Clases;

public class Baloncesto extends DeporteConjunto{
	
	public Baloncesto() {
		super(5);
	}

	@Override
	public int calcularPuntos(int marcadorPropio, int marcadorRival) {
		if (marcadorPropio == marcadorRival) {
			throw new IllegalArgumentException("En baloncesto no puede haber empate");
		}
		if (marcadorPropio > marcadorRival) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean admiteEmpate() {
		return false;
	}
}
