package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.ControlSanciones;
import DeportesConjunto_Competencias.EstadoJugador;

public class ControlSancionesTest
{
    private ControlSanciones control;

    @BeforeEach
    void setUp() throws Exception
    {
        control = new ControlSanciones();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testControlNuevo()
    {
        assertTrue(control.estaHabilitado(), "Un control nuevo debe estar habilitado.");
        assertEquals(EstadoJugador.HABILITADO, control.getEstado(), "El estado inicial no es el esperado.");
        assertEquals(0, control.getAmarillasAcumuladas(), "Un control nuevo no debe tener amarillas.");
    }

    @Test
    void testRegistrarUnaAmarilla()
    {
        control.registrarTarjetas(1, 0);

        assertEquals(1, control.getAmarillasAcumuladas(), "Debe haber una amarilla acumulada.");
        assertTrue(control.estaHabilitado(), "Una amarilla no suspende al jugador.");
    }

    @Test
    void testRegistrarDosAmarillasSeparadas()
    {
        control.registrarTarjetas(1, 0);
        control.registrarTarjetas(1, 0);

        assertFalse(control.estaHabilitado(), "Dos amarillas acumuladas suspenden al jugador.");
        assertEquals(EstadoJugador.SUSPENDIDO, control.getEstado(), "El estado debe ser suspendido.");
        assertEquals(0, control.getAmarillasAcumuladas(), "Las amarillas se reinician al suspender.");
    }

    @Test
    void testRegistrarDosAmarillasJuntas()
    {
        control.registrarTarjetas(2, 0);

        assertFalse(control.estaHabilitado(), "Dos amarillas en un partido suspenden al jugador.");
        assertEquals(0, control.getAmarillasAcumuladas(), "Las amarillas se reinician al suspender.");
    }

    @Test
    void testRegistrarRoja()
    {
        control.registrarTarjetas(0, 1);

        assertFalse(control.estaHabilitado(), "Una roja suspende al jugador.");
        assertEquals(0, control.getAmarillasAcumuladas(), "Las amarillas se reinician al suspender.");
    }

    @Test
    void testRegistrarAmarillaYRoja()
    {
        control.registrarTarjetas(1, 1);

        assertFalse(control.estaHabilitado(), "Una roja suspende al jugador aunque tenga una amarilla.");
        assertEquals(0, control.getAmarillasAcumuladas(), "Las amarillas se reinician al suspender.");
    }

    @Test
    void testRegistrarSinTarjetas()
    {
        control.registrarTarjetas(0, 0);

        assertTrue(control.estaHabilitado(), "Sin tarjetas el jugador sigue habilitado.");
        assertEquals(0, control.getAmarillasAcumuladas(), "No debe haber amarillas acumuladas.");
    }

    @Test
    void testRegistrarTarjetasNegativas()
    {
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> control.registrarTarjetas(-1, 0), "Debe lanzarse IllegalArgumentException con amarillas negativas.");

        assertEquals("No puede haber tarjetas negativas", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertThrows(IllegalArgumentException.class, () -> control.registrarTarjetas(0, -1), "Debe lanzarse IllegalArgumentException con rojas negativas.");
        assertTrue(control.estaHabilitado(), "El jugador debe seguir habilitado.");
    }

    @Test
    void testsuspenderYHabilitar()
    {
        control.suspender();
        assertFalse(control.estaHabilitado(), "El jugador debe quedar suspendido.");

        control.habilitar();
        assertTrue(control.estaHabilitado(), "El jugador debe quedar habilitado.");
    }

    @Test
    void testToJSON()
    {
        control.registrarTarjetas(1, 0);

        JSONObject json = control.toJSON();

        assertEquals(1, json.getInt("amarillasAcumuladas"), "Las amarillas en el JSON no son las esperadas.");
        assertEquals("HABILITADO", json.getString("estado"), "El estado en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        control.registrarTarjetas(0, 1);
        JSONObject json = control.toJSON();
        json.put("amarillasAcumuladas", 1);

        ControlSanciones restaurado = ControlSanciones.fromJSON(json);

        assertEquals(1, restaurado.getAmarillasAcumuladas(), "Las amarillas restauradas no son las esperadas.");
        assertEquals(EstadoJugador.SUSPENDIDO, restaurado.getEstado(), "El estado restaurado no es el esperado.");
    }

    @Test
    void testFromJSONEstadoInvalido()
    {
        JSONObject json = control.toJSON();
        json.put("estado", "LESIONADO");

        assertThrows(IllegalArgumentException.class, () -> ControlSanciones.fromJSON(json), "Un estado desconocido debe lanzar IllegalArgumentException.");
    }
}
