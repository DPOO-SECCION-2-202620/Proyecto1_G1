package NucleoSistema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionCompetencia;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesConjunto_Competencias.TorneoCerros;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.EstadisticaPractica;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Reserva;
import Entrenamiento.EstadoAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Excepciones.ReglaNegocioException;
import Instalaciones.Instalacion;
import Instalaciones.InstalacionConjunto;
import Instalaciones.InstalacionIndividual;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import PartidosEquipo_Estadisticas.Partido;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.CafeteriaSocios;
import Tienda_Cafeteria_Inventario.CodigoDescuento;
import Tienda_Cafeteria_Inventario.Inventario;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import Tienda_Cafeteria_Inventario.Tienda;
import Tienda_Cafeteria_Inventario.Ubicacion;
import Tienda_Cafeteria_Inventario.Venta;
import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.GestorTurnos;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Entrenador;
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
		boolean inscrito = equipo.agregarJugador(socio, posicion, numeroCamiseta);
		if (inscrito) {
			guardarDatos();
		}
		return inscrito;
	}
	
	public PartidoInterno registrarPartidoInterno(Equipo equipoLocal, Equipo equipoVisitante,
			int marcadorLocal, int marcadorVisitante,
			List<EstadisticaConjunto> estadisticas) {
		if (equipoLocal == null || equipoVisitante == null || equipoLocal.equals(equipoVisitante)) {
			return null;
		}
		if (!equipoLocal.getDeporte().equals(equipoVisitante.getDeporte())) {
			return null;
		}
		if (equipoLocal.getCategoria() != equipoVisitante.getCategoria()) {
			return null;
		}
		if (!equipoLocal.puedeJugar() || !equipoVisitante.puedeJugar()) {
			return null;
		}
		PartidoInterno partido = new PartidoInterno(LocalDate.now(), equipoLocal, equipoVisitante,
				marcadorLocal, marcadorVisitante);
		if (estadisticas != null) {
			for (EstadisticaConjunto estadistica : estadisticas) {
				partido.agregarEstadistica(estadistica);
				if (estadistica instanceof EstadisticaFutbol) {
					EstadisticaFutbol estadisticaFutbol = (EstadisticaFutbol) estadistica;
					estadistica.getJugador().registrarTarjetas(estadisticaFutbol.getTarjetasAmarillas(),
							estadisticaFutbol.getTarjetasRojas());
				}
			}
		}
		historial.agregarPartido(partido);
		guardarDatos();
		return partido;
	}
 
	public PartidoExterno registrarPartidoExterno(Competencia competencia, Equipo equipo,
			LocalDate fecha, int marcadorPropio, int marcadorRival, String institucionRival,
			String escenario, List<EstadisticaConjunto> estadisticas) {
		if (competencia == null || equipo == null) {
			return null;
		}
		InscripcionCompetencia inscripcionCompetencia = competencia.getInscripcionDe(equipo);
		if (estadisticas != null && inscripcionCompetencia != null) {
			for (EstadisticaConjunto estadistica : estadisticas) {
				if (!inscripcionCompetencia.puedeAlinear(estadistica.getJugador().getJugador())) {
					return null;
				}
			}
		}
		PartidoExterno partido = new PartidoExterno(fecha, marcadorPropio, marcadorRival, institucionRival,
				escenario, equipo);
		if (estadisticas != null) {
			for (PartidosEquipo_Estadisticas.EstadisticaConjunto estadistica : estadisticas) {
				partido.agregarEstadistica(estadistica);
				if (estadistica instanceof EstadisticaFutbol && inscripcionCompetencia != null) {
					EstadisticaFutbol estadisticaFutbol = (EstadisticaFutbol) estadistica;
					inscripcionCompetencia.getRegistroDe(estadistica.getJugador().getJugador()).registrarTarjetas(
							estadisticaFutbol.getTarjetasAmarillas(), estadisticaFutbol.getTarjetasRojas());
				}
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
		if (!competencia.equipoCumpleRequisitos(equipo)) {
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
			LocalDate fecha, LocalTime horaInicio, LocalTime horaFinal,
			int numeroJugadores, Modalidad modalidad) {
		if (socio == null || disciplina == null || instalacion == null) {
			return null;
		}
		if (!horaInicio.isBefore(horaFinal)) {
			return null;
		}
		if (!instalacion.admiteModalidad(modalidad, numeroJugadores)) {
			return null;
		}
		for (Reserva reservaExistente : historial.getReservas()) {
			if (reservaExistente.getInstalacion().equals(instalacion)
					&& reservaExistente.seSolapaCon(fecha, horaInicio, horaFinal)) {
				return null;
			}
		}
		if (!disciplina.hayCupo(historial.getReservas(), fecha, horaInicio, horaFinal)) {
			return null;
		}
		Reserva reserva = new Reserva(numeroJugadores, horaInicio, horaFinal, fecha, modalidad, socio,
				instalacion);
		historial.agregarReserva(reserva);
		guardarDatos();
		return reserva;
	}
	
	public PartidoPractica programarPartidoPractica(DisciplinaIndividual disciplina,
			InstalacionIndividual instalacion, LocalDate fecha, List<Socio> jugadores) {
		if (disciplina == null || instalacion == null || jugadores == null
				|| (jugadores.size() != 2 && jugadores.size() != 4)) {
			return null;
		}
		PartidoPractica partidoPractica = new PartidoPractica(fecha, disciplina, jugadores, instalacion);
		if (!partidoPractica.validarNiveles()) {
			return null;
		}
		historial.agregarPartidosPractica(partidoPractica);
		guardarDatos();
		return partidoPractica;
	}
	
	public void finalizarPartidoPractica(PartidoPractica partido,
			List<EstadisticaPractica> estadisticas) {
		if (partido == null) {
			return;
		}
		try {
			partido.finalizar(estadisticas);
		} catch (ReglaNegocioException excepcion) {
			excepcion.printStackTrace();
			return;
		}
		Socio ganador = partido.getGanador();
		Map<RankingJugadorDisciplina, Double> promediosRivales = new HashMap<>();
		for (Socio jugador : partido.getJugadores()) {
			RankingJugadorDisciplina rankingJugador = null;
			for (RankingJugadorDisciplina ranking : rankings) {
				if (ranking.getDisciplina().equals(partido.getDisciplina()) && ranking.getJugador().equals(jugador)) {
					rankingJugador = ranking;
					break;
				}
			}
			if (rankingJugador == null) {
				continue;
			}
			double sumaRivales = 0;
			int rivales = 0;
			for (Socio otro : partido.getJugadores()) {
				if (otro.equals(jugador)) {
					continue;
				}
				for (RankingJugadorDisciplina ranking : rankings) {
					if (ranking.getDisciplina().equals(partido.getDisciplina()) && ranking.getJugador().equals(otro)) {
						sumaRivales += ranking.getPuntajeElo();
						rivales++;
						break;
					}
				}
			}
			if (rivales > 0) {
				promediosRivales.put(rankingJugador, sumaRivales / rivales);
			}
		}
		for (Map.Entry<RankingJugadorDisciplina, Double> entrada : promediosRivales.entrySet()) {
			RankingJugadorDisciplina rankingJugador = entrada.getKey();
			rankingJugador.calcularPuntajeElo(entrada.getValue(), rankingJugador.getJugador().equals(ganador));
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
	
	public SesionEntrenamiento planearSesion(Entrenador entrenador, LocalDate fecha,
			LocalTime hora, int duracionMinutos, Instalacion instalacion, List<Socio> convocados) {
		if (entrenador == null || instalacion == null || duracionMinutos <= 0) {
			return null;
		}
		LocalTime fin = hora.plusMinutes(duracionMinutos);
		for (SesionEntrenamiento sesionExistente : historial.getSesiones()) {
			if (!sesionExistente.getFecha().equals(fecha)) {
				continue;
			}
			LocalTime sesionExistenteFin = sesionExistente.getHora().plusMinutes(sesionExistente.getDuracionMinutos());
			boolean seSolapan = hora.isBefore(sesionExistenteFin) && sesionExistente.getHora().isBefore(fin);
			boolean mismoRecurso = sesionExistente.getInstalacion().equals(instalacion)
					|| sesionExistente.getEntrenador().equals(entrenador);
			if (seSolapan && mismoRecurso) {
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
			EstadoAsistencia estado, int evaluacion, String comentario) {
		if (sesion == null || socio == null) {
			return;
		}
		sesion.registrarAsistencia(socio, estado, evaluacion, comentario);
		guardarDatos();
	}
	
	public void registrarVenta(Socio comprador, Map<Producto, Integer> productos, Rubro rubro,
			Instalacion instalacionActual, CodigoDescuento codigoDescuento, int puntosARedimir, double propina) {
		if (comprador == null || productos == null || productos.isEmpty() || rubro == null) {
			return;
		}
		for (Map.Entry<Producto, Integer> linea : productos.entrySet()) {
			if (linea.getKey() == null || linea.getKey().getCantidad() < linea.getValue()) {
				return;
			}
		}
		if (puntosARedimir < 0 || puntosARedimir > comprador.getPuntosFidelidad()) {
			puntosARedimir = 0;
		}
		Venta venta;
		List<LineaVenta> lineas = new ArrayList<>();
		productos.forEach((producto, cantidad) -> {
		    lineas.add(new LineaVenta(cantidad, producto));
		});
		if (rubro == Rubro.TIENDA) {
			venta = tienda.vender(comprador, lineas, instalacionActual, codigoDescuento, puntosARedimir, propina);
		} else if (rubro == Rubro.CAFETERIA) {
			venta = cafeteria.vender(comprador, lineas, instalacionActual, codigoDescuento, puntosARedimir, propina);;
		} else {
			return;
		}
		if (venta == null) {
			return;
		}
		for (Map.Entry<Producto, Integer> linea : productos.entrySet()) {
			linea.getKey().reducirCantidad(linea.getValue());
			if (linea.getKey() instanceof Bebida && ((Bebida) linea.getKey()).getCaliente()) {
				comprador.registrarBebidaCaliente(LocalDateTime.now());
			}
		}
		if (venta.getPuntosRedimidos() > 0) {
			comprador.redimirPuntos(venta.getPuntosRedimidos());
		}
		comprador.acumularPuntos(venta.getPuntosGenerados());
		ventas.add(venta);
		guardarDatos();
	}
	
	public void reabastecerTienda(Articulo articulo, int cantidad) {
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
	
	public boolean moverArticulo(Articulo articulo, Ubicacion origen, Ubicacion destino) {
		Inventario inventarioOrigen = null;
		Inventario inventarioDestino = null;
		List<Inventario> inventarios = getInventario();
		for (Inventario inventario : inventarios) {
			if (inventario.getUbicacion().equals(origen)) {
				inventarioOrigen = inventario;
			}
			if (inventario.getUbicacion().equals(destino)) {
				inventarioDestino  = inventario;
			}
			if (inventarioOrigen != null && inventarioDestino != null) {
				break;
			}
		}
		if (inventarioOrigen == null || inventarioDestino == null) {
			return false;
		}
		if (!inventarioOrigen.tieneArticulo(articulo) || inventarioDestino.tieneArticulo(articulo)) {
			return false;
		}
		inventarioOrigen.quitarArticulo(articulo);
		inventarioDestino.agregarArticulo(articulo); 
		guardarDatos();
		return true;
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
			tienda.agregarArticulo(new Articulo(sugerencia.getNombrePropuesto(),
					sugerencia.getCategoria(), sugerencia.getPrecioSugerido(), 0));
		} else {
			sugerencia.rechazar();
		}
		guardarDatos();
	}
	
	public void resolverSolicitud(SolicitudCambioTurno solicitud, boolean aprobada) {
		if (solicitud == null || !solicitudes.contains(solicitud)
				|| solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
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
	
	public Equipo crearEquipo(String nombre, int cupoMaximo, DeporteConjunto deporte, Categoria categoria) {
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
		for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
			inscripcion.getJugador().getInscripcionesEquipo().remove(inscripcion);
		}
		for (Competencia competencia : competencias) {
			competencia.getInscripciones().removeIf(inscripcion -> inscripcion.getEquipo() == equipo);
			competencia.getListaPartidos().removeIf(partido -> partido.getEquipo() == equipo);
			if (competencia instanceof TorneoCerros) {
				((TorneoCerros) competencia).getTablaPosiciones().remove(equipo);
			}
		}
		historial.getPartidos().removeIf(partido -> 
				(partido instanceof PartidoInterno && (((PartidoInterno) partido).getEquipoLocal() == equipo
						|| ((PartidoInterno) partido).getEquipoVisitante() == equipo))
				|| (partido instanceof PartidoExterno && ((PartidoExterno) partido).getEquipo() == equipo));
		guardarDatos();
		return true;
	}
	
	public boolean darDeBajaSocio(Socio socio) {
	    if (socio == null || !usuarios.contains(socio)) {
	        return false;
	    }
	    if (socio.getInscripcionesEquipo().isEmpty()) {
	        return false;
	    }
	    usuarios.remove(socio);
	    rankings.removeIf(ranking -> ranking.getJugador().equals(socio));
		for (Equipo equipo : equipos) {
			equipo.getInscripciones().removeIf(inscripcion -> inscripcion.getJugador() == socio);
		}
		for (Competencia competencia : competencias) {
			for (InscripcionCompetencia inscripcion : competencia.getInscripciones()) {
				inscripcion.getNomina().removeIf(registro -> registro.getInscripcionEquipo().getJugador() == socio);
			}
			for (PartidoExterno partido : competencia.getListaPartidos()) {
				partido.getEstadisticas().removeIf(estadistica -> estadistica.getJugador().getJugador() == socio);
			}
		}
		for (Partido partido : historial.getPartidos()) {
			partido.getEstadisticas().removeIf(estadistica -> estadistica.getJugador().getJugador() == socio);
		}
		historial.getReservas().removeIf(reserva -> reserva.getJugador() == socio);
		historial.getPartidosPractica().removeIf(partido -> partido.getJugadores().contains(socio));
		historial.getSesiones().removeIf(sesion -> sesion.getEntrenador() == socio);
		for (SesionEntrenamiento sesion : historial.getSesiones()) {
			sesion.getConvocados().remove(socio);
			sesion.getAsistencias().removeIf(asistencia -> asistencia.getJugador() == socio);
		}
		solicitudes.removeIf(solicitud -> solicitud.getSolicitante() == socio);
		guardarDatos();
	    return true;
	}
	
	public Map<LocalDate, Map<Rubro, Double>>  generarReporteVentas(Rubro rubro, Periodo periodo) {
		return generadorReportes.reporteVentas(ventas, rubro, periodo);
	}
	
	public List<RankingJugadorDisciplina> generarReporteDeportivo(){
		List<RankingJugadorDisciplina> reporte = generadorReportes.reporteRankings(rankings);
		return reporte;
	}
	
	public void guardarDatos() {
		try {
			persistencia.guardar(this);
		} catch (IOException excepcio) {
			throw new UncheckedIOException("No se pudieron guardar los datos del club", excepcio);
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
		} catch (IOException excepcion) {
			throw new UncheckedIOException("No se pudieron cargar los datos del club", excepcion);
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
	
	public InscripcionIndividual inscribirEnDisciplina(Socio socio, DisciplinaIndividual disciplina, NivelJugador nivel) {
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
	
	public void registrarSolicitudTurno(Empleado solicitante, Turno turnoActual, Turno turnoNuevo) throws ReglaNegocioException {
		if (solicitante == null) {
			return;
		}
		SolicitudCambioTurno solicitud = solicitante.solicitarCambioTurno(turnoActual, turnoNuevo);
		solicitudes.add(solicitud);
		guardarDatos();
	}
	
	public void asignarTurno(Empleado empleado, Turno turno) {
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
	    
	    Map<String, InstalacionConjunto> instalacionesConjunto = new HashMap<>();
	    for (SesionEntrenamiento sesion : historial.getSesiones()) {
	    	if (sesion.getInstalacion() instanceof InstalacionConjunto) {
	    		instalacionesConjunto.put(sesion.getInstalacion().getNombre(), (InstalacionConjunto) sesion.getInstalacion());
	    	}
	    }
	    for (Inventario inventario : inventarios) {
	    	for (Instalacion instalacion : inventario.getInstalaciones()) {
	    		if (instalacion instanceof InstalacionConjunto) {
	    			instalacionesConjunto.put(instalacion.getNombre(), (InstalacionConjunto) instalacion);
	    		}
	    	}
	    }
	    JSONArray jsonInstalacionesConjunto = new JSONArray();
	    for (InstalacionConjunto instalacion : instalacionesConjunto.values()) {
	    	jsonInstalacionesConjunto.put(instalacion.toJSON());
	    }
	    json.put("instalacionesConjunto", jsonInstalacionesConjunto);
	    
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
		
		JSONArray jsonInstalacionesConjunto = leerLista(json, "instalacionesConjunto");
		for (int indice = 0; indice < jsonInstalacionesConjunto.length(); indice++) {
			Instalacion instalacion = InstalacionConjunto.fromJSON(jsonInstalacionesConjunto.getJSONObject(indice));
			mapaInstalaciones.put(instalacion.getNombre(), instalacion);
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
			Inventario inventario = Inventario.fromJSON(jsonInventarios.getJSONObject(indice), mapaProductos, mapaInstalaciones, mapaDisciplinas);
			club.inventarios.add(inventario);
			for (Instalacion instalacion : inventario.getInstalaciones()) {
				mapaInstalaciones.put(instalacion.getNombre(), instalacion);
			}
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

