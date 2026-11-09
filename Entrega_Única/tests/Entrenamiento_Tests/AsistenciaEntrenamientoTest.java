package Entrenamiento_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Entrenamiento.AsistenciaEntrenamiento;
import Entrenamiento.EstadoAsistencia;
import Usuarios.Administrador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class AsistenciaEntrenamientoTest
{
    private Socio socio;
    private AsistenciaEntrenamiento asistenciaSinNota;
    private AsistenciaEntrenamiento asistenciaConNota;
    private AsistenciaEntrenamiento asistenciaFalto;

    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1234");

        asistenciaSinNota = new AsistenciaEntrenamiento(0, null, EstadoAsistencia.ASISTIO, socio);
        asistenciaConNota = new AsistenciaEntrenamiento(4, "Buen trabajo", EstadoAsistencia.LLEGOTARDE, socio);
        asistenciaFalto = new AsistenciaEntrenamiento(0, null, EstadoAsistencia.FALTO, socio);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", socio);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals(4, asistenciaConNota.getEvaluacion(), "La evaluación no es la esperada.");
        assertEquals("Buen trabajo", asistenciaConNota.getComentario(), "El comentario no es el esperado.");
        assertEquals(EstadoAsistencia.LLEGOTARDE, asistenciaConNota.getEstado(), "El estado no es el esperado.");
        assertSame(socio, asistenciaConNota.getJugador(), "El jugador no es el esperado.");
        assertNull(asistenciaSinNota.getComentario(), "Sin nota el comentario debe ser nulo.");
    }

    @Test
    void testRegistrarEvaluacion()
    {
        assertTrue(asistenciaSinNota.registrarEvaluacion(5, "Excelente"), "Una evaluación válida debe registrarse.");

        assertEquals(5, asistenciaSinNota.getEvaluacion(), "La evaluación registrada no es la esperada.");
        assertEquals("Excelente", asistenciaSinNota.getComentario(), "El comentario registrado no es el esperado.");
    }

    @Test
    void testRegistrarEvaluacionEnLosLimites()
    {
        assertTrue(asistenciaSinNota.registrarEvaluacion(1, "Regular"), "La evaluación 1 debe ser válida.");
        assertEquals(1, asistenciaSinNota.getEvaluacion(), "La evaluación registrada no es la esperada.");

        assertTrue(asistenciaSinNota.registrarEvaluacion(5, "Perfecto"), "La evaluación 5 debe ser válida.");
        assertEquals(5, asistenciaSinNota.getEvaluacion(), "La evaluación registrada no es la esperada.");
    }

    @Test
    void testRegistrarEvaluacionInvalida()
    {
        assertFalse(asistenciaSinNota.registrarEvaluacion(0, "Nada"), "La evaluación 0 no es válida.");
        assertFalse(asistenciaSinNota.registrarEvaluacion(6, "Demasiado"), "La evaluación 6 no es válida.");
        assertFalse(asistenciaSinNota.registrarEvaluacion(-1, "Negativo"), "Una evaluación negativa no es válida.");

        assertEquals(0, asistenciaSinNota.getEvaluacion(), "La evaluación no debe cambiar si es inválida.");
        assertNull(asistenciaSinNota.getComentario(), "El comentario no debe cambiar si la evaluación es inválida.");
    }

    @Test
    void testRegistrarEvaluacionAJugadorQueFalto()
    {
        assertFalse(asistenciaFalto.registrarEvaluacion(3, "No vino"), "No se puede evaluar a un jugador que faltó.");

        assertEquals(0, asistenciaFalto.getEvaluacion(), "La evaluación no debe cambiar si el jugador faltó.");
    }

    @Test
    void testToStringSinEvaluacion()
    {
        assertEquals("Ana Torres: ASISTIO", asistenciaSinNota.toString(), "El texto sin evaluación no es el esperado.");
    }

    @Test
    void testToStringConEvaluacion()
    {
        assertEquals("Ana Torres: LLEGOTARDE | nota 4 - Buen trabajo", asistenciaConNota.toString(), "El texto con evaluación no es el esperado.");
    }

    @Test
    void testToJSONSinComentario()
    {
        JSONObject json = asistenciaSinNota.toJSON();

        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals("ASISTIO", json.getString("estado"), "El estado en el JSON no es el esperado.");
        assertEquals(0, json.getInt("evaluacion"), "La evaluación en el JSON no es la esperada.");
        assertFalse(json.has("comentario"), "Sin comentario el JSON no debe incluirlo.");
    }

    @Test
    void testToJSONConComentario()
    {
        JSONObject json = asistenciaConNota.toJSON();

        assertEquals("Buen trabajo", json.getString("comentario"), "El comentario en el JSON no es el esperado.");
        assertEquals(4, json.getInt("evaluacion"), "La evaluación en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        AsistenciaEntrenamiento restaurada = AsistenciaEntrenamiento.fromJSON(asistenciaConNota.toJSON(), mapaUsuarios);

        assertSame(socio, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(EstadoAsistencia.LLEGOTARDE, restaurada.getEstado(), "El estado restaurado no es el esperado.");
        assertEquals(4, restaurada.getEvaluacion(), "La evaluación restaurada no es la esperada.");
        assertEquals("Buen trabajo", restaurada.getComentario(), "El comentario restaurado no es el esperado.");
    }

    @Test
    void testFromJSONSinComentario()
    {
        AsistenciaEntrenamiento restaurada = AsistenciaEntrenamiento.fromJSON(asistenciaSinNota.toJSON(), mapaUsuarios);

        assertNull(restaurada.getComentario(), "Sin comentario en el JSON el comentario restaurado debe ser nulo.");
    }

    @Test
    void testFromJSONJugadorNoEsSocio()
    {
        JSONObject json = asistenciaSinNota.toJSON();
        json.put("jugador", "admin");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> AsistenciaEntrenamiento.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el jugador no es socio.");

        assertEquals("Jugador de asistencia no válido: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONJugadorInexistente()
    {
        JSONObject json = asistenciaSinNota.toJSON();
        json.put("jugador", "nadie");

        assertThrows(IllegalArgumentException.class, () -> AsistenciaEntrenamiento.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el jugador no existe.");
    }
}
