package Clases;

public class InscripcionIndividual {

	private DisciplinaIndividual disciplina;
	private NivelJugador nivel;
	
	public InscripcionIndividual( DisciplinaIndividual disciplina, NivelJugador nivel) {
		this.disciplina = disciplina;
		this.nivel = nivel;
	}
	
	public void cambiarNivel(NivelJugador nuevoNivel) {
		if (nuevoNivel == null) {
			throw new IllegalArgumentException("El nivel no puede ser nulo");
		}
		this.nivel = nuevoNivel;
	}

	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}

	public NivelJugador getNivel() {
		return nivel;
	}
	
	
	
	
	
}
