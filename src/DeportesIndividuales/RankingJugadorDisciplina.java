package DeportesIndividuales;

import org.json.JSONObject;

import Usuarios.Socio;

public class RankingJugadorDisciplina {

	private double puntajeElo = 1000;
	private Socio jugador;
	private DisciplinaIndividual disciplina;
	
	public RankingJugadorDisciplina(Socio jugador, DisciplinaIndividual disciplina) {
		this.jugador = jugador;
		this.disciplina = disciplina;
	}
	

	public double getPuntajeElo() {
		return puntajeElo;
	}

	public Socio getJugador() {
		return jugador;
	}
	
	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("puntajeElo", puntajeElo);
	    json.put("jugador", jugador.getLogin());
	    json.put("disciplina", disciplina.getNombre());
		return json;
	}
}
