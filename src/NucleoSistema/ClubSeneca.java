package NucleoSistema;

import java.util.ArrayList;
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
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Tienda_Cafeteria_Inventario.CafeteriaSocios;
import Tienda_Cafeteria_Inventario.Inventario;
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
	
	private List<Empleado> empleado;
	
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
	
}
