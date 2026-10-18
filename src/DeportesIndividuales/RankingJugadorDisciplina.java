package DeportesIndividuales;

import java.util.Map;

import org.json.JSONObject;

import Usuarios.Socio;
import Usuarios.Usuario;

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
	
	
	public void setPuntajeElo(double puntajeElo, boolean gano) {
		if (gano) {
			this.puntajeElo = puntajeElo;
		}
	}


	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("puntajeElo", puntajeElo);
	    json.put("jugador", jugador.getLogin());
	    json.put("disciplina", disciplina.getNombre());
		return json;
	}
	
	public static RankingJugadorDisciplina fromJSON(
			JSONObject json,
			Map<String, Usuario> mapaUsuarios,
			Map<String, DisciplinaIndividual> mapaDisciplinas) {

		String login = json.getString("jugador");
		String nombreDisciplina = json.getString("disciplina");

		Usuario usuario = mapaUsuarios.get(login);
		if (!(usuario instanceof Socio)) {
			throw new IllegalArgumentException("El jugador del ranking no es un socio válido: " + login);
		}

		DisciplinaIndividual disciplina = mapaDisciplinas.get(nombreDisciplina);
		if (disciplina == null) {
			throw new IllegalArgumentException("Disciplina desconocida en ranking: " + nombreDisciplina);
		}

		RankingJugadorDisciplina ranking = new RankingJugadorDisciplina((Socio) usuario, disciplina);
		ranking.puntajeElo = json.getDouble("puntajeElo");
		return ranking;
	}
}
