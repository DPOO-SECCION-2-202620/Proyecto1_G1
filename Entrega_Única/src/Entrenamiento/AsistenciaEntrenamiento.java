package Entrenamiento;

import java.util.Map;

import org.json.JSONObject;

import Usuarios.Socio;
import Usuarios.Usuario;

public class AsistenciaEntrenamiento {

	private int evaluacion;
	private String comentario;
	private EstadoAsistencia estado;
	private Socio jugador;
	
	

	public AsistenciaEntrenamiento(int evaluacion, String comentario, EstadoAsistencia estado, Socio jugador) {
		this.evaluacion = evaluacion;
		this.comentario = comentario;
		this.estado = estado;
		this.jugador = jugador;
	}
	
	public boolean registrarEvaluacion(int evaluacion, String comentario) {
		if (evaluacion < 1 || evaluacion > 5 || estado == EstadoAsistencia.FALTO) {
			return false;
		}
		this.evaluacion = evaluacion;
		this.comentario = comentario;
		return true;
	}
	

	public int getEvaluacion() {
		return evaluacion;
	}

	public String getComentario() {
		return comentario;
	}

	public EstadoAsistencia getEstado() {
		return estado;
	}

	public Socio getJugador() {
		return jugador;
	}
	
	@Override
	public String toString() {
		String texto = jugador.getNombre() + ": " + estado;
		if (evaluacion != 0) {
			texto += " | nota " + evaluacion + " - " + comentario;
		}
		return texto;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("jugador", jugador.getLogin());
		json.put("estado", estado.name());
		json.put("evaluacion", evaluacion);
		if (comentario != null) {
			json.put("comentario", comentario);
		}
		return json;
	}

	public static AsistenciaEntrenamiento fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios) {
		String loginJugador = json.getString("jugador");
		Usuario usuario = mapaUsuarios.get(loginJugador);
		if (!(usuario instanceof Socio)) {
			throw new IllegalArgumentException("Jugador de asistencia no válido: " + loginJugador);
		}

		String comentario = null;
		if (json.has("comentario")) {
			comentario = json.getString("comentario");
		}

		return new AsistenciaEntrenamiento(
				json.getInt("evaluacion"),
				comentario,
				EstadoAsistencia.valueOf(json.getString("estado")),
				(Socio) usuario);
	}

	

	}
