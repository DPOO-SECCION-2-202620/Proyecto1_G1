package Clases;

public class RankingJugadorDisciplina {

	private double puntajeElo = 1000;
	private Socio jugador;
	private DisciplinaIndividual disciplina;
	
	public RankingJugadorDisciplina(double puntajeElo, Socio jugador, DisciplinaIndividual disciplina) {
		this.puntajeElo = puntajeElo;
		this.jugador = jugador;
		this.disciplina = disciplina;
	}
	
	

	public double getPuntajeElo() {
		return puntajeElo;
	}

	public Socio getJugador() {
		return jugador;
	}
	
		
}
