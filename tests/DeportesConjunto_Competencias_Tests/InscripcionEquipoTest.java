package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import Usuarios.Administrador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class InscripcionEquipoTest
{
    private Socio ana;
    private Equipo equipo;
    private InscripcionEquipo inscripcion;

    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        equipo = new Equipo("Los Cerros FC", 10, new Futbol(), Categoria.MAYORES);
        inscripcion = new InscripcionEquipo(ana, equipo, "Delantero", 9);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", ana);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertSame(ana, inscripcion.getJugador(), "El jugador no es el esperado.");
        assertSame(equipo, inscripcion.getEquipo(), "El equipo no es el esperado.");
        assertEquals("Delantero", inscripcion.getPosicion(), "La posición no es la esperada.");
        assertEquals(9, inscripcion.getNumeroCamiseta(), "El número de camiseta no es el esperado.");
        assertNotNull(inscripcion.getControlSanciones(), "La inscripción debe tener un control de sanciones.");
    }

    @Test
    void testsetPosicion()
    {
        inscripcion.setPosicion("Mediocampista");

        assertEquals("Mediocampista", inscripcion.getPosicion(), "La posición modificada no es la esperada.");
    }

    @Test
    void testEstaHabilitadoInicialmente()
    {
        assertTrue(inscripcion.estaHabilitado(), "Una inscripción nueva debe estar habilitada.");
    }

    @Test
    void testRegistrarTarjetas()
    {
        inscripcion.registrarTarjetas(1, 0);
        assertTrue(inscripcion.estaHabilitado(), "Con una amarilla sigue habilitado.");
        assertEquals(1, inscripcion.getControlSanciones().getAmarillasAcumuladas(), "Debe haber una amarilla acumulada.");

        inscripcion.registrarTarjetas(1, 0);
        assertFalse(inscripcion.estaHabilitado(), "Con dos amarillas queda suspendido.");
    }

    @Test
    void testRegistrarRoja()
    {
        inscripcion.registrarTarjetas(0, 1);

        assertFalse(inscripcion.estaHabilitado(), "Con una roja queda suspendido.");
    }

    @Test
    void testsuspenderYCumplirSuspension()
    {
        inscripcion.suspender();
        assertFalse(inscripcion.estaHabilitado(), "La inscripción debe quedar suspendida.");

        inscripcion.cumplirSuspension();
        assertTrue(inscripcion.estaHabilitado(), "Al cumplir la suspensión debe quedar habilitada.");
    }

    @Test
    void testToJSON()
    {
        inscripcion.registrarTarjetas(1, 0);

        JSONObject json = inscripcion.toJSON();

        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals("Delantero", json.getString("posicion"), "La posición en el JSON no es la esperada.");
        assertEquals(9, json.getInt("numeroCamiseta"), "La camiseta en el JSON no es la esperada.");
        assertEquals(1, json.getJSONObject("controlSanciones").getInt("amarillasAcumuladas"), "Las amarillas en el JSON no son las esperadas.");
    }

    @Test
    void testFromJSON()
    {
        inscripcion.registrarTarjetas(0, 1);

        InscripcionEquipo restaurada = InscripcionEquipo.fromJSON(inscripcion.toJSON(), equipo, mapaUsuarios);

        assertSame(ana, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertSame(equipo, restaurada.getEquipo(), "El equipo restaurado no es el esperado.");
        assertEquals("Delantero", restaurada.getPosicion(), "La posición restaurada no es la esperada.");
        assertEquals(9, restaurada.getNumeroCamiseta(), "La camiseta restaurada no es la esperada.");
        assertFalse(restaurada.estaHabilitado(), "El estado de sanción restaurado no es el esperado.");
    }

    @Test
    void testFromJSONJugadorNoEsSocio()
    {
        JSONObject json = inscripcion.toJSON();
        json.put("jugador", "admin");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> InscripcionEquipo.fromJSON(json, equipo, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el jugador no es socio.");

        assertEquals("El jugador de la inscripción no es un socio válido: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
