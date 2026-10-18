package DeportesIndividuales;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.json.JSONObject;

import Instalaciones.Instalacion;
import Instalaciones.InstalacionIndividual;
import Usuarios.Socio;
import Usuarios.Usuario;

public class Reserva {

	private int numeroJugadores;
	private LocalTime horaInicio;
	private LocalTime horaFinal;
	private LocalDate fecha;
	private Modalidad modalidad;
	private Socio jugador;
	private InstalacionIndividual instalacion;
	
	public Reserva(int numeroJugadores, LocalTime horaInicio, LocalTime horaFinal, LocalDate fecha, Modalidad modalidad,
			Socio jugador, InstalacionIndividual instalacion) {
		this.numeroJugadores = numeroJugadores;
		this.horaInicio = horaInicio;
		this.horaFinal = horaFinal;
		this.fecha = fecha;
		this.modalidad = modalidad;
		this.jugador = jugador;
		this.instalacion = instalacion;
	}

	public int getNumeroJugadores() {
		return numeroJugadores;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFinal() {
		return horaFinal;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Modalidad getModalidad() {
		return modalidad;
	}

	public InstalacionIndividual getInstalacion() {
		return instalacion;
	}
	
	public Socio getJugador() {
		return jugador;
	}
	
	
	public boolean seSolapaCon (LocalDate fecha,LocalTime horaInicio,LocalTime horaFinal) {
		return false;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("numeroJugadores", numeroJugadores);
		json.put("horaInicio", horaInicio.toString());
		json.put("horaFinal", horaFinal.toString());
		json.put("fecha", fecha.toString());
		json.put("modalidad", modalidad.name());
		json.put("jugador", jugador.getLogin());
		json.put("instalacion", instalacion.getNombre());
		return json;
	}

	public static Reserva fromJSON(JSONObject json,
			Map<String, Usuario> mapaUsuarios,
			Map<String, Instalacion> mapaInstalaciones) {

		String loginJugador = json.getString("jugador");
		Usuario usuario = mapaUsuarios.get(loginJugador);
		if (!(usuario instanceof Socio)) {
			throw new IllegalArgumentException("El jugador de la reserva no es un socio válido: " + loginJugador);
		}

		String nombreInstalacion = json.getString("instalacion");
		Instalacion instalacion = mapaInstalaciones.get(nombreInstalacion);
		if (!(instalacion instanceof InstalacionIndividual)) {
			throw new IllegalArgumentException("Instalación individual desconocida: " + nombreInstalacion);
		}

		return new Reserva(
				json.getInt("numeroJugadores"),
				LocalTime.parse(json.getString("horaInicio")),
				LocalTime.parse(json.getString("horaFinal")),
				LocalDate.parse(json.getString("fecha")),
				Modalidad.valueOf(json.getString("modalidad")),
				(Socio) usuario,
				(InstalacionIndividual) instalacion);
	}
	
}


