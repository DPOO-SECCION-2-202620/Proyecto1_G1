package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import Excepciones.ReglaNegocioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import Usuarios.Socio;
import Usuarios.Usuario;

public class EquipoTest
{
    private Futbol futbol;
    private Baloncesto baloncesto;

    private Equipo equipoMayores;
    private Equipo equipoSub15;
    private Equipo equipoBaloncesto;

    private Socio ana;
    private Socio pedro;
    private Socio luis;
    private Socio menor;

    private Map<String, DeporteConjunto> mapaDeportes;
    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
        baloncesto = new Baloncesto();

        equipoMayores = new Equipo("Los Cerros FC", 2, futbol, Categoria.MAYORES);
        equipoSub15 = new Equipo("Cerritos", 10, futbol, Categoria.SUB15);
        equipoBaloncesto = new Equipo("Cerros Basket", 8, baloncesto, Categoria.MAYORES);

        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");
        luis = new Socio(30, "Luis Mora", "lmora", "3");
        menor = new Socio(14, "Nino Perez", "nperez", "4");

        mapaDeportes = new HashMap<String, DeporteConjunto>();
        mapaDeportes.put("Futbol", futbol);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", ana);
        mapaUsuarios.put("pgil", pedro);
        mapaUsuarios.put("lmora", luis);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters() throws Exception
    {
        assertEquals("Los Cerros FC", equipoMayores.getNombre(), "El nombre del equipo no es el esperado.");
        assertEquals(2, equipoMayores.getCupoMaximo(), "El cupo del equipo no es el esperado.");
        assertSame(futbol, equipoMayores.getDeporte(), "El deporte del equipo no es el esperado.");
        assertEquals(Categoria.MAYORES, equipoMayores.getCategoria(), "La categoría del equipo no es la esperada.");
        assertEquals(0, equipoMayores.getInscripciones().size(), "Un equipo nuevo no debe tener inscripciones.");
    }

    @Test
    void testToString() throws Exception
    {
        assertEquals("Los Cerros FC (Futbol, MAYORES)", equipoMayores.toString(), "El texto del equipo no es el esperado.");
    }

