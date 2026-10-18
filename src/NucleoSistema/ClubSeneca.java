package NucleoSistema;

import java.util.List;
import java.util.Map;

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
	
	private List<Empleados> empleado;
	
	private List<Venta> ventas;
	
	private List<Usuario> usuarios;
	
	private List<Competencia> competencias;
	
	private List<DisciplinaIndividual> disciplinas;

	public ClubSeneca(String nombre) {
		super();
		this.nombre = nombre;
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
	
	
	
}
