package PruebasJson;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import DeportesConjunto_Competencias.ASCUN;
import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.ControlSanciones;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.FaseASCUN;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionCompetencia;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesConjunto_Competencias.RegistroNomina;
import DeportesConjunto_Competencias.TorneoCerros;
import DeportesConjunto_Competencias.Voleibol;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.EstadisticaPractica;
import DeportesIndividuales.EstadisticaRaqueta;
import DeportesIndividuales.EstadisticaTenisDeMesa;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Padel;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Reserva;
import DeportesIndividuales.Tenis;
import DeportesIndividuales.TenisDeMesa;
import Entrenamiento.AsistenciaEntrenamiento;
import Entrenamiento.EstadoAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Excepciones.ReglaNegocioException;
import Instalaciones.CanchaBaloncesto;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaPadel;
import Instalaciones.CanchaTenis;
import Instalaciones.CanchaVoleibol;
import Instalaciones.Instalacion;
import Instalaciones.MesaTenisDeMesa;
import NucleoSistema.ClubSeneca;
import NucleoSistema.GeneradorReportes;
import NucleoSistema.Persistencia;
import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import PartidosEquipo_Estadisticas.EstadisticaVoleibol;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Tienda_Cafeteria_Inventario.Alergeno;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.CafeteriaSocios;
import Tienda_Cafeteria_Inventario.CodigoDescuento;
import Tienda_Cafeteria_Inventario.EstadoSugerencia;
import Tienda_Cafeteria_Inventario.Inventario;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Snack;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import Tienda_Cafeteria_Inventario.Ubicacion;
import Tienda_Cafeteria_Inventario.Venta;
import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.GestorTurnos;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;
import Usuarios.Usuario;

/**
 * Prueba completa de ClubSeneca.
 *
 * Recorre, con datos propios y a través de la API pública del club, las reglas de negocio de las
 * historias de usuario (reservas, equipos, competencias, entrenamientos, ventas, turnos, bajas...)
 * y después comprueba que todo ese estado sobrevive a guardar y cargar el JSON.
 *
 * No toca la carpeta "datos": trabaja en una carpeta temporal que se borra al terminar.
 * Ejecutar como aplicación Java (la salida solo muestra los fallos; al final hay un resumen).
 */
public class PruebaCompleta {

	private interface Tarea {
		void ejecutar() throws Exception;
	}

	// ---------------------------------------------------------------- contadores

	private static int comprobaciones = 0;
	private static int fallos = 0;
	private static int comprobacionesSeccion = 0;
	private static int fallosSeccion = 0;
	private static final Map<String, int[]> resumenSecciones = new LinkedHashMap<>();

	// ---------------------------------------------------------------- escenario compartido

	private static final LocalDate FECHA_BASE = LocalDate.of(2026, 10, 19); // lunes
	private static Path carpetaDatos;
	private static ClubSeneca club;

	private static Administrador administrador;
	private static Entrenador luis, carla, pablo;
	private static Fisioterapeuta marta, diego;
	private static final List<Socio> jugadores = new ArrayList<>();
	private static Socio ana, pedro, camilo, nino, nina;

	private static Tenis tenis;
	private static Padel padel;
	private static TenisDeMesa tenisDeMesa;
	private static CanchaTenis canchaTenis1, canchaTenis2;
	private static CanchaPadel canchaPadel1, canchaPadel2, canchaPadel3;
	private static MesaTenisDeMesa mesaCubierta, mesaAbierta;
	private static CanchaFutbol canchaFutbol;
	private static CanchaBaloncesto canchaBaloncesto;
	private static CanchaVoleibol canchaVoleibol;

	private static Futbol futbol;
	private static Voleibol voleibol;
	private static Baloncesto baloncesto;
	private static Equipo cerros, andes, sub17, voley, basket;
	private static TorneoCerros torneo;
	private static ASCUN ascun;

	private static SesionEntrenamiento sesionBasket;
	private static Articulo balon, raqueta;
	private static Bebida cafe, jugo;
	private static Snack barra;

	// ---------------------------------------------------------------- utilidades de prueba

	private static void registrarFallo(String mensaje) {
		fallos++;
		fallosSeccion++;
		System.out.println("  [FALLO] " + mensaje);
	}

	private static void exigir(boolean condicion, String mensaje) {
		comprobaciones++;
		comprobacionesSeccion++;
		if (!condicion) {
			registrarFallo(mensaje);
		}
	}

	private static void exigirIgual(Object esperado, Object real, String mensaje) {
		exigir(Objects.equals(esperado, real), mensaje + " (esperado: " + esperado + ", real: " + real + ")");
	}

	private static void exigirCercano(double esperado, double real, String mensaje) {
		exigir(Math.abs(esperado - real) < 0.01, mensaje + " (esperado: " + esperado + ", real: " + real + ")");
	}

	private static Throwable capturar(Tarea tarea) {
		try {
			tarea.ejecutar();
			return null;
		} catch (Throwable throwable) {
			return throwable;
		}
	}

	private static String resumir(Throwable throwable) {
		if (throwable == null) {
			return "nada";
		}
		String texto = throwable.getClass().getSimpleName();
		if (throwable.getMessage() != null) {
			String mensaje = throwable.getMessage().replace('\n', ' ');
			texto += ": " + (mensaje.length() > 140 ? mensaje.substring(0, 140) + "..." : mensaje);
		}
		return texto;
	}

	private static void exigirSinExcepcion(String descripcion, Tarea tarea) {
		Throwable problema = capturar(tarea);
		exigir(problema == null, descripcion + " lanzó " + resumir(problema));
	}

	private static void exigirExcepcion(Class<? extends Throwable> tipo, String descripcion, Tarea tarea) {
		Throwable problema = capturar(tarea);
		exigir(tipo.isInstance(problema),
				descripcion + " debía lanzar " + tipo.getSimpleName() + " y lanzó " + resumir(problema));
	}

	/** Ejecuta una tarea que imprime stack traces esperados (el club los imprime al rechazar datos). */
	private static void sinSalidaDeError(Tarea tarea) throws Exception {
		PrintStream original = System.err;
		System.setErr(new PrintStream(OutputStream.nullOutputStream()));
		try {
			tarea.ejecutar();
		} finally {
			System.setErr(original);
		}
	}

	private static void seccion(String nombre, Tarea tarea) {
		comprobacionesSeccion = 0;
		fallosSeccion = 0;
		System.out.println("== " + nombre);
		Throwable problema = capturar(tarea);
		if (problema != null) {
			StackTraceElement[] pila = problema.getStackTrace();
			String lugar = pila.length > 0 ? " en " + pila[0] : "";
			registrarFallo("la sección se interrumpió: " + resumir(problema) + lugar);
		}
		resumenSecciones.put(nombre, new int[] { comprobacionesSeccion, fallosSeccion });
	}

	@SuppressWarnings("unchecked")
	private static <Elemento> List<Elemento> listaPrivada(Object objeto, String nombreCampo) throws Exception {
		Field campo = objeto.getClass().getDeclaredField(nombreCampo);
		campo.setAccessible(true);
		return (List<Elemento>) campo.get(objeto);
	}

	private static Socio jugador(int numero) {
		return jugadores.get(numero - 1);
	}

	private static LocalTime hora(int horas) {
		return LocalTime.of(horas, 0);
	}

	private static JSONObject viajarPorTexto(JSONObject json) {
		return new JSONObject(json.toString(2));
	}

	private static double totalReporte(Rubro rubro) {
		return club.generarReporteVentas().getOrDefault(rubro, 0.0);
	}

	private static double totalGeneralVentas() {
		double suma = 0;
		for (double total : club.generarReporteVentas().values()) {
			suma += total;
		}
		return suma;
	}

	private static Map<Producto, Integer> pedido(Producto producto, int cantidad) {
		Map<Producto, Integer> productos = new LinkedHashMap<>();
		productos.put(producto, cantidad);
		return productos;
	}

