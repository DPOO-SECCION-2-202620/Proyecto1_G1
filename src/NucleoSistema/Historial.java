package NucleoSistema;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.Reserva;
import Entrenamiento.SesionEntrenamiento;
import PartidosEquipo_Estadisticas.Partido;

public class Historial {

	private List<Partido> partidos;
	
	private List<PartidoPractica> paridosPractica;
	
	private List<Reserva> reservas;
	
	private List<SesionEntrenamiento> sesiones;

	public Historial() {
		super();
		this.partidos = new ArrayList<>();
		this.paridosPractica = new ArrayList<>();
		this.reservas = new ArrayList<>();
		this.sesiones = new ArrayList<>();
	}

	public List<Partido> getPartidos() {
		return partidos;
	}

	public List<PartidoPractica> getParidosPractica() {
		return paridosPractica;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public List<SesionEntrenamiento> getSesiones() {
		return sesiones;
	}

	public void agregarReserva() {
		
	}
	
	public void agregarPartido() {
		
	}
	
	public void agregarParidosPractica() {
		
	}
	
	public void agregarSesion() {
		
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		JSONArray jsonReservas = new JSONArray();
		for (Reserva reserva : reservas) {
			jsonReservas.put(reserva.toJSON());
		}
		json.put("reservas", jsonReservas);

		JSONArray jsonPartidos = new JSONArray();
		for (Partido partido : partidos) {
			jsonPartidos.put(partido.toJSON());     
		}
		json.put("partidos", jsonPartidos);

		JSONArray jsonPractica = new JSONArray();
		for (PartidoPractica partidoPractica : paridosPractica) {
			jsonPractica.put(partidoPractica.toJSON());
		}
		json.put("partidosPractica", jsonPractica);

		JSONArray jsonSesiones = new JSONArray();
		for (SesionEntrenamiento sesion : sesiones) {
			jsonSesiones.put(sesion.toJSON());
		}
		json.put("sesiones", jsonSesiones);

		return json;
	}
	
}