    @Test
    void testAgregarJugador () throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);

        assertEquals(1, equipoMayores.getInscripciones().size(), "El equipo debe tener una inscripción.");
        assertEquals("Delantero", equipoMayores.getInscripciones().get(0).getPosicion(), "La posición de la inscripción no es la esperada.");
        assertEquals(9, equipoMayores.getInscripciones().get(0).getNumeroCamiseta(), "La camiseta de la inscripción no es la esperada.");
        assertEquals(1, ana.getInscripcionesEquipo().size(), "El socio debe tener la inscripción registrada.");
        assertTrue(ana.tieneEquipoEn(futbol), "El socio debe tener equipo en fútbol.");
    }

    @Test
    void testAgregarJugadorConCupoLleno() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);
        equipoMayores.agregarJugador(pedro, "Portero", 1);

        assertThrows(ReglaNegocioException.class, () -> equipoMayores.agregarJugador(luis, "Defensa", 4), "No se puede inscribir un jugador si el cupo está lleno.");
        assertEquals(2, equipoMayores.getInscripciones().size(), "El equipo debe seguir con dos inscripciones.");
        assertEquals(0, luis.getInscripcionesEquipo().size(), "El socio no debe tener inscripciones.");
    }

    @Test
    void testAgregarJugadorFueraDeCategoria() throws Exception
    {
        assertThrows(ReglaNegocioException. class, () -> equipoMayores.agregarJugador(menor, "Delantero", 9), "Un menor no puede inscribirse en mayores.");
        equipoSub15.agregarJugador(menor, "Delantero", 9);
        assertThrows(ReglaNegocioException. class, () -> equipoSub15.agregarJugador(ana, "Defensa", 4), "Un adulto no puede inscribirse en SUB15.");
    }

    @Test
    void testAgregarJugadorConEquipoEnElMismoDeporte() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);
        Equipo otroEquipo = new Equipo("Otro FC", 10, futbol, Categoria.MAYORES);

        assertThrows(ReglaNegocioException. class, () -> otroEquipo.agregarJugador(ana, "Defensa", 4), "Un socio no puede estar en dos equipos del mismo deporte.");
        assertEquals(0, otroEquipo.getInscripciones().size(), "El otro equipo no debe tener inscripciones.");
    }

    @Test
    void testAgregarJugadorConEquipoEnOtroDeporte() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);

        equipoBaloncesto.agregarJugador(ana, "Alero", 9);
        assertEquals(2, ana.getInscripcionesEquipo().size(), "El socio debe tener dos inscripciones.");
    }

    @Test
    void testAgregarJugadorConCamisetaOcupada() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);

        assertThrows(ReglaNegocioException. class, () -> equipoMayores.agregarJugador(pedro, "Portero", 9), "No se puede repetir el número de camiseta.");
        assertEquals(1, equipoMayores.getInscripciones().size(), "El equipo debe seguir con una inscripción.");
    }

    @Test
    void testCamisetaOcupada() throws Exception
    {
        assertFalse(equipoMayores.camisetaOcupada(9), "Con el equipo vacío ninguna camiseta está ocupada.");

        equipoMayores.agregarJugador(ana, "Delantero", 9);

        assertTrue(equipoMayores.camisetaOcupada(9), "La camiseta 9 debe estar ocupada.");
        assertFalse(equipoMayores.camisetaOcupada(10), "La camiseta 10 no debe estar ocupada.");
    }

    @Test
    void testGetInscripcionDe() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);

        assertNotNull(equipoMayores.getInscripcionDe(ana), "Debe encontrarse la inscripción del jugador.");
        assertSame(ana, equipoMayores.getInscripcionDe(ana).getJugador(), "La inscripción debe ser del jugador.");
        assertNull(equipoMayores.getInscripcionDe(pedro), "Un jugador no inscrito no tiene inscripción.");
    }

    @Test
    void testEstaHabilitado() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);

        assertTrue(equipoMayores.estaHabilitado(ana), "Un jugador inscrito y sin sanciones está habilitado.");

        equipoMayores.getInscripcionDe(ana).suspender();

        assertFalse(equipoMayores.estaHabilitado(ana), "Un jugador suspendido no está habilitado.");
    }

    @Test
    void testEstaHabilitadoJugadorNoInscrito() throws Exception
    {
        assertFalse(equipoMayores.estaHabilitado(pedro), "Un jugador no inscrito no está habilitado.");
    }

    @Test
    void testContarJugadoresHabilitados() throws Exception
    {
        assertEquals(0, equipoMayores.contarJugadoresHabilitados(), "Un equipo vacío no tiene jugadores habilitados.");

        equipoMayores.agregarJugador(ana, "Delantero", 9);
        equipoMayores.agregarJugador(pedro, "Portero", 1);
        assertEquals(2, equipoMayores.contarJugadoresHabilitados(), "Debe haber dos jugadores habilitados.");

        equipoMayores.getInscripcionDe(pedro).registrarTarjetas(0, 1);
        assertEquals(1, equipoMayores.contarJugadoresHabilitados(), "Debe haber un jugador habilitado.");
    }

    @Test
    void testPuedeJugar() throws Exception
    {
        assertFalse(equipoBaloncesto.puedeJugar(), "Un equipo vacío no puede jugar.");

        for(int i = 1; i <= 4; i++)
        {
            equipoBaloncesto.agregarJugador(new Socio(20 + i, "Jugador " + i, "j" + i, "x"), "Alero", i);
        }
        assertFalse(equipoBaloncesto.puedeJugar(), "Con cuatro jugadores no se puede jugar baloncesto.");

        equipoBaloncesto.agregarJugador(new Socio(30, "Jugador 5", "j5", "x"), "Alero", 5);
        assertTrue(equipoBaloncesto.puedeJugar(), "Con cinco jugadores habilitados se puede jugar baloncesto.");
    }

    @Test
    void testPuedeJugarConSuspendidos() throws Exception
    {
        for(int i = 1; i <= 5; i++)
        {
            equipoBaloncesto.agregarJugador(new Socio(20 + i, "Jugador " + i, "j" + i, "x"), "Alero", i);
        }

        equipoBaloncesto.getInscripciones().get(0).suspender();

        assertFalse(equipoBaloncesto.puedeJugar(), "Con un jugador suspendido ya no hay cinco habilitados.");
    }

    @Test
    void testGetInscripcionesSuspendidas() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);
        equipoMayores.agregarJugador(pedro, "Portero", 1);
        assertEquals(0, equipoMayores.getInscripcionesSuspendidas().size(), "Sin sanciones no hay suspendidos.");

        equipoMayores.getInscripcionDe(pedro).suspender();

        List<InscripcionEquipo> suspendidas = equipoMayores.getInscripcionesSuspendidas();
        assertEquals(1, suspendidas.size(), "Debe haber un suspendido.");
        assertSame(pedro, suspendidas.get(0).getJugador(), "El suspendido no es el esperado.");
    }

    @Test
    void testToJSON() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);

        JSONObject json = equipoMayores.toJSON();

        assertEquals("Los Cerros FC", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(2, json.getInt("cupoMaximo"), "El cupo en el JSON no es el esperado.");
        assertEquals("MAYORES", json.getString("categoria"), "La categoría en el JSON no es la esperada.");
        assertEquals("Futbol", json.getJSONObject("deporte").getString("nombre"), "El deporte en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("inscripciones").length(), "El JSON debe tener una inscripción.");
    }

    @Test
    void testFromJSONConDeporteComoObjeto() throws Exception
    {
        equipoMayores.agregarJugador(ana, "Delantero", 9);
        Socio anaNueva = new Socio(20, "Ana Torres", "ana", "1");
        mapaUsuarios.put("ana", anaNueva);

        Equipo restaurado = Equipo.fromJSON(equipoMayores.toJSON(), mapaDeportes, mapaUsuarios);

        assertEquals("Los Cerros FC", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(2, restaurado.getCupoMaximo(), "El cupo restaurado no es el esperado.");
        assertSame(futbol, restaurado.getDeporte(), "El deporte restaurado debe ser el del mapa.");
        assertEquals(Categoria.MAYORES, restaurado.getCategoria(), "La categoría restaurada no es la esperada.");
        assertEquals(1, restaurado.getInscripciones().size(), "Debe restaurarse una inscripción.");
        assertSame(anaNueva, restaurado.getInscripciones().get(0).getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(1, anaNueva.getInscripcionesEquipo().size(), "El socio restaurado debe tener su inscripción.");
    }

    @Test
    void testFromJSONConDeporteComoTexto() throws Exception
    {
        JSONObject json = equipoMayores.toJSON();
        json.put("deporte", "Futbol");

        Equipo restaurado = Equipo.fromJSON(json, mapaDeportes, mapaUsuarios);

        assertSame(futbol, restaurado.getDeporte(), "El deporte restaurado debe ser el del mapa.");
    }

    @Test
    void testFromJSONDeporteTextoInexistente() throws Exception
    {
        JSONObject json = equipoMayores.toJSON();
        json.put("deporte", "Golf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Equipo.fromJSON(json, mapaDeportes, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el deporte no existe.");

        assertEquals("Deporte no encontrado para el equipo: Golf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinInscripciones() throws Exception
    {
        JSONObject json = equipoMayores.toJSON();
        json.remove("inscripciones");

        Equipo restaurado = Equipo.fromJSON(json, mapaDeportes, mapaUsuarios);

        assertEquals(0, restaurado.getInscripciones().size(), "Sin inscripciones en el JSON el equipo debe quedar vacío.");
    }
}
