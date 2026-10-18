package DeportesConjunto_Competencias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import PartidosEquipo_Estadisticas.EstadisticaVoleibol;
import PartidosEquipo_Estadisticas.PartidoExterno;
import Usuarios.Socio;

public abstract class Competencia {

	private String nombre;
	private String entidadOrganizadora;
	private int anio;
	private String formatoJuego;
	private int nominaMinima;
	private Categoria categoria;
	private DeporteConjunto deporte;
	private List<InscripcionCompetencia> inscripciones;
	private List<PartidoExterno> listaPartidos;
	
	public Competencia(String nombre, String entidadOrganizadora, int anio, String formatoJuego, int nominaMinima,
			Categoria categoria, DeporteConjunto deporte) {
		this.nombre = nombre;
		this.entidadOrganizadora = entidadOrganizadora;
		this.anio = anio;
		this.formatoJuego = formatoJuego;
		this.nominaMinima = nominaMinima;
		this.categoria = categoria;
		this.deporte = deporte;
		this.inscripciones = new ArrayList<>();
		this.listaPartidos = new ArrayList<>();
	}


	public String getNombre() {
		return nombre;
	}

	public String getEntidadOrganizadora() {
		return entidadOrganizadora;
	}

	public int getAnio() {
		return anio;
	}

	public String getFormatoJuego() {
		return formatoJuego;
	}

	public int getNominaMinima() {
		return nominaMinima;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public DeporteConjunto getDeporte() {
		return deporte;
	}

	public List<InscripcionCompetencia> getInscripciones() {
		return inscripciones;
	}

	public List<PartidoExterno> getListaPartidos() {
		return listaPartidos;
	}

	public boolean equipoCumpleRequisitos(Equipo equipo) {
		if (equipo.getDeporte() != deporte) {
	        return false;
	    }
	    if (equipo.getCategoria() != categoria) {
	        return false;
	    }
	    return equipo.contarJugadoresHabilitados() >= nominaMinima;
	}

	public boolean inscribirEquipo(Equipo equipo) {
		if (getInscripcionDe(equipo) != null) {
	        return false; 
	    }
	    if (!equipoCumpleRequisitos(equipo)) {
	        return false; 
	    }
	    inscripciones.add(new InscripcionCompetencia(equipo, this));
	    return true;
	}

	public InscripcionCompetencia getInscripcionDe(Equipo equipo) {
		for (InscripcionCompetencia inscripcion : inscripciones) {
			if (inscripcion.getEquipo() == equipo) {
				return inscripcion;
			}
		}
		return null;
	}

	public abstract void registrarPartido(PartidoExterno partido);

	protected void agregarALista(PartidoExterno partido) {
		listaPartidos.add(partido);
	}

	public Socio getGoleador() {
		Map<Socio, Integer> anotaciones = new HashMap<>();
		for (PartidoExterno partido : listaPartidos) {
	        for (EstadisticaConjunto estadistica : partido.getEstadisticas()) {
	            int valor = 0;
	            if (estadistica instanceof EstadisticaFutbol) {
	                EstadisticaFutbol futbol = (EstadisticaFutbol) estadistica;
	                valor = futbol.getGoles();
	            } else if (estadistica instanceof EstadisticaBaloncesto) {
	                EstadisticaBaloncesto baloncesto = (EstadisticaBaloncesto) estadistica;
	                valor = baloncesto.getPuntos();
	            } else if (estadistica instanceof EstadisticaVoleibol) {
	                EstadisticaVoleibol voleibol = (EstadisticaVoleibol) estadistica;
	                valor = voleibol.getAces() + voleibol.getBloqueos();
	            }

	            Socio socio = estadistica.getJugador().getJugador();
	            anotaciones.put(socio, anotaciones.getOrDefault(socio, 0) + valor);
	        }
	    }
		Socio mejor = null;
	    for (Socio socio : anotaciones.keySet()) {
	        if (mejor == null || anotaciones.get(socio) > anotaciones.get(mejor)) {
	            mejor = socio;
	        }
	    }
	    return mejor;
	}

	public Socio getJugadorDestacado() {
		Map<Socio, Integer> aportes = new HashMap<>();
		for (PartidoExterno partido : listaPartidos) {
	        for (EstadisticaConjunto estadistica : partido.getEstadisticas()) {
	            Socio socio = estadistica.getJugador().getJugador();
	            aportes.put(socio, aportes.getOrDefault(socio, 0) + estadistica.calcularAporte());
	        }
	    }
	    Socio mejor = null;
	    for (Socio socio : aportes.keySet()) {
	        if (mejor == null || aportes.get(socio) > aportes.get(mejor)) {
	            mejor = socio;
	        }
	    }
	    return mejor;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("nombre", nombre);
		json.put("entidadOrganizadora", entidadOrganizadora);
		json.put("anio", anio);
		json.put("formatoJuego", formatoJuego);
		json.put("nominaMinima", nominaMinima);
		json.put("categoria", categoria.name());
		json.put("deporte", deporte.toJSON());
		JSONArray jsonInscripciones = new JSONArray();
		for (InscripcionCompetencia inscripcion : inscripciones) {
			jsonInscripciones.put(inscripcion.toJSON());
		}
		json.put("inscripciones", jsonInscripciones);
		JSONArray jsonListaPartidos = new JSONArray();
		for (PartidoExterno partido : listaPartidos) {
			jsonListaPartidos.put(partido.toJSON());
		}
		json.put("listaPartidos", jsonListaPartidos);
		return json;
	}

	public static Competencia fromJSON(JSONObject json,
			Map<String, DeporteConjunto> mapaDeportes,
			Map<String, Equipo> mapaEquipos) {

		DeporteConjunto deporte = DeporteConjunto.fromJSON(json.getJSONObject("deporte"), mapaDeportes);
		Categoria categoria = Categoria.valueOf(json.getString("categoria"));

		Competencia competencia;
		String tipo = json.getString("tipo");
		switch (tipo) {
			case "ASCUN":
				competencia = ASCUN.fromJSON(json, categoria, deporte);
				break;
			case "TorneoCerros":
				competencia = TorneoCerros.fromJSON(json, categoria, deporte, mapaEquipos);
				break;
			default:
				throw new IllegalArgumentException("Tipo de competencia desconocido: " + tipo);
		}

		JSONArray jsonInscripciones = json.optJSONArray("inscripciones");
		if (jsonInscripciones != null) {
			for (int indice = 0; indice < jsonInscripciones.length(); indice++) {
				competencia.inscripciones.add(InscripcionCompetencia.fromJSON(
						jsonInscripciones.getJSONObject(indice), competencia, mapaEquipos));
			}
		}

		JSONArray jsonPartidos = json.optJSONArray("listaPartidos");
		if (jsonPartidos != null) {
			for (int indice = 0; indice < jsonPartidos.length(); indice++) {
				PartidoExterno partido = PartidoExterno.fromJSON(jsonPartidos.getJSONObject(indice), mapaEquipos);
				competencia.listaPartidos.add(partido);
			}
		}
		return competencia;
	}
}
