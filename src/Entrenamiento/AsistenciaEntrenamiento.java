package Entrenamiento;

import Usuarios.Socio;

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

	

	

	}
