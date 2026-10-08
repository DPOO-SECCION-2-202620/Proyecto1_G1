package Clases;

public class InscripcionIndividual {

	private Socio jugador;
	private DisciplinaIndividual disciplina;
	private NivelJugador nivel;
	
	public InscripcionIndividual(Socio jugador, DisciplinaIndividual disciplina, NivelJugador nivel) {
		super();
		this.jugador = jugador;
		this.disciplina = disciplina;
		this.nivel = nivel;
	}
	
	public void cambiarNivel(NivelJugador nuevoNivel) {
		if (nuevoNivel == null) {
			throw new IllegalArgumentException("El nivel no puede ser nulo");
		}
		this.nivel = nuevoNivel;
	}

	public Socio getJugador() {
		return jugador;
	}

	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}

	public NivelJugador getNivel() {
		return nivel;
	}
	
	
	
	
	
}
