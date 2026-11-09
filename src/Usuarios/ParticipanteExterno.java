package Usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.NivelJugador;
import Tienda_Cafeteria_Inventario.BonoDescuento;
import TorneosInternos.JugadorTorneo;

public class ParticipanteExterno extends Usuario implements JugadorTorneo {

	public static final int EDAD_MINIMA = 5;
	public static final int EDAD_MAXIMA = 100;

	private int edad;
	private boolean estudiante;
	private List<InscripcionIndividual> niveles;
	private List<BonoDescuento> bonos;

	public ParticipanteExterno(int edad, String nombre, String login, String password, boolean estudiante) {
		super(nombre, login, password);
		this.edad = edad;
		this.estudiante = estudiante;
		this.niveles = new ArrayList<>();
		this.bonos = new ArrayList<>();
	}

	public void asignarNivel(DisciplinaIndividual disciplina, NivelJugador nivel) {
		if (disciplina == null || nivel == null) {
			throw new IllegalArgumentException("La disciplina y el nivel son obligatorios");
		}
		for (InscripcionIndividual existente : niveles) {
			if (existente.getDisciplina() == disciplina) {
				existente.cambiarNivel(nivel);
				return;
			}
		}
		niveles.add(new InscripcionIndividual(disciplina, nivel));
	}

	@Override
	public NivelJugador getNivelEn(DisciplinaIndividual disciplina) {
		for (InscripcionIndividual inscripcion : niveles) {
			if (inscripcion.getDisciplina() == disciplina) {
				return inscripcion.getNivel();
			}
		}
		return null;
	}

	@Override
	public List<BonoDescuento> getBonos() {
		return bonos;
	}

	@Override
	public void agregarBono(BonoDescuento bono) {
		bonos.add(bono);
	}

	@Override
	public boolean esEmpleado() {
		return false;
	}

	public boolean esEstudiante() {
		return estudiante;
	}

	public int getEdad() {
		return edad;
	}

	public List<InscripcionIndividual> getNiveles() {
		return niveles;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "ParticipanteExterno");
		json.put("edad", edad);
		json.put("estudiante", estudiante);

		JSONArray jsonNiveles = new JSONArray();
		for (InscripcionIndividual nivel : niveles) {
			jsonNiveles.put(nivel.toJSON());
		}
		json.put("niveles", jsonNiveles);

		JSONArray jsonBonos = new JSONArray();
		for (BonoDescuento bono : bonos) {
			jsonBonos.put(bono.toJSON());
		}
		json.put("bonos", jsonBonos);
		return json;
	}

	public static ParticipanteExterno fromJSON(JSONObject json, Map<String, DisciplinaIndividual> mapaDisciplinas) {
		ParticipanteExterno participante = new ParticipanteExterno(
				json.getInt("edad"),
				json.getString("nombre"),
				json.getString("login"),
				json.getString("password"),
				json.optBoolean("estudiante", false));

		JSONArray jsonNiveles = json.getJSONArray("niveles");
		for (int indice = 0; indice < jsonNiveles.length(); indice++) {
			JSONObject jsonNivel = jsonNiveles.getJSONObject(indice);
			DisciplinaIndividual disciplina = mapaDisciplinas.get(jsonNivel.getString("disciplina"));
			if (disciplina == null) {
				throw new IllegalStateException("Disciplina inexistente: " + jsonNivel.getString("disciplina"));
			}
			participante.niveles.add(InscripcionIndividual.fromJSON(jsonNivel, disciplina));
		}

		JSONArray jsonBonos = json.optJSONArray("bonos");
		if (jsonBonos != null) {
			for (int indice = 0; indice < jsonBonos.length(); indice++) {
				participante.bonos.add(BonoDescuento.fromJSON(jsonBonos.getJSONObject(indice)));
			}
		}
		return participante;
	}
}
