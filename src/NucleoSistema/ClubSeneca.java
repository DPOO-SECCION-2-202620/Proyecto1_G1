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
	
	public boolean inscribirJugador(Socio socio, Equipo equipo, String posicion, int numeroCamiseta) {
		if (socio == null || equipo == null) {
			return false;
		}
		if (equipo.getInscripciones().size() >= equipo.getCupoMaximo()) {
			return false;
		}
		if (equipo.camisetaOcupada(numeroCamiseta)) {
			return false;
		}
		if (!equipo.getCategoria().admiteEdad(socio.getEdad())) {
			return false;
		}
		for (DeportesConjunto_Competencias.InscripcionEquipo existente : socio.getInscripcionesEquipo()) {
			if (existente.getEquipo().getDeporte().equals(equipo.getDeporte())) {
				return false;
			}
		}
		DeportesConjunto_Competencias.InscripcionEquipo inscripcion = equipo.agregarJugador(socio, posicion, numeroCamiseta);
		if (inscripcion == null) {
			return false;
		}
		socio.agregarInscripcionEquipo(inscripcion);
		guardarDatos();
		return true;
	}
	
	public PartidoInterno registrarPartidoInterno(Equipo equipoLocal, Equipo equipoVisitante,
			int marcadorLocal, int marcadorVisitante,
			List<PartidosEquipo_Estadisticas.EstadisticaConjunto> estadisticas) {
		if (equipoLocal == null || equipoVisitante == null || equipoLocal.equals(equipoVisitante)) {
			return null;
		}
		if (!equipoLocal.getDeporte().equals(equipoVisitante.getDeporte())) {
			return null;
		}
		PartidoInterno partido = new PartidoInterno(java.time.LocalDate.now(), equipoLocal, equipoVisitante,
				marcadorLocal, marcadorVisitante);
		if (estadisticas != null) {
			for (PartidosEquipo_Estadisticas.EstadisticaConjunto estadistica : estadisticas) {
				partido.agregarEstadistica(estadistica);
			}
		}
		historial.agregarPartido(partido);
		guardarDatos();
		return partido;
	}
 
	public PartidoExterno registrarPartidoExterno(Competencia competencia, Equipo equipo,
			java.time.LocalDate fecha, int marcadorPropio, int marcadorRival, String institucionRival,
			String escenario, List<PartidosEquipo_Estadisticas.EstadisticaConjunto> estadisticas) {
		if (competencia == null || equipo == null) {
			return null;
		}
		PartidoExterno partido = new PartidoExterno(fecha, equipo, marcadorPropio, marcadorRival,
				institucionRival, escenario);
		if (estadisticas != null) {
			for (PartidosEquipo_Estadisticas.EstadisticaConjunto estadistica : estadisticas) {
				partido.agregarEstadistica(estadistica);
			}
		}
		competencia.registrarPartido(partido);
		historial.agregarPartido(partido);
		guardarDatos();
		return partido;
	}
	
	public boolean inscribirEquipoEnCompetencia(Equipo equipo, Competencia competencia) {
		if (equipo == null || competencia == null) {
			return false;
		}
		if (!equipo.cumpleRequisitos()) {
			return false;
		}
		boolean inscrito = competencia.inscribirEquipo(equipo);
		if (inscrito) {
			if (!competencias.contains(competencia)) {
				competencias.add(competencia);
			}
			guardarDatos();
		}
		return inscrito;
	}
	
	public Reserva reservar(Socio socio, DisciplinaIndividual disciplina, InstalacionIndividual instalacion,
			java.time.LocalDate fecha, java.time.LocalTime horaInicio, java.time.LocalTime horaFinal,
			int numeroJugadores, DeportesIndividuales.Modalidad modalidad) {
		if (socio == null || disciplina == null || instalacion == null) {
			return null;
		}
		if (!horaInicio.isBefore(horaFinal)) {
			return null;
		}
		if (!instalacion.admiteModalidad(modalidad, numeroJugadores)) {
			return null;
		}
		for (Reserva existente : historial.getReservas()) {
			if (existente.getInstalacion().equals(instalacion)
					&& existente.seTraslapaCon(fecha, horaInicio, horaFinal)) {
				return null;
			}
		}
		Reserva reserva = new Reserva(numeroJugadores, horaInicio, horaFinal, fecha, modalidad, socio,
				instalacion);
		historial.agregarReserva(reserva);
		guardarDatos();
		return reserva;
	}
	
	public PartidoPractica programarPartidoPractica(DisciplinaIndividual disciplina,
			InstalacionIndividual instalacion, java.time.LocalDate fecha, List<Socio> jugadores) {
		if (disciplina == null || instalacion == null || jugadores == null || jugadores.size() < 2) {
			return null;
		}
		PartidoPractica partido = new PartidoPractica(fecha, disciplina, jugadores, instalacion);
		historial.agregarPartidoPractica(partido);
		guardarDatos();
		return partido;
	}
	
	public void finalizarPartidoPractica(PartidoPractica partido,
			List<DeportesIndividuales.EstadisticaPractica> estadisticas) {
		if (partido == null) {
			return;
		}
		partido.finalizar(estadisticas);
		Socio ganador = partido.getGanador();
		for (Socio jugador : partido.getJugadores()) {
			RankingJugadorDisciplina ranking = null;
			for (RankingJugadorDisciplina r : rankings) {
				if (r.getDisciplina().equals(partido.getDisciplina()) && r.getJugador().equals(jugador)) {
					ranking = r;
					break;
				}
			}
			if (ranking == null) {
				continue;
			}
			double sumaRivales = 0;
			int rivales = 0;
			for (Socio otro : partido.getJugadores()) {
				if (otro.equals(jugador)) {
					continue;
				}
				for (RankingJugadorDisciplina r : rankings) {
					if (r.getDisciplina().equals(partido.getDisciplina()) && r.getJugador().equals(otro)) {
						sumaRivales += r.getPuntajeElo();
						rivales++;
						break;
					}
				}
			}
			if (rivales > 0) {
				ranking.actualizarPuntaje(sumaRivales / rivales, jugador.equals(ganador));
			}
		}
		guardarDatos();
	}
	
	public RankingJugadorDisciplina getRanking(DisciplinaIndividual disciplina, Socio socio) {
		for (RankingJugadorDisciplina ranking : rankings) {
			if (ranking.getDisciplina().equals(disciplina) && ranking.getJugador().equals(socio)) {
				return ranking;
			}
		}
		return null;
	}
	
	public SesionEntrenamiento planearSesion(Usuarios.Entrenador entrenador, java.time.LocalDate fecha,
			java.time.LocalTime hora, int duracionMinutos, Instalacion instalacion, List<Socio> convocados) {
		if (entrenador == null || instalacion == null || duracionMinutos <= 0) {
			return null;
		}
		java.time.LocalTime fin = hora.plusMinutes(duracionMinutos);
		for (SesionEntrenamiento otra : historial.getSesiones()) {
			if (!otra.getFecha().equals(fecha)) {
				continue;
			}
			java.time.LocalTime otraFin = otra.getHora().plusMinutes(otra.getDuracionMinutos());
			boolean traslape = hora.isBefore(otraFin) && otra.getHora().isBefore(fin);
			boolean mismoRecurso = otra.getInstalacion().equals(instalacion)
					|| otra.getEntrenador().equals(entrenador);
			if (traslape && mismoRecurso) {
				return null;
			}
		}
		SesionEntrenamiento sesion = new SesionEntrenamiento(fecha, hora, duracionMinutos, entrenador,
				instalacion, convocados);
		historial.agregarSesion(sesion);
		guardarDatos();
		return sesion;
	}
	
	public void registrarAsistencia(SesionEntrenamiento sesion, Socio socio,
			Entrenamiento.EstadoAsistencia estado, int evaluacion, String comentario) {
		if (sesion == null || socio == null) {
			return;
		}
		sesion.registrarAsistencia(socio, estado, evaluacion, comentario);
		guardarDatos();
	}
	
	public void registrarVenta(Socio comprador, Map<Producto, Integer> productos, Rubro rubro,
			Instalacion instalacionActual, String codigoDescuento, int puntosARedimir, double propina) {
		if (comprador == null || productos == null || productos.isEmpty() || rubro == null) {
			return;
		}
		Venta venta;
		if (rubro == Rubro.Tienda) {
			venta = tienda.vender(comprador, productos, codigoDescuento, puntosARedimir);
		} else if (rubro == Rubro.Cafeteria) {
			venta = cafeteria.vender(comprador, productos, instalacionActual, puntosARedimir, propina);
		} else {
			return;
		}
		if (venta == null) {
			return;
		}
		ventas.add(venta);
		guardarDatos();
	}
	
	public void reabastecerTienda(Producto articulo, int cantidad) {
		if (articulo == null || cantidad <= 0) {
			return;
		}
		tienda.reabastecer(articulo, cantidad);
		guardarDatos();
	}
	
	public void reabasteceCafeteriaSocios(Producto articulo, int cantidad) {
		if (articulo == null || cantidad <= 0) {
			return;
		}
		cafeteria.reabastecer(articulo, cantidad);
		guardarDatos();
	}
	
	public boolean moverArticulo(Tienda_Cafeteria_Inventario.Articulo articulo,
			Tienda_Cafeteria_Inventario.Ubicacion origen, Tienda_Cafeteria_Inventario.Ubicacion destino) {
		boolean movido = getInventario().moverArticulo(articulo, origen, destino);
		if (movido) {
			guardarDatos();
		}
		return movido;
	}
	
	public SugerenciaArticulo registrarSugerencia(Empleado empleado, String nombrePropuesto,
			String categoria, double precioSugerido) {
		if (empleado == null) {
			return null;
		}
		SugerenciaArticulo sugerencia = empleado.sugerirArticulo(nombrePropuesto, categoria, precioSugerido);
		sugerencias.add(sugerencia);
		guardarDatos();
		return sugerencia;
	}
	
	public void resolverSugerencia(SugerenciaArticulo sugerencia, boolean aprobada) {
		if (sugerencia == null || !sugerencias.contains(sugerencia)) {
			return;
		}
		if (aprobada) {
			sugerencia.aprobar();
			tienda.agregarArticulo(new Tienda_Cafeteria_Inventario.Articulo(sugerencia.getNombrePropuesto(),
					sugerencia.getCategoria(), sugerencia.getPrecioSugerido(), 0));
		} else {
			sugerencia.rechazar();
		}
		guardarDatos();
	}
	
	public void resolverSolicitud(SolicitudCambioTurno solicitud, boolean aprobada) {
		if (solicitud == null || !solicitudes.contains(solicitud)) {
			return;
		}
		List<Empleado> todosEmpleados = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			if (usuario instanceof Empleado) {
				todosEmpleados.add((Empleado) usuario);
			}
		}
		if (aprobada && gestorTurnos.puedeAprobar(solicitud, todosEmpleados)) {
			solicitud.aprobar();
			solicitud.getSolicitante().cambiarTurno(solicitud.getTurnoActual(), solicitud.getTurnoNuevo());
		} else {
			solicitud.rechazar();
		}
		guardarDatos();
	}
	
	public Usuario iniciarSesion(String login, String password) {
		return autenticador.autenticar(login, usuarios, password);
	}
	
	public Equipo crearEquipo(String nombre, int cupoMaximo, DeporteConjunto deporte,
			DeportesConjunto_Competencias.Categoria categoria) {
		for (Equipo equipo : equipos) {
			if (equipo.getNombre().equalsIgnoreCase(nombre)) {
				return null;
			}
		}
		if (cupoMaximo < deporte.getMinimoJugadores()) {
			return null;
		}
		Equipo equipo = new Equipo(nombre, cupoMaximo, deporte, categoria);
		equipos.add(equipo);
		guardarDatos();
		return equipo;
	}
	
	public boolean darDeBajaEquipo(Equipo equipo) {
		if (equipo == null || !equipos.remove(equipo)) {
			return false;
		}
		guardarDatos();
		return true;
	}
	
	public boolean darDeBajaSocio(Socio socio) {
		if (socio == null || !usuarios.remove(socio)) {
			return false;
		}
		rankings.removeIf(ranking -> ranking.getJugador().equals(socio));
		guardarDatos();
		return true;
	}
	
	public Map<Rubro, Double> generarReporteVentas(String periodo, java.time.LocalDate fechaReferencia) {
		return generadorReportes.reporteVentas(ventas, periodo, fechaReferencia);
	}
	
	public List<String> generarReporteDeportivo(){
		List<String> reporte = new ArrayList<>();
		reporte.addAll(generadorReportes.reporteResultados(historial.getPartidos()));
		for (DisciplinaIndividual disciplina : disciplinas) {
			reporte.addAll(generadorReportes.reporteRanking(rankings, disciplina));
		}
		return reporte;
	}
	
	public void guardarDatos() {
		try {
			persistencia.guardar(this);
		} catch (java.io.IOException e) {
			throw new java.io.UncheckedIOException("No se pudieron guardar los datos del club", e);
		}
	}
	
	public void cargarDatos() {
		try {
			ClubSeneca cargado = persistencia.cargar();
			if (cargado == null) {
				return;
			}
			this.nombre = cargado.nombre;
			this.usuarios = cargado.usuarios;
			this.deportes = cargado.deportes;
			this.disciplinas = cargado.disciplinas;
			this.rankings = cargado.rankings;
			this.inventarios = cargado.inventarios;
			this.equipos = cargado.equipos;
			this.competencias = cargado.competencias;
			this.ventas = cargado.ventas;
			this.solicitudes = cargado.solicitudes;
			this.sugerencias = cargado.sugerencias;
			this.tienda = cargado.tienda;
			this.cafeteria = cargado.cafeteria;
			this.historial = cargado.historial;
		} catch (java.io.IOException e) {
			throw new java.io.UncheckedIOException("No se pudieron cargar los datos del club", e);
		}
	}
	
	public Socio crearSocio(int edad, String nombre, String login, String password) {
		for (Usuario usuario : usuarios) {
			if (usuario.getLogin().equals(login)) {
				return null;
			}
		}
		Socio socio = new Socio(edad, nombre, login, password);
		usuarios.add(socio);
		guardarDatos();
		return socio;
	}
	
	public InscripcionIndividual inscribirEnDisciplina(Socio socio, DisciplinaIndividual disciplina,
			DeportesIndividuales.NivelJugador nivel) {
		if (socio == null || disciplina == null) {
			return null;
		}
		for (InscripcionIndividual existente : socio.getInscripcionesIndividuales()) {
			if (existente.getDisciplina().equals(disciplina)) {
				return null;
			}
		}
		InscripcionIndividual inscripcion = new InscripcionIndividual(disciplina, nivel);
		socio.agregarInscripcionIndividual(inscripcion);
		if (getRanking(disciplina, socio) == null) {
			rankings.add(new RankingJugadorDisciplina(socio, disciplina));
		}
		guardarDatos();
		return inscripcion;
	}
	
	public void registrarSolicitudTurno(Empleado solicitante, TurnosEmpleados.Turno turnoActual,
			TurnosEmpleados.Turno turnoNuevo) throws Excepciones.ReglaNegocioException {
		if (solicitante == null) {
			return;
		}
		SolicitudCambioTurno solicitud = solicitante.solicitarCambioTurno(turnoActual, turnoNuevo);
		solicitudes.add(solicitud);
		guardarDatos();
	}
	
	public void asignarTurno(Empleado empleado, TurnosEmpleados.Turno turno) {
		if (empleado == null || turno == null) {
			return;
		}
		empleado.agregarTurno(turno);
		guardarDatos();
	}
	
	public List<Inventario> getInventario() {
		return inventarios;
	}
	
	public Historial getHistorial() {
		return historial;
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
