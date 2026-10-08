package Clases;

public class Voleibol extends DeporteConjunto{

	public Voleibol() {
		super(6);
	}

	@Override
	public int calcularPuntos(int marcadorPropio, int marcadorRival) {
		if (marcadorPropio == marcadorRival) {
			throw new IllegalArgumentException("En voleibol no puede haber empate");
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
