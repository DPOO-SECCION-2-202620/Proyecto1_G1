package NucleoSistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.Reserva;
import Entrenamiento.SesionEntrenamiento;
import Instalaciones.Instalacion;
import PartidosEquipo_Estadisticas.Partido;
import Usuarios.Usuario;

public class Historial {

	private List<Partido> partidos;
	
	private List<PartidoPractica> partidosPractica;
	
	private List<Reserva> reservas;
	
	private List<SesionEntrenamiento> sesiones;

	public Historial() {
		super();
		this.partidos = new ArrayList<>();
		this.partidosPractica = new ArrayList<>();
		this.reservas = new ArrayList<>();
		this.sesiones = new ArrayList<>();
	}

	public List<Partido> getPartidos() {
		return partidos;
	}

	public List<PartidoPractica> getPartidosPractica() {
		return partidosPractica;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public List<SesionEntrenamiento> getSesiones() {
		return sesiones;
	}

	public void agregarReserva(Reserva reserva) {
		reservas.add(reserva);
	}
	
	public void agregarPartido(Partido partido) {
		partidos.add(partido);
	}
	
	public void agregarPartidosPractica(PartidoPractica partidoPractica) {
		partidosPractica.add(partidoPractica);
	}
	
	public void agregarSesion(SesionEntrenamiento sesion) {
		sesiones.add(sesion);
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
		for (PartidoPractica partidoPractica : partidosPractica) {
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
	
	public static Historial fromJSON(JSONObject historialJson,
			Map<String, Usuario> mapaUsuarios,
			Map<String, Equipo> mapaEquipos,
			Map<String, DisciplinaIndividual> mapaDisciplinas,
			Map<String, Instalacion> mapaInstalaciones) {

		Historial historial = new Historial();

		JSONArray jsonReservas = historialJson.getJSONArray("reservas");
		for (int indice = 0; indice < jsonReservas.length(); indice++) {
			historial.reservas.add(Reserva.fromJSON(
					jsonReservas.getJSONObject(indice), mapaUsuarios, mapaInstalaciones));
		}

		JSONArray jsonPracticas = historialJson.getJSONArray("partidosPractica");
		for (int indice = 0; indice < jsonPracticas.length(); indice++) {
			historial.partidosPractica.add(PartidoPractica.fromJSON(
					jsonPracticas.getJSONObject(indice), mapaUsuarios, mapaDisciplinas, mapaInstalaciones));
		}

		JSONArray jsonSesiones = historialJson.getJSONArray("sesiones");
		for (int indice = 0; indice < jsonSesiones.length(); indice++) {
			historial.sesiones.add(SesionEntrenamiento.fromJSON(
					jsonSesiones.getJSONObject(indice), mapaUsuarios, mapaInstalaciones));
		}

		JSONArray jsonPartidos = historialJson.optJSONArray("partidos");
		if (jsonPartidos != null) {
			for (int indice = 0; indice < jsonPartidos.length(); indice++) {
				historial.partidos.add(Partido.fromJSON(
						jsonPartidos.getJSONObject(indice), mapaEquipos));
			}
		}

		return historial;
	}
}