	private static List<Empleado> empleadosDelClub() throws Exception {
		List<Usuario> usuarios = listaPrivada(club, "usuarios");
		List<Empleado> empleados = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			if (usuario instanceof Empleado) {
				empleados.add((Empleado) usuario);
			}
		}
		return empleados;
	}

	private static AsistenciaEntrenamiento buscarAsistencia(SesionEntrenamiento sesion, Socio socio) {
		for (AsistenciaEntrenamiento asistencia : sesion.getAsistencias()) {
			if (asistencia.getJugador() == socio) {
				return asistencia;
			}
		}
		return null;
	}

	private static void compararJson(String ruta, Object esperado, Object real, List<String> diferencias) {
		if (esperado instanceof JSONObject && real instanceof JSONObject) {
			JSONObject jsonEsperado = (JSONObject) esperado;
			JSONObject jsonReal = (JSONObject) real;
			TreeSet<String> claves = new TreeSet<>(jsonEsperado.keySet());
			claves.addAll(jsonReal.keySet());
			for (String clave : claves) {
				if (!jsonEsperado.has(clave)) {
					diferencias.add(ruta + "." + clave + " solo aparece al recargar");
				} else if (!jsonReal.has(clave)) {
					diferencias.add(ruta + "." + clave + " se perdió al recargar");
				} else {
					compararJson(ruta + "." + clave, jsonEsperado.get(clave), jsonReal.get(clave), diferencias);
				}
			}
		} else if (esperado instanceof JSONArray && real instanceof JSONArray) {
			JSONArray listaEsperada = (JSONArray) esperado;
			JSONArray listaReal = (JSONArray) real;
			if (listaEsperada.length() != listaReal.length()) {
				diferencias.add(ruta + ": tamaño " + listaEsperada.length() + " vs " + listaReal.length());
			}
			int comunes = Math.min(listaEsperada.length(), listaReal.length());
			for (int indice = 0; indice < comunes; indice++) {
				compararJson(ruta + "[" + indice + "]", listaEsperada.get(indice), listaReal.get(indice), diferencias);
			}
		} else if (esperado instanceof Number && real instanceof Number) {
			if (((Number) esperado).doubleValue() != ((Number) real).doubleValue()) {
				diferencias.add(ruta + ": " + esperado + " vs " + real);
			}
		} else if (!Objects.equals(esperado, real)) {
			diferencias.add(ruta + ": '" + esperado + "' vs '" + real + "'");
		}
	}

	private static void borrarCarpeta(Path carpeta) {
		if (carpeta == null || !Files.exists(carpeta)) {
			return;
		}
		try (Stream<Path> rutas = Files.walk(carpeta)) {
			rutas.sorted(Comparator.reverseOrder()).forEach(ruta -> ruta.toFile().delete());
		} catch (Exception excepcion) {
			System.out.println("  [AVISO] no se pudo borrar " + carpeta + ": " + excepcion);
		}
	}

	/** Devuelve null si el login no existe o si el autenticador falla (el fallo se prueba en la sección 01). */
	private static Usuario iniciarSesionSinFallar(ClubSeneca destino, String login, String password) {
		try {
			return destino.iniciarSesion(login, password);
		} catch (RuntimeException excepcion) {
			return null;
		}
	}

	private static ClubSeneca crearClubPequeno(String nombre) {
		ClubSeneca pequeno = new ClubSeneca(nombre);
		pequeno.setPersistencia(new Persistencia(carpetaDatos.resolve(nombre).toString()));
		return pequeno;
	}

	private static ClubSeneca recargar(ClubSeneca origen, String nombreCarpeta) {
		ClubSeneca recargado = new ClubSeneca("recargado");
		recargado.setPersistencia(new Persistencia(carpetaDatos.resolve(nombreCarpeta).toString()));
		recargado.cargarDatos();
		return recargado;
	}

	// ---------------------------------------------------------------- main

	public static void main(String[] args) throws Exception {
		long inicio = System.nanoTime();
		carpetaDatos = Files.createTempDirectory("club_prueba_completa");
		try {
			seccion("01 Usuarios y autenticación", PruebaCompleta::usuariosYAutenticacion);
			seccion("02 Disciplinas e instalaciones", PruebaCompleta::disciplinasEInstalaciones);
			seccion("03 Inscripciones individuales y niveles", PruebaCompleta::inscripcionesIndividuales);
			seccion("04 Reservas (cupo, solapes, modalidad)", PruebaCompleta::reservas);
			seccion("05 Partidos de práctica y ranking", PruebaCompleta::partidosDePractica);
			seccion("06 Equipos e inscripción de jugadores", PruebaCompleta::equipos);
			seccion("07 Sanciones y tarjetas", PruebaCompleta::sanciones);
			seccion("08 Partidos internos", PruebaCompleta::partidosInternos);
			seccion("09 Competencias (Torneo de los Cerros y ASCUN)", PruebaCompleta::competencias);
			seccion("10 Entrenamientos, asistencia y evaluación", PruebaCompleta::entrenamientos);
			seccion("11 Tienda, cafetería, puntos y descuentos", PruebaCompleta::ventas);
			seccion("12 Sugerencias e inventario", PruebaCompleta::sugerenciasEInventario);
			seccion("13 Turnos de empleados", PruebaCompleta::turnos);
			seccion("14 Reportes", PruebaCompleta::reportes);
			seccion("15 Bajas de socios y equipos", PruebaCompleta::bajas);
			seccion("16 Persistencia: ida y vuelta por clase", PruebaCompleta::persistenciaPorClase);
			seccion("17 Persistencia: club completo", PruebaCompleta::persistenciaClubCompleto);
		} finally {
			borrarCarpeta(carpetaDatos);
		}

		System.out.println("\n== Resumen por sección (comprobaciones / fallos)");
		for (Map.Entry<String, int[]> fila : resumenSecciones.entrySet()) {
			System.out.println(String.format("  %-52s %4d / %d", fila.getKey(), fila.getValue()[0], fila.getValue()[1]));
		}
		long milisegundos = (System.nanoTime() - inicio) / 1_000_000;
		System.out.println("\nComprobaciones: " + comprobaciones + " | fallos: " + fallos + " | tiempo: " + milisegundos + " ms");
		System.out.println(fallos == 0 ? "PRUEBA COMPLETA OK" : "REVISAR FALLOS");
		if (fallos > 0) {
			System.exit(1);
		}
	}

	// ================================================================ 01 usuarios

	private static void usuariosYAutenticacion() throws Exception {
		club = new ClubSeneca("Club Seneca (prueba completa)");
		club.setPersistencia(new Persistencia(carpetaDatos.toString()));

		exigir(club.getHistorial() != null, "un club nuevo debe tener historial");
		exigir(club.getInventario().isEmpty(), "un club nuevo no tiene inventarios");
		exigir(club.generarReporteVentas().isEmpty(), "un club nuevo no tiene ventas");
		exigir(club.generarReporteDeportivo().isEmpty(), "un club nuevo no tiene rankings");

		for (int numero = 1; numero <= 16; numero++) {
			String login = String.format("jugador%02d", numero);
			Socio jugador = club.crearSocio(19 + numero, "Jugador " + numero, login, "clave" + numero);
			exigir(jugador != null, "crearSocio debe crear a " + login);
			jugadores.add(jugador);
		}
		ana = club.crearSocio(24, "Ana Torres", "ana", "clave-ana");
		pedro = club.crearSocio(22, "Pedro Ramirez", "pedro", "clave-pedro");
		camilo = club.crearSocio(27, "Camilo Vargas", "camilo", "clave-camilo");
		nino = club.crearSocio(15, "Nino Sub17", "nino", "clave-nino");
		nina = club.crearSocio(11, "Nina Sub13", "nina", "clave-nina");
		exigir(ana != null && pedro != null && camilo != null && nino != null && nina != null,
				"crearSocio debe crear a los socios de las disciplinas individuales");
		exigir(club.crearSocio(30, "Otra Ana", "ana", "otra") == null, "no se puede repetir el login de un usuario");

		// El club no expone un método para registrar personal ni administradores: se inyectan por reflexión.
		administrador = new Administrador("Admin Seneca", "admin", "admin123");
		administrador.setClub(club);
		luis = new Entrenador(38, "Luis Perez", "luis", "clave-luis", "EMP-001");
		carla = new Entrenador(34, "Carla Gomez", "carla", "clave-carla", "EMP-002");
		pablo = new Entrenador(41, "Pablo Rios", "pablo", "clave-pablo", "EMP-003");
		marta = new Fisioterapeuta(36, "Marta Diaz", "marta", "clave-marta", "EMP-004");
		diego = new Fisioterapeuta(39, "Diego Castro", "diego", "clave-diego", "EMP-005");
		List<Usuario> usuarios = listaPrivada(club, "usuarios");
		usuarios.add(administrador);
		usuarios.add(luis);
		usuarios.add(carla);
		usuarios.add(pablo);
		usuarios.add(marta);
		usuarios.add(diego);

		exigir(club.iniciarSesion("ana", "clave-ana") == ana, "iniciarSesion con credenciales correctas devuelve al socio");
		exigir(club.iniciarSesion("ana", "incorrecta") == null, "iniciarSesion con contraseña errónea devuelve null");
		exigir(club.iniciarSesion("admin", "admin123") == administrador, "el administrador también inicia sesión");
		exigir(club.iniciarSesion(null, "x") == null, "iniciarSesion con login null devuelve null");
		exigirSinExcepcion("iniciarSesion con un login inexistente (debe devolver null)", () -> club.iniciarSesion("fantasma", "x"));

		exigir(((Object) luis) instanceof Socio, "un empleado es también socio (puede comprar, jugar y reservar)");
		exigir(!(((Object) administrador) instanceof Socio), "el administrador no es socio");
		exigir(luis.esEntrenador() && !marta.esEntrenador() && !ana.esEntrenador(), "solo los entrenadores responden esEntrenador()");
		exigirIgual("DESC-EMP-001", luis.getCodigoDescuento().getCodigo(), "el empleado recibe su código de descuento");
		exigir(ana.verificarPassword("clave-ana") && !ana.verificarPassword("otra"), "verificarPassword");
	}

	// ================================================================ 02 disciplinas

	private static void disciplinasEInstalaciones() throws Exception {
		tenis = new Tenis(2, "Tenis");
		padel = new Padel(2, "Padel");
		tenisDeMesa = new TenisDeMesa(2, "TenisDeMesa");
		canchaTenis1 = new CanchaTenis("Tenis 1", false, 1, tenis);
		canchaTenis2 = new CanchaTenis("Tenis 2", true, 1, tenis);
		canchaPadel1 = new CanchaPadel("Padel 1", true, 2, padel);
		canchaPadel2 = new CanchaPadel("Padel 2", true, 2, padel);
		canchaPadel3 = new CanchaPadel("Padel 3", true, 2, padel);
		mesaCubierta = new MesaTenisDeMesa("Mesa 1", true, 3, tenisDeMesa);
		mesaAbierta = new MesaTenisDeMesa("Mesa 2", false, 3, tenisDeMesa);
		canchaFutbol = new CanchaFutbol("Cancha Futbol 1");
		canchaBaloncesto = new CanchaBaloncesto("Cancha Basket 1");
		canchaVoleibol = new CanchaVoleibol("Cancha Voley 1");

		List<DisciplinaIndividual> disciplinasClub = listaPrivada(club, "disciplinas");
		disciplinasClub.add(tenis);
		disciplinasClub.add(padel);
		disciplinasClub.add(tenisDeMesa);

		exigirIgual(2, tenis.getInstalaciones().size(), "cada instalación se registra sola en su disciplina (tenis)");
		exigirIgual(3, padel.getInstalaciones().size(), "cada instalación se registra sola en su disciplina (pádel)");
		exigirIgual(2, tenisDeMesa.getInstalaciones().size(), "cada instalación se registra sola en su disciplina (mesa)");
		exigirIgual(2, tenis.getMaxInstalacionesSimultaneas(), "máximo de instalaciones simultáneas");
		exigirIgual("Padel", padel.getNombre(), "nombre de la disciplina");

		exigirIgual(4, canchaTenis1.getCapacidadMaxima(), "capacidad de una cancha de tenis");
		exigirIgual(22, canchaFutbol.getCapacidadMaxima(), "capacidad de la cancha de fútbol");
		exigirIgual(10, canchaBaloncesto.getCapacidadMaxima(), "capacidad de la cancha de baloncesto");
		exigirIgual(12, canchaVoleibol.getCapacidadMaxima(), "capacidad de la cancha de voleibol");

		exigir(canchaTenis1.admiteModalidad(Modalidad.SENCILLOS, 2), "tenis admite sencillos con 2 jugadores");
		exigir(canchaTenis1.admiteModalidad(Modalidad.DOBLES, 4), "tenis admite dobles con 4 jugadores");
		exigir(!canchaTenis1.admiteModalidad(Modalidad.SENCILLOS, 4), "tenis no admite sencillos con 4 jugadores");
		exigir(!canchaTenis1.admiteModalidad(Modalidad.DOBLES, 2), "tenis no admite dobles con 2 jugadores");
		exigir(canchaPadel1.admiteModalidad(Modalidad.DOBLES, 4), "pádel solo se juega en dobles con 4 jugadores");
		exigir(!canchaPadel1.admiteModalidad(Modalidad.SENCILLOS, 2), "pádel no admite sencillos");
		exigir(mesaCubierta.admiteModalidad(Modalidad.SENCILLOS, 2) && mesaCubierta.admiteModalidad(Modalidad.DOBLES, 4),
				"la mesa admite 2 o 4 jugadores");

		// Regla de la bebida caliente: pádel y mesa techada no permiten entrar con una bebida caliente.
		exigir(canchaTenis1.permiteBebidaCaliente(), "la cancha de tenis permite bebida caliente");
		exigir(!canchaPadel1.permiteBebidaCaliente(), "la cancha de pádel no permite bebida caliente");
		exigir(!mesaCubierta.permiteBebidaCaliente(), "la mesa techada no permite bebida caliente");
		exigir(mesaAbierta.permiteBebidaCaliente(), "la mesa descubierta sí permite bebida caliente");

		Socio visitante = new Socio(30, "Visitante", "visitante", "x");
		LocalDateTime momento = LocalDateTime.of(2026, 10, 19, 9, 0);
		exigir(canchaPadel1.puedeUsar(visitante, momento), "sin bebida caliente se puede usar el pádel");
		visitante.registrarBebidaCaliente(momento);
		exigir(!canchaPadel1.puedeUsar(visitante, momento.plusMinutes(10)), "con bebida caliente reciente no se usa el pádel");
		exigir(!mesaCubierta.puedeUsar(visitante, momento.plusMinutes(10)), "con bebida caliente reciente no se usa la mesa techada");
		exigir(canchaTenis1.puedeUsar(visitante, momento.plusMinutes(10)), "con bebida caliente sí se puede usar el tenis");
		exigir(mesaAbierta.puedeUsar(visitante, momento.plusMinutes(10)), "con bebida caliente sí se puede usar la mesa descubierta");
		exigir(canchaPadel1.puedeUsar(visitante, momento.plusMinutes(Socio.MINUTOS_BEBIDA_CALIENTE + 1)),
				"pasado el tiempo límite se vuelve a poder usar el pádel");

		futbol = new Futbol();
		voleibol = new Voleibol();
		baloncesto = new Baloncesto();
		exigirIgual(7, futbol.getMinimoJugadores(), "mínimo de jugadores de fútbol");
		exigirIgual(6, voleibol.getMinimoJugadores(), "mínimo de jugadores de voleibol");
		exigirIgual(5, baloncesto.getMinimoJugadores(), "mínimo de jugadores de baloncesto");
		exigirIgual(3, futbol.calcularPuntos(2, 1), "fútbol: victoria = 3 puntos");
		exigirIgual(1, futbol.calcularPuntos(1, 1), "fútbol: empate = 1 punto");
		exigirIgual(0, futbol.calcularPuntos(0, 1), "fútbol: derrota = 0 puntos");
		exigirIgual(1, baloncesto.calcularPuntos(80, 70), "baloncesto: victoria = 1 punto");
		exigirExcepcion(IllegalArgumentException.class, "empate en baloncesto", () -> baloncesto.calcularPuntos(70, 70));
		exigirExcepcion(IllegalArgumentException.class, "empate en voleibol", () -> voleibol.calcularPuntos(2, 2));
		exigir(futbol.admiteEmpate() && !voleibol.admiteEmpate() && !baloncesto.admiteEmpate(), "solo el fútbol admite empate");
		exigir(Categoria.SUB17.admiteEdad(15) && Categoria.SUB17.admiteEdad(16) && !Categoria.SUB17.admiteEdad(17),
				"límites de edad de SUB17");
		exigir(Categoria.MAYORES.admiteEdad(17) && !Categoria.SUB13.admiteEdad(13), "límites de edad de MAYORES y SUB13");
	}

	// ================================================================ 03 inscripciones individuales

	private static void inscripcionesIndividuales() throws Exception {
		exigir(club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO) != null, "ana se inscribe en tenis");
		exigir(club.inscribirEnDisciplina(pedro, tenis, NivelJugador.PRINCIPIANTE) != null, "pedro se inscribe en tenis");
		exigir(club.inscribirEnDisciplina(camilo, tenis, NivelJugador.AVANZADO) != null, "camilo se inscribe en tenis");
		exigir(club.inscribirEnDisciplina(ana, padel, NivelJugador.INTERMEDIO) != null, "ana se inscribe en pádel");
		exigir(club.inscribirEnDisciplina(pedro, padel, NivelJugador.INTERMEDIO) != null, "pedro se inscribe en pádel");
		exigir(club.inscribirEnDisciplina(camilo, padel, NivelJugador.INTERMEDIO) != null, "camilo se inscribe en pádel");
		exigir(club.inscribirEnDisciplina(jugador(3), padel, NivelJugador.INTERMEDIO) != null, "jugador03 se inscribe en pádel");
		exigir(club.inscribirEnDisciplina(jugador(1), tenisDeMesa, NivelJugador.INTERMEDIO) != null, "jugador01 se inscribe en mesa");
		exigir(club.inscribirEnDisciplina(jugador(2), tenisDeMesa, NivelJugador.PRINCIPIANTE) != null, "jugador02 se inscribe en mesa");

		exigir(club.inscribirEnDisciplina(ana, tenis, NivelJugador.AVANZADO) == null, "no se inscribe dos veces en la misma disciplina");
		exigir(club.inscribirEnDisciplina(null, tenis, NivelJugador.AVANZADO) == null, "inscribir un socio null devuelve null");
		exigir(club.inscribirEnDisciplina(ana, null, NivelJugador.AVANZADO) == null, "inscribir en una disciplina null devuelve null");

		exigirIgual(NivelJugador.INTERMEDIO, ana.getNivelEn(tenis), "nivel de ana en tenis");
		exigirIgual(NivelJugador.AVANZADO, camilo.getNivelEn(tenis), "nivel de camilo en tenis");
		exigir(ana.getNivelEn(tenisDeMesa) == null, "sin inscripción no hay nivel");
		exigirIgual(2, ana.getInscripcionesIndividuales().size(), "ana tiene dos inscripciones individuales");

		exigir(club.getRanking(tenis, ana) != null, "al inscribirse se crea el ranking");
		exigirCercano(1000, club.getRanking(tenis, ana).getPuntajeElo(), "el puntaje inicial del ranking es 1000");
		exigir(club.getRanking(tenisDeMesa, ana) == null, "sin inscripción no hay ranking");

		exigir(NivelJugador.PRINCIPIANTE.esCompatibleCon(NivelJugador.INTERMEDIO), "principiante e intermedio son adyacentes");
		exigir(NivelJugador.AVANZADO.esCompatibleCon(NivelJugador.INTERMEDIO), "avanzado e intermedio son adyacentes");
		exigir(!NivelJugador.PRINCIPIANTE.esCompatibleCon(NivelJugador.AVANZADO), "principiante y avanzado no son compatibles");
		exigir(NivelJugador.INTERMEDIO.esCompatibleCon(NivelJugador.INTERMEDIO), "un nivel es compatible consigo mismo");

		InscripcionIndividual temporal = new InscripcionIndividual(tenis, NivelJugador.PRINCIPIANTE);
		temporal.cambiarNivel(NivelJugador.AVANZADO);
		exigirIgual(NivelJugador.AVANZADO, temporal.getNivel(), "cambiarNivel actualiza el nivel");
		exigirExcepcion(IllegalArgumentException.class, "cambiarNivel(null)", () -> temporal.cambiarNivel(null));
	}

	// ================================================================ 04 reservas

	private static void reservas() throws Exception {
		Reserva primera = club.reservar(ana, tenis, canchaTenis1, FECHA_BASE, hora(9), hora(10), 2, Modalidad.SENCILLOS);
		exigir(primera != null, "una reserva libre se acepta");
		exigirIgual(1, club.getHistorial().getReservas().size(), "la reserva queda en el historial");

		exigir(club.reservar(pedro, tenis, canchaTenis1, FECHA_BASE, LocalTime.of(9, 30), LocalTime.of(10, 30), 2,
				Modalidad.SENCILLOS) == null, "una reserva que se solapa en la misma cancha se rechaza");
		exigir(club.reservar(pedro, tenis, canchaTenis1, FECHA_BASE, hora(10), hora(11), 2, Modalidad.SENCILLOS) != null,
				"una reserva contigua (termina cuando empieza otra) es válida");
		exigir(club.reservar(pedro, tenis, canchaTenis1, FECHA_BASE.plusDays(1), hora(9), hora(10), 2, Modalidad.SENCILLOS) != null,
				"el mismo horario otro día es válido");
		exigir(club.reservar(pedro, tenis, canchaTenis2, FECHA_BASE, hora(9), hora(10), 2, Modalidad.SENCILLOS) != null,
				"otra cancha de tenis en el mismo horario es válida (cupo de 2 simultáneas)");

		exigir(club.reservar(ana, tenis, canchaTenis2, FECHA_BASE, hora(11), hora(10), 2, Modalidad.SENCILLOS) == null,
				"hora de inicio posterior a la final se rechaza");
		exigir(club.reservar(ana, tenis, canchaTenis2, FECHA_BASE, hora(11), hora(11), 2, Modalidad.SENCILLOS) == null,
				"una reserva de duración cero se rechaza");
		exigir(club.reservar(ana, padel, canchaPadel1, FECHA_BASE, hora(11), hora(12), 2, Modalidad.SENCILLOS) == null,
				"el pádel no admite sencillos");
		exigir(club.reservar(ana, tenis, canchaTenis2, FECHA_BASE, hora(11), hora(12), 4, Modalidad.SENCILLOS) == null,
				"sencillos con 4 jugadores se rechaza");
		exigir(club.reservar(null, tenis, canchaTenis2, FECHA_BASE, hora(11), hora(12), 2, Modalidad.SENCILLOS) == null,
				"reservar sin socio devuelve null");
		exigir(club.reservar(ana, null, canchaTenis2, FECHA_BASE, hora(11), hora(12), 2, Modalidad.SENCILLOS) == null,
				"reservar sin disciplina devuelve null");
		exigir(club.reservar(ana, tenis, null, FECHA_BASE, hora(11), hora(12), 2, Modalidad.SENCILLOS) == null,
				"reservar sin instalación devuelve null");
		exigir(club.reservar(ana, tenis, canchaTenis2, FECHA_BASE, hora(12), hora(13), 4, Modalidad.DOBLES) != null,
				"dobles con 4 jugadores en tenis es válido");
		exigir(club.reservar(jugador(1), tenisDeMesa, mesaCubierta, FECHA_BASE, hora(9), hora(10), 4, Modalidad.DOBLES) != null,
				"dobles en mesa de tenis de mesa es válido");

		// Historia Socio-01: el club tiene un máximo de instalaciones simultáneas por disciplina (pádel: 2).
		exigir(club.reservar(camilo, padel, canchaPadel1, FECHA_BASE, hora(14), hora(15), 4, Modalidad.DOBLES) != null,
				"primera cancha de pádel a las 14:00");
		exigir(club.reservar(pedro, padel, canchaPadel2, FECHA_BASE, hora(14), hora(15), 4, Modalidad.DOBLES) != null,
				"segunda cancha de pádel a las 14:00 (cupo máximo)");
		exigir(club.reservar(ana, padel, canchaPadel3, FECHA_BASE, hora(14), hora(15), 4, Modalidad.DOBLES) == null,
				"una tercera cancha de pádel simultánea supera el cupo de la disciplina y debe rechazarse "
						+ "(DisciplinaIndividual.hayCupo existe pero reservar no lo usa)");
		exigir(!padel.hayCupo(club.getHistorial().getReservas(), FECHA_BASE, hora(14), hora(15)),
				"hayCupo es falso cuando ya hay 2 pádeles reservados");
		exigir(padel.hayCupo(club.getHistorial().getReservas(), FECHA_BASE, hora(16), hora(17)),
				"hayCupo es verdadero en un horario libre");

		Reserva ejemplo = club.getHistorial().getReservas().get(0);
		exigir(ejemplo.seSolapaCon(FECHA_BASE, LocalTime.of(9, 59), hora(11)), "seSolapaCon detecta un solape parcial");
		exigir(!ejemplo.seSolapaCon(FECHA_BASE, hora(10), hora(11)), "seSolapaCon no cuenta los extremos que se tocan");
		exigir(!ejemplo.seSolapaCon(FECHA_BASE.plusDays(1), hora(9), hora(10)), "seSolapaCon distingue las fechas");
		exigir(!ejemplo.seSolapaCon(null, hora(9), hora(10)), "seSolapaCon con fecha null es falso");
	}

	// ================================================================ 05 partidos de práctica

	private static void partidosDePractica() throws Exception {
		LocalDate fecha = FECHA_BASE.plusDays(2);

		// Historia Socio-03: niveles iguales o adyacentes, salvo que uno de los dos sea entrenador.
		PartidoPractica enLimite = new PartidoPractica(fecha, tenis, List.of(ana, pedro), canchaTenis1);
		exigir(enLimite.validarNiveles(), "intermedio y principiante son compatibles");
		exigir(new PartidoPractica(fecha, tenis, List.of(ana, camilo), canchaTenis1).validarNiveles(),
				"intermedio y avanzado son compatibles");
		exigir(!new PartidoPractica(fecha, tenis, List.of(pedro, camilo), canchaTenis1).validarNiveles(),
				"principiante y avanzado no son compatibles");
		exigir(new PartidoPractica(fecha, tenis, List.of(luis, pedro), canchaTenis1).validarNiveles(),
				"con un entrenador no se exige compatibilidad");
		exigirSinExcepcion("validarNiveles con un jugador que no está inscrito en la disciplina",
				() -> new PartidoPractica(fecha, tenis, List.of(ana, jugador(10)), canchaTenis1).validarNiveles());
		exigirExcepcion(IllegalArgumentException.class, "un partido de práctica con 3 jugadores",
				() -> new PartidoPractica(fecha, tenis, List.of(ana, pedro, camilo), canchaTenis1));

		PartidoPractica partido = club.programarPartidoPractica(tenis, canchaTenis1, fecha, List.of(ana, pedro));
		exigir(partido != null, "se programa un partido entre niveles compatibles");
		exigir(!partido.isFinalizado(), "un partido recién programado no está finalizado");
		exigir(club.programarPartidoPractica(tenis, canchaTenis1, fecha, List.of(pedro, camilo)) == null,
				"un partido entre principiante y avanzado (sin entrenador) debe negarse de inmediato "
						+ "(programarPartidoPractica no llama a validarNiveles)");
		exigir(club.programarPartidoPractica(tenis, canchaTenis1, fecha, List.of(ana)) == null, "con un solo jugador devuelve null");
		exigir(club.programarPartidoPractica(null, canchaTenis1, fecha, List.of(ana, pedro)) == null, "sin disciplina devuelve null");
		exigirSinExcepcion("programarPartidoPractica con 3 jugadores",
				() -> club.programarPartidoPractica(tenis, canchaTenis1, fecha, List.of(ana, pedro, camilo)));

		// Finalizar y ranking.
		List<EstadisticaPractica> estadisticas = List.of(new EstadisticaRaqueta(2, ana, 12, 5, 8),
				new EstadisticaRaqueta(1, pedro, 8, 2, 14));
		club.finalizarPartidoPractica(partido, estadisticas);
		exigir(partido.isFinalizado(), "el partido queda finalizado");
		exigirIgual(ana, partido.getGanador(), "gana quien tiene más sets");
		exigirIgual(2, partido.getEstadisticas().size(), "una estadística por jugador");
		double eloAna = club.getRanking(tenis, ana).getPuntajeElo();
		double eloPedro = club.getRanking(tenis, pedro).getPuntajeElo();
		exigir(eloAna > 1000, "tras ganar, el puntaje de ranking debe subir de 1000 (era " + eloAna + ")");
		exigir(eloPedro < 1000, "tras perder, el puntaje de ranking debe bajar de 1000 (era " + eloPedro + ")");

		sinSalidaDeError(() -> club.finalizarPartidoPractica(partido, estadisticas));
		exigirCercano(eloAna, club.getRanking(tenis, ana).getPuntajeElo(), "finalizar dos veces no actualiza el ranking otra vez");

		PartidoPractica incompleto = club.programarPartidoPractica(tenis, canchaTenis2, fecha, List.of(ana, pedro));
		sinSalidaDeError(() -> club.finalizarPartidoPractica(incompleto,
				List.of(new EstadisticaRaqueta(1, ana, 6, 1, 3))));
		exigir(!incompleto.isFinalizado(), "faltan estadísticas de un jugador: el partido no se finaliza");
		sinSalidaDeError(() -> club.finalizarPartidoPractica(incompleto,
				List.of(new EstadisticaRaqueta(1, ana, 6, 1, 3), new EstadisticaRaqueta(1, camilo, 6, 1, 3))));
		exigir(!incompleto.isFinalizado(), "una estadística de un jugador ajeno al partido: no se finaliza");
		exigirSinExcepcion("finalizarPartidoPractica(null, ...)", () -> club.finalizarPartidoPractica(null, estadisticas));

		// Tenis de mesa (estadística distinta) y pádel en dobles.
		PartidoPractica mesa = club.programarPartidoPractica(tenisDeMesa, mesaAbierta, fecha, List.of(jugador(1), jugador(2)));
		exigir(mesa != null, "partido de tenis de mesa entre jugadores compatibles");
		club.finalizarPartidoPractica(mesa, List.of(new EstadisticaTenisDeMesa(3, jugador(1), 33),
				new EstadisticaTenisDeMesa(1, jugador(2), 25)));
		exigir(mesa.isFinalizado() && mesa.getGanador() == jugador(1), "gana jugador01 en tenis de mesa");
		exigir(club.getRanking(tenisDeMesa, jugador(1)).getPuntajeElo() > club.getRanking(tenisDeMesa, jugador(2)).getPuntajeElo(),
				"el ganador de tenis de mesa queda por encima en el ranking");

		PartidoPractica dobles = club.programarPartidoPractica(padel, canchaPadel1, fecha, List.of(ana, pedro, camilo, jugador(3)));
		exigir(dobles != null, "partido de pádel en dobles con 4 jugadores");
		club.finalizarPartidoPractica(dobles, List.of(new EstadisticaRaqueta(2, ana, 12, 3, 6),
				new EstadisticaRaqueta(1, pedro, 9, 1, 8), new EstadisticaRaqueta(0, camilo, 7, 0, 9),
				new EstadisticaRaqueta(1, jugador(3), 8, 2, 7)));
		exigir(dobles.isFinalizado() && dobles.getGanador() == ana, "ana gana el partido de pádel");
	}

	// ================================================================ 06 equipos

	private static void equipos() throws Exception {
		cerros = club.crearEquipo("Cerros FC", 8, futbol, Categoria.MAYORES);
		andes = club.crearEquipo("Andes FC", 8, futbol, Categoria.MAYORES);
		sub17 = club.crearEquipo("Seneca Sub17", 8, futbol, Categoria.SUB17);
		voley = club.crearEquipo("Voley Seneca", 8, voleibol, Categoria.MAYORES);
		basket = club.crearEquipo("Basket Seneca", 6, baloncesto, Categoria.MAYORES);
		exigir(cerros != null && andes != null && sub17 != null && voley != null && basket != null, "crearEquipo con datos válidos");
		exigir(club.crearEquipo("cerros fc", 8, futbol, Categoria.MAYORES) == null, "no se repite el nombre de un equipo (sin distinguir mayúsculas)");
		exigir(club.crearEquipo("Muy chico", 4, futbol, Categoria.MAYORES) == null, "el cupo no puede ser menor al mínimo del deporte");
		exigirIgual(8, cerros.getCupoMaximo(), "cupo máximo del equipo");

		for (int numero = 1; numero <= 8; numero++) {
			exigir(club.inscribirJugador(jugador(numero), cerros, "Posicion " + numero, numero),
					"jugador" + numero + " entra a Cerros FC");
		}
		exigirIgual(8, cerros.getInscripciones().size(), "Cerros FC tiene 8 jugadores");
		boolean rechazoPorCupo = !club.inscribirJugador(jugador(16), cerros, "Suplente", 20);
		exigir(cerros.getInscripcionDe(jugador(16)) == null, "el jugador rechazado por cupo no queda inscrito");
		exigirIgual(8, cerros.getInscripciones().size(), "no se supera el cupo máximo");

		// Historia Socio-02: un solo equipo por deporte, pero se puede estar en varios deportes.
		boolean rechazoPorDeporte = !club.inscribirJugador(jugador(1), andes, "Defensa", 2);
		exigir(andes.getInscripcionDe(jugador(1)) == null, "el jugador no queda duplicado en el segundo equipo de fútbol");
		exigir(club.inscribirJugador(jugador(1), voley, "Central", 1), "el mismo jugador sí puede estar en voleibol");
		exigir(club.inscribirJugador(jugador(1), basket, "Base", 1), "el mismo jugador sí puede estar en baloncesto");

		// Camiseta repetida y edad.
		exigir(club.inscribirJugador(jugador(9), andes, "Portero", 5), "jugador09 entra a Andes FC con la 5");
		boolean rechazoPorCamiseta = !club.inscribirJugador(jugador(10), andes, "Defensa", 5);
		exigir(andes.getInscripcionDe(jugador(10)) == null, "el jugador con camiseta repetida no queda inscrito");
		for (int numero = 10; numero <= 15; numero++) {
			exigir(club.inscribirJugador(jugador(numero), andes, "Posicion " + numero, numero), "jugador" + numero + " entra a Andes FC");
		}
		boolean rechazoPorEdadMayores = !club.inscribirJugador(nino, cerros, "Extremo", 30);
		boolean rechazoPorEdadSub17 = !club.inscribirJugador(nina, sub17, "Extremo", 3);
		exigir(cerros.getInscripcionDe(nino) == null, "un menor de 17 no entra a un equipo de mayores");
		exigir(sub17.getInscripcionDe(nina) == null, "una niña de 11 no entra a un equipo sub17");
		exigir(club.inscribirJugador(nino, sub17, "Extremo", 3), "un jugador de 15 entra a un equipo sub17");

		for (int numero = 2; numero <= 6; numero++) {
			exigir(club.inscribirJugador(jugador(numero), voley, "Posicion " + numero, numero), "jugador" + numero + " entra a Voley");
		}
		for (int numero = 2; numero <= 5; numero++) {
			exigir(club.inscribirJugador(jugador(numero), basket, "Posicion " + numero, numero), "jugador" + numero + " entra a Basket");
		}

		exigir(rechazoPorCupo && rechazoPorDeporte && rechazoPorCamiseta && rechazoPorEdadMayores && rechazoPorEdadSub17,
				"inscribirJugador debe devolver false cuando el equipo rechaza al jugador (cupo lleno, otro equipo del mismo deporte, "
						+ "camiseta repetida, edad fuera de categoría): hoy devuelve true siempre");
		exigir(cerros.puedeJugar() && andes.puedeJugar(), "equipos de fútbol con 7 o más habilitados pueden jugar");
		exigir(voley.puedeJugar() && basket.puedeJugar(), "voleibol (6) y baloncesto (5) llegan a su mínimo");
		exigir(!sub17.puedeJugar(), "un equipo con un solo jugador no puede jugar");
		exigir(cerros.camisetaOcupada(3) && !cerros.camisetaOcupada(99), "camisetaOcupada");
		exigir(cerros.estaHabilitado(jugador(1)) && !cerros.estaHabilitado(jugador(16)), "estaHabilitado");
		exigir(jugador(1).tieneEquipoEn(futbol) && !jugador(16).tieneEquipoEn(futbol), "tieneEquipoEn");
		exigirIgual(3, jugador(1).getInscripcionesEquipo().size(), "jugador01 está en fútbol, voleibol y baloncesto");
	}

	// ================================================================ 07 sanciones

	private static void sanciones() throws Exception {
		InscripcionEquipo primero = cerros.getInscripcionDe(jugador(1));
		InscripcionEquipo segundo = cerros.getInscripcionDe(jugador(2));

		primero.registrarTarjetas(1, 0);
		exigir(primero.estaHabilitado(), "una amarilla no suspende");
		exigirIgual(1, primero.getControlSanciones().getAmarillasAcumuladas(), "se acumula una amarilla");
		primero.registrarTarjetas(1, 0);
		exigir(!primero.estaHabilitado(), "dos amarillas suspenden");
		exigirIgual(0, primero.getControlSanciones().getAmarillasAcumuladas(), "al suspender se reinicia el conteo de amarillas");
		exigirIgual(7, cerros.contarJugadoresHabilitados(), "queda un jugador menos habilitado");
		exigir(cerros.puedeJugar(), "con 7 habilitados el equipo aún puede jugar");

		segundo.registrarTarjetas(0, 1);
		exigir(!segundo.estaHabilitado(), "una roja suspende");
		exigirIgual(2, cerros.getInscripcionesSuspendidas().size(), "dos inscripciones suspendidas");
		exigir(!cerros.puedeJugar(), "con 6 habilitados el equipo de fútbol no puede jugar");
		exigir(!new PartidoInterno(FECHA_BASE, cerros, andes, 0, 0).puedeJugarse(), "el partido interno no puede jugarse");
		exigir(!new PartidoExterno(FECHA_BASE, 0, 0, "Rival", "Estadio", cerros).puedeJugarse(), "el partido externo no puede jugarse");
		exigirExcepcion(IllegalArgumentException.class, "tarjetas negativas", () -> primero.registrarTarjetas(-1, 0));

		primero.cumplirSuspension();
		segundo.cumplirSuspension();
		exigirIgual(8, cerros.contarJugadoresHabilitados(), "cumplir la suspensión habilita de nuevo");
		exigir(cerros.puedeJugar(), "con todos habilitados el equipo juega");

		ControlSanciones control = new ControlSanciones();
		control.suspender();
		exigirIgual("SUSPENDIDO", control.getEstado().name(), "suspender cambia el estado");
		control.habilitar();
		exigir(control.estaHabilitado(), "habilitar devuelve al estado habilitado");
	}

	// ================================================================ 08 partidos internos

	private static void partidosInternos() throws Exception {
		EstadisticaFutbol golesUno = new EstadisticaFutbol(cerros.getInscripcionDe(jugador(1)), 2, 1, 0, 0);
		EstadisticaFutbol golesDos = new EstadisticaFutbol(cerros.getInscripcionDe(jugador(2)), 1, 0, 0, 0);
		EstadisticaFutbol golesAndes = new EstadisticaFutbol(andes.getInscripcionDe(jugador(9)), 1, 0, 0, 0);
		exigirIgual(5, golesUno.calcularAporte(), "aporte de fútbol = goles*2 + asistencias");
		exigirIgual(11, new EstadisticaBaloncesto(basket.getInscripcionDe(jugador(2)), 4, 3).calcularAporte(),
				"aporte de baloncesto = puntos*2 + rebotes");
		exigirIgual(5, new EstadisticaVoleibol(voley.getInscripcionDe(jugador(2)), 3, 2).calcularAporte(),
				"aporte de voleibol = aces + bloqueos");

		int antes = club.getHistorial().getPartidos().size();
		PartidoInterno primero = club.registrarPartidoInterno(cerros, andes, 3, 1, List.of(golesUno, golesDos, golesAndes));
		exigir(primero != null, "partido interno entre equipos válidos");
		exigirIgual(antes + 1, club.getHistorial().getPartidos().size(), "el partido queda en el historial");
		exigirIgual(cerros, primero.getGanador(), "gana el equipo con más goles");
		exigirIgual(3, primero.getEstadisticas().size(), "se guardan las estadísticas");
		exigir(primero.puedeJugarse(), "ambos equipos tienen el mínimo de habilitados");
		PartidoInterno empate = club.registrarPartidoInterno(cerros, andes, 2, 2, null);
		exigir(empate != null && empate.getGanador() == null, "un empate no tiene ganador");
		empate.registrarMarcador(2, 3);
		exigirIgual(andes, empate.getGanador(), "registrarMarcador actualiza el resultado");

		exigir(club.registrarPartidoInterno(cerros, cerros, 1, 0, null) == null, "un equipo no juega contra sí mismo");
		exigir(club.registrarPartidoInterno(null, andes, 1, 0, null) == null, "equipo null devuelve null");
		exigir(club.registrarPartidoInterno(cerros, voley, 1, 0, null) == null, "equipos de distinto deporte se rechazan");
		exigir(club.registrarPartidoInterno(cerros, sub17, 1, 0, null) == null,
				"equipos de distinta categoría se rechazan (los partidos internos son de la misma categoría y deporte)");

		// Un equipo sin el mínimo de jugadores habilitados no puede jugar.
		InscripcionEquipo tercero = cerros.getInscripcionDe(jugador(3));
		InscripcionEquipo cuarto = cerros.getInscripcionDe(jugador(4));
		tercero.registrarTarjetas(0, 1);
		cuarto.registrarTarjetas(0, 1);
		exigir(club.registrarPartidoInterno(cerros, andes, 1, 0, null) == null,
				"un partido con un equipo que no alcanza el mínimo de habilitados no puede jugarse (el club no lo valida)");
		tercero.cumplirSuspension();
		cuarto.cumplirSuspension();

		// Las tarjetas del partido deben reflejarse en la suspensión del jugador.
		InscripcionEquipo quinto = cerros.getInscripcionDe(jugador(5));
		club.registrarPartidoInterno(cerros, andes, 1, 0, List.of(new EstadisticaFutbol(quinto, 0, 0, 2, 0)));
		exigir(!cerros.estaHabilitado(jugador(5)),
				"dos amarillas en un partido deben suspender al jugador (el club no aplica las tarjetas de la estadística)");
		quinto.cumplirSuspension();
	}

	// ================================================================ 09 competencias

	private static void competencias() throws Exception {
		torneo = new TorneoCerros("Torneo de los Cerros 2026", "Universidades de Bogotá", 2026, "Liga", 7, Categoria.MAYORES, futbol);
		ascun = new ASCUN("ASCUN Futbol 2026", "ASCUN", 2026, "Grupos y eliminación directa", 7, Categoria.MAYORES, futbol, FaseASCUN.GRUPOS);
		List<Competencia> competenciasClub = listaPrivada(club, "competencias");

		exigir(club.inscribirEquipoEnCompetencia(cerros, torneo), "Cerros FC cumple los requisitos del torneo");
		exigir(club.inscribirEquipoEnCompetencia(andes, torneo), "Andes FC cumple los requisitos del torneo");
		exigir(!club.inscribirEquipoEnCompetencia(cerros, torneo), "un equipo no se inscribe dos veces");
		exigir(!club.inscribirEquipoEnCompetencia(sub17, torneo), "categoría distinta: no se inscribe");
		exigir(!club.inscribirEquipoEnCompetencia(voley, torneo), "deporte distinto: no se inscribe");
		exigir(!club.inscribirEquipoEnCompetencia(null, torneo) && !club.inscribirEquipoEnCompetencia(cerros, null), "argumentos null");
		exigirIgual(1, competenciasClub.size(), "el club registra la competencia al inscribir el primer equipo");
		exigirIgual(2, torneo.getInscripciones().size(), "dos equipos inscritos en el torneo");

		InscripcionEquipo temporal = sub17.getInscripcionDe(nino);
		exigir(temporal != null, "(control) el sub17 tiene a su jugador");
		InscripcionCompetencia inscripcionCerros = torneo.getInscripcionDe(cerros);
		for (int numero = 1; numero <= 7; numero++) {
			exigir(inscripcionCerros.agregarANomina(jugador(numero)), "jugador" + numero + " entra a la nómina del torneo");
		}
		exigir(!inscripcionCerros.agregarANomina(jugador(1)), "no se repite un jugador en la nómina");
		exigir(!inscripcionCerros.agregarANomina(jugador(16)), "un jugador ajeno al equipo no entra a la nómina");
		exigir(inscripcionCerros.puedeAlinear(jugador(1)), "un jugador de la nómina puede alinearse");
		exigir(!inscripcionCerros.puedeAlinear(jugador(8)), "un jugador fuera de la nómina no puede alinearse");

		// Partidos del torneo (liga): 3 puntos por victoria, 1 por empate.
		List<EstadisticaConjunto> primerPartido = List.of(
				new EstadisticaFutbol(cerros.getInscripcionDe(jugador(1)), 2, 0, 0, 0),
				new EstadisticaFutbol(cerros.getInscripcionDe(jugador(2)), 1, 1, 0, 0));
		List<EstadisticaConjunto> segundoPartido = List.of(
				new EstadisticaFutbol(cerros.getInscripcionDe(jugador(1)), 1, 0, 0, 0));
		PartidoExterno victoria = club.registrarPartidoExterno(torneo, cerros, FECHA_BASE.plusDays(3), 3, 1,
				"Universidad Rival", "Estadio Rival", primerPartido);
		exigir(victoria != null, "partido externo del torneo");
		exigirIgual(3, torneo.getTablaPosiciones().get(cerros), "una victoria suma 3 puntos");
		club.registrarPartidoExterno(torneo, cerros, FECHA_BASE.plusDays(4), 1, 1, "Otra Universidad", "Coliseo", segundoPartido);
		exigirIgual(4, torneo.getTablaPosiciones().get(cerros), "un empate suma 1 punto");
		club.registrarPartidoExterno(torneo, cerros, FECHA_BASE.plusDays(5), 0, 2, "Tercera Universidad", "Cancha 3", null);
		exigirIgual(4, torneo.getTablaPosiciones().get(cerros), "una derrota suma 0 puntos");
		club.registrarPartidoExterno(torneo, andes, FECHA_BASE.plusDays(5), 2, 0, "Cuarta Universidad", "Cancha 4", null);
		exigirIgual(3, torneo.getTablaPosiciones().get(andes), "Andes suma su victoria");
		exigirIgual(cerros, torneo.getLider(), "Cerros lidera con 4 puntos");
		exigirIgual(4, torneo.getListaPartidos().size(), "el torneo guarda sus 4 partidos");
		exigirIgual(jugador(1), torneo.getGoleador(), "el goleador del torneo es jugador01 (3 goles)");
		exigirIgual(jugador(1), torneo.getJugadorDestacado(), "el jugador destacado es jugador01 (aporte 7)");
		exigir(club.registrarPartidoExterno(null, cerros, FECHA_BASE, 1, 0, "X", "Y", null) == null, "sin competencia devuelve null");
		exigir(club.registrarPartidoExterno(torneo, null, FECHA_BASE, 1, 0, "X", "Y", null) == null, "sin equipo devuelve null");

		// Nómina: solo los jugadores de la nómina disputan partidos oficiales.
		int partidosAntes = torneo.getListaPartidos().size();
		club.registrarPartidoExterno(torneo, cerros, FECHA_BASE.plusDays(6), 1, 0, "Quinta Universidad", "Cancha 5",
				List.of(new EstadisticaFutbol(cerros.getInscripcionDe(jugador(8)), 1, 0, 0, 0)));
		exigirIgual(partidosAntes, torneo.getListaPartidos().size(),
				"un jugador que no está en la nómina no puede disputar un partido oficial (el club no lo valida)");

		RegistroNomina registro = inscripcionCerros.getRegistroDe(jugador(2));
		registro.registrarTarjetas(2, 0);
		exigir(!inscripcionCerros.puedeAlinear(jugador(2)), "dos amarillas en la competencia suspenden en la nómina");
		exigir(cerros.estaHabilitado(jugador(2)), "el conteo de la competencia es independiente del de los partidos internos");
		int partidosAntesSuspendido = torneo.getListaPartidos().size();
		club.registrarPartidoExterno(torneo, cerros, FECHA_BASE.plusDays(6), 1, 0, "Sexta Universidad", "Cancha 6",
				List.of(new EstadisticaFutbol(cerros.getInscripcionDe(jugador(2)), 1, 0, 0, 0)));
		exigirIgual(partidosAntesSuspendido, torneo.getListaPartidos().size(),
				"un jugador suspendido en la competencia no puede alinearse (el club no lo valida)");
		registro.cumplirSuspension();
		exigir(inscripcionCerros.puedeAlinear(jugador(2)), "cumplir la suspensión lo habilita de nuevo");

		// ASCUN: fase de grupos y eliminación directa.
		exigirIgual(FaseASCUN.GRUPOS, ascun.getFase(), "ASCUN empieza en fase de grupos");
		exigir(club.inscribirEquipoEnCompetencia(cerros, ascun) && club.inscribirEquipoEnCompetencia(andes, ascun),
				"los dos equipos se inscriben en ASCUN");
		exigirIgual(2, competenciasClub.size(), "el club registra las dos competencias");
		club.registrarPartidoExterno(ascun, cerros, FECHA_BASE.plusDays(7), 2, 0, "U Uno", "E1", null);
		club.registrarPartidoExterno(ascun, cerros, FECHA_BASE.plusDays(8), 1, 0, "U Dos", "E2", null);
		club.registrarPartidoExterno(ascun, cerros, FECHA_BASE.plusDays(9), 0, 1, "U Tres", "E3", null);
		club.registrarPartidoExterno(ascun, andes, FECHA_BASE.plusDays(9), 0, 1, "U Tres", "E3", null);
		List<Equipo> clasificados = ascun.determinarClasificados();
		exigir(clasificados.contains(cerros), "Cerros (2 victorias, 1 derrota) clasifica");
		exigir(!clasificados.contains(andes), "Andes (0 victorias, 1 derrota) no clasifica");
		ascun.avanzarFase();
		exigirIgual(FaseASCUN.ELIMINACION_DIRECTA, ascun.getFase(), "avanzarFase pasa a eliminación directa");
		ascun.avanzarFase();
		exigirIgual(FaseASCUN.ELIMINACION_DIRECTA, ascun.getFase(), "no hay fase posterior a la eliminación directa");
	}

	// ================================================================ 10 entrenamientos

	private static void entrenamientos() throws Exception {
		LocalDate viernes = FECHA_BASE.plusDays(4);
		List<Socio> convocados = new ArrayList<>(jugadores.subList(0, 5));

		// Historia Entrenador-01.
		sesionBasket = club.planearSesion(luis, viernes, hora(17), 90, canchaBaloncesto, convocados);
		exigir(sesionBasket != null, "se planea una sesión en una cancha libre");
		exigirIgual(1, club.getHistorial().getSesiones().size(), "la sesión queda en el historial");
		exigir(club.planearSesion(carla, viernes, hora(18), 60, canchaBaloncesto, convocados) == null,
				"la cancha ya está reservada a esa hora: se rechaza");
		exigir(club.planearSesion(luis, viernes, LocalTime.of(17, 30), 60, canchaFutbol, convocados) == null,
				"el mismo entrenador no puede estar en dos sesiones a la vez");
		exigir(club.planearSesion(carla, viernes, hora(17), 60, canchaFutbol, new ArrayList<>(jugadores.subList(5, 8))) != null,
				"otro entrenador en otra cancha a la misma hora es válido");
		exigir(club.planearSesion(luis, viernes, LocalTime.of(18, 30), 60, canchaBaloncesto, convocados) != null,
				"una sesión que empieza cuando termina la anterior es válida");
		exigir(club.planearSesion(luis, viernes.plusDays(1), hora(17), 0, canchaBaloncesto, convocados) == null, "duración cero se rechaza");
		exigir(club.planearSesion(null, viernes, hora(9), 60, canchaBaloncesto, convocados) == null, "sin entrenador devuelve null");
		exigir(club.planearSesion(luis, viernes, hora(9), 60, null, convocados) == null, "sin instalación devuelve null");
		exigir(sesionBasket.seSolapaCon(viernes, hora(18), 30) && !sesionBasket.seSolapaCon(viernes, LocalTime.of(18, 30), 30),
				"seSolapaCon en la sesión");

		// Historias Entrenador-02 y Entrenador-03.
		club.registrarAsistencia(sesionBasket, jugador(1), EstadoAsistencia.ASISTIO, 5, "Excelente puntería");
		club.registrarAsistencia(sesionBasket, jugador(2), EstadoAsistencia.LLEGOTARDE, 3, "Debe llegar a tiempo");
		club.registrarAsistencia(sesionBasket, jugador(3), EstadoAsistencia.FALTO, 0, null);
		exigirIgual(3, sesionBasket.getAsistencias().size(), "se registran las tres asistencias");
		exigirIgual(5, buscarAsistencia(sesionBasket, jugador(1)).getEvaluacion(), "la evaluación queda registrada");

		club.registrarAsistencia(sesionBasket, jugador(10), EstadoAsistencia.ASISTIO, 4, "No convocado");
		exigirIgual(3, sesionBasket.getAsistencias().size(), "un jugador no convocado no se registra");
		club.registrarAsistencia(sesionBasket, jugador(1), EstadoAsistencia.ASISTIO, 2, "Duplicado");
		exigirIgual(5, buscarAsistencia(sesionBasket, jugador(1)).getEvaluacion(), "no se registra dos veces la asistencia de un jugador");
		club.registrarAsistencia(sesionBasket, jugador(4), EstadoAsistencia.ASISTIO, 6, "Fuera de rango");
		AsistenciaEntrenamiento fueraDeRango = buscarAsistencia(sesionBasket, jugador(4));
		exigir(fueraDeRango == null || fueraDeRango.getEvaluacion() == 0, "una nota fuera de 1 a 5 no queda registrada");
		club.registrarAsistencia(sesionBasket, jugador(5), EstadoAsistencia.FALTO, 4, "Faltó pero lo evalúan");
		AsistenciaEntrenamiento faltoEvaluado = buscarAsistencia(sesionBasket, jugador(5));
		exigir(faltoEvaluado == null || faltoEvaluado.getEvaluacion() == 0, "no se evalúa a quien faltó");
		exigirSinExcepcion("registrarAsistencia con sesión null", () -> club.registrarAsistencia(null, jugador(1), EstadoAsistencia.ASISTIO, 0, null));

		AsistenciaEntrenamiento aislada = new AsistenciaEntrenamiento(0, null, EstadoAsistencia.ASISTIO, jugador(1));
		exigir(aislada.registrarEvaluacion(1, "ok") && aislada.registrarEvaluacion(5, "ok"), "notas 1 y 5 son válidas");
		exigir(!aislada.registrarEvaluacion(0, "mal") && !aislada.registrarEvaluacion(6, "mal"), "notas 0 y 6 son inválidas");
		exigir(aislada.toString().contains("Jugador 1"), "toString de la asistencia nombra al jugador");

		Map<Socio, Integer> reporte = new GeneradorReportes().reporteAsistencias(sesionBasket.getAsistencias());
		exigirIgual(1, reporte.get(jugador(1)), "reporte de asistencias: jugador01 asistió una vez");
		exigirIgual(1, reporte.get(jugador(2)), "reporte de asistencias: llegar tarde cuenta como asistencia");
		exigir(!reporte.containsKey(jugador(3)) || reporte.get(jugador(3)) == 0,
				"reporte de asistencias: quien faltó no suma asistencias (reporteAsistencias cuenta todos los registros)");
	}

	// ================================================================ 11 ventas

	private static void ventas() throws Exception {
		balon = new Articulo("Balón de fútbol", "Fútbol", 50000, 10);
		raqueta = new Articulo("Raqueta de tenis", "Tenis", 120000, 5);
		cafe = new Bebida("Café", "Bebidas calientes", 4000, 20, true);
		jugo = new Bebida("Jugo de naranja", "Bebidas frías", 5000, 20, false);
		barra = new Snack("Barra de maní", "Snacks", 3000, 15, new ArrayList<>(List.of(Alergeno.MANI)));
		club.reabastecerTienda(balon, 3);
		club.reabastecerTienda(raqueta, 2);
		club.reabasteceCafeteriaSocios(cafe, 5);
		club.reabasteceCafeteriaSocios(jugo, 5);
		club.reabasteceCafeteriaSocios(barra, 5);
		exigirCercano(0.1, CafeteriaSocios.getPropinaSugerida(), "propina sugerida de la cafetería");

		// Tienda: IVA 19 %, 2 % del valor en puntos de fidelidad.
		Socio comprador = jugador(16);
		double ventasAntes = totalGeneralVentas();
		club.registrarVenta(comprador, pedido(balon, 2), Rubro.TIENDA, null, null, 0, 0);
		exigirCercano(119000, totalGeneralVentas() - ventasAntes, "2 balones de 50.000 con IVA del 19 %");
		exigirCercano(119000, totalReporte(Rubro.TIENDA),
				"la venta de la tienda debe contarse en el rubro Tienda (Tienda.vender la registra como Cafeteria)");
		exigirIgual(2380, comprador.getPuntosFidelidad(), "cada venta genera el 2 % de su valor en puntos para el comprador");
		exigirIgual(8, balon.getCantidad(), "la venta descuenta las existencias del producto");
		double ventasTrasStock = totalGeneralVentas();
		club.registrarVenta(comprador, pedido(balon, 50), Rubro.TIENDA, null, null, 0, 0);
		exigirCercano(0, totalGeneralVentas() - ventasTrasStock, "no se vende más de lo que hay en existencias");

		// Cafetería: impuesto al consumo 8 % y propina.
		double antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(jugador(15), pedido(jugo, 2), Rubro.CAFETERIA, canchaFutbol, null, 0, 0);
		exigirCercano(10800, totalReporte(Rubro.CAFETERIA) - antesCafeteria, "2 jugos de 5.000 con impuesto al consumo del 8 %");
		Map<Producto, Integer> combo = new LinkedHashMap<>();
		combo.put(jugo, 1);
		combo.put(barra, 1);
		antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(jugador(14), combo, Rubro.CAFETERIA, canchaFutbol, null, 0, 800);
		exigirCercano(9440, totalReporte(Rubro.CAFETERIA) - antesCafeteria, "jugo + snack con impuesto y propina de 800");

		// Historia Empleado-03: 15 % al empleado, 8 % a quien comparte su código.
		antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(luis, pedido(jugo, 1), Rubro.CAFETERIA, canchaFutbol, luis.getCodigoDescuento(), 0, 0);
		exigirCercano(4590, totalReporte(Rubro.CAFETERIA) - antesCafeteria, "el empleado paga 15 % menos con su código");
		antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(jugador(13), pedido(jugo, 1), Rubro.CAFETERIA, canchaFutbol, luis.getCodigoDescuento(), 0, 0);
		exigirCercano(4968, totalReporte(Rubro.CAFETERIA) - antesCafeteria,
				"un socio que usa el código de un empleado recibe solo 8 % (CodigoDescuento usa 15 % para ambos)");
		exigirCercano(0.15, luis.getCodigoDescuento().getPorcentaje(true), "porcentaje del dueño");
		exigirCercano(0.08, luis.getCodigoDescuento().getPorcentaje(false), "porcentaje compartido");

		// Historia Socio-04: redimir puntos.
		Socio conPuntos = jugador(12);
		conPuntos.acumularPuntos(5000);
		antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(conPuntos, pedido(barra, 1), Rubro.CAFETERIA, canchaFutbol, null, 1000, 0);
		exigirCercano(2240, totalReporte(Rubro.CAFETERIA) - antesCafeteria, "3.240 menos 1.000 puntos redimidos (1 punto = 1 peso)");
		exigirIgual(4045, conPuntos.getPuntosFidelidad(),
				"tras redimir 1.000 puntos se descuentan y se suman los 45 generados por la compra");
		exigirExcepcion(IllegalArgumentException.class, "redimir más puntos de los que se tienen", () -> conPuntos.redimirPuntos(999999));
		exigirExcepcion(IllegalArgumentException.class, "redimir 0 puntos", () -> conPuntos.redimirPuntos(0));

		// Regla de seguridad: bebida caliente en instalaciones que no la permiten.
		CafeteriaSocios cafeteriaAislada = new CafeteriaSocios();
		exigir(!cafeteriaAislada.puedeDespachar(cafe, canchaPadel1), "no se despacha una bebida caliente en la cancha de pádel");
		exigir(!cafeteriaAislada.puedeDespachar(cafe, mesaCubierta), "no se despacha una bebida caliente en la mesa techada");
		exigir(cafeteriaAislada.puedeDespachar(cafe, canchaTenis1), "sí se despacha una bebida caliente en la cancha de tenis");
		exigir(cafeteriaAislada.puedeDespachar(cafe, mesaAbierta), "sí se despacha una bebida caliente en la mesa descubierta");
		exigir(cafeteriaAislada.puedeDespachar(jugo, canchaPadel1), "una bebida fría se despacha en cualquier instalación");
		exigir(cafeteriaAislada.puedeDespachar(cafe, canchaFutbol), "una bebida caliente se despacha en una cancha de conjunto");
		antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(jugador(11), pedido(cafe, 1), Rubro.CAFETERIA, canchaPadel1, null, 0, 0);
		exigirCercano(0, totalReporte(Rubro.CAFETERIA) - antesCafeteria,
				"vender un café en la cancha de pádel debe rechazarse (CafeteriaSocios.vender ignora el resultado de puedeDespachar)");
		antesCafeteria = totalReporte(Rubro.CAFETERIA);
		club.registrarVenta(jugador(11), pedido(cafe, 1), Rubro.CAFETERIA, canchaTenis1, null, 0, 0);
		exigirCercano(4320, totalReporte(Rubro.CAFETERIA) - antesCafeteria, "un café en la cancha de tenis sí se vende (con impuesto)");
		exigir(jugador(11).llevaBebidaCaliente(LocalDateTime.now()),
				"tras comprar una bebida caliente el socio queda marcado como portador (nadie llama a registrarBebidaCaliente)");

		// Ventas inválidas.
		double totalAntes = totalGeneralVentas();
		exigirSinExcepcion("registrarVenta con datos inválidos", () -> {
			club.registrarVenta(null, pedido(jugo, 1), Rubro.CAFETERIA, null, null, 0, 0);
			club.registrarVenta(jugador(1), new LinkedHashMap<Producto, Integer>(), Rubro.CAFETERIA, null, null, 0, 0);
			club.registrarVenta(jugador(1), pedido(jugo, 1), null, null, null, 0, 0);
			club.registrarVenta(jugador(1), pedido(jugo, 1), Rubro.MENSUALIDAD, null, null, 0, 0);
		});
		exigirCercano(0, totalGeneralVentas() - totalAntes, "las ventas inválidas no se registran");
		exigir(new Venta(0, 0, Rubro.TIENDA, null, jugador(1), List.of(new LineaVenta(1, balon)), 0.19).getTotal() > 0,
				"una venta construida directamente calcula su total");
		exigirSinExcepcion("reabastecer con cantidad inválida", () -> {
			club.reabastecerTienda(null, 5);
			club.reabastecerTienda(balon, 0);
			club.reabasteceCafeteriaSocios(null, 5);
		});
	}

	// ================================================================ 12 sugerencias e inventario

	private static void sugerenciasEInventario() throws Exception {
		SugerenciaArticulo aprobada = club.registrarSugerencia(luis, "Agua de coco", "Bebidas frías", 4500);
		exigir(aprobada != null && aprobada.getEstado() == EstadoSugerencia.PENDIENTE, "una sugerencia nace Pendiente");
		SugerenciaArticulo rechazada = club.registrarSugerencia(carla, "Chicle", "Snacks", 1000);
		exigir(club.registrarSugerencia(null, "Nada", "Nada", 1) == null, "sin empleado no hay sugerencia");
		club.resolverSugerencia(aprobada, true);
		exigirIgual(EstadoSugerencia.ACEPTADA, aprobada.getEstado(), "el administrador aprueba la sugerencia");
		administrador.resolverSugerencia(rechazada, false);
		exigirIgual(EstadoSugerencia.RECHAZADA, rechazada.getEstado(), "el administrador rechaza la sugerencia");
		SugerenciaArticulo ajena = new SugerenciaArticulo("Ajena", "Otra", 10);
		club.resolverSugerencia(ajena, true);
		exigirIgual(EstadoSugerencia.PENDIENTE, ajena.getEstado(), "una sugerencia no registrada en el club se ignora");
		exigirSinExcepcion("resolverSugerencia(null)", () -> club.resolverSugerencia(null, true));

		// Historia Administrador-02: mover artículos entre ubicaciones.
		Ubicacion bodega = new Ubicacion("Bodega");
		Ubicacion sucursal = new Ubicacion("Sucursal Norte");
		Inventario inventarioBodega = new Inventario(new ArrayList<Instalacion>(List.of(canchaTenis1)), bodega);
		Inventario inventarioSucursal = new Inventario(new ArrayList<Instalacion>(), sucursal);
		club.getInventario().add(inventarioBodega);
		club.getInventario().add(inventarioSucursal);
		inventarioBodega.agregarArticulo(balon);

		exigir(club.moverArticulo(balon, bodega, sucursal), "se mueve un artículo que está en el origen y no en el destino");
		exigir(!inventarioBodega.tieneArticulo(balon) && inventarioSucursal.tieneArticulo(balon), "el artículo cambia de ubicación");
		exigir(!club.moverArticulo(balon, bodega, sucursal), "no se mueve un artículo que ya no está en el origen");
		exigirIgual(1, inventarioSucursal.getArticulos().size(), "un traslado rechazado no duplica el artículo en el destino");
		exigir(!club.moverArticulo(raqueta, bodega, sucursal), "no se mueve un artículo que no está en el origen");
		exigir(!inventarioSucursal.tieneArticulo(raqueta), "el artículo inexistente no aparece en el destino");
		inventarioBodega.agregarArticulo(raqueta);
		inventarioSucursal.agregarArticulo(raqueta);
		exigir(!club.moverArticulo(raqueta, bodega, sucursal), "no se mueve un artículo que ya está en el destino");
		exigirIgual(1, inventarioBodega.getArticulos().size(), "el origen no cambia en un traslado rechazado");
		exigir(!club.moverArticulo(raqueta, bodega, new Ubicacion("Desconocida")), "una ubicación sin inventario rechaza el traslado");
		exigirIgual("Bodega", inventarioBodega.getUbicacion().getNombre(), "nombre de la ubicación");
		exigirIgual(1, inventarioBodega.getInstalaciones().size(), "el inventario conserva sus instalaciones");
	}

	// ================================================================ 13 turnos

	private static void turnos() throws Exception {
		Turno turnoLuis = new Turno("Lunes", hora(8), hora(12));
		Turno turnoCarla = new Turno("Lunes", hora(8), hora(12));
		Turno turnoPablo = new Turno("Lunes", hora(8), hora(12));
		Turno turnoMarta = new Turno("Lunes", hora(8), hora(12));
		Turno turnoDiego = new Turno("Lunes", hora(14), hora(18));
		club.asignarTurno(luis, turnoLuis);
		club.asignarTurno(carla, turnoCarla);
		club.asignarTurno(pablo, turnoPablo);
		club.asignarTurno(marta, turnoMarta);
		club.asignarTurno(diego, turnoDiego);
		exigirSinExcepcion("asignarTurno con datos null", () -> {
			club.asignarTurno(null, turnoLuis);
			club.asignarTurno(luis, null);
		});
		exigirIgual(1, luis.getTurnos().size(), "asignarTurno ignora los datos null");

		exigir(turnoLuis.cubre("Lunes", hora(8)) && turnoLuis.cubre("lunes", hora(12)), "un turno cubre sus extremos y no distingue mayúsculas");
		exigir(!turnoLuis.cubre("Lunes", LocalTime.of(12, 1)) && !turnoLuis.cubre("Martes", hora(9)), "un turno no cubre otras horas ni otros días");

		GestorTurnos gestor = new GestorTurnos();
		exigir(gestor.cumpleMinimo("Lunes", empleadosDelClub(), hora(9)), "el lunes a las 9 hay 3 entrenadores y 1 fisioterapeuta");
		exigir(!gestor.cumpleMinimo("Lunes", empleadosDelClub(), hora(15)), "el lunes a las 15 solo hay un fisioterapeuta y ningún entrenador");

		List<SolicitudCambioTurno> solicitudes = listaPrivada(club, "solicitudes");
		exigirExcepcion(ReglaNegocioException.class, "pedir el cambio de un turno ajeno",
				() -> club.registrarSolicitudTurno(luis, turnoCarla, new Turno("Lunes", hora(14), hora(18))));
		exigirSinExcepcion("registrarSolicitudTurno con solicitante null", () -> club.registrarSolicitudTurno(null, turnoLuis, turnoLuis));
		exigirIgual(0, solicitudes.size(), "las solicitudes inválidas no se registran");

		// Luis cambia a la tarde: quedan carla y pablo (2 entrenadores) y marta: se aprueba.
		Turno tardeLuis = new Turno("Lunes", hora(14), hora(18));
		club.registrarSolicitudTurno(luis, turnoLuis, tardeLuis);
		SolicitudCambioTurno aprobable = solicitudes.get(0);
		exigirIgual(EstadoSolicitud.PENDIENTE, aprobable.getEstado(), "la solicitud nace Pendiente");
		club.resolverSolicitud(aprobable, true);
		exigirIgual(EstadoSolicitud.ACEPTADA, aprobable.getEstado(), "el cambio que cumple el mínimo se acepta");
		exigir(luis.getTurnos().contains(tardeLuis) && !luis.getTurnos().contains(turnoLuis), "el empleado pasa a trabajar el turno nuevo");
		club.resolverSolicitud(aprobable, true);
		exigirIgual(EstadoSolicitud.ACEPTADA, aprobable.getEstado(),
				"resolver otra vez una solicitud ya aceptada no debe cambiarle el estado (hoy pasa a Rechazada)");

		// Carla también quiere cambiar: quedaría un solo entrenador en la mañana: se rechaza.
		club.registrarSolicitudTurno(carla, turnoCarla, new Turno("Lunes", hora(14), hora(18)));
		SolicitudCambioTurno sinEntrenadores = solicitudes.get(1);
		administrador.resolverSolicitudTurno(sinEntrenadores, true);
		exigirIgual(EstadoSolicitud.RECHAZADA, sinEntrenadores.getEstado(), "un cambio que deja menos de 2 entrenadores se rechaza");
		exigir(carla.getTurnos().contains(turnoCarla), "el empleado sigue con su turno habitual");

		// Marta es la única fisioterapeuta de la mañana: se rechaza.
		club.registrarSolicitudTurno(marta, turnoMarta, new Turno("Lunes", hora(14), hora(18)));
		SolicitudCambioTurno sinFisio = solicitudes.get(2);
		club.resolverSolicitud(sinFisio, true);
		exigirIgual(EstadoSolicitud.RECHAZADA, sinFisio.getEstado(), "un cambio que deja sin fisioterapeuta se rechaza");

		// El administrador puede rechazar aunque se cumpla el mínimo.
		club.registrarSolicitudTurno(pablo, turnoPablo, new Turno("Martes", hora(8), hora(12)));
		SolicitudCambioTurno denegada = solicitudes.get(3);
		club.resolverSolicitud(denegada, false);
		exigirIgual(EstadoSolicitud.RECHAZADA, denegada.getEstado(), "el administrador rechaza la solicitud");
		exigir(pablo.getTurnos().contains(turnoPablo), "una solicitud rechazada no cambia el turno");
		SolicitudCambioTurno ajena = new SolicitudCambioTurno(turnoPablo, tardeLuis, pablo);
		club.resolverSolicitud(ajena, true);
		exigirIgual(EstadoSolicitud.PENDIENTE, ajena.getEstado(), "una solicitud no registrada en el club se ignora");
		exigirSinExcepcion("resolverSolicitud(null)", () -> club.resolverSolicitud(null, true));
	}

	// ================================================================ 14 reportes

	private static void reportes() throws Exception {
		Map<Rubro, Double> ventas = club.generarReporteVentas();
		exigir(ventas.containsKey(Rubro.CAFETERIA), "el reporte de ventas incluye la cafetería");
		exigir(!ventas.containsKey(Rubro.MENSUALIDAD), "no hay ventas de mensualidades");

		List<RankingJugadorDisciplina> ranking = club.generarReporteDeportivo();
		exigirIgual(9, ranking.size(), "un ranking por cada inscripción individual (9)");
		boolean ordenado = true;
		for (int indice = 0; indice < ranking.size() - 1; indice++) {
			if (ranking.get(indice).getPuntajeElo() < ranking.get(indice + 1).getPuntajeElo()) {
				ordenado = false;
			}
		}
		exigir(ordenado, "el reporte deportivo va de mayor a menor puntaje");

		GeneradorReportes generador = new GeneradorReportes();
		exigir(generador.reporteVentas(null).isEmpty(), "reporteVentas(null) devuelve un mapa vacío");
		exigir(generador.reporteAsistencias(null).isEmpty(), "reporteAsistencias(null) devuelve un mapa vacío");
		exigir(generador.reporteResultados(null).isEmpty(), "reporteResultados(null) devuelve una lista vacía");
		exigir(generador.reporteRankings(null).isEmpty(), "reporteRankings(null) devuelve una lista vacía");

		List<String> resultados = generador.reporteResultados(club.getHistorial().getPartidos());
		exigirIgual(club.getHistorial().getPartidos().size(), resultados.size(), "un resultado por partido");
		exigir(!resultados.isEmpty() && resultados.get(0).contains("Cerros FC"),
				"cada resultado debe describir el partido (los partidos no tienen toString y el reporte muestra el hash del objeto)");
	}

	// ================================================================ 15 bajas

	private static void bajas() throws Exception {
		// Historia Administrador-01: dar de baja a un miembro de un equipo.
		ClubSeneca pequeno = crearClubPequeno("bajas_equipo");
		Futbol deporte = new Futbol();
		Equipo equipo = pequeno.crearEquipo("Mini FC", 7, deporte, Categoria.MAYORES);
		Socio miembro = pequeno.crearSocio(25, "Miembro", "miembro", "x");
		Socio companero = pequeno.crearSocio(26, "Companero", "companero", "x");
		Socio sinEquipo = pequeno.crearSocio(27, "Sin equipo", "sinequipo", "x");
		exigir(pequeno.inscribirJugador(miembro, equipo, "Portero", 1) && pequeno.inscribirJugador(companero, equipo, "Defensa", 2),
				"se arma el equipo del club pequeño");

		exigir(!pequeno.darDeBajaSocio(sinEquipo), "dar de baja a quien no está en ningún equipo se rechaza (historia Administrador-01)");
		exigir(iniciarSesionSinFallar(pequeno, "sinequipo", "x") != null, "el rechazo no cambia nada: el socio sigue pudiendo iniciar sesión");
		exigir(!pequeno.darDeBajaSocio(null), "darDeBajaSocio(null) devuelve false");
		exigir(pequeno.darDeBajaSocio(miembro), "se da de baja a un miembro del equipo");
		exigir(iniciarSesionSinFallar(pequeno, "miembro", "x") == null, "el socio dado de baja ya no inicia sesión");
		exigir(equipo.getInscripcionDe(miembro) == null, "el jugador dado de baja deja de hacer parte del equipo");
		exigir(!pequeno.darDeBajaSocio(miembro), "no se da de baja dos veces");

		// El archivo guardado después de la baja debe poder cargarse.
		Throwable problemaTrasBaja = capturar(() -> recargar(pequeno, "bajas_equipo"));
		exigir(problemaTrasBaja == null,
				"guardar y cargar después de dar de baja a un jugador de un equipo (queda una inscripción huérfana) lanzó " + resumir(problemaTrasBaja));

		exigir(pequeno.darDeBajaEquipo(equipo), "se da de baja un equipo");
		exigir(!pequeno.darDeBajaEquipo(equipo) && !pequeno.darDeBajaEquipo(null), "no se da de baja dos veces ni un equipo null");
		exigir(!companero.tieneEquipoEn(deporte),
				"al dar de baja un equipo sus jugadores quedan libres para otro equipo del deporte (la inscripción vieja queda en el socio)");

		// Baja de un socio con reservas.
		ClubSeneca conReservas = crearClubPequeno("bajas_reserva");
		Tenis tenisPequeno = new Tenis(1, "Tenis");
		CanchaTenis canchaPequena = new CanchaTenis("Cancha Unica", false, 1, tenisPequeno);
		List<DisciplinaIndividual> disciplinasPequenas = listaPrivada(conReservas, "disciplinas");
		disciplinasPequenas.add(tenisPequeno);
		Socio reservador = conReservas.crearSocio(30, "Reservador", "reservador", "x");
		conReservas.inscribirEnDisciplina(reservador, tenisPequeno, NivelJugador.INTERMEDIO);
		exigir(conReservas.reservar(reservador, tenisPequeno, canchaPequena, FECHA_BASE, hora(9), hora(10), 2, Modalidad.SENCILLOS) != null,
				"el club pequeño acepta la reserva");
		Equipo equipoReservas = conReservas.crearEquipo("Equipo Reservas", 7, new Futbol(), Categoria.MAYORES);
		exigir(conReservas.inscribirJugador(reservador, equipoReservas, "Portero", 1), "el socio con reserva hace parte de un equipo");
		exigirIgual(1, conReservas.getHistorial().getReservas().size(), "el club tiene la reserva antes de la baja");
		exigir(conReservas.darDeBajaSocio(reservador), "se da de baja al socio con reserva");
		exigirIgual(0, conReservas.getHistorial().getReservas().size(), "la reserva del socio dado de baja se quita del historial");
		Throwable problemaTrasBajaConReserva = capturar(() -> recargar(conReservas, "bajas_reserva"));
		exigir(problemaTrasBajaConReserva == null,
				"guardar y cargar después de dar de baja a un socio con reservas lanzó " + resumir(problemaTrasBajaConReserva));

		// Delegación del administrador.
		Equipo delAdministrador = administrador.crearEquipo("Equipo del administrador", 5, baloncesto, Categoria.MAYORES);
		exigir(delAdministrador != null, "el administrador crea equipos a través del club");
		exigir(administrador.darDeBajaEquipo(delAdministrador), "el administrador da de baja equipos a través del club");
	}

	// ================================================================ 16 persistencia por clase

	private static void ida(String nombre, Tarea tarea) {
		exigirSinExcepcion("la ida y vuelta de " + nombre, tarea);
	}

	private static Equipo equipoUnitario(Futbol deporte, List<Socio> destino, int cantidad, String nombre) {
		Equipo equipo = new Equipo(nombre, 12, deporte, Categoria.MAYORES);
		for (int numero = 1; numero <= cantidad; numero++) {
			Socio socio = new Socio(20 + numero, "Socio " + nombre + numero, nombre + numero, "clave");
			destino.add(socio);
			equipo.agregarJugador(socio, "Posicion " + numero, numero);
		}
		return equipo;
	}

	private static void persistenciaPorClase() throws Exception {
		Map<String, DisciplinaIndividual> disciplinasPorNombre = new HashMap<>();
		disciplinasPorNombre.put("Tenis", tenis);

		ida("Socio (puntos, bebida caliente e inscripciones)", () -> {
			Socio original = new Socio(22, "Ana Unitaria", "ana.unitaria", "clave");
			original.acumularPuntos(120);
			original.registrarBebidaCaliente(LocalDateTime.of(2026, 10, 18, 9, 30));
			original.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.AVANZADO));
			Socio copia = Socio.fromJSON(viajarPorTexto(original.toJSON()), disciplinasPorNombre);
			exigirIgual("Ana Unitaria", copia.getNombre(), "nombre del socio");
			exigirIgual("ana.unitaria", copia.getLogin(), "login del socio");
			exigirIgual("clave", copia.getPassword(), "password del socio");
			exigirIgual(22, copia.getEdad(), "edad del socio");
			exigirIgual(120, copia.getPuntosFidelidad(), "puntos del socio");
			exigirIgual(original.getHoraUltimaBebidaCaliente(), copia.getHoraUltimaBebidaCaliente(), "bebida caliente del socio");
			exigirIgual(NivelJugador.AVANZADO, copia.getNivelEn(tenis), "nivel del socio en tenis");
		});

		ida("Entrenador (a través de Usuario.fromJSON)", () -> {
			Entrenador original = new Entrenador(38, "Luis Unitario", "luis.unitario", "clave-luis", "EMP-901");
			original.agregarTurno(new Turno("Lunes", hora(8), hora(12)));
			Usuario copia = Usuario.fromJSON(viajarPorTexto(original.toJSON()), disciplinasPorNombre);
			exigir(copia instanceof Entrenador, "el tipo Entrenador se conserva");
			Entrenador entrenador = (Entrenador) copia;
			exigirIgual("Luis Unitario", entrenador.getNombre(), "nombre del entrenador (Entrenador.fromJSON pasa los argumentos en otro orden)");
			exigirIgual("luis.unitario", entrenador.getLogin(), "login del entrenador");
			exigirIgual("clave-luis", entrenador.getPassword(), "password del entrenador");
			exigirIgual("EMP-901", entrenador.getCodigoEmpleado(), "código del entrenador");
			exigirIgual(38, entrenador.getEdad(), "edad del entrenador");
			exigirIgual(1, entrenador.getTurnos().size(), "turnos del entrenador");
		});

		ida("Fisioterapeuta (a través de Usuario.fromJSON)", () -> {
			Fisioterapeuta original = new Fisioterapeuta(36, "Marta Unitaria", "marta.unitaria", "clave-marta", "EMP-902");
			Usuario copia = Usuario.fromJSON(viajarPorTexto(original.toJSON()), disciplinasPorNombre);
			exigir(copia instanceof Fisioterapeuta, "el tipo Fisioterapeuta se conserva");
			Fisioterapeuta fisioterapeuta = (Fisioterapeuta) copia;
			exigirIgual("Marta Unitaria", fisioterapeuta.getNombre(), "nombre de la fisioterapeuta (Fisioterapeuta.fromJSON pasa los argumentos en otro orden)");
			exigirIgual("marta.unitaria", fisioterapeuta.getLogin(), "login de la fisioterapeuta");
			exigirIgual("EMP-902", fisioterapeuta.getCodigoEmpleado(), "código de la fisioterapeuta");
		});

		ida("Administrador (a través de Usuario.fromJSON)", () -> {
			Administrador original = new Administrador("Admin Unitario", "admin.unitario", "clave-admin");
			Usuario copia = Usuario.fromJSON(viajarPorTexto(original.toJSON()), disciplinasPorNombre);
			exigir(copia instanceof Administrador, "el tipo Administrador se conserva");
			exigirIgual("admin.unitario", copia.getLogin(), "login del administrador");
		});
		exigirExcepcion(IllegalArgumentException.class, "Usuario.fromJSON con un tipo desconocido",
				() -> Usuario.fromJSON(new JSONObject().put("tipo", "Marciano"), disciplinasPorNombre));

		ida("Turno y SolicitudCambioTurno", () -> {
			Turno turno = new Turno("Sábado", hora(6), hora(14));
			Turno copiaTurno = Turno.fromJSON(viajarPorTexto(turno.toJSON()));
			exigirIgual("Sábado", copiaTurno.getDiaSemana(), "día del turno");
			exigirIgual(hora(14), copiaTurno.getHoraFinal(), "hora final del turno");

			Entrenador solicitante = new Entrenador(30, "Solicitante", "solicitante", "x", "EMP-903");
			Turno actual = new Turno("Lunes", hora(8), hora(12));
			solicitante.agregarTurno(actual);
			SolicitudCambioTurno solicitud = solicitante.solicitarCambioTurno(actual, new Turno("Martes", hora(8), hora(12)));
			solicitud.aprobar();
			Map<String, Usuario> usuariosPorLogin = new HashMap<>();
			usuariosPorLogin.put("solicitante", solicitante);
			SolicitudCambioTurno copia = SolicitudCambioTurno.fromJSON(viajarPorTexto(solicitud.toJSON()), usuariosPorLogin);
			exigirIgual(EstadoSolicitud.ACEPTADA, copia.getEstado(), "estado de la solicitud");
			exigir(copia.getTurnoActual() == actual, "la solicitud reutiliza el turno actual del empleado (misma instancia)");
			exigirIgual("Martes", copia.getTurnoNuevo().getDiaSemana(), "turno nuevo de la solicitud");
		});

		ida("Articulo (nombre, categoría, precio y cantidad)", () -> {
			Articulo original = new Articulo("Pelota", "Tenis", 8000, 12);
			Articulo copia = Articulo.fromJSON(viajarPorTexto(original.toJSON()));
			exigirIgual("Pelota", copia.getNombre(), "nombre del artículo (Articulo.toJSON solo escribe el tipo)");
			exigirCercano(8000, copia.getPrecio(), "precio del artículo");
			exigirIgual(12, copia.getCantidad(), "cantidad del artículo");
		});
		ida("Bebida y Snack", () -> {
			Bebida bebida = Bebida.fromJSON(viajarPorTexto(cafe.toJSON()));
			exigirIgual("Café", bebida.getNombre(), "nombre de la bebida");
			exigir(bebida.getCaliente(), "la bebida sigue siendo caliente");
			Snack snack = Snack.fromJSON(viajarPorTexto(barra.toJSON()));
			exigirIgual(List.of(Alergeno.MANI), snack.getAlergenos(), "alérgenos del snack");
		});
		ida("Producto.fromJSON (despachador por tipo)", () -> {
			exigir(Producto.fromJSON(viajarPorTexto(cafe.toJSON())) instanceof Bebida, "Producto.fromJSON reconoce una bebida");
			exigir(Producto.fromJSON(viajarPorTexto(barra.toJSON())) instanceof Snack, "Producto.fromJSON reconoce un snack");
		});
		ida("CodigoDescuento, SugerenciaArticulo y Ubicacion", () -> {
			CodigoDescuento codigo = CodigoDescuento.fromJSON(viajarPorTexto(new CodigoDescuento("DESC-X").toJSON()));
			exigirIgual("DESC-X", codigo.getCodigo(), "código de descuento");
			SugerenciaArticulo sugerencia = new SugerenciaArticulo("Jugo", "Bebidas", 3500);
			sugerencia.aprobar();
			SugerenciaArticulo copia = SugerenciaArticulo.fromJSON(viajarPorTexto(sugerencia.toJSON()));
			exigirIgual(EstadoSugerencia.ACEPTADA, copia.getEstado(), "estado de la sugerencia");
			exigirIgual("Bodega", Ubicacion.fromJSON(viajarPorTexto(new Ubicacion("Bodega").toJSON())).getNombre(), "nombre de la ubicación");
		});
		ida("Venta y LineaVenta", () -> {
			Socio comprador = new Socio(30, "Comprador", "comprador", "x");
			Map<String, Usuario> usuariosPorLogin = new HashMap<>();
			usuariosPorLogin.put("comprador", comprador);
			Map<String, Producto> productosPorNombre = new HashMap<>();
			productosPorNombre.put("Café", cafe);
			Venta original = new Venta(500, 0, Rubro.CAFETERIA, null, comprador, List.of(new LineaVenta(2, cafe)), 0.08);
			Venta copia = Venta.fromJSON(viajarPorTexto(original.toJSON()), usuariosPorLogin, productosPorNombre);
			exigirCercano(original.getTotal(), copia.getTotal(), "total de la venta");
			exigirIgual(Rubro.CAFETERIA, copia.getRubro(), "rubro de la venta");
			exigirIgual(original.getFecha(), copia.getFecha(), "fecha de la venta");
			exigirIgual(original.getPuntosGenerados(), copia.getPuntosGenerados(), "puntos generados por la venta");
		});
		ida("Inventario", () -> {
			Inventario original = new Inventario(new ArrayList<Instalacion>(List.of(canchaTenis1)), new Ubicacion("Bodega"));
			original.agregarArticulo(balon);
			Map<String, Producto> productosPorNombre = new HashMap<>();
			productosPorNombre.put(balon.getNombre(), balon);
			Map<String, Instalacion> instalacionesPorNombre = new HashMap<>();
			instalacionesPorNombre.put(canchaTenis1.getNombre(), canchaTenis1);
			Map<String, DisciplinaIndividual> disciplinasPorNombreLocal = new HashMap<>();
			disciplinasPorNombre.put("Tenis", tenis);
			Inventario copia = Inventario.fromJSON(viajarPorTexto(original.toJSON()), productosPorNombre, instalacionesPorNombre, disciplinasPorNombreLocal);
			exigirIgual("Bodega", copia.getUbicacion().getNombre(), "ubicación del inventario");
			exigirIgual(1, copia.getArticulos().size(), "artículos del inventario");
			exigirIgual(1, copia.getInstalaciones().size(), "instalaciones del inventario");
		});

		ida("Disciplina de tenis con sus canchas", () -> {
			DisciplinaIndividual copia = DisciplinaIndividual.fromJSON(viajarPorTexto(tenis.toJSON()));
			exigirIgual(tenis.getInstalaciones().size(), copia.getInstalaciones().size(),
					"cantidad de canchas de tenis tras cargar (las canchas se agregan dos veces a la disciplina al restaurarla)");
			exigirIgual(tenis.getMaxInstalacionesSimultaneas(), copia.getMaxInstalacionesSimultaneas(), "cupo simultáneo de tenis");
		});
		ida("Disciplina de pádel y de tenis de mesa", () -> {
			DisciplinaIndividual copiaPadel = DisciplinaIndividual.fromJSON(viajarPorTexto(padel.toJSON()));
			exigirIgual(padel.getInstalaciones().size(), copiaPadel.getInstalaciones().size(), "cantidad de canchas de pádel tras cargar");
			DisciplinaIndividual copiaMesa = DisciplinaIndividual.fromJSON(viajarPorTexto(tenisDeMesa.toJSON()));
			exigirIgual(tenisDeMesa.getInstalaciones().size(), copiaMesa.getInstalaciones().size(), "cantidad de mesas tras cargar");
			exigir(copiaMesa.getInstalaciones().get(0) instanceof MesaTenisDeMesa, "la mesa se restaura como MesaTenisDeMesa");
		});
		ida("Instalaciones de conjunto (nombre)", () -> {
			Instalacion futbolCopia = Instalacion.fromJSON(viajarPorTexto(canchaFutbol.toJSON()), new HashMap<String, DisciplinaIndividual>());
			exigirIgual("Cancha Futbol 1", futbolCopia.getNombre(), "nombre de la cancha de fútbol (fromJSON usa el campo tipo como nombre)");
			Instalacion baloncestoCopia = Instalacion.fromJSON(viajarPorTexto(canchaBaloncesto.toJSON()), new HashMap<String, DisciplinaIndividual>());
			exigirIgual("Cancha Basket 1", baloncestoCopia.getNombre(), "nombre de la cancha de baloncesto");
		});
		ida("Instalacion.fromJSON con una cancha individual", () -> {
			Map<String, DisciplinaIndividual> disciplinasPorNombreCancha = new HashMap<>();
			disciplinasPorNombreCancha.put("Tenis", new Tenis(2, "Tenis"));
			Instalacion copia = Instalacion.fromJSON(viajarPorTexto(canchaTenis1.toJSON()), disciplinasPorNombreCancha);
			exigirIgual("Tenis 1", copia.getNombre(), "nombre de la cancha de tenis");
		});

		ida("Reserva y RankingJugadorDisciplina", () -> {
			Socio socio = new Socio(30, "Reservante", "reservante", "x");
			Map<String, Usuario> usuariosPorLogin = new HashMap<>();
			usuariosPorLogin.put("reservante", socio);
			Map<String, Instalacion> instalacionesPorNombre = new HashMap<>();
			instalacionesPorNombre.put("Tenis 1", canchaTenis1);
			Reserva reserva = new Reserva(2, hora(9), hora(10), FECHA_BASE, Modalidad.SENCILLOS, socio, canchaTenis1);
			Reserva copia = Reserva.fromJSON(viajarPorTexto(reserva.toJSON()), usuariosPorLogin, instalacionesPorNombre);
			exigirIgual(hora(9), copia.getHoraInicio(), "hora de inicio de la reserva");
			exigirIgual(Modalidad.SENCILLOS, copia.getModalidad(), "modalidad de la reserva");
			exigirIgual(FECHA_BASE, copia.getFecha(), "fecha de la reserva");
			exigir(copia.getInstalacion() == canchaTenis1 && copia.getJugador() == socio, "la reserva reutiliza socio e instalación");

			Map<String, DisciplinaIndividual> disciplinasPorNombreLocal = new HashMap<>();
			disciplinasPorNombreLocal.put("Tenis", tenis);
			RankingJugadorDisciplina ranking = new RankingJugadorDisciplina(socio, tenis);
			ranking.setPuntajeElo(1234.5);
			RankingJugadorDisciplina copiaRanking = RankingJugadorDisciplina.fromJSON(viajarPorTexto(ranking.toJSON()),
					usuariosPorLogin, disciplinasPorNombreLocal);
			exigirCercano(1234.5, copiaRanking.getPuntajeElo(), "puntaje del ranking");
		});

		ida("Equipo, inscripciones y sanciones", () -> {
			List<Socio> socios = new ArrayList<>();
			Futbol deporte = new Futbol();
			Equipo original = equipoUnitario(deporte, socios, 3, "unitario");
			original.getInscripcionDe(socios.get(1)).registrarTarjetas(1, 0);
			original.getInscripcionDe(socios.get(2)).registrarTarjetas(0, 1);
			Map<String, Usuario> usuariosPorLogin = new HashMap<>();
			for (Socio socio : socios) {
				usuariosPorLogin.put(socio.getLogin(), socio);
			}
			Equipo copia = Equipo.fromJSON(viajarPorTexto(original.toJSON()), new HashMap<String, DeporteConjunto>(), usuariosPorLogin);
			exigirIgual("unitario", copia.getNombre(), "nombre del equipo");
			exigirIgual(Categoria.MAYORES, copia.getCategoria(), "categoría del equipo");
			exigirIgual(3, copia.getInscripciones().size(), "inscripciones del equipo");
			exigirIgual(1, copia.getInscripciones().get(1).getControlSanciones().getAmarillasAcumuladas(), "amarillas acumuladas");
			exigir(!copia.getInscripciones().get(2).estaHabilitado(), "el jugador suspendido sigue suspendido");
			exigirIgual(1, copia.getInscripciones().get(0).getNumeroCamiseta(), "número de camiseta");
			ControlSanciones control = ControlSanciones.fromJSON(viajarPorTexto(original.getInscripcionDe(socios.get(2)).getControlSanciones().toJSON()));
			exigirIgual("SUSPENDIDO", control.getEstado().name(), "estado de sanción");
		});

		ida("Competencia TorneoCerros con tabla, nómina y partidos sin estadísticas", () -> {
			List<Socio> socios = new ArrayList<>();
			Futbol deporte = new Futbol();
			Equipo equipo = equipoUnitario(deporte, socios, 7, "torneo");
			TorneoCerros original = new TorneoCerros("Torneo", "Org", 2026, "Liga", 7, Categoria.MAYORES, deporte);
			original.inscribirEquipo(equipo);
			original.getInscripcionDe(equipo).agregarANomina(socios.get(0));
			original.registrarPartido(new PartidoExterno(FECHA_BASE, 2, 0, "Rival", "Estadio", equipo));
			Map<String, Equipo> equiposPorNombre = new HashMap<>();
			equiposPorNombre.put(equipo.getNombre(), equipo);
			Competencia copia = Competencia.fromJSON(viajarPorTexto(original.toJSON()), new HashMap<String, DeporteConjunto>(), equiposPorNombre);
			exigir(copia instanceof TorneoCerros, "el tipo TorneoCerros se conserva");
			exigirIgual(3, ((TorneoCerros) copia).getTablaPosiciones().get(equipo), "tabla de posiciones");
			exigirIgual(1, copia.getInscripciones().size(), "inscripciones de la competencia");
			exigirIgual(1, copia.getInscripciones().get(0).getNomina().size(), "nómina de la competencia");
			exigirIgual(1, copia.getListaPartidos().size(), "partidos de la competencia");
		});
		ida("Competencia ASCUN (fase)", () -> {
			Futbol deporte = new Futbol();
			ASCUN original = new ASCUN("ASCUN", "ASCUN", 2026, "Grupos", 7, Categoria.MAYORES, deporte, FaseASCUN.GRUPOS);
			original.avanzarFase();
			Competencia copia = Competencia.fromJSON(viajarPorTexto(original.toJSON()), new HashMap<String, DeporteConjunto>(), new HashMap<String, Equipo>());
			exigirIgual(FaseASCUN.ELIMINACION_DIRECTA, ((ASCUN) copia).getFase(), "fase de ASCUN");
		});

		List<Socio> sociosEstadisticas = new ArrayList<>();
		Futbol deporteEstadisticas = new Futbol();
		Equipo equipoEstadisticas = equipoUnitario(deporteEstadisticas, sociosEstadisticas, 2, "estadistica");
		InscripcionEquipo inscripcionEstadistica = equipoEstadisticas.getInscripciones().get(0);
		exigirSinExcepcion("la ida y vuelta de EstadisticaFutbol",
				() -> EstadisticaConjunto.fromJSON(viajarPorTexto(new EstadisticaFutbol(inscripcionEstadistica, 2, 1, 1, 0).toJSON()),
						equipoEstadisticas.getInscripciones()));
		exigirSinExcepcion("la ida y vuelta de EstadisticaBaloncesto",
				() -> EstadisticaConjunto.fromJSON(viajarPorTexto(new EstadisticaBaloncesto(inscripcionEstadistica, 20, 5).toJSON()),
						equipoEstadisticas.getInscripciones()));
		exigirSinExcepcion("la ida y vuelta de EstadisticaVoleibol",
				() -> EstadisticaConjunto.fromJSON(viajarPorTexto(new EstadisticaVoleibol(inscripcionEstadistica, 3, 2).toJSON()),
						equipoEstadisticas.getInscripciones()));

		ida("PartidoInterno con estadísticas", () -> {
			List<Socio> socios = new ArrayList<>();
			Futbol deporte = new Futbol();
			Equipo local = equipoUnitario(deporte, socios, 2, "local");
			Equipo visitante = equipoUnitario(deporte, socios, 2, "visitante");
			PartidoInterno original = new PartidoInterno(FECHA_BASE, local, visitante, 2, 1);
			original.agregarEstadistica(new EstadisticaFutbol(local.getInscripciones().get(0), 2, 0, 0, 0));
			Map<String, Equipo> equiposPorNombre = new HashMap<>();
			equiposPorNombre.put("local", local);
			equiposPorNombre.put("visitante", visitante);
			PartidoInterno copia = PartidoInterno.fromJSON(viajarPorTexto(original.toJSON()), equiposPorNombre);
			exigirIgual(2, copia.getMarcadorLocal(), "marcador local");
			exigirIgual(1, copia.getEstadisticas().size(), "estadísticas del partido");
		});
		ida("PartidoExterno con estadísticas", () -> {
			List<Socio> socios = new ArrayList<>();
			Futbol deporte = new Futbol();
			Equipo equipo = equipoUnitario(deporte, socios, 2, "externo");
			PartidoExterno original = new PartidoExterno(FECHA_BASE, 1, 0, "Rival", "Estadio", equipo);
			original.agregarEstadistica(new EstadisticaFutbol(equipo.getInscripciones().get(0), 1, 0, 0, 0));
			Map<String, Equipo> equiposPorNombre = new HashMap<>();
			equiposPorNombre.put("externo", equipo);
			PartidoExterno copia = PartidoExterno.fromJSON(viajarPorTexto(original.toJSON()), equiposPorNombre);
			exigirIgual("Rival", copia.getInstitucionRival(), "institución rival");
			exigirIgual(1, copia.getEstadisticas().size(), "estadísticas del partido");
		});

		ida("PartidoPractica con estadísticas", () -> {
			Tenis disciplinaLocal = new Tenis(2, "Tenis");
			CanchaTenis cancha = new CanchaTenis("Cancha Unitaria", false, 1, disciplinaLocal);
			Socio primero = new Socio(20, "Primero", "primero", "x");
			Socio segundo = new Socio(21, "Segundo", "segundo", "x");
			PartidoPractica original = new PartidoPractica(FECHA_BASE, disciplinaLocal, List.of(primero, segundo), cancha);
			original.finalizar(List.of(new EstadisticaRaqueta(2, primero, 12, 3, 5), new EstadisticaRaqueta(0, segundo, 6, 0, 9)));
			Map<String, Usuario> usuariosPorLogin = new HashMap<>();
			usuariosPorLogin.put("primero", primero);
			usuariosPorLogin.put("segundo", segundo);
			Map<String, DisciplinaIndividual> disciplinasPorNombreLocal = new HashMap<>();
			disciplinasPorNombreLocal.put("Tenis", disciplinaLocal);
			Map<String, Instalacion> instalacionesPorNombre = new HashMap<>();
			instalacionesPorNombre.put("Cancha Unitaria", cancha);
			PartidoPractica copia = PartidoPractica.fromJSON(viajarPorTexto(original.toJSON()), usuariosPorLogin,
					disciplinasPorNombreLocal, instalacionesPorNombre);
			exigir(copia.isFinalizado(), "el partido sigue finalizado");
			exigirIgual(2, copia.getEstadisticas().size(), "estadísticas del partido de práctica");
			exigirIgual(primero, copia.getGanador(), "ganador del partido de práctica");

			TenisDeMesa mesaLocal = new TenisDeMesa(1, "TenisDeMesa");
			MesaTenisDeMesa mesa = new MesaTenisDeMesa("Mesa Unitaria", false, 1, mesaLocal);
			PartidoPractica partidoMesa = new PartidoPractica(FECHA_BASE, mesaLocal, List.of(primero, segundo), mesa);
			partidoMesa.finalizar(List.of(new EstadisticaTenisDeMesa(3, primero, 30), new EstadisticaTenisDeMesa(1, segundo, 22)));
			disciplinasPorNombreLocal.put("TenisDeMesa", mesaLocal);
			instalacionesPorNombre.put("Mesa Unitaria", mesa);
			PartidoPractica copiaMesa = PartidoPractica.fromJSON(viajarPorTexto(partidoMesa.toJSON()), usuariosPorLogin,
					disciplinasPorNombreLocal, instalacionesPorNombre);
			exigirIgual(2, copiaMesa.getEstadisticas().size(), "estadísticas del partido de tenis de mesa");
		});

		ida("SesionEntrenamiento con asistencias", () -> {
			Entrenador entrenador = new Entrenador(30, "Entrenador Unitario", "entrenador.u", "x", "EMP-904");
			Socio convocado = new Socio(20, "Convocado", "convocado", "x");
			Tenis disciplinaLocal = new Tenis(2, "Tenis");
			CanchaTenis cancha = new CanchaTenis("Cancha Entreno", false, 1, disciplinaLocal);
			SesionEntrenamiento original = new SesionEntrenamiento(FECHA_BASE, hora(17), 90, entrenador, cancha, List.of(convocado));
			original.registrarAsistencia(convocado, EstadoAsistencia.ASISTIO, 4, "Bien");
			Map<String, Usuario> usuariosPorLogin = new HashMap<>();
			usuariosPorLogin.put("entrenador.u", entrenador);
			usuariosPorLogin.put("convocado", convocado);
			Map<String, Instalacion> instalacionesPorNombre = new HashMap<>();
			instalacionesPorNombre.put("Cancha Entreno", cancha);
			SesionEntrenamiento copia = SesionEntrenamiento.fromJSON(viajarPorTexto(original.toJSON()), usuariosPorLogin, instalacionesPorNombre);
			exigirIgual(90, copia.getDuracionMinutos(), "duración de la sesión");
			exigirIgual(1, copia.getAsistencias().size(), "asistencias de la sesión");
			exigirIgual(4, copia.getAsistencias().get(0).getEvaluacion(), "evaluación de la asistencia");
			exigirIgual("Bien", copia.getAsistencias().get(0).getComentario(), "comentario de la asistencia");
		});
	}

	// ================================================================ 17 persistencia: club completo

	private static void persistenciaClubCompleto() throws Exception {
		club.guardarDatos();
		Path archivo = carpetaDatos.resolve("club.json");
		exigir(Files.exists(archivo) && Files.size(archivo) > 1000, "guardarDatos crea club.json con contenido");
		JSONObject guardado = new JSONObject(Files.readString(archivo));
		String[] secciones = { "nombre", "usuarios", "deportes", "disciplinas", "rankings", "inventarios", "tienda", "cafeteria",
				"equipos", "competencias", "ventas", "solicitudes", "sugerencias", "historial" };
		for (String seccion : secciones) {
			exigir(guardado.has(seccion), "club.json tiene la sección '" + seccion + "'");
		}
		exigirIgual(club.generarReporteDeportivo().size(), guardado.getJSONArray("rankings").length(), "rankings guardados");
		exigirIgual(16 + 5 + 6, guardado.getJSONArray("usuarios").length(), "usuarios guardados (16 jugadores + 5 socios + 6 de personal)");

		// Cargar en un club nuevo.
		ClubSeneca recargado = new ClubSeneca("recargado");
		recargado.setPersistencia(new Persistencia(carpetaDatos.toString()));
		Throwable problema = capturar(() -> recargado.cargarDatos());
		exigir(problema == null, "cargar el club completo que acaba de guardarse lanzó " + resumir(problema));
		if (problema != null) {
			System.out.println("     (se omiten las comprobaciones posteriores a la carga; revisa la sección 16 para ver qué clase falla)");
		} else {
			comprobarClubRecargado(recargado);
		}

		// Casos de archivo.
		Path carpetaVacia = Files.createDirectories(carpetaDatos.resolve("vacia"));
		Persistencia persistenciaVacia = new Persistencia(carpetaVacia.toString());
		exigir(persistenciaVacia.cargar() == null, "cargar sin archivo devuelve null");
		ClubSeneca sinArchivo = new ClubSeneca("Sin archivo");
		sinArchivo.setPersistencia(persistenciaVacia);
		exigirSinExcepcion("cargarDatos sin archivo", () -> sinArchivo.cargarDatos());
		exigir(iniciarSesionSinFallar(sinArchivo, "ana", "x") == null, "cargarDatos sin archivo deja el club como estaba");

		Path carpetaCorrupta = Files.createDirectories(carpetaDatos.resolve("corrupta"));
		ClubSeneca conArchivoCorrupto = new ClubSeneca("Club intacto");
		conArchivoCorrupto.setPersistencia(new Persistencia(carpetaCorrupta.toString()));
		conArchivoCorrupto.crearSocio(20, "Intacto", "intacto", "x"); // guarda un club.json válido...
		Files.writeString(carpetaCorrupta.resolve("club.json"), "{ esto no es json"); // ...que se corrompe después
		Throwable problemaCorrupto = capturar(() -> conArchivoCorrupto.cargarDatos());
		exigir(problemaCorrupto != null, "un club.json corrupto debe avisar con una excepción en vez de ignorarse");
		exigir(iniciarSesionSinFallar(conArchivoCorrupto, "intacto", "x") != null, "un archivo corrupto no debe dejar el club a medias");

		// Instalaciones de conjunto: sesiones e inventarios que las usan deben poder guardarse y cargarse.
		ClubSeneca conCancha = crearClubPequeno("con_cancha_conjunto");
		Entrenador entrenadorPequeno = new Entrenador(35, "Entrenador Cancha", "entrenador.cancha", "x", "EMP-950");
		Socio convocadoPequeno = conCancha.crearSocio(22, "Convocado Cancha", "convocado.cancha", "x");
		List<Usuario> usuariosPequenos = listaPrivada(conCancha, "usuarios");
		usuariosPequenos.add(entrenadorPequeno);
		exigir(conCancha.planearSesion(entrenadorPequeno, FECHA_BASE, hora(17), 60, canchaBaloncesto, new ArrayList<>(List.of(convocadoPequeno))) != null,
				"se planea una sesión en una cancha de baloncesto");
		Throwable problemaCancha = capturar(() -> recargar(conCancha, "con_cancha_conjunto"));
		exigir(problemaCancha == null,
				"guardar y cargar una sesión planeada en una cancha de conjunto lanzó " + resumir(problemaCancha)
						+ " (las instalaciones de conjunto no se guardan en ninguna parte del JSON)");
	}

	private static void comprobarClubRecargado(ClubSeneca recargado) throws Exception {
		String textoOriginal = viajarPorTexto(club.toJSON()).toString(2);
		String textoRecargado = viajarPorTexto(recargado.toJSON()).toString(2);
		List<String> diferencias = new ArrayList<>();
		compararJson("club", new JSONObject(textoOriginal), new JSONObject(textoRecargado), diferencias);
		exigir(diferencias.isEmpty(), "el club recargado debe regenerar el mismo JSON (" + diferencias.size() + " diferencias; primeras: "
				+ diferencias.subList(0, Math.min(5, diferencias.size())) + ")");

		Usuario ana2 = recargado.iniciarSesion("ana", "clave-ana");
		exigir(ana2 != null && ana2 != ana, "tras cargar se puede iniciar sesión con un objeto nuevo");
		exigirIgual(club.getHistorial().getReservas().size(), recargado.getHistorial().getReservas().size(), "reservas tras cargar");
		exigirIgual(club.getHistorial().getPartidos().size(), recargado.getHistorial().getPartidos().size(), "partidos tras cargar");
		exigirIgual(club.getHistorial().getSesiones().size(), recargado.getHistorial().getSesiones().size(), "sesiones tras cargar");
		exigirIgual(club.generarReporteVentas(), recargado.generarReporteVentas(), "reporte de ventas tras cargar");
		exigirIgual(club.generarReporteDeportivo().size(), recargado.generarReporteDeportivo().size(), "rankings tras cargar");

		Reserva reserva = recargado.getHistorial().getReservas().get(0);
		exigir(recargado.reservar(reserva.getJugador(), reserva.getInstalacion().getDisciplina(), reserva.getInstalacion(),
				reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFinal(), reserva.getNumeroJugadores(), reserva.getModalidad()) == null,
				"tras cargar, una reserva que choca con una cargada sigue rechazándose");
		exigir(recargado.crearSocio(30, "Duplicada", "ana", "x") == null, "tras cargar, no se repite un login existente");
		exigir(recargado.crearSocio(30, "Nuevo", "socio.nuevo", "x") != null, "tras cargar se pueden crear socios nuevos");

		// 10 ciclos guardar/cargar: el resultado no debe cambiar nunca.
		ClubSeneca actual = recargado;
		int ciclosConCambio = 0;
		String referencia = viajarPorTexto(recargado.toJSON()).toString(2);
		for (int ciclo = 1; ciclo <= 10; ciclo++) {
			actual.guardarDatos();
			actual = recargar(actual, "");
			if (!referencia.equals(viajarPorTexto(actual.toJSON()).toString(2))) {
				ciclosConCambio++;
			}
		}
		exigirIgual(0, ciclosConCambio, "ciclos de guardar/cargar que cambiaron los datos (de 10)");
	}
}
