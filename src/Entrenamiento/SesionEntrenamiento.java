package Entrenamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Instalaciones.Instalacion;
import Usuarios.Entrenador;
import Usuarios.Socio;


public class SesionEntrenamiento {
	private LocalDate fecha;
	private LocalTime hora;
	private int duracionMinutos;
	private Entrenador entrenador;
	private Instalacion instalacion;
	private List<Socio> convocados;
	private List<AsistenciaEntrenamiento> asistencias;
	
	
	public SesionEntrenamiento(LocalDate fecha, LocalTime hora, int duracionMinutos, Entrenador entrenador,
			Instalacion instalacion, List<Socio> convocados) {
		super();
		this.fecha = fecha;
		this.hora = hora;
		this.duracionMinutos = duracionMinutos;
		this.entrenador = entrenador;
		this.instalacion = instalacion;
		this.convocados = new ArrayList<>(convocados);;
		this.asistencias = new ArrayList<>();
	}


	public LocalDate getFecha() {
		return fecha;
	}


	public LocalTime getHora() {
		return hora;
	}


	public int getDuracionMinutos() {
		return duracionMinutos;
	}


	public Entrenador getEntrenador() {
		return entrenador;
	}


	public Instalacion getInstalacion() {
		return instalacion;
	}


	public List<Socio> getConvocados() {
		return convocados;
	}


	public List<AsistenciaEntrenamiento> getAsistencias() {
		return asistencias;
	}
	
	public boolean seSolapaCon(LocalDate fecha, LocalTime hora, int duracionMinutos) {
		LocalTime fin = this.hora.plusMinutes(this.duracionMinutos);
		LocalTime otroFin = hora.plusMinutes(duracionMinutos);
		return this.fecha.equals(fecha) && this.hora.isBefore(otroFin) && hora.isBefore(fin);
	}

	public boolean registrarAsistencia(Socio socio, EstadoAsistencia estado, int evaluacion, String comentario) {
		if (!convocados.contains(socio)) {
			return false;
		}
		for (AsistenciaEntrenamiento asistencia : asistencias) {
			if (asistencia.getJugador() == socio) {
				return false;
			}
		}
		
		AsistenciaEntrenamiento nueva = new AsistenciaEntrenamiento (0,null,estado,socio);
		if (evaluacion != 0) {
			if(!nueva.registrarEvaluacion(evaluacion,comentario)) {
				return false;
			}
		}
		asistencias.add(nueva);
		return true;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("fecha", fecha.toString());
	    json.put("hora", hora.toString());
	    json.put("duracionMinutos", duracionMinutos);
	    json.put("entrenador", entrenador.getLogin());
	    json.put("instalacion", instalacion.getNombre());
	    JSONArray jsonConvocados = new JSONArray();
	    for (Socio convocado : convocados) {
	    	jsonConvocados.put(convocado.getLogin());
	    }
	    json.put("convocados", jsonConvocados);
	    JSONArray jsonAsistencias = new JSONArray();
	    for (AsistenciaEntrenamiento asistencia : asistencias) {
	    	jsonAsistencias.put(asistencia.toJSON());
	    }
	    json.put("asistencias", jsonAsistencias);
		return json;
	}
	
}
