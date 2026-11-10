package Usuarios_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.TorneoCerros;
import Excepciones.ReglaNegocioException;
import Instalaciones.Instalacion;
import NucleoSistema.ClubSeneca;
import NucleoSistema.Persistencia;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.EstadoSugerencia;
import Tienda_Cafeteria_Inventario.Inventario;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import Tienda_Cafeteria_Inventario.Ubicacion;
import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class AdministradorTest
{
    @TempDir
    File carpetaTemporal;

    private ClubSeneca club;
    private Administrador administrador;
    private Administrador administradorSinClub;

    private Futbol futbol;
    private Socio ana;
    private Socio pedro;

    @BeforeEach
    void setUp() throws Exception
    {
        club = new ClubSeneca("Club Seneca");
        club.setPersistencia(new Persistencia(carpetaTemporal.getAbsolutePath()));

        administrador = new Administrador("Carlos Admin", "cadmin", "admin1");
        administrador.setClub(club);
        administradorSinClub = new Administrador("Sin Club", "sinclub", "x");

        futbol = new Futbol();
        ana = club.crearSocio(20, "Ana Torres", "ana", "1234");
        pedro = club.crearSocio(25, "Pedro Gil", "pgil", "5678");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsUsuario()
    {
        assertTrue(administrador instanceof Usuario, "Un administrador es un usuario.");
        assertEquals("Carlos Admin", administrador.getNombre(), "El nombre del administrador no es el esperado.");
        assertEquals("cadmin", administrador.getLogin(), "El login del administrador no es el esperado.");
        assertTrue(administrador.verificarPassword("admin1"), "La contraseña del administrador debe verificarse.");
    }

    @Test
    void testCrearEquipo() throws Exception
    {
        Equipo equipo = administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        assertNotNull(equipo, "El equipo debe crearse.");
        assertEquals("Los Cerros FC", equipo.getNombre(), "El nombre del equipo no es el esperado.");
        assertEquals(1, club.toJSON().getJSONArray("equipos").length(), "El club debe tener un equipo.");
    }

    @Test
    void testCrearEquipoRepetido() throws Exception
    {
        administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        assertNull(administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES), "No se puede repetir el nombre del equipo.");
    }

    @Test
    void testCrearEquipoConCupoInsuficiente() throws Exception
    {
        assertNull(administrador.crearEquipo("Pocos FC", 3, futbol, Categoria.MAYORES), "El cupo no puede ser menor al mínimo del deporte.");
    }

    @Test
    void testDarDeBajaEquipo() throws Exception
    {
        Equipo equipo = administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        assertTrue(administrador.darDeBajaEquipo(equipo), "El equipo debe poder darse de baja.");

        assertEquals(0, club.toJSON().getJSONArray("equipos").length(), "El club no debe tener equipos.");
    }

    @Test
    void testDarDeBajaEquipoInexistente()
    {
        assertFalse(administrador.darDeBajaEquipo(new Equipo("Ajeno", 10, futbol, Categoria.MAYORES)), "Un equipo que no está en el club no puede darse de baja.");
        assertFalse(administrador.darDeBajaEquipo(null), "Un equipo nulo no puede darse de baja.");
    }

    @Test
    void testDarDeBajaSocio() throws Exception
    {
        Equipo equipo = administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        club.inscribirJugador(ana, equipo, "Delantero", 9);

        assertTrue(administrador.darDeBajaSocio(ana), "El socio debe poder darse de baja.");

        assertNull(club.iniciarSesion("ana", "1234"), "El socio dado de baja no debe poder iniciar sesión.");
    }
    
    @Test
    void testDarDeBajaSocioSinEquipos()
    {
        assertTrue(administrador.darDeBajaSocio(pedro), "Un socio sin equipos también debe poder darse de baja.");

        assertNull(club.iniciarSesion("pgil", "5678"), "El socio dado de baja no debe poder iniciar sesión.");
    }

    @Test
    void testDarDeBajaSocioInexistente()
    {
        assertFalse(administrador.darDeBajaSocio(null), "Un socio nulo no puede darse de baja.");
        assertFalse(administrador.darDeBajaSocio(new Socio(30, "Ajeno", "ajeno", "x")), "Un socio que no está en el club no puede darse de baja.");
    }

    @Test
    void testInscribirEquipoEnCompetencia() throws Exception
    {
        Equipo equipo = administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);

        assertTrue(administrador.inscribirEquipoEnCompetencia(equipo, torneo), "El equipo debe poder inscribirse en la competencia.");

        assertEquals(1, torneo.getInscripciones().size(), "La competencia debe tener una inscripción.");
    }

    @Test
    void testInscribirEquipoEnCompetenciaQueNoCumple() throws Exception
    {
        Equipo equipo = administrador.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);
        TorneoCerros torneo = new TorneoCerros("Liga 2026", 2026, 5, Categoria.MAYORES, futbol);

        assertFalse(administrador.inscribirEquipoEnCompetencia(equipo, torneo), "Un equipo sin los jugadores mínimos no puede inscribirse.");
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

        assertTrue(administrador.moverArticulo(conos, bodega, cancha), "El artículo debe poder moverse.");

        assertTrue(inventarioCancha.tieneArticulo(conos), "El artículo debe estar en el destino.");
        assertFalse(inventarioBodega.tieneArticulo(conos), "El artículo no debe seguir en el origen.");
    }

    @Test
    void testMoverArticuloSinInventarios()
    {
        assertFalse(administrador.moverArticulo(new Articulo("Conos", "Entrenamiento", 2000, 40), new Ubicacion("A"), new Ubicacion("B")), "Sin inventarios no se puede mover el artículo.");
    }

    @Test
    void testReabastecerTienda() throws Exception
    {
        administrador.reabastecerTienda(new Articulo("Pelotas", "Pelotas", 10000, 5), 4);

        assertEquals(4, club.toJSON().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda debe reflejar el reabastecimiento.");
    }

    @Test
    void testReabastecerTiendaConCantidadInvalida() throws Exception
    {
        administrador.reabastecerTienda(new Articulo("Pelotas", "Pelotas", 10000, 5), 0);

        assertEquals(0, club.toJSON().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda no debe cambiar.");
    }

    @Test
    void testResolverSugerenciaAprobada()
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        SugerenciaArticulo sugerencia = club.registrarSugerencia(empleado, "Gorra", "Accesorios", 35000);

        administrador.resolverSugerencia(sugerencia, true);

        assertEquals(EstadoSugerencia.ACEPTADA, sugerencia.getEstado(), "La sugerencia debe quedar aceptada.");
        assertEquals(1, club.toJSON().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda debe tener el nuevo artículo.");
    }

    @Test
    void testResolverSugerenciaRechazada()
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        SugerenciaArticulo sugerencia = club.registrarSugerencia(empleado, "Gorra", "Accesorios", 35000);

        administrador.resolverSugerencia(sugerencia, false);

        assertEquals(EstadoSugerencia.RECHAZADA, sugerencia.getEstado(), "La sugerencia debe quedar rechazada.");
        assertEquals(0, club.toJSON().getJSONObject("tienda").getJSONArray("articulos").length(), "La tienda no debe tener artículos nuevos.");
    }

    @Test
    void testResolverSolicitudTurnoNoRegistrada() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        empleado.agregarTurno(actual);
        SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(actual, nuevo);

        assertThrows(ReglaNegocioException.class, () -> administrador.resolverSolicitudTurno(solicitud, true), "Una solicitud que no está en el club no puede resolverse.");

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado(), "Una solicitud que no está en el club no debe cambiar.");
    }

    @Test
    void testResolverSolicitudTurnoRegistrada() throws Exception
    {
        Empleado empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        Turno actual = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        Turno nuevo = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        empleado.agregarTurno(actual);
        club.registrarSolicitudTurno(empleado, actual, nuevo);

        assertEquals(1, club.toJSON().getJSONArray("solicitudes").length(), "El club debe tener la solicitud registrada.");
    }

    @Test
    void testSinClubNoSePuedeOperar()
    {
        assertThrows(NullPointerException.class, () -> administradorSinClub.crearEquipo("Los Cerros FC", 12, futbol, Categoria.MAYORES), "Sin club asignado no se puede crear un equipo.");
        assertThrows(NullPointerException.class, () -> administradorSinClub.darDeBajaEquipo(null), "Sin club asignado no se puede dar de baja un equipo.");
        assertThrows(NullPointerException.class, () -> administradorSinClub.darDeBajaSocio(ana), "Sin club asignado no se puede dar de baja un socio.");
        assertThrows(NullPointerException.class, () -> administradorSinClub.resolverSugerencia(null, true), "Sin club asignado no se puede resolver una sugerencia.");
    }

    @Test
    void testSetClubCambiaDeClub() throws Exception
    {
        ClubSeneca otroClub = new ClubSeneca("Otro Club");
        otroClub.setPersistencia(new Persistencia(new File(carpetaTemporal, "otro").getAbsolutePath()));

        administrador.setClub(otroClub);
        administrador.crearEquipo("Equipo del otro club", 12, futbol, Categoria.MAYORES);

        assertEquals(1, otroClub.toJSON().getJSONArray("equipos").length(), "El equipo debe crearse en el nuevo club.");
        assertEquals(0, club.toJSON().getJSONArray("equipos").length(), "El club original no debe cambiar.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = administrador.toJSON();

        assertEquals("Administrador", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Carlos Admin", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals("cadmin", json.getString("login"), "El login en el JSON no es el esperado.");
        assertEquals("admin1", json.getString("password"), "La contraseña en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        Administrador restaurado = Administrador.fromJSON(administrador.toJSON());

        assertEquals("Carlos Admin", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals("cadmin", restaurado.getLogin(), "El login restaurado no es el esperado.");
        assertTrue(restaurado.verificarPassword("admin1"), "La contraseña restaurada no es la esperada.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject json = new JSONObject();
        json.put("nombre", "Solo nombre");

        assertThrows(org.json.JSONException.class, () -> Administrador.fromJSON(json), "Debe lanzarse JSONException si faltan campos.");
    }
}