package NucleoSistema_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.TorneoCerros;
import DeportesIndividuales.EstadisticaPractica;
import DeportesIndividuales.EstadisticaRaqueta;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Padel;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Reserva;
import DeportesIndividuales.Tenis;
import Entrenamiento.EstadoAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Excepciones.ReglaNegocioException;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaPadel;
import Instalaciones.CanchaTenis;
import Instalaciones.Instalacion;
import NucleoSistema.ClubSeneca;
import NucleoSistema.Periodo;
import NucleoSistema.Persistencia;
import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.CodigoDescuento;
import Tienda_Cafeteria_Inventario.EstadoSugerencia;
import Tienda_Cafeteria_Inventario.Inventario;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Snack;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import Tienda_Cafeteria_Inventario.Ubicacion;
import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;
import Usuarios.Usuario;

public class ClubSenecaTest
{
    @TempDir
    File carpetaTemporal;

    private ClubSeneca club;
    private Persistencia persistencia;

    private Futbol futbol;
    private Baloncesto baloncesto;

    private Tenis tenis;
    private Tenis tenisUnaCancha;
    private Padel padel;
    private CanchaTenis canchaTenis1;
    private CanchaTenis canchaTenis2;
    private CanchaTenis canchaUnica1;
    private CanchaTenis canchaUnica2;
    private CanchaPadel canchaPadel;
    private CanchaFutbol canchaFutbol;

    private Socio ana;
    private Socio pedro;
    private Entrenador entrenador;

    private LocalDate fecha;

    @BeforeEach
    void setUp() throws Exception
    {
        persistencia = new Persistencia(carpetaTemporal.getAbsolutePath());
        club = new ClubSeneca("Club Seneca");
        club.setPersistencia(persistencia);

        futbol = new Futbol();
        baloncesto = new Baloncesto();

        tenis = new Tenis(2, "Tenis");
        tenisUnaCancha = new Tenis(1, "Tenis");
        padel = new Padel(1, "Padel");
        canchaTenis1 = new CanchaTenis("Tenis 1", true, 1, tenis);
        canchaTenis2 = new CanchaTenis("Tenis 2", true, 1, tenis);
        canchaUnica1 = new CanchaTenis("Unica 1", true, 1, tenisUnaCancha);
        canchaUnica2 = new CanchaTenis("Unica 2", true, 1, tenisUnaCancha);
        canchaPadel = new CanchaPadel("Padel 1", false, 1, padel);
        canchaFutbol = new CanchaFutbol("Futbol 1");

        ana = club.crearSocio(20, "Ana Torres", "ana", "1234");
        pedro = club.crearSocio(25, "Pedro Gil", "pgil", "5678");
        entrenador = new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01");

        fecha = LocalDate.of(2026, 11, 10);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private void llenarEquipo(Equipo equipo, String prefijo, int cantidad) throws Exception
    {
        for(int i = 1; i <= cantidad; i++)
        {
            Socio socio = club.crearSocio(20 + i, "Jugador " + prefijo + i, prefijo + i, "x");
            equipo.agregarJugador(socio, "Jugador", i);
        }
    }

    private Socio jugadorDe(Equipo equipo, int posicion)
    {
        return equipo.getInscripciones().get(posicion).getJugador();
    }

    @SuppressWarnings("unchecked")
    private List<SolicitudCambioTurno> obtenerSolicitudes() throws Exception
    {
        Field campo = ClubSeneca.class.getDeclaredField("solicitudes");
        campo.setAccessible(true);
        return (List<SolicitudCambioTurno>) campo.get(club);
    }

    private void cargarClubConUsuarios(Usuario... usuarios) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("nombre", "Club Seneca");
        JSONArray lista = new JSONArray();
        for(Usuario usuario : usuarios)
        {
            lista.put(usuario.toJSON());
        }
        json.put("usuarios", lista);
        Files.writeString(carpetaTemporal.toPath().resolve("club.json"), json.toString());
        club = persistencia.cargar();
    }

    private Map<Producto, Integer> crearCompra(Producto producto, int cantidad)
    {
        Map<Producto, Integer> compra = new LinkedHashMap<Producto, Integer>();
        compra.put(producto, cantidad);
        return compra;
    }

    private List<Socio> listaDe(Socio... socios)
    {
        List<Socio> lista = new ArrayList<Socio>();
        for(Socio socio : socios)
        {
            lista.add(socio);
        }
        return lista;
    }

    private JSONObject jsonDelClub()
    {
        return club.toJSON();
    }

    @Test
    void testClubNuevo()
    {
        assertNotNull(club.getHistorial(), "El club debe tener un historial.");
        assertEquals(0, club.getInventario().size(), "Un club nuevo no debe tener inventarios.");
        assertEquals(0, jsonDelClub().getJSONArray("equipos").length(), "Un club nuevo no debe tener equipos.");
        assertEquals(0, jsonDelClub().getJSONArray("ventas").length(), "Un club nuevo no debe tener ventas.");
    }
    
    @Test
    void testCrearSocio()
    {
        assertNotNull(ana, "El socio debe crearse.");
        assertEquals("Ana Torres", ana.getNombre(), "El nombre del socio no es el esperado.");
        assertEquals(20, ana.getEdad(), "La edad del socio no es la esperada.");
        assertEquals(2, jsonDelClub().getJSONArray("usuarios").length(), "El club debe tener dos usuarios.");
    }

    @Test
    void testCrearSocioConLoginRepetido()
    {
        assertNull(club.crearSocio(30, "Otra Ana", "ana", "9999"), "No se puede repetir el login.");

        assertEquals(2, jsonDelClub().getJSONArray("usuarios").length(), "El club debe seguir con dos usuarios.");
    }

    @Test
    void testCrearSocioGuardaEnDisco() throws Exception
    {
        String contenido = Files.readString(carpetaTemporal.toPath().resolve("club.json"));

        assertTrue(contenido.contains("\"ana\""), "El archivo guardado debe contener al socio creado.");
    }

    @Test
    void testIniciarSesion()
    {
        assertSame(ana, club.iniciarSesion("ana", "1234"), "Debe iniciar sesión con credenciales correctas.");
        assertSame(pedro, club.iniciarSesion("pgil", "5678"), "Debe iniciar sesión con credenciales correctas.");
    }

    @Test
    void testIniciarSesionConDatosIncorrectos()
    {
        assertNull(club.iniciarSesion("ana", "0000"), "Con una contraseña incorrecta no debe iniciar sesión.");
        assertNull(club.iniciarSesion("nadie", "1234"), "Con un login inexistente no debe iniciar sesión.");
        assertNull(club.iniciarSesion(null, null), "Con datos nulos no debe iniciar sesión.");
    }

