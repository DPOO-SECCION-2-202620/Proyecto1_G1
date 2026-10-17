package DeportesConjunto_Competencias;

public class Futbol extends DeporteConjunto{

	public Futbol() {
		super("Futbol",7);
	}

	@Override
	public int calcularPuntos(int marcadorPropio, int marcadorRival) {
		if (marcadorPropio > marcadorRival) {
			return 3;
		}
		if (marcadorPropio == marcadorRival) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean admiteEmpate() {
		return true;
	}

	
}
