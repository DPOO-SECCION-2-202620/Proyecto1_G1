package DeportesIndividuales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import Excepciones.ReglaNegocioException;
import Instalaciones.Instalacion;
import Instalaciones.InstalacionIndividual;
import Usuarios.Socio;
import Usuarios.Usuario;

public class PartidoPractica {

	private LocalDate fecha;
	private boolean finalizado;
	private DisciplinaIndividual disciplina;
	private List<Socio> jugadores;
	private InstalacionIndividual instalacion;
	private List<EstadisticaPractica> estadisticas;
	
	
	public PartidoPractica(LocalDate fecha, DisciplinaIndividual disciplina, List<Socio> jugadores,
			InstalacionIndividual instalacion) {
		if (jugadores.size() != 2 && jugadores.size() != 4) {
			throw new IllegalArgumentException("Un partido de práctica tiene 2 o 4 jugadores");
		}
		this.fecha = fecha;
		this.finalizado = false;
		this.disciplina = disciplina;
		this.jugadores = new ArrayList <>(jugadores);
		this.instalacion = instalacion;
		this.estadisticas = new ArrayList <>();
	}


	public LocalDate getFecha() {
		return fecha;
	}


	public boolean isFinalizado() {
		return finalizado;
	}


	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}


	public List<Socio> getJugadores() {
		return jugadores;
	}


	public InstalacionIndividual getInstalacion() {
		return instalacion;
	}


	public List<EstadisticaPractica> getEstadisticas() {
		return estadisticas;
	}
	
	public boolean validarNiveles() {
		for (Socio jugador : jugadores) {
			if(jugador.esEntrenador()) {
				return true;
			}
		}
		for (Socio a: jugadores) {
			for(Socio b : jugadores) {
				NivelJugador nivelA = a.getNivelEn(disciplina);
				NivelJugador nivelB = b.getNivelEn(disciplina);
				if (!nivelA.esCompatibleCon(nivelB)) {
					return false;
				}
			}
		}
		return true;
	}

	public void finalizar(List<EstadisticaPractica> estadisticas) throws ReglaNegocioException {
		if (finalizado == true) {
	        throw new ReglaNegocioException("El partido ya está finalizado");
	    }
	    if (estadisticas.size() != jugadores.size()) {
	        throw new ReglaNegocioException("Debe haber una estadística por jugador");
	    }
	    for (EstadisticaPractica estadistica : estadisticas) {
	        if (!jugadores.contains(estadistica.getJugador())) {
	            throw new ReglaNegocioException(estadistica.getJugador().getNombre() + " no juega este partido");
	        }
	    }
	    this.estadisticas.addAll(estadisticas);
	    this.finalizado = true;
	}

	public Socio getGanador() {
		EstadisticaPractica mejor = null;
	    for (EstadisticaPractica estadistica : estadisticas) {
	        if (mejor == null || estadistica.getSetsGanados() > mejor.getSetsGanados()) {
	            mejor = estadistica;
	        }
	    }
	    if (mejor == null) {
	        return null;
	    }
	    return mejor.getJugador();
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("fecha", fecha);
	    json.put("finalizado", finalizado);
	    json.put("disciplina", disciplina.getNombre());
	    JSONArray jsonJugadores = new JSONArray();
	    for (Socio jugador : jugadores) {
	    	jsonJugadores.put(jugador.getLogin());
	    }
	    json.put("jugadores", jsonJugadores);
	    json.put("instalacion", instalacion.getNombre());
	    JSONArray jsonEstadisticas = new JSONArray();
	    for (EstadisticaPractica estadistica : estadisticas) {
	    	jsonEstadisticas.put(estadistica.toJSON());
	    }
	    json.put("estadisticas", jsonEstadisticas);
		return json;
	}
	
	public static PartidoPractica fromJSON(JSONObject json,
			Map<String, Usuario> mapaUsuarios,
			Map<String, DisciplinaIndividual> mapaDisciplinas,
			Map<String, Instalacion> mapaInstalaciones) {

		String nombreDisciplina = json.getString("disciplina");
		DisciplinaIndividual disciplina = mapaDisciplinas.get(nombreDisciplina);
		if (disciplina == null) {
			throw new IllegalArgumentException("Disciplina desconocida en el partido: " + nombreDisciplina);
		}

		String nombreInstalacion = json.getString("instalacion");
		Instalacion instalacion = mapaInstalaciones.get(nombreInstalacion);
		if (!(instalacion instanceof InstalacionIndividual)) {
			throw new IllegalArgumentException("Instalación individual desconocida: " + nombreInstalacion);
		}

		List<Socio> jugadores = new ArrayList<>();
		JSONArray jsonJugadores = json.getJSONArray("jugadores");
		for (int indice = 0; indice < jsonJugadores.length(); indice++) {
			String loginJugador = jsonJugadores.getString(indice);
			Usuario usuario = mapaUsuarios.get(loginJugador);
			if (!(usuario instanceof Socio)) {
				throw new IllegalArgumentException("Jugador no válido en el partido: " + loginJugador);
			}
			jugadores.add((Socio) usuario);
		}

		PartidoPractica partido = new PartidoPractica(
				LocalDate.parse(json.getString("fecha")),
				disciplina,
				jugadores,
				(InstalacionIndividual) instalacion);
		partido.finalizado = json.getBoolean("finalizado");

		JSONArray jsonEstadisticas = json.optJSONArray("estadisticas");
		if (jsonEstadisticas != null) {
			for (int indice = 0; indice < jsonEstadisticas.length(); indice++) {
				partido.estadisticas.add(EstadisticaPractica.fromJSON(jsonEstadisticas.getJSONObject(indice), mapaUsuarios));
			}
		}
		return partido;
	}
}
