package NucleoSistema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Reserva;
import Entrenamiento.SesionEntrenamiento;
import Instalaciones.Instalacion;
import Instalaciones.InstalacionIndividual;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Tienda_Cafeteria_Inventario.CafeteriaSocios;
import Tienda_Cafeteria_Inventario.Inventario;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import Tienda_Cafeteria_Inventario.Tienda;
import Tienda_Cafeteria_Inventario.Venta;
import TurnosEmpleados.GestorTurnos;
import TurnosEmpleados.SolicitudCambioTurno;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class ClubSeneca {

	private String nombre;
	
	private List<DeporteConjunto> deportes;
	
	private List<Equipo> equipos;
	
	private Autenticador autenticador;
	
	private Persistencia persistencia;
	
	private List<SolicitudCambioTurno> solicitudes;
	
	private GeneradorReportes generadorReportes;
	
	private GestorTurnos gestorTurnos;
	
	private Historial historial;
	
	private List<SugerenciaArticulo> sugerencias;
	
	private List<Inventario> inventarios;
	
	private CafeteriaSocios cafeteria;
	
	private Tienda tienda;
	
	private List<Empleado> empleados;
	
	private List<Venta> ventas;
	
	private List<Usuario> usuarios;
	
	private List<Competencia> competencias;
	
	private List<DisciplinaIndividual> disciplinas;
	
	private List<RankingJugadorDisciplina> rankings;

	public ClubSeneca(String nombre) {
		super();
		this.nombre = nombre;
		
		this.usuarios = new ArrayList<>();
		this.deportes = new ArrayList<>();
		this.disciplinas = new ArrayList<>();
		this.rankings = new ArrayList<>();
		this.inventarios = new ArrayList<>();
		this.equipos = new ArrayList<>();
		this.competencias = new ArrayList<>();
		this.ventas = new ArrayList<>();
		this.solicitudes = new ArrayList<>();
		this.sugerencias = new ArrayList<>();

		this.tienda = new Tienda();
		this.cafeteria = new CafeteriaSocios();
		this.historial = new Historial();

		this.autenticador = new Autenticador();
		this.gestorTurnos = new GestorTurnos();
		this.generadorReportes = new GeneradorReportes();
		this.persistencia = new Persistencia("datos");
	}
	
	public boolean inscribirJugador() {
		
	}
	
	public PartidoInterno registrarPartidoInterno() {
		
	}

	public PartidoExterno registrarPartidoExterno() {
		
	}
	
	public boolean inscribirEquipoEnCompetencia() {
		
	}
	
	public Reserva reservar() {
		
	}
	
	public PartidoPractica programarPartidoPractica() {
		
	}
	
	public void finalizarPartidoPractica() {
		
	}
	
	public RankingJugadorDisciplina getRanking() {
		
	}
	
	public SesionEntrenamiento planearSesion() {
		
	}
	
	public void registrarAsistencia() {
		
	}
	
	public void registrarVenta() {
		
	}
	
	public void reabastecerTienda() {
		
	}
	
	public void reabasteceCafeteriaSocios() {
		
	}
	
	public boolean moverArticulo() {
		
	}
	
	public SugerenciaArticulo registrarSugerencia() {
		
	}
	
	public void resolverSugerencia() {
		
	}
	
	public void resolverSolicitud() {
		
	}
	
	public Usuario iniciarSesion() {
		
	}
	
	public Equipo crearEquipo() {
		
	}
	
	public boolean darDeBajaEquipo() {
		
	}
	
	public boolean darDeBajaSocio() {
		
	}
	
	public Map<Rubro, Double> generarReporteVentas() {
		
	}
	
	public List<String> generarReporteDeportivo(){
		
	}
	
	public void guardarDatos() {
		
	}
	
	public void cargarDatos() {
		
	}
	
	public Socio crearSocio() {
		
	}
	
	public InscripcionIndividual inscribirEnDisciplina() {
		
	}
	
	public void registrarSolicitudTurno() {
		
	}
	
	public void asignarTurno() {
		
	}
	
	public Inventario getInventario() {
		
	}
	
	public Historial getHistorial() {
		
	}
	
	public void setPersistencia(Persistencia persistencia) {
		this.persistencia = persistencia;
	}

	public JSONObject toJSON() {
		
	    JSONObject json = new JSONObject();
	    json.put("nombre", nombre);

	    JSONArray jsonUsuarios = new JSONArray();
	    for (Usuario usuario : usuarios) {
	        jsonUsuarios.put(usuario.toJSON());
	    }
	    json.put("usuarios", jsonUsuarios);

	    JSONArray jsonDeportes = new JSONArray();
	    for (DeporteConjunto deporte : deportes) {
	    	jsonDeportes.put(deporte.toJSON());
	    }
	    json.put("deportes", jsonDeportes);

	    JSONArray jsonDisciplinas = new JSONArray();
	    for (DisciplinaIndividual disciplina : disciplinas) {
	    	jsonDisciplinas.put(disciplina.toJSON());
	    }
	    json.put("disciplinas", jsonDisciplinas);
	    
	    JSONArray jsonRankings = new JSONArray();
	    for (RankingJugadorDisciplina ranking : rankings) {
	    	jsonRankings.put(ranking.toJSON());
	    }
	    json.put("rankings", jsonRankings);
	    
	    JSONArray jsonInventarios = new JSONArray();
	    for (Inventario inventario : inventarios) {
	    	jsonInventarios.put(inventario.toJSON());
	    }
	    json.put("inventarios", jsonInventarios);
	    
	    json.put("tienda", tienda.toJSON());
	    
	    json.put("cafeteria", cafeteria.toJSON());
	    
	    JSONArray jsonEquipos = new JSONArray();
	    for (Equipo equipo : equipos) {
	    	jsonEquipos.put(equipo.toJSON());
	    }
	    json.put("equipos", jsonEquipos);
	    
	    JSONArray jsonCompetencias = new JSONArray();
	    for (Competencia competencia : competencias) {
	    	jsonCompetencias.put(competencia.toJSON());
	    }
	    json.put("competencias", jsonCompetencias);
	    
	    JSONArray jsonVentas = new JSONArray();
	    for (Venta venta : ventas) {
	    	jsonVentas.put(venta.toJSON());
	    }
	    json.put("ventas", jsonVentas);
	    
	    JSONArray jsonSolicitudes = new JSONArray();
	    for (SolicitudCambioTurno solicitud : solicitudes) {
	    	jsonSolicitudes.put(solicitud.toJSON());
	    }
	    json.put("solicitudes", jsonSolicitudes);
	    
	    JSONArray jsonSugerencias = new JSONArray();
	    for (SugerenciaArticulo sugerencia : sugerencias) {
	    	jsonSugerencias.put(sugerencia.toJSON());
	    }
	    json.put("sugerencias", jsonSugerencias);
	    
	    json.put("historial", historial.toJSON());
	    
	    return json;
	}
	
	public static ClubSeneca fromJSON(JSONObject json) {
		ClubSeneca club = new ClubSeneca(json.getString("nombre"));

		Map<String, Usuario> mapaUsuarios = new HashMap<>();
		Map<String, DeporteConjunto> mapaDeportes = new HashMap<>();
		Map<String, DisciplinaIndividual> mapaDisciplinas = new HashMap<>();
		Map<String, Instalacion> mapaInstalaciones = new HashMap<>();
		Map<String, Producto> mapaProductos = new HashMap<>();
		Map<String, Equipo> mapaEquipos = new HashMap<>();

		JSONArray jsonDisciplinas = leerLista(json, "disciplinas");
		for (int indice = 0; indice < jsonDisciplinas.length(); indice++) {
			DisciplinaIndividual disciplina = DisciplinaIndividual.fromJSON(jsonDisciplinas.getJSONObject(indice));
			club.disciplinas.add(disciplina);
			mapaDisciplinas.put(disciplina.getNombre(), disciplina);
			for (InstalacionIndividual instalacion : disciplina.getInstalaciones()) {
				mapaInstalaciones.put(instalacion.getNombre(), instalacion);
			}
		}
		
		JSONArray jsonUsuarios = leerLista(json, "usuarios");
		for (int indice = 0; indice < jsonUsuarios.length(); indice++) {
			Usuario usuario = Usuario.fromJSON(jsonUsuarios.getJSONObject(indice), mapaDisciplinas);
			club.usuarios.add(usuario);
			mapaUsuarios.put(usuario.getLogin(), usuario);
		}

		JSONArray jsonDeportes = leerLista(json, "deportes");
		for (int indice = 0; indice < jsonDeportes.length(); indice++) {
			DeporteConjunto deporte = DeporteConjunto.fromJSON(jsonDeportes.getJSONObject(indice), mapaDeportes);
			club.deportes.add(deporte);
			mapaDeportes.put(deporte.getNombre(), deporte);
		}

		JSONArray jsonRankings = leerLista(json, "rankings");
		for (int indice = 0; indice < jsonRankings.length(); indice++) {
			club.rankings.add(RankingJugadorDisciplina.fromJSON(jsonRankings.getJSONObject(indice), mapaUsuarios, mapaDisciplinas));
		}

		if (json.has("tienda")) {
			club.tienda = Tienda.fromJSON(json.getJSONObject("tienda"), mapaProductos);
		}
		if (json.has("cafeteria")) {
			club.cafeteria = CafeteriaSocios.fromJSON(json.getJSONObject("cafeteria"), mapaProductos);
		}

		JSONArray jsonInventarios = leerLista(json, "inventarios");
		for (int indice = 0; indice < jsonInventarios.length(); indice++) {
			Inventario inventario = Inventario.fromJSON(jsonInventarios.getJSONObject(indice), mapaProductos, mapaInstalaciones);
			club.inventarios.add(inventario);
			Instalacion instalacion = inventario.getInstalacion();
			mapaInstalaciones.put(instalacion.getNombre(), instalacion);
		}

		JSONArray jsonEquipos = leerLista(json, "equipos");
		for (int indice = 0; indice < jsonEquipos.length(); indice++) {
			Equipo equipo = Equipo.fromJSON(jsonEquipos.getJSONObject(indice), mapaDeportes, mapaUsuarios);
			club.equipos.add(equipo);
			mapaEquipos.put(equipo.getNombre(), equipo);
		}

		JSONArray jsonCompetencias = leerLista(json, "competencias");
		for (int indice = 0; indice < jsonCompetencias.length(); indice++) {
			club.competencias.add(Competencia.fromJSON(
					jsonCompetencias.getJSONObject(indice), mapaDeportes, mapaEquipos));
		}

		JSONArray jsonVentas = leerLista(json, "ventas");
		for (int indice = 0; indice < jsonVentas.length(); indice++) {
			club.ventas.add(Venta.fromJSON(jsonVentas.getJSONObject(indice), mapaUsuarios, mapaProductos));
		}

		JSONArray jsonSolicitudes = leerLista(json, "solicitudes");
		for (int indice = 0; indice < jsonSolicitudes.length(); indice++) {
			club.solicitudes.add(SolicitudCambioTurno.fromJSON(jsonSolicitudes.getJSONObject(indice), mapaUsuarios));
		}

		JSONArray jsonSugerencias = leerLista(json, "sugerencias");
		for (int indice = 0; indice < jsonSugerencias.length(); indice++) {
			club.sugerencias.add(SugerenciaArticulo.fromJSON(jsonSugerencias.getJSONObject(indice)));
		}

		if (json.has("historial")) {
			club.historial = Historial.fromJSON(json.getJSONObject("historial"),
					mapaUsuarios, mapaEquipos, mapaDisciplinas, mapaInstalaciones);
		}

		return club;
	}
	
	private static JSONArray leerLista(JSONObject json, String clave) {
		JSONArray lista = json.optJSONArray(clave);         
		return lista != null ? lista : new JSONArray();    
	}
	
}
