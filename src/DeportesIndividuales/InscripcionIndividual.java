package DeportesIndividuales;

import org.json.JSONObject;

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
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("disciplina", disciplina.getNombre());
	    json.put("nivel", nivel.name());
		return json;
	}
	
	public static InscripcionIndividual fromJSON(JSONObject json, DisciplinaIndividual disciplina) {
		return new InscripcionIndividual(
				disciplina,
				NivelJugador.valueOf(json.getString("nivel")));
	}
}