    @Test
    void testCrearEquipo()
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        assertNotNull(equipo, "El equipo debe crearse.");
        assertEquals("Los Cerros FC", equipo.getNombre(), "El nombre del equipo no es el esperado.");
        assertEquals(12, equipo.getCupoMaximo(), "El cupo del equipo no es el esperado.");
        assertSame(futbol, equipo.getDeporte(), "El deporte del equipo no es el esperado.");
        assertEquals(1, jsonDelClub().getJSONArray("equipos").length(), "El club debe tener un equipo.");
    }

    @Test
    void testCrearEquipoConNombreRepetido()
    {
        club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        assertNull(club.crearEquipo("Los Cerros FC", 10, futbol, Categoria.MAYORES), "No se puede repetir el nombre del equipo.");
        assertNull(club.crearEquipo("LOS CERROS fc", 10, baloncesto, Categoria.SUB15), "El nombre se compara sin distinguir mayúsculas.");
        assertEquals(1, jsonDelClub().getJSONArray("equipos").length(), "El club debe seguir con un equipo.");
    }

    @Test
    void testCrearEquipoConCupoInsuficiente()
    {
        assertNull(club.crearEquipo("Pocos FC", 6, futbol, Categoria.MAYORES), "El cupo no puede ser menor al mínimo de jugadores del deporte.");
        assertNotNull(club.crearEquipo("Justos FC", 7, futbol, Categoria.MAYORES), "El cupo igual al mínimo debe aceptarse.");
    }

    @Test
    void testInscribirJugador() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        club.inscribirJugador(ana, equipo, "Delantero", 9);

        assertEquals(1, equipo.getInscripciones().size(), "El equipo debe tener un jugador.");
        assertEquals(1, jsonDelClub().getJSONArray("equipos").getJSONObject(0).getJSONArray("inscripciones").length(), "El estado guardado debe incluir la inscripción.");
    }

    @Test
    void testInscribirJugadorQueNoCumple()
    {
        Equipo equipo = club.crearEquipo("Cerritos", 12, futbol, Categoria.SUB15);

        assertThrows(ReglaNegocioException.class, () -> club.inscribirJugador(ana, equipo, "Delantero", 9), "Un adulto no puede inscribirse en SUB15.");
        assertEquals(0, equipo.getInscripciones().size(), "El equipo debe seguir vacío.");
    }

    @Test
    void testDarDeBajaEquipo() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);

        assertTrue(club.darDeBajaEquipo(equipo), "El equipo debe poder darse de baja.");

        assertEquals(0, jsonDelClub().getJSONArray("equipos").length(), "El club no debe tener equipos.");
        assertEquals(0, ana.getInscripcionesEquipo().size(), "El socio no debe conservar la inscripción al equipo.");
    }

    @Test
    void testDarDeBajaEquipoInexistente()
    {
        Equipo ajeno = new Equipo("Ajeno", 10, futbol, Categoria.MAYORES);

        assertFalse(club.darDeBajaEquipo(ajeno), "Un equipo que no está en el club no puede darse de baja.");
        assertFalse(club.darDeBajaEquipo(null), "Un equipo nulo no puede darse de baja.");
    }

    @Test
    void testDarDeBajaEquipoDosVeces() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        assertTrue(club.darDeBajaEquipo(equipo), "La primera baja debe funcionar.");
        assertFalse(club.darDeBajaEquipo(equipo), "La segunda baja del mismo equipo debe fallar.");
    }

    @Test
    void testDarDeBajaEquipoLimpiaPartidosYCompetencias() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);
        club.registrarPartidoExterno(torneo, equipo, fecha, 2, 1, "Rival", "Estadio", null);

        assertEquals(1, club.getHistorial().getPartidos().size(), "Antes de la baja debe haber un partido.");
        assertEquals(1, torneo.getTablaPosiciones().size(), "Antes de la baja debe haber un equipo en la tabla.");

        club.darDeBajaEquipo(equipo);

        assertEquals(0, club.getHistorial().getPartidos().size(), "Después de la baja no debe haber partidos en el historial.");
        assertEquals(0, torneo.getInscripciones().size(), "Después de la baja no debe haber inscripciones en la competencia.");
        assertEquals(0, torneo.getListaPartidos().size(), "Después de la baja no debe haber partidos en la competencia.");
        assertEquals(0, torneo.getTablaPosiciones().size(), "Después de la baja no debe haber equipos en la tabla.");
    }

    @Test
    void testDarDeBajaSocioNuloOInexistente()
    {
        Socio ajeno = new Socio(30, "Ajeno", "ajeno", "x");

        assertFalse(club.darDeBajaSocio(null), "Un socio nulo no puede darse de baja.");
        assertFalse(club.darDeBajaSocio(ajeno), "Un socio que no está en el club no puede darse de baja.");
    }

    @Test
    void testDarDeBajaSocioSinEquipos()
    {
        assertTrue(club.darDeBajaSocio(ana), "Un socio sin equipos también debe poder darse de baja.");

        assertNull(club.iniciarSesion("ana", "1234"), "El socio dado de baja no debe poder iniciar sesión.");
        assertNotNull(club.iniciarSesion("pgil", "5678"), "Los demás socios deben conservarse.");
        assertEquals(1, jsonDelClub().getJSONArray("usuarios").length(), "El club debe quedar con un solo usuario.");
    }

    @Test
    void testDarDeBajaSocioSinEquiposLimpiaSusDatosIndividuales() throws Exception
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.INTERMEDIO);
        club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);
        club.programarPartidoPractica(tenis, canchaTenis2, fecha, listaDe(ana, pedro));

        assertTrue(club.darDeBajaSocio(ana), "Un socio solo con disciplinas individuales debe poder darse de baja.");

        assertNull(club.getRanking(tenis, ana), "El ranking del socio debe eliminarse.");
        assertNotNull(club.getRanking(tenis, pedro), "El ranking del otro socio debe conservarse.");
        assertEquals(0, club.getHistorial().getReservas().size(), "Las reservas del socio deben eliminarse.");
        assertEquals(0, club.getHistorial().getPartidosPractica().size(), "Los partidos de práctica del socio deben eliminarse.");
    }

    @Test
    void testDarDeBajaSocioDosVeces() throws Exception
    {
        assertTrue(club.darDeBajaSocio(ana), "La primera baja debe funcionar.");
        assertFalse(club.darDeBajaSocio(ana), "La segunda baja del mismo socio debe fallar.");
    }

    @Test
    void testDarDeBajaSocio() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);

        assertTrue(club.darDeBajaSocio(ana), "El socio debe poder darse de baja.");

        assertNull(club.iniciarSesion("ana", "1234"), "El socio dado de baja no debe poder iniciar sesión.");
        assertNotNull(club.iniciarSesion("pgil", "5678"), "Los demás socios deben conservarse.");
        assertEquals(1, equipo.getInscripciones().size(), "El equipo debe conservar solo al otro jugador.");
        assertNull(club.getRanking(tenis, ana), "El ranking del socio debe eliminarse.");
        assertEquals(0, club.getHistorial().getReservas().size(), "Las reservas del socio deben eliminarse.");
    }

    @Test
    void testDarDeBajaSocioEliminaPartidosDePractica() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.INTERMEDIO);
        club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro));

        club.darDeBajaSocio(ana);

        assertEquals(0, club.getHistorial().getPartidosPractica().size(), "Los partidos de práctica del socio deben eliminarse.");
    }

    @Test
    void testDarDeBajaSocioLimpiaNominasYEstadisticasDeCompetencias() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);
        torneo.getInscripcionDe(equipo).agregarANomina(ana);
        torneo.getInscripcionDe(equipo).agregarANomina(pedro);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(ana), 2, 0, 0, 0));
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(pedro), 1, 1, 0, 0));
        PartidoExterno partido = club.registrarPartidoExterno(torneo, equipo, fecha, 3, 1, "Rival", "Estadio", estadisticas);

        assertEquals(2, torneo.getInscripcionDe(equipo).getNomina().size(), "Antes de la baja la nómina debe tener dos jugadores.");
        assertEquals(2, partido.getEstadisticas().size(), "Antes de la baja el partido debe tener dos estadísticas.");

        assertTrue(club.darDeBajaSocio(ana), "El socio debe poder darse de baja.");

        assertEquals(1, torneo.getInscripcionDe(equipo).getNomina().size(), "Después de la baja la nómina debe tener un solo jugador.");
        assertNull(torneo.getInscripcionDe(equipo).getRegistroDe(ana), "El socio dado de baja no debe seguir en la nómina.");
        assertNotNull(torneo.getInscripcionDe(equipo).getRegistroDe(pedro), "El otro jugador debe seguir en la nómina.");
        assertEquals(1, partido.getEstadisticas().size(), "El partido externo debe conservar solo la estadística del otro jugador.");
        assertSame(pedro, partido.getEstadisticas().get(0).getJugador().getJugador(), "La estadística que queda debe ser la del otro jugador.");
    }

    @Test
    void testDarDeBajaSocioLimpiaEstadisticasDePartidosInternos() throws Exception
    {
        Equipo local = club.crearEquipo("Local", 8, baloncesto, Categoria.MAYORES);
        Equipo visitante = club.crearEquipo("Visitante", 8, baloncesto, Categoria.MAYORES);
        local.agregarJugador(ana, "Alero", 10);
        llenarEquipo(local, "l", 4);
        llenarEquipo(visitante, "v", 5);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaBaloncesto(local.getInscripcionDe(ana), 20, 5));
        estadisticas.add(new EstadisticaBaloncesto(visitante.getInscripciones().get(0), 15, 7));
        PartidoInterno partido = club.registrarPartidoInterno(local, visitante, 80, 75, estadisticas);

        assertEquals(2, partido.getEstadisticas().size(), "Antes de la baja el partido debe tener dos estadísticas.");

        assertTrue(club.darDeBajaSocio(ana), "El socio debe poder darse de baja.");

        assertEquals(1, partido.getEstadisticas().size(), "El partido interno debe conservar solo la estadística del otro jugador.");
        assertEquals(1, club.getHistorial().getPartidos().size(), "El partido debe seguir en el historial.");
    }

    @Test
    void testDarDeBajaSocioLimpiaSesionesDeEntrenamiento() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        SesionEntrenamiento sesion = club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana, pedro));
        club.registrarAsistencia(sesion, ana, EstadoAsistencia.ASISTIO, 4, "Buen trabajo");
        club.registrarAsistencia(sesion, pedro, EstadoAsistencia.ASISTIO, 3, "Bien");

        assertEquals(2, sesion.getConvocados().size(), "Antes de la baja la sesión debe tener dos convocados.");
        assertEquals(2, sesion.getAsistencias().size(), "Antes de la baja la sesión debe tener dos asistencias.");

        assertTrue(club.darDeBajaSocio(ana), "El socio debe poder darse de baja.");

        assertEquals(1, club.getHistorial().getSesiones().size(), "La sesión debe seguir en el historial.");
        assertEquals(1, sesion.getConvocados().size(), "El socio dado de baja no debe seguir convocado.");
        assertSame(pedro, sesion.getConvocados().get(0), "El convocado que queda no es el esperado.");
        assertEquals(1, sesion.getAsistencias().size(), "La asistencia del socio dado de baja debe eliminarse.");
        assertSame(pedro, sesion.getAsistencias().get(0).getJugador(), "La asistencia que queda no es la esperada.");
    }

    @Test
    void testDarDeBajaEntrenadorEliminaSusSesionesYSolicitudes() throws Exception
    {
        cargarClubConUsuarios(new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01"));
        Entrenador entrenadorDelClub = (Entrenador) club.iniciarSesion("jruiz", "pass");
        Socio socioNuevo = club.crearSocio(22, "Sofia Vega", "svega", "x");
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(entrenadorDelClub, equipo, "Portero", 1);

        club.planearSesion(entrenadorDelClub, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(socioNuevo));
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        entrenadorDelClub.agregarTurno(actual);
        club.registrarSolicitudTurno(entrenadorDelClub, actual, nuevo);

        assertEquals(1, club.getHistorial().getSesiones().size(), "Antes de la baja debe haber una sesión.");
        assertEquals(1, obtenerSolicitudes().size(), "Antes de la baja debe haber una solicitud.");

        assertTrue(club.darDeBajaSocio(entrenadorDelClub), "El entrenador debe poder darse de baja.");

        assertEquals(0, club.getHistorial().getSesiones().size(), "Las sesiones del entrenador deben eliminarse.");
        assertEquals(0, obtenerSolicitudes().size(), "Las solicitudes del entrenador deben eliminarse.");
        assertNull(club.iniciarSesion("jruiz", "pass"), "El entrenador dado de baja no debe poder iniciar sesión.");
    }

    @Test
    void testInscribirEnDisciplina()
    {
        InscripcionIndividual inscripcion = club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);

        assertNotNull(inscripcion, "La inscripción debe crearse.");
        assertSame(tenis, inscripcion.getDisciplina(), "La disciplina de la inscripción no es la esperada.");
        assertEquals(NivelJugador.INTERMEDIO, ana.getNivelEn(tenis), "El nivel del socio no es el esperado.");
        assertEquals(1, jsonDelClub().getJSONArray("rankings").length(), "El club debe tener un ranking.");
    }

    @Test
    void testInscribirEnDisciplinaCreaRanking()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);

        RankingJugadorDisciplina ranking = club.getRanking(tenis, ana);

        assertNotNull(ranking, "Debe existir el ranking del socio.");
        assertEquals(1000, ranking.getPuntajeElo(), 0.001, "El puntaje inicial debe ser 1000.");
    }

    @Test
    void testInscribirEnDisciplinaRepetida()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);

        assertNull(club.inscribirEnDisciplina(ana, tenis, NivelJugador.AVANZADO), "No se puede inscribir dos veces en la misma disciplina.");
        assertEquals(NivelJugador.INTERMEDIO, ana.getNivelEn(tenis), "El nivel original no debe cambiar.");
        assertEquals(1, jsonDelClub().getJSONArray("rankings").length(), "No debe duplicarse el ranking.");
    }

    @Test
    void testInscribirEnDisciplinaConDatosNulos()
    {
        assertNull(club.inscribirEnDisciplina(null, tenis, NivelJugador.INTERMEDIO), "Un socio nulo no puede inscribirse.");
        assertNull(club.inscribirEnDisciplina(ana, null, NivelJugador.INTERMEDIO), "Una disciplina nula no es válida.");
    }

    @Test
    void testInscribirEnVariasDisciplinas()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(ana, padel, NivelJugador.PRINCIPIANTE);

        assertEquals(2, ana.getInscripcionesIndividuales().size(), "El socio debe tener dos inscripciones.");
        assertEquals(2, jsonDelClub().getJSONArray("rankings").length(), "El club debe tener dos rankings.");
    }

    @Test
    void testGetRankingInexistente() throws Exception
    {
        assertNull(club.getRanking(tenis, ana), "Sin inscripción no existe el ranking.");
    }

    @Test
    void testInscribirEquipoEnCompetencia() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);

        assertTrue(club.inscribirEquipoEnCompetencia(equipo, torneo), "El equipo debe poder inscribirse en la competencia.");

        assertEquals(1, torneo.getInscripciones().size(), "La competencia debe tener una inscripción.");
        assertEquals(1, jsonDelClub().getJSONArray("competencias").length(), "El club debe tener una competencia.");
    }

    @Test
    void testInscribirEquipoEnCompetenciaConDatosNulos()
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);

        assertFalse(club.inscribirEquipoEnCompetencia(null, torneo), "Un equipo nulo no puede inscribirse.");
        assertFalse(club.inscribirEquipoEnCompetencia(equipo, null), "Una competencia nula no es válida.");
    }

    @Test
    void testInscribirEquipoEnCompetenciaSinRequisitos() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);

        assertFalse(club.inscribirEquipoEnCompetencia(equipo, torneo), "Un equipo con pocos jugadores no puede inscribirse.");

        assertEquals(0, torneo.getInscripciones().size(), "La competencia no debe tener inscripciones.");
        assertEquals(0, jsonDelClub().getJSONArray("competencias").length(), "El club no debe registrar la competencia.");
    }

    @Test
    void testInscribirEquipoEnCompetenciaRepetida() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);

        assertFalse(club.inscribirEquipoEnCompetencia(equipo, torneo), "Un equipo no puede inscribirse dos veces.");
        assertEquals(1, jsonDelClub().getJSONArray("competencias").length(), "La competencia no debe duplicarse en el club.");
    }

    @Test
    void testInscribirDosEquiposEnLaMismaCompetencia() throws Exception
    {
        Equipo equipoA = club.crearEquipo("Equipo A", 12, futbol, Categoria.MAYORES);
        Equipo equipoB = club.crearEquipo("Equipo B", 12, futbol, Categoria.MAYORES);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);

        club.inscribirEquipoEnCompetencia(equipoA, torneo);
        club.inscribirEquipoEnCompetencia(equipoB, torneo);

        assertEquals(2, torneo.getInscripciones().size(), "La competencia debe tener dos inscripciones.");
        assertEquals(1, jsonDelClub().getJSONArray("competencias").length(), "La competencia debe aparecer una sola vez en el club.");
    }

    @Test
    void testRegistrarPartidoInterno() throws Exception
    {
        Equipo local = club.crearEquipo("Local", 8, baloncesto, Categoria.MAYORES);
        Equipo visitante = club.crearEquipo("Visitante", 8, baloncesto, Categoria.MAYORES);
        llenarEquipo(local, "l", 5);
        llenarEquipo(visitante, "v", 5);

        PartidoInterno partido = club.registrarPartidoInterno(local, visitante, 80, 75, null);

        assertNotNull(partido, "El partido debe registrarse.");
        assertEquals(LocalDate.now(), partido.getFecha(), "El partido debe quedar con la fecha de hoy.");
        assertSame(local, partido.getGanador(), "El ganador del partido no es el esperado.");
        assertEquals(1, club.getHistorial().getPartidos().size(), "El historial debe tener un partido.");
        assertSame(partido, club.getHistorial().getPartidos().get(0), "El partido del historial no es el esperado.");
    }

    @Test
    void testRegistrarPartidoInternoConEstadisticas() throws Exception
    {
        Equipo local = club.crearEquipo("Local", 8, baloncesto, Categoria.MAYORES);
        Equipo visitante = club.crearEquipo("Visitante", 8, baloncesto, Categoria.MAYORES);
        llenarEquipo(local, "l", 5);
        llenarEquipo(visitante, "v", 5);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaBaloncesto(local.getInscripciones().get(0), 20, 5));
        estadisticas.add(new EstadisticaBaloncesto(visitante.getInscripciones().get(0), 15, 7));

        PartidoInterno partido = club.registrarPartidoInterno(local, visitante, 80, 75, estadisticas);

        assertEquals(2, partido.getEstadisticas().size(), "El partido debe tener dos estadísticas.");
    }

    @Test
    void testRegistrarPartidoInternoRegistraTarjetas() throws Exception
    {
        Equipo local = club.crearEquipo("Local", 8, futbol, Categoria.MAYORES);
        Equipo visitante = club.crearEquipo("Visitante", 8, futbol, Categoria.MAYORES);
        llenarEquipo(local, "l", 7);
        llenarEquipo(visitante, "v", 7);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(local.getInscripciones().get(0), 1, 0, 0, 1));
        estadisticas.add(new EstadisticaFutbol(local.getInscripciones().get(1), 0, 0, 1, 0));

        club.registrarPartidoInterno(local, visitante, 2, 0, estadisticas);

        assertFalse(local.estaHabilitado(jugadorDe(local, 0)), "El jugador con roja debe quedar suspendido.");
        assertTrue(local.estaHabilitado(jugadorDe(local, 1)), "El jugador con una amarilla sigue habilitado.");
        assertEquals(1, local.getInscripciones().get(1).getControlSanciones().getAmarillasAcumuladas(), "La amarilla debe acumularse.");
    }

    @Test
    void testRegistrarPartidoInternoConEquiposNulosOIguales() throws Exception
    {
        Equipo local = club.crearEquipo("Local", 8, baloncesto, Categoria.MAYORES);
        llenarEquipo(local, "l", 5);

        assertNull(club.registrarPartidoInterno(null, local, 1, 0, null), "Con local nulo no se registra el partido.");
        assertNull(club.registrarPartidoInterno(local, null, 1, 0, null), "Con visitante nulo no se registra el partido.");
        assertNull(club.registrarPartidoInterno(local, local, 1, 0, null), "Un equipo no puede jugar contra sí mismo.");
        assertEquals(0, club.getHistorial().getPartidos().size(), "No debe registrarse ningún partido.");
    }

    @Test
    void testRegistrarPartidoInternoConDeportesDistintos() throws Exception
    {
        Equipo basket = club.crearEquipo("Basket", 8, baloncesto, Categoria.MAYORES);
        Equipo soccer = club.crearEquipo("Soccer", 8, futbol, Categoria.MAYORES);
        llenarEquipo(basket, "b", 5);
        llenarEquipo(soccer, "s", 7);

        assertNull(club.registrarPartidoInterno(basket, soccer, 1, 0, null), "Los equipos deben ser del mismo deporte.");
    }

    @Test
    void testRegistrarPartidoInternoConCategoriasDistintas() throws Exception
    {
        Equipo mayores = club.crearEquipo("Mayores", 8, baloncesto, Categoria.MAYORES);
        Equipo juvenil = club.crearEquipo("Juvenil", 8, baloncesto, Categoria.SUB17);
        llenarEquipo(mayores, "m", 5);
        for(int i = 1; i <= 5; i++)
        {
            juvenil.agregarJugador(club.crearSocio(15, "Juvenil " + i, "juv" + i, "x"), "Alero", i);
        }

        assertNull(club.registrarPartidoInterno(mayores, juvenil, 1, 0, null), "Los equipos deben ser de la misma categoría.");
    }

    @Test
    void testRegistrarPartidoInternoConEquipoIncompleto() throws Exception
    {
        Equipo completo = club.crearEquipo("Completo", 8, baloncesto, Categoria.MAYORES);
        Equipo incompleto = club.crearEquipo("Incompleto", 8, baloncesto, Categoria.MAYORES);
        llenarEquipo(completo, "c", 5);
        llenarEquipo(incompleto, "i", 4);

        assertNull(club.registrarPartidoInterno(completo, incompleto, 1, 0, null), "Un equipo sin los jugadores mínimos no puede jugar.");
        assertNull(club.registrarPartidoInterno(incompleto, completo, 1, 0, null), "Un equipo sin los jugadores mínimos no puede jugar.");
    }

    @Test
    void testRegistrarPartidoExterno() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);

        PartidoExterno partido = club.registrarPartidoExterno(torneo, equipo, fecha, 3, 1, "Rival", "Estadio", null);

        assertNotNull(partido, "El partido debe registrarse.");
        assertEquals(fecha, partido.getFecha(), "La fecha del partido no es la esperada.");
        assertEquals("Rival", partido.getInstitucionRival(), "La institución rival no es la esperada.");
        assertEquals(1, torneo.getListaPartidos().size(), "La competencia debe tener un partido.");
        assertEquals(1, club.getHistorial().getPartidos().size(), "El historial debe tener un partido.");
        assertEquals(3, torneo.getTablaPosiciones().get(equipo), "La tabla debe reflejar la victoria.");
    }

    @Test
    void testRegistrarPartidoExternoConDatosNulos() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);

        assertNull(club.registrarPartidoExterno(null, equipo, fecha, 1, 0, "Rival", "Estadio", null), "Sin competencia no se registra el partido.");
        assertNull(club.registrarPartidoExterno(torneo, null, fecha, 1, 0, "Rival", "Estadio", null), "Sin equipo no se registra el partido.");
        assertEquals(0, club.getHistorial().getPartidos().size(), "No debe registrarse ningún partido.");
    }

    @Test
    void testRegistrarPartidoExternoConJugadorFueraDeLaNomina() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);
        torneo.getInscripcionDe(equipo).agregarANomina(ana);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(pedro), 1, 0, 0, 0));

        assertNull(club.registrarPartidoExterno(torneo, equipo, fecha, 1, 0, "Rival", "Estadio", estadisticas), "Un jugador fuera de la nómina no puede alinearse.");
        assertEquals(0, torneo.getListaPartidos().size(), "No debe registrarse el partido.");
    }

    @Test
    void testRegistrarPartidoExternoConJugadorEnLaNomina() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);
        torneo.getInscripcionDe(equipo).agregarANomina(ana);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(ana), 2, 0, 0, 0));

        PartidoExterno partido = club.registrarPartidoExterno(torneo, equipo, fecha, 2, 0, "Rival", "Estadio", estadisticas);

        assertNotNull(partido, "Un jugador de la nómina puede alinearse.");
        assertEquals(1, partido.getEstadisticas().size(), "El partido debe tener una estadística.");
        assertSame(ana, torneo.getGoleador(), "La goleadora de la competencia no es la esperada.");
    }

    @Test
    void testRegistrarPartidoExternoConJugadorSuspendido() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);
        torneo.getInscripcionDe(equipo).agregarANomina(ana);
        torneo.getInscripcionDe(equipo).getRegistroDe(ana).registrarTarjetas(0, 1);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(ana), 1, 0, 0, 0));

        assertNull(club.registrarPartidoExterno(torneo, equipo, fecha, 1, 0, "Rival", "Estadio", estadisticas), "Un jugador suspendido no puede alinearse.");
    }

    @Test
    void testRegistrarPartidoExternoRegistraTarjetasEnLaNomina() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        club.inscribirJugador(pedro, equipo, "Portero", 1);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        club.inscribirEquipoEnCompetencia(equipo, torneo);
        torneo.getInscripcionDe(equipo).agregarANomina(ana);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(ana), 0, 0, 0, 1));

        club.registrarPartidoExterno(torneo, equipo, fecha, 0, 1, "Rival", "Estadio", estadisticas);

        assertFalse(torneo.getInscripcionDe(equipo).puedeAlinear(ana), "La roja debe suspender al jugador en la competencia.");
        assertTrue(equipo.estaHabilitado(ana), "La roja de la competencia no debe afectar la inscripción al equipo.");
    }

    @Test
    void testRegistrarPartidoExternoSinInscripcionEnLaCompetencia() throws Exception
    {
        Equipo equipo = club.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);

        List<EstadisticaConjunto> estadisticas = new ArrayList<EstadisticaConjunto>();
        estadisticas.add(new EstadisticaFutbol(equipo.getInscripcionDe(ana), 1, 0, 0, 0));

        PartidoExterno partido = club.registrarPartidoExterno(torneo, equipo, fecha, 1, 0, "Rival", "Estadio", estadisticas);

        assertNotNull(partido, "Si el equipo no está inscrito no se valida la nómina.");
        assertEquals(1, torneo.getListaPartidos().size(), "La competencia debe tener el partido.");
    }

    @Test
    void testReservar()
    {
        Reserva reserva = club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);

        assertNotNull(reserva, "La reserva debe crearse.");
        assertSame(ana, reserva.getJugador(), "El jugador de la reserva no es el esperado.");
        assertSame(canchaTenis1, reserva.getInstalacion(), "La instalación de la reserva no es la esperada.");
        assertEquals(1, club.getHistorial().getReservas().size(), "El historial debe tener una reserva.");
    }

    @Test
    void testReservarConDatosNulos()
    {
        assertNull(club.reservar(null, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "Sin socio no se crea la reserva.");
        assertNull(club.reservar(ana, null, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "Sin disciplina no se crea la reserva.");
        assertNull(club.reservar(ana, tenis, null, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "Sin instalación no se crea la reserva.");
        assertEquals(0, club.getHistorial().getReservas().size(), "No debe haber reservas.");
    }

    @Test
    void testReservarConHorarioInvalido()
    {
        assertNull(club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(11, 0), LocalTime.of(10, 0), 2, Modalidad.SENCILLOS), "La hora final no puede ser anterior a la inicial.");
        assertNull(club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(10, 0), 2, Modalidad.SENCILLOS), "La hora final no puede ser igual a la inicial.");
    }

    @Test
    void testReservarConModalidadNoAdmitida()
    {
        assertNull(club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, Modalidad.SENCILLOS), "Sencillos con cuatro jugadores no se admite.");
        assertNull(club.reservar(ana, padel, canchaPadel, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "El pádel solo admite dobles.");
    }

    @Test
    void testReservarInstalacionOcupada()
    {
        club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(12, 0), 2, Modalidad.SENCILLOS);

        assertNull(club.reservar(pedro, tenis, canchaTenis1, fecha, LocalTime.of(11, 0), LocalTime.of(13, 0), 2, Modalidad.SENCILLOS), "No se puede reservar una instalación ocupada.");
        assertEquals(1, club.getHistorial().getReservas().size(), "Debe haber una sola reserva.");
    }

    @Test
    void testReservarInstalacionEnHorariosContiguos()
    {
        club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);

        assertNotNull(club.reservar(pedro, tenis, canchaTenis1, fecha, LocalTime.of(11, 0), LocalTime.of(12, 0), 2, Modalidad.SENCILLOS), "Un horario contiguo no se solapa.");
    }

    @Test
    void testReservarOtraFecha()
    {
        club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);

        assertNotNull(club.reservar(pedro, tenis, canchaTenis1, fecha.plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "En otra fecha la instalación está libre.");
    }

    @Test
    void testReservarSinCupoEnLaDisciplina()
    {
        club.reservar(ana, tenisUnaCancha, canchaUnica1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);

        assertNull(club.reservar(pedro, tenisUnaCancha, canchaUnica2, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "Si la disciplina llegó a su máximo simultáneo no se puede reservar otra instalación.");
    }

    @Test
    void testReservarDosInstalacionesConCupo()
    {
        club.reservar(ana, tenis, canchaTenis1, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);

        assertNotNull(club.reservar(pedro, tenis, canchaTenis2, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS), "Con máximo de dos instalaciones simultáneas se puede reservar la segunda.");
    }

    @Test
    void testProgramarPartidoPractica()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.AVANZADO);

        PartidoPractica partido = club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro));

        assertNotNull(partido, "El partido de práctica debe programarse.");
        assertEquals(1, club.getHistorial().getPartidosPractica().size(), "El historial debe tener un partido de práctica.");
        assertFalse(partido.isFinalizado(), "El partido nuevo no debe estar finalizado.");
    }

    @Test
    void testProgramarPartidoPracticaConDatosInvalidos()
    {
        assertNull(club.programarPartidoPractica(null, canchaTenis1, fecha, listaDe(ana, pedro)), "Sin disciplina no se programa el partido.");
        assertNull(club.programarPartidoPractica(tenis, null, fecha, listaDe(ana, pedro)), "Sin instalación no se programa el partido.");
        assertNull(club.programarPartidoPractica(tenis, canchaTenis1, fecha, null), "Sin jugadores no se programa el partido.");
        assertNull(club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana)), "Con un solo jugador no se programa el partido.");
        assertNull(club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro, ana)), "Con tres jugadores no se programa el partido.");
    }

    @Test
    void testProgramarPartidoPracticaConNivelesIncompatibles()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.PRINCIPIANTE);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.AVANZADO);

        assertNull(club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro)), "Un principiante y un avanzado no pueden jugar juntos.");
        assertEquals(0, club.getHistorial().getPartidosPractica().size(), "No debe registrarse el partido.");
    }

    @Test
    void testProgramarPartidoPracticaConJugadorSinInscripcion()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);

        assertNull(club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro)), "Un jugador sin nivel en la disciplina no puede jugar.");
    }

    @Test
    void testFinalizarPartidoPracticaActualizaElRanking()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.INTERMEDIO);
        PartidoPractica partido = club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro));

        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, ana, 12, 3, 5));
        estadisticas.add(new EstadisticaRaqueta(0, pedro, 6, 1, 9));

        club.finalizarPartidoPractica(partido, estadisticas);

        assertTrue(partido.isFinalizado(), "El partido debe quedar finalizado.");
        assertEquals(1016, club.getRanking(tenis, ana).getPuntajeElo(), 0.001, "La ganadora debe subir 16 puntos.");
        assertEquals(984, club.getRanking(tenis, pedro).getPuntajeElo(), 0.001, "El perdedor debe bajar 16 puntos.");
    }

    @Test
    void testFinalizarPartidoPracticaConRivalesDistintos()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.INTERMEDIO);
        club.getRanking(tenis, pedro).setPuntajeElo(1400);
        PartidoPractica partido = club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro));

        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, ana, 12, 3, 5));
        estadisticas.add(new EstadisticaRaqueta(0, pedro, 6, 1, 9));

        club.finalizarPartidoPractica(partido, estadisticas);

        assertEquals(1029.0909, club.getRanking(tenis, ana).getPuntajeElo(), 0.001, "Ganar contra un rival más fuerte debe subir más puntos.");
        assertEquals(1400 + 32 * (0 - 1.0 / (1 + Math.pow(10, (1000 - 1400) / 400.0))), club.getRanking(tenis, pedro).getPuntajeElo(), 0.001, "Perder contra un rival más débil debe bajar más puntos.");
    }

    @Test
    void testFinalizarPartidoPracticaNulo()
    {
        club.finalizarPartidoPractica(null, new ArrayList<EstadisticaPractica>());

        assertEquals(0, club.getHistorial().getPartidosPractica().size(), "No debe cambiar nada.");
    }

    @Test
    void testFinalizarPartidoPracticaConEstadisticasInvalidas()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.INTERMEDIO);
        PartidoPractica partido = club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, pedro));

        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, ana, 12, 3, 5));

        PrintStream errorOriginal = System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        try
        {
            club.finalizarPartidoPractica(partido, estadisticas);
        }
        finally
        {
            System.setErr(errorOriginal);
        }

        assertFalse(partido.isFinalizado(), "El partido no debe finalizarse con estadísticas inválidas.");
        assertEquals(1000, club.getRanking(tenis, ana).getPuntajeElo(), 0.001, "El ranking no debe cambiar.");
        assertEquals(1000, club.getRanking(tenis, pedro).getPuntajeElo(), 0.001, "El ranking no debe cambiar.");
    }

    @Test
    void testFinalizarPartidoPracticaSinRanking()
    {
        Socio sinRanking = new Socio(30, "Sin Ranking", "sr", "x");
        sinRanking.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        PartidoPractica partido = club.programarPartidoPractica(tenis, canchaTenis1, fecha, listaDe(ana, sinRanking));

        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, ana, 12, 3, 5));
        estadisticas.add(new EstadisticaRaqueta(0, sinRanking, 6, 1, 9));

        club.finalizarPartidoPractica(partido, estadisticas);

        assertTrue(partido.isFinalizado(), "El partido debe finalizarse.");
        assertEquals(1000, club.getRanking(tenis, ana).getPuntajeElo(), 0.001, "Sin rival con ranking el puntaje no debe cambiar.");
    }

    @Test
    void testPlanearSesion()
    {
        SesionEntrenamiento sesion = club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana, pedro));

        assertNotNull(sesion, "La sesión debe planearse.");
        assertSame(entrenador, sesion.getEntrenador(), "El entrenador de la sesión no es el esperado.");
        assertEquals(2, sesion.getConvocados().size(), "La sesión debe tener dos convocados.");
        assertEquals(1, club.getHistorial().getSesiones().size(), "El historial debe tener una sesión.");
    }

    @Test
    void testPlanearSesionConDatosInvalidos()
    {
        assertNull(club.planearSesion(null, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana)), "Sin entrenador no se planea la sesión.");
        assertNull(club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, null, listaDe(ana)), "Sin instalación no se planea la sesión.");
        assertNull(club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 0, canchaFutbol, listaDe(ana)), "Con duración cero no se planea la sesión.");
        assertNull(club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), -30, canchaFutbol, listaDe(ana)), "Con duración negativa no se planea la sesión.");
    }

    @Test
    void testPlanearSesionEnLaMismaInstalacionSolapada()
    {
        club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana));
        Entrenador otroEntrenador = new Entrenador(41, "Otro Entrenador", "otro", "x", "ENT02");

        assertNull(club.planearSesion(otroEntrenador, fecha, LocalTime.of(8, 30), 60, canchaFutbol, listaDe(pedro)), "No puede haber dos sesiones solapadas en la misma instalación.");
    }

    @Test
    void testPlanearSesionConElMismoEntrenadorSolapada()
    {
        club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana));
        CanchaFutbol otraCancha = new CanchaFutbol("Futbol 2");

        assertNull(club.planearSesion(entrenador, fecha, LocalTime.of(8, 30), 60, otraCancha, listaDe(pedro)), "Un entrenador no puede tener dos sesiones solapadas.");
    }

    @Test
    void testPlanearSesionSolapadaConDistintosRecursos()
    {
        club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana));
        Entrenador otroEntrenador = new Entrenador(41, "Otro Entrenador", "otro", "x", "ENT02");
        CanchaFutbol otraCancha = new CanchaFutbol("Futbol 2");

        assertNotNull(club.planearSesion(otroEntrenador, fecha, LocalTime.of(8, 30), 60, otraCancha, listaDe(pedro)), "Dos sesiones simultáneas con distinto entrenador e instalación son válidas.");
    }

    @Test
    void testPlanearSesionContigua()
    {
        club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana));

        assertNotNull(club.planearSesion(entrenador, fecha, LocalTime.of(9, 0), 60, canchaFutbol, listaDe(ana)), "Una sesión que empieza cuando termina la anterior es válida.");
    }

    @Test
    void testPlanearSesionEnOtraFecha()
    {
        club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana));

        assertNotNull(club.planearSesion(entrenador, fecha.plusDays(1), LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana)), "En otra fecha no hay conflicto.");
    }

    @Test
    void testRegistrarAsistencia()
    {
        SesionEntrenamiento sesion = club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana, pedro));

        club.registrarAsistencia(sesion, ana, EstadoAsistencia.ASISTIO, 4, "Buen trabajo");

        assertEquals(1, sesion.getAsistencias().size(), "La sesión debe tener una asistencia.");
        assertEquals(4, sesion.getAsistencias().get(0).getEvaluacion(), "La evaluación registrada no es la esperada.");
    }

    @Test
    void testRegistrarAsistenciaConDatosNulos()
    {
        SesionEntrenamiento sesion = club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana, pedro));

        club.registrarAsistencia(null, ana, EstadoAsistencia.ASISTIO, 0, null);
        club.registrarAsistencia(sesion, null, EstadoAsistencia.ASISTIO, 0, null);

        assertEquals(0, sesion.getAsistencias().size(), "No debe registrarse ninguna asistencia.");
    }

    @Test
    void testRegistrarAsistenciaDeNoConvocado()
    {
        SesionEntrenamiento sesion = club.planearSesion(entrenador, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(ana));

        club.registrarAsistencia(sesion, pedro, EstadoAsistencia.ASISTIO, 0, null);

        assertEquals(0, sesion.getAsistencias().size(), "No debe registrarse la asistencia de un socio no convocado.");
    }

    @Test
    void testRegistrarVentaDeTienda()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);

        club.registrarVenta(ana, crearCompra(pelotas, 2), Rubro.TIENDA, null, null, 0, 0);

        assertEquals(48, pelotas.getCantidad(), "El inventario del artículo debe reducirse.");
        assertEquals(1, jsonDelClub().getJSONArray("ventas").length(), "El club debe tener una venta.");
        assertEquals(23800, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "El total de la venta no es el esperado.");
        assertEquals(476, ana.getPuntosFidelidad(), "El socio debe acumular los puntos de la venta.");
    }

    @Test
    void testRegistrarVentaDeCafeteria()
    {
        Bebida limonada = new Bebida("Limonada", "Frias", 6000, 10, false);

        club.registrarVenta(ana, crearCompra(limonada, 1), Rubro.CAFETERIA, null, null, 0, 0);

        assertEquals(9, limonada.getCantidad(), "El inventario de la bebida debe reducirse.");
        assertEquals(6480, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "El total incluye el impuesto al consumo.");
        assertEquals(130, ana.getPuntosFidelidad(), "El socio debe acumular los puntos de la venta.");
    }

    @Test
    void testRegistrarVentaConDatosInvalidos()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);

        club.registrarVenta(null, crearCompra(pelotas, 1), Rubro.TIENDA, null, null, 0, 0);
        club.registrarVenta(ana, null, Rubro.TIENDA, null, null, 0, 0);
        club.registrarVenta(ana, new LinkedHashMap<Producto, Integer>(), Rubro.TIENDA, null, null, 0, 0);
        club.registrarVenta(ana, crearCompra(pelotas, 1), null, null, null, 0, 0);

        assertEquals(50, pelotas.getCantidad(), "El inventario no debe cambiar.");
        assertEquals(0, jsonDelClub().getJSONArray("ventas").length(), "No debe registrarse ninguna venta.");
    }

    @Test
    void testRegistrarVentaSinExistencias()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 3);

        club.registrarVenta(ana, crearCompra(pelotas, 4), Rubro.TIENDA, null, null, 0, 0);

        assertEquals(3, pelotas.getCantidad(), "El inventario no debe cambiar.");
        assertEquals(0, jsonDelClub().getJSONArray("ventas").length(), "No debe registrarse la venta.");
        assertEquals(0, ana.getPuntosFidelidad(), "No debe acumularse ningún punto.");
    }

    @Test
    void testRegistrarVentaConTodasLasExistencias()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 3);

        club.registrarVenta(ana, crearCompra(pelotas, 3), Rubro.TIENDA, null, null, 0, 0);

        assertEquals(0, pelotas.getCantidad(), "El inventario debe quedar en cero.");
        assertEquals(1, jsonDelClub().getJSONArray("ventas").length(), "Debe registrarse la venta.");
    }

    @Test
    void testRegistrarVentaConUnProductoSinExistenciasEntreVarios()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        Articulo gorra = new Articulo("Gorra", "Accesorios", 30000, 0);
        Map<Producto, Integer> compra = new LinkedHashMap<Producto, Integer>();
        compra.put(pelotas, 1);
        compra.put(gorra, 1);

        club.registrarVenta(ana, compra, Rubro.TIENDA, null, null, 0, 0);

        assertEquals(50, pelotas.getCantidad(), "Si un producto falla no debe cambiar ninguno.");
        assertEquals(0, jsonDelClub().getJSONArray("ventas").length(), "No debe registrarse la venta.");
    }

    @Test
    void testRegistrarVentaConRubroNoVendible()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);

        club.registrarVenta(ana, crearCompra(pelotas, 1), Rubro.MENSUALIDAD, null, null, 0, 0);

        assertEquals(50, pelotas.getCantidad(), "El inventario no debe cambiar.");
        assertEquals(0, jsonDelClub().getJSONArray("ventas").length(), "No debe registrarse la venta.");
    }

    @Test
    void testRegistrarVentaRedimiendoPuntos()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        ana.acumularPuntos(1000);

        club.registrarVenta(ana, crearCompra(pelotas, 2), Rubro.TIENDA, null, null, 500, 0);

        assertEquals(23300, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "Los puntos redimidos deben descontarse del total.");
        assertEquals(1000 - 500 + 466, ana.getPuntosFidelidad(), "El saldo debe descontar lo redimido y sumar lo generado.");
    }

    @Test
    void testRegistrarVentaRedimiendoMasPuntosDeLosQueTiene()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        ana.acumularPuntos(100);

        club.registrarVenta(ana, crearCompra(pelotas, 2), Rubro.TIENDA, null, null, 500, 0);

        assertEquals(23800, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "Si los puntos no alcanzan no deben redimirse.");
        assertEquals(100 + 476, ana.getPuntosFidelidad(), "El saldo no debe descontarse.");
    }

    @Test
    void testRegistrarVentaRedimiendoPuntosNegativos()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        ana.acumularPuntos(100);

        club.registrarVenta(ana, crearCompra(pelotas, 2), Rubro.TIENDA, null, null, -50, 0);

        assertEquals(23800, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "Los puntos negativos se ignoran.");
    }

    @Test
    void testRegistrarVentaPagadaConPuntos()
    {
        Articulo barato = new Articulo("Barato", "Varios", 1000, 10);
        ana.acumularPuntos(5000);

        club.registrarVenta(ana, crearCompra(barato, 1), Rubro.TIENDA, null, null, 5000, 0);

        int puntosNecesarios = (int) Math.ceil(1000 * 1.19);
        assertEquals(0, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "La venta pagada con puntos debe costar 0.");
        assertEquals(5000 - puntosNecesarios, ana.getPuntosFidelidad(), "Solo deben descontarse los puntos necesarios.");
    }

    @Test
    void testRegistrarVentaConPropina()
    {
        Bebida limonada = new Bebida("Limonada", "Frias", 6000, 10, false);

        club.registrarVenta(ana, crearCompra(limonada, 1), Rubro.CAFETERIA, null, null, 0, 600);

        assertEquals(6480 + 600, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "La propina debe sumarse al total.");
        assertEquals(600, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("propina"), 0.001, "La propina registrada no es la esperada.");
    }

    @Test
    void testRegistrarVentaConCodigoDeDescuento()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);

        club.registrarVenta(ana, crearCompra(pelotas, 2), Rubro.TIENDA, null, new CodigoDescuento("OTRO"), 0, 0);

        assertEquals(23800 * 0.92, jsonDelClub().getJSONArray("ventas").getJSONObject(0).getDouble("total"), 0.001, "El código de descuento debe aplicar 8%.");
    }

    @Test
    void testRegistrarVentaDeBebidaCalienteRegistraLaHora()
    {
        Bebida cafe = new Bebida("Cafe", "Calientes", 4000, 10, true);

        club.registrarVenta(ana, crearCompra(cafe, 1), Rubro.CAFETERIA, canchaTenis1, null, 0, 0);

        assertNotNull(ana.getHoraUltimaBebidaCaliente(), "El socio debe quedar con la hora de su bebida caliente.");
        assertTrue(ana.llevaBebidaCaliente(java.time.LocalDateTime.now()), "El socio debe llevar la bebida caliente.");
    }

    @Test
    void testRegistrarVentaDeBebidaFriaNoRegistraLaHora()
    {
        Bebida limonada = new Bebida("Limonada", "Frias", 6000, 10, false);

        club.registrarVenta(ana, crearCompra(limonada, 1), Rubro.CAFETERIA, null, null, 0, 0);

        assertNull(ana.getHoraUltimaBebidaCaliente(), "Una bebida fría no debe registrar la hora.");
    }

    @Test
    void testRegistrarVentaDeBebidaCalienteEnCanchaRestringida()
    {
        Bebida cafe = new Bebida("Cafe", "Calientes", 4000, 10, true);

        club.registrarVenta(ana, crearCompra(cafe, 1), Rubro.CAFETERIA, canchaPadel, null, 0, 0);

        assertEquals(10, cafe.getCantidad(), "El inventario no debe cambiar.");
        assertEquals(0, jsonDelClub().getJSONArray("ventas").length(), "No debe registrarse la venta.");
        assertNull(ana.getHoraUltimaBebidaCaliente(), "No debe registrarse la bebida caliente.");
    }

    @Test
    void testRegistrarVentaDeSnackEnCanchaRestringida()
    {
        Snack galletas = new Snack("Galletas", "Snacks", 3000, 10, new ArrayList<Tienda_Cafeteria_Inventario.Alergeno>());

        club.registrarVenta(ana, crearCompra(galletas, 1), Rubro.CAFETERIA, canchaPadel, null, 0, 0);

        assertEquals(9, galletas.getCantidad(), "El snack debe poder venderse en la cancha de pádel.");
    }

    @Test
    void testGenerarReporteVentas()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        Bebida limonada = new Bebida("Limonada", "Frias", 6000, 10, false);
        club.registrarVenta(ana, crearCompra(pelotas, 1), Rubro.TIENDA, null, null, 0, 0);
        club.registrarVenta(pedro, crearCompra(limonada, 1), Rubro.CAFETERIA, null, null, 0, 0);

        Map<LocalDate, Map<Rubro, Double>> reporte = club.generarReporteVentas(null, Periodo.DIARIO);

        assertEquals(1, reporte.size(), "Debe haber un solo día con ventas.");
        assertEquals(11900, reporte.get(LocalDate.now()).get(Rubro.TIENDA), 0.001, "El total de tienda no es el esperado.");
        assertEquals(6480, reporte.get(LocalDate.now()).get(Rubro.CAFETERIA), 0.001, "El total de cafetería no es el esperado.");
    }

    @Test
    void testGenerarReporteVentasFiltradoPorRubro()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        Bebida limonada = new Bebida("Limonada", "Frias", 6000, 10, false);
        club.registrarVenta(ana, crearCompra(pelotas, 1), Rubro.TIENDA, null, null, 0, 0);
        club.registrarVenta(pedro, crearCompra(limonada, 1), Rubro.CAFETERIA, null, null, 0, 0);

        Map<LocalDate, Map<Rubro, Double>> reporte = club.generarReporteVentas(Rubro.TIENDA, Periodo.MENSUAL);

        assertEquals(1, reporte.size(), "Debe haber un solo mes con ventas.");
        assertEquals(1, reporte.get(LocalDate.now().withDayOfMonth(1)).size(), "Solo debe estar el rubro de tienda.");
    }

    @Test
    void testGenerarReporteVentasSinVentas()
    {
        assertTrue(club.generarReporteVentas(null, Periodo.DIARIO).isEmpty(), "Sin ventas el reporte debe estar vacío.");
    }

    @Test
    void testGenerarReporteDeportivo()
    {
        club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(pedro, tenis, NivelJugador.INTERMEDIO);
        club.getRanking(tenis, ana).setPuntajeElo(1100);
        club.getRanking(tenis, pedro).setPuntajeElo(1300);

        List<RankingJugadorDisciplina> reporte = club.generarReporteDeportivo();

        assertEquals(2, reporte.size(), "Debe haber dos rankings.");
        assertSame(pedro, reporte.get(0).getJugador(), "El primero debe ser el de mayor puntaje.");
        assertSame(ana, reporte.get(1).getJugador(), "El segundo debe ser el de menor puntaje.");
    }

    @Test
    void testGenerarReporteDeportivoSinRankings()
    {
        assertTrue(club.generarReporteDeportivo().isEmpty(), "Sin rankings el reporte debe estar vacío.");
    }

    @Test
    void testReabastecerTienda()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 5);

        club.reabastecerTienda(pelotas, 3);

        assertEquals(3, jsonDelClub().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda debe reflejar el reabastecimiento.");
    }

    @Test
    void testReabastecerTiendaConDatosInvalidos()
    {
        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 5);

        club.reabastecerTienda(null, 3);
        club.reabastecerTienda(pelotas, 0);
        club.reabastecerTienda(pelotas, -2);

        assertEquals(0, jsonDelClub().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda no debe cambiar.");
    }

    @Test
    void testReabastecerCafeteria()
    {
        Snack galletas = new Snack("Galletas", "Snacks", 3000, 10, new ArrayList<Tienda_Cafeteria_Inventario.Alergeno>());

        club.reabasteceCafeteriaSocios(galletas, 2);

        assertEquals(2, jsonDelClub().getJSONObject("cafeteria").getJSONArray("snacks").length(), "La cafetería debe reflejar el reabastecimiento.");
    }

    @Test
    void testReabastecerCafeteriaConDatosInvalidos()
    {
        Snack galletas = new Snack("Galletas", "Snacks", 3000, 10, new ArrayList<Tienda_Cafeteria_Inventario.Alergeno>());

        club.reabasteceCafeteriaSocios(null, 2);
        club.reabasteceCafeteriaSocios(galletas, 0);
        club.reabasteceCafeteriaSocios(galletas, -1);

        assertEquals(0, jsonDelClub().getJSONObject("cafeteria").getJSONArray("snacks").length(), "La cafetería no debe cambiar.");
    }

    @Test
    void testMoverArticulo()
    {
        Ubicacion bodega = new Ubicacion("Bodega");
        Ubicacion cancha = new Ubicacion("Cancha");
        Inventario inventarioBodega = new Inventario(new ArrayList<Instalacion>(), bodega);
        Inventario inventarioCancha = new Inventario(new ArrayList<Instalacion>(), cancha);
        club.getInventario().add(inventarioBodega);
        club.getInventario().add(inventarioCancha);
        Articulo conos = new Articulo("Conos", "Entrenamiento", 2000, 40);
        inventarioBodega.agregarArticulo(conos);

        assertTrue(club.moverArticulo(conos, bodega, cancha), "El artículo debe poder moverse.");

        assertFalse(inventarioBodega.tieneArticulo(conos), "El artículo no debe seguir en el origen.");
        assertTrue(inventarioCancha.tieneArticulo(conos), "El artículo debe estar en el destino.");
    }

    @Test
    void testMoverArticuloQueNoEstaEnElOrigen()
    {
        Ubicacion bodega = new Ubicacion("Bodega");
        Ubicacion cancha = new Ubicacion("Cancha");
        club.getInventario().add(new Inventario(new ArrayList<Instalacion>(), bodega));
        club.getInventario().add(new Inventario(new ArrayList<Instalacion>(), cancha));

        assertFalse(club.moverArticulo(new Articulo("Conos", "Entrenamiento", 2000, 40), bodega, cancha), "No se puede mover un artículo que no está en el origen.");
    }

    @Test
    void testMoverArticuloQueYaEstaEnElDestino()
    {
        Ubicacion bodega = new Ubicacion("Bodega");
        Ubicacion cancha = new Ubicacion("Cancha");
        Inventario inventarioBodega = new Inventario(new ArrayList<Instalacion>(), bodega);
        Inventario inventarioCancha = new Inventario(new ArrayList<Instalacion>(), cancha);
        club.getInventario().add(inventarioBodega);
        club.getInventario().add(inventarioCancha);
        Articulo conos = new Articulo("Conos", "Entrenamiento", 2000, 40);
        inventarioBodega.agregarArticulo(conos);
        inventarioCancha.agregarArticulo(conos);

        assertFalse(club.moverArticulo(conos, bodega, cancha), "No se puede mover un artículo que ya está en el destino.");
    }

    @Test
    void testMoverArticuloConUbicacionInexistente()
    {
        Ubicacion bodega = new Ubicacion("Bodega");
        Inventario inventarioBodega = new Inventario(new ArrayList<Instalacion>(), bodega);
        club.getInventario().add(inventarioBodega);
        Articulo conos = new Articulo("Conos", "Entrenamiento", 2000, 40);
        inventarioBodega.agregarArticulo(conos);

        assertFalse(club.moverArticulo(conos, bodega, new Ubicacion("Fantasma")), "No se puede mover a una ubicación sin inventario.");
        assertFalse(club.moverArticulo(conos, new Ubicacion("Fantasma"), bodega), "No se puede mover desde una ubicación sin inventario.");
        assertTrue(inventarioBodega.tieneArticulo(conos), "El artículo debe seguir en la bodega.");
    }

    @Test
    void testRegistrarSugerencia() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");

        SugerenciaArticulo sugerencia = club.registrarSugerencia(empleado, "Gorra", "Accesorios", 35000);

        assertNotNull(sugerencia, "La sugerencia debe registrarse.");
        assertEquals("Gorra", sugerencia.getNombrePropuesto(), "El nombre propuesto no es el esperado.");
        assertEquals(EstadoSugerencia.PENDIENTE, sugerencia.getEstado(), "La sugerencia debe quedar pendiente.");
        assertEquals(1, jsonDelClub().getJSONArray("sugerencias").length(), "El club debe tener una sugerencia.");
    }

    @Test
    void testRegistrarSugerenciaSinEmpleado()
    {
        assertNull(club.registrarSugerencia(null, "Gorra", "Accesorios", 35000), "Sin empleado no se registra la sugerencia.");
        assertEquals(0, jsonDelClub().getJSONArray("sugerencias").length(), "No debe haber sugerencias.");
    }

    @Test
    void testResolverSugerenciaAprobada()
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        SugerenciaArticulo sugerencia = club.registrarSugerencia(empleado, "Gorra", "Accesorios", 35000);

        club.resolverSugerencia(sugerencia, true);

        assertEquals(EstadoSugerencia.ACEPTADA, sugerencia.getEstado(), "La sugerencia debe quedar aceptada.");
        JSONArray articulos = jsonDelClub().getJSONObject("tienda").getJSONArray("articulos");
        assertEquals(1, articulos.length(), "La tienda debe tener el nuevo artículo.");
        assertEquals("Gorra", articulos.getJSONObject(0).getString("nombre"), "El nombre del nuevo artículo no es el esperado.");
        assertEquals("Accesorios", articulos.getJSONObject(0).getString("categoria"), "La categoría del nuevo artículo no es la esperada.");
        assertEquals(35000, articulos.getJSONObject(0).getDouble("precio"), 0.001, "El precio del nuevo artículo no es el esperado.");
        assertEquals(0, articulos.getJSONObject(0).getInt("cantidad"), "El nuevo artículo debe empezar sin existencias.");
    }

    @Test
    void testResolverSugerenciaRechazada()
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        SugerenciaArticulo sugerencia = club.registrarSugerencia(empleado, "Gorra", "Accesorios", 35000);

        club.resolverSugerencia(sugerencia, false);

        assertEquals(EstadoSugerencia.RECHAZADA, sugerencia.getEstado(), "La sugerencia debe quedar rechazada.");
        assertEquals(0, jsonDelClub().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda no debe tener artículos nuevos.");
    }

    @Test
    void testResolverSugerenciaNoRegistrada()
    {
        SugerenciaArticulo ajena = new SugerenciaArticulo("Gorra", "Accesorios", 35000);

        club.resolverSugerencia(ajena, true);
        club.resolverSugerencia(null, true);

        assertEquals(EstadoSugerencia.PENDIENTE, ajena.getEstado(), "Una sugerencia que no está en el club no debe cambiar.");
        assertEquals(0, jsonDelClub().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda no debe tener artículos nuevos.");
    }

    @Test
    void testAsignarTurno() throws Exception
    {
        cargarClubConUsuarios(new Entrenador(30, "Entrenador Uno", "e1", "1", "E1"));
        Entrenador e1 = (Entrenador) club.iniciarSesion("e1", "1");
        Turno turno = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));

        club.asignarTurno(e1, turno);

        assertEquals(1, e1.getTurnos().size(), "El empleado debe tener un turno.");
        assertSame(turno, e1.getTurnos().get(0), "El turno asignado no es el esperado.");
    }

    @Test
    void testAsignarTurnoConDatosNulos() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");

        club.asignarTurno(null, new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        club.asignarTurno(empleado, null);

        assertEquals(0, empleado.getTurnos().size(), "El empleado no debe recibir turnos.");
    }

    @Test
    void testRegistrarSolicitudTurno() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        empleado.agregarTurno(actual);

        club.registrarSolicitudTurno(empleado, actual, nuevo);

        assertEquals(1, obtenerSolicitudes().size(), "El club debe tener una solicitud.");
        assertEquals(EstadoSolicitud.PENDIENTE, obtenerSolicitudes().get(0).getEstado(), "La solicitud debe quedar pendiente.");
        assertEquals(1, jsonDelClub().getJSONArray("solicitudes").length(), "El estado guardado debe incluir la solicitud.");
    }

    @Test
    void testRegistrarSolicitudTurnoSinSolicitante() throws Exception
    {
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));

        club.registrarSolicitudTurno(null, actual, nuevo);

        assertEquals(0, obtenerSolicitudes().size(), "No debe registrarse ninguna solicitud.");
    }

    @Test
    void testRegistrarSolicitudTurnoAjeno() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        Turno ajeno = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));

        assertThrows(ReglaNegocioException.class, () -> club.registrarSolicitudTurno(empleado, ajeno, nuevo), "No se puede solicitar el cambio de un turno ajeno.");

        assertEquals(0, obtenerSolicitudes().size(), "No debe registrarse ninguna solicitud.");
    }

    private Entrenador[] prepararEquipoDeTurnos() throws Exception
    {
        Entrenador uno = new Entrenador(30, "Entrenador Uno", "e1", "1", "E1");
        Entrenador dos = new Entrenador(31, "Entrenador Dos", "e2", "2", "E2");
        Entrenador tres = new Entrenador(32, "Entrenador Tres", "e3", "3", "E3");
        Fisioterapeuta fisio = new Fisioterapeuta(33, "Fisio Uno", "f1", "4", "F1");
        uno.agregarTurno(new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        dos.agregarTurno(new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        tres.agregarTurno(new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        fisio.agregarTurno(new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0)));

        cargarClubConUsuarios(uno, dos, tres, fisio);

        return new Entrenador[] {(Entrenador) club.iniciarSesion("e1", "1"), (Entrenador) club.iniciarSesion("e2", "2"), (Entrenador) club.iniciarSesion("e3", "3")};
    }

    @Test
    void testResolverSolicitudAprobada() throws Exception
    {
        Entrenador[] entrenadores = prepararEquipoDeTurnos();
        Entrenador e3 = entrenadores[2];
        Turno actual = e3.getTurnos().get(0);
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        club.registrarSolicitudTurno(e3, actual, nuevo);
        SolicitudCambioTurno solicitud = obtenerSolicitudes().get(0);

        club.resolverSolicitud(solicitud, true);

        assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado(), "La solicitud debe quedar aceptada.");
        assertEquals(1, e3.getTurnos().size(), "El empleado debe seguir con un turno.");
        assertSame(nuevo, e3.getTurnos().get(0), "El turno del empleado debe cambiar por el nuevo.");
    }

    @Test
    void testResolverSolicitudRechazada() throws Exception
    {
        Entrenador[] entrenadores = prepararEquipoDeTurnos();
        Entrenador e3 = entrenadores[2];
        Turno actual = e3.getTurnos().get(0);
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        club.registrarSolicitudTurno(e3, actual, nuevo);
        SolicitudCambioTurno solicitud = obtenerSolicitudes().get(0);

        club.resolverSolicitud(solicitud, false);

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado(), "La solicitud debe quedar rechazada.");
        assertSame(actual, e3.getTurnos().get(0), "El turno del empleado no debe cambiar.");
    }

    @Test
    void testResolverSolicitudQueNoCumpleElMinimo() throws Exception
    {
        Entrenador[] entrenadores = prepararEquipoDeTurnos();
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno actual1 = entrenadores[0].getTurnos().get(0);
        Turno actual3 = entrenadores[2].getTurnos().get(0);
        club.registrarSolicitudTurno(entrenadores[0], actual1, nuevo);
        club.registrarSolicitudTurno(entrenadores[2], actual3, nuevo);
        SolicitudCambioTurno primera = obtenerSolicitudes().get(0);
        SolicitudCambioTurno segunda = obtenerSolicitudes().get(1);

        club.resolverSolicitud(primera, true);
        club.resolverSolicitud(segunda, true);

        assertEquals(EstadoSolicitud.ACEPTADA, primera.getEstado(), "La primera solicitud deja a dos entrenadores y debe aceptarse.");
        assertEquals(EstadoSolicitud.RECHAZADA, segunda.getEstado(), "La segunda dejaría a un solo entrenador y debe rechazarse.");
        assertSame(actual3, entrenadores[2].getTurnos().get(0), "El turno del empleado no debe cambiar si se rechaza.");
    }

    @Test
    void testResolverSolicitudNoRegistrada() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        empleado.agregarTurno(actual);
        SolicitudCambioTurno ajena = empleado.solicitarCambioTurno(actual, nuevo);

        club.resolverSolicitud(ajena, true);
        club.resolverSolicitud(null, true);

        assertEquals(EstadoSolicitud.PENDIENTE, ajena.getEstado(), "Una solicitud que no está en el club no debe cambiar.");
    }

    @Test
    void testResolverSolicitudYaResuelta() throws Exception
    {
        Entrenador[] entrenadores = prepararEquipoDeTurnos();
        Entrenador e3 = entrenadores[2];
        Turno actual = e3.getTurnos().get(0);
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        club.registrarSolicitudTurno(e3, actual, nuevo);
        SolicitudCambioTurno solicitud = obtenerSolicitudes().get(0);
        club.resolverSolicitud(solicitud, false);

        club.resolverSolicitud(solicitud, true);

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado(), "Una solicitud ya resuelta no debe cambiar de estado.");
    }

    @Test
    void testGuardarDatos() throws Exception
    {
        File archivo = new File(carpetaTemporal, "club.json");
        archivo.delete();

        club.guardarDatos();

        assertTrue(archivo.exists(), "Debe crearse el archivo del club.");
        assertEquals("Club Seneca", new JSONObject(Files.readString(archivo.toPath())).getString("nombre"), "El nombre guardado no es el esperado.");
    }

    @Test
    void testCargarDatos()
    {
        ClubSeneca otroClub = new ClubSeneca("Otro Club");
        otroClub.setPersistencia(persistencia);

        otroClub.cargarDatos();

        assertNotNull(otroClub.iniciarSesion("ana", "1234"), "El club debe cargar los socios guardados.");
        assertNotNull(otroClub.iniciarSesion("pgil", "5678"), "El club debe cargar los socios guardados.");
        assertEquals("Club Seneca", otroClub.toJSON().getString("nombre"), "El club debe cargar el nombre guardado.");
    }

    @Test
    void testCargarDatosSinArchivo() throws Exception
    {
        File carpetaVacia = new File(carpetaTemporal, "vacia");
        ClubSeneca otroClub = new ClubSeneca("Otro Club");
        otroClub.setPersistencia(new Persistencia(carpetaVacia.getAbsolutePath()));

        otroClub.cargarDatos();

        assertEquals("Otro Club", otroClub.toJSON().getString("nombre"), "Sin archivo el club debe conservar su estado.");
    }

    @Test
    void testGuardarDatosEnUnaRutaInvalida() throws Exception
    {
        File archivo = new File(carpetaTemporal, "archivo.txt");
        Files.writeString(archivo.toPath(), "contenido");
        club.setPersistencia(new Persistencia(archivo.getAbsolutePath()));

        UncheckedIOException excepcion = assertThrows(UncheckedIOException.class, () -> club.guardarDatos(), "Debe lanzarse UncheckedIOException si no se puede guardar.");

        assertEquals("No se pudieron guardar los datos del club", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testCargarDatosDeUnArchivoIlegible() throws Exception
    {
        File carpeta = new File(carpetaTemporal, "ilegible");
        new File(carpeta, "club.json").mkdirs();
        ClubSeneca otroClub = new ClubSeneca("Otro Club");
        otroClub.setPersistencia(new Persistencia(carpeta.getAbsolutePath()));

        UncheckedIOException excepcion = assertThrows(UncheckedIOException.class, () -> otroClub.cargarDatos(), "Debe lanzarse UncheckedIOException si no se puede leer.");

        assertEquals("No se pudieron cargar los datos del club", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = jsonDelClub();

        assertEquals("Club Seneca", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(2, json.getJSONArray("usuarios").length(), "El JSON debe tener dos usuarios.");

        String[] claves = {"deportes", "disciplinas", "rankings", "inventarios", "instalacionesConjunto", "equipos", "competencias", "ventas", "solicitudes", "sugerencias"};
        for(String clave : claves)
        {
            assertTrue(json.has(clave), "El JSON debe tener la clave " + clave + ".");
        }
        assertTrue(json.has("tienda"), "El JSON debe tener la tienda.");
        assertTrue(json.has("cafeteria"), "El JSON debe tener la cafetería.");
        assertTrue(json.has("historial"), "El JSON debe tener el historial.");
    }

    @Test
    void testToJSONIncluyeInstalacionesDeConjuntoDeLosInventarios()
    {
        Inventario inventario = new Inventario(new ArrayList<Instalacion>(List.of(canchaFutbol)), new Ubicacion("Bodega"));
        club.getInventario().add(inventario);

        JSONObject json = jsonDelClub();

        assertEquals(1, json.getJSONArray("instalacionesConjunto").length(), "El JSON debe incluir la instalación de conjunto.");
        assertEquals("Futbol 1", json.getJSONArray("instalacionesConjunto").getJSONObject(0).getString("nombre"), "El nombre de la instalación no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        ClubSeneca restaurado = ClubSeneca.fromJSON(jsonDelClub());

        assertNotNull(restaurado.iniciarSesion("ana", "1234"), "Debe restaurarse el socio.");
        assertEquals(2, restaurado.toJSON().getJSONArray("usuarios").length(), "Deben restaurarse los dos usuarios.");
    }

    @Test
    void testFromJSONConDeportesInventariosYSolicitudes() throws Exception
    {
        Entrenador empleado = new Entrenador(30, "Luis Mora", "lmora", "abc", "E01");
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        empleado.agregarTurno(actual);
        SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(actual, nuevo);

        Inventario inventario = new Inventario(new ArrayList<Instalacion>(List.of(canchaFutbol)), new Ubicacion("Bodega"));

        JSONObject json = new JSONObject();
        json.put("nombre", "Club Completo");
        json.put("usuarios", new JSONArray().put(empleado.toJSON()));
        json.put("deportes", new JSONArray().put(futbol.toJSON()));
        json.put("inventarios", new JSONArray().put(inventario.toJSON()));
        json.put("solicitudes", new JSONArray().put(solicitud.toJSON()));

        club = ClubSeneca.fromJSON(json);

        JSONObject restaurado = jsonDelClub();
        assertEquals("Club Completo", restaurado.getString("nombre"), "El nombre restaurado no es el esperado.");
        assertEquals(1, restaurado.getJSONArray("deportes").length(), "Debe restaurarse un deporte.");
        assertEquals("Futbol", restaurado.getJSONArray("deportes").getJSONObject(0).getString("nombre"), "El deporte restaurado no es el esperado.");
        assertEquals(1, club.getInventario().size(), "Debe restaurarse un inventario.");
        assertEquals("Futbol 1", club.getInventario().get(0).getInstalaciones().get(0).getNombre(), "La instalación del inventario restaurado no es la esperada.");
        assertEquals(1, obtenerSolicitudes().size(), "Debe restaurarse una solicitud.");
        assertEquals(EstadoSolicitud.PENDIENTE, obtenerSolicitudes().get(0).getEstado(), "El estado de la solicitud restaurada no es el esperado.");
        assertEquals("lmora", obtenerSolicitudes().get(0).getSolicitante().getLogin(), "El solicitante restaurado no es el esperado.");
    }

    @Test
    void testFromJSONMinimo()
    {
        JSONObject json = new JSONObject();
        json.put("nombre", "Club Minimo");

        ClubSeneca restaurado = ClubSeneca.fromJSON(json);

        assertEquals("Club Minimo", restaurado.toJSON().getString("nombre"), "El nombre restaurado no es el esperado.");
        assertEquals(0, restaurado.toJSON().getJSONArray("usuarios").length(), "Sin usuarios en el JSON el club debe quedar sin usuarios.");
    }

    @Test
    void testFromJSONSinNombre()
    {
        assertThrows(org.json.JSONException.class, () -> ClubSeneca.fromJSON(new JSONObject()), "Debe lanzarse JSONException si falta el nombre.");
    }

    @Test
    void testIdaYVueltaDeUnClubCompleto() throws Exception
    {
        cargarClubConUsuarios(new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01"));
        Entrenador entrenadorDelClub = (Entrenador) club.iniciarSesion("jruiz", "pass");
        Socio socioA = club.crearSocio(20, "Ana Torres", "ana", "1234");
        Socio socioB = club.crearSocio(25, "Pedro Gil", "pgil", "5678");

        Equipo local = club.crearEquipo("Local", 8, baloncesto, Categoria.MAYORES);
        Equipo visitante = club.crearEquipo("Visitante", 8, baloncesto, Categoria.MAYORES);
        llenarEquipo(local, "l", 5);
        llenarEquipo(visitante, "v", 5);
        club.registrarPartidoInterno(local, visitante, 80, 75, null);

        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 5, Categoria.MAYORES, baloncesto);
        club.inscribirEquipoEnCompetencia(local, torneo);
        club.registrarPartidoExterno(torneo, local, fecha, 60, 55, "Rival", "Coliseo", null);

        club.planearSesion(entrenadorDelClub, fecha, LocalTime.of(8, 0), 60, canchaFutbol, listaDe(socioA, socioB));

        Articulo pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        club.reabastecerTienda(pelotas, 2);
        club.registrarVenta(socioA, crearCompra(pelotas, 2), Rubro.TIENDA, null, null, 0, 0);

        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        club.registrarSugerencia(empleado, "Gorra", "Accesorios", 35000);

        JSONObject original = jsonDelClub();
        JSONObject cadena = new JSONObject(original.toString());

        ClubSeneca restaurado = ClubSeneca.fromJSON(cadena);

        assertEquals(original.getJSONArray("usuarios").length(), restaurado.toJSON().getJSONArray("usuarios").length(), "Los usuarios restaurados no son los esperados.");
        assertEquals(2, restaurado.toJSON().getJSONArray("equipos").length(), "Los equipos restaurados no son los esperados.");
        assertEquals(1, restaurado.toJSON().getJSONArray("competencias").length(), "Las competencias restauradas no son las esperadas.");
        assertEquals(1, restaurado.toJSON().getJSONArray("ventas").length(), "Las ventas restauradas no son las esperadas.");
        assertEquals(1, restaurado.toJSON().getJSONArray("sugerencias").length(), "Las sugerencias restauradas no son las esperadas.");
        assertEquals(2, restaurado.getHistorial().getPartidos().size(), "Los partidos restaurados no son los esperados.");
        assertEquals(1, restaurado.getHistorial().getSesiones().size(), "Las sesiones restauradas no son las esperadas.");
        assertEquals("jruiz", restaurado.getHistorial().getSesiones().get(0).getEntrenador().getLogin(), "El entrenador de la sesión restaurada no es el esperado.");
    }

    @Test
    void testIdaYVueltaConDisciplinasIndividuales() throws Exception
    {
        JSONObject base = new JSONObject();
        base.put("nombre", "Club Seneca");
        base.put("disciplinas", new JSONArray().put(tenis.toJSON()));
        Files.writeString(carpetaTemporal.toPath().resolve("club.json"), base.toString());
        club = persistencia.cargar();

        Tenis tenisDelClub = obtenerDisciplinas().get(0);
        CanchaTenis cancha = (CanchaTenis) tenisDelClub.getInstalaciones().get(0);
        Socio socioA = club.crearSocio(20, "Ana Torres", "ana", "1234");
        Socio socioB = club.crearSocio(25, "Pedro Gil", "pgil", "5678");

        club.inscribirEnDisciplina(socioA, tenisDelClub, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(socioB, tenisDelClub, NivelJugador.INTERMEDIO);
        club.reservar(socioA, tenisDelClub, cancha, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Modalidad.SENCILLOS);
        PartidoPractica partido = club.programarPartidoPractica(tenisDelClub, cancha, fecha, listaDe(socioA, socioB));
        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, socioA, 12, 3, 5));
        estadisticas.add(new EstadisticaRaqueta(1, socioB, 9, 1, 7));
        club.finalizarPartidoPractica(partido, estadisticas);

        ClubSeneca restaurado = persistencia.cargar();

        Usuario usuario = restaurado.iniciarSesion("ana", "1234");
        assertNotNull(usuario, "Debe restaurarse el socio.");
        assertEquals(NivelJugador.INTERMEDIO, ((Socio) usuario).getInscripcionesIndividuales().get(0).getNivel(), "El nivel restaurado no es el esperado.");
        assertEquals(1, restaurado.getHistorial().getReservas().size(), "Debe restaurarse la reserva.");
        assertEquals(1, restaurado.getHistorial().getPartidosPractica().size(), "Debe restaurarse el partido de práctica.");
        assertTrue(restaurado.getHistorial().getPartidosPractica().get(0).isFinalizado(), "El partido restaurado debe estar finalizado.");
        assertEquals(2, restaurado.generarReporteDeportivo().size(), "Deben restaurarse los dos rankings.");
        assertEquals(1016, restaurado.generarReporteDeportivo().get(0).getPuntajeElo(), 0.001, "El puntaje restaurado de la ganadora no es el esperado.");
    }

    @SuppressWarnings("unchecked")
    private List<Tenis> obtenerDisciplinas() throws Exception
    {
        Field campo = ClubSeneca.class.getDeclaredField("disciplinas");
        campo.setAccessible(true);
        return (List<Tenis>) campo.get(club);
    }
}
