package PartidosEquipo_Estadisticas_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;
import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import Usuarios.Socio;

public class EstadisticaBaloncestoTest
{
    private Socio ana;
    private Socio pedro;
    private Equipo equipo;
    private InscripcionEquipo inscripcionAna;
    private EstadisticaBaloncesto estadistica;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");

        equipo = new Equipo("Cerros Basket", 10, new Baloncesto(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Alero", 7);
        inscripcionAna = equipo.getInscripcionDe(ana);

        estadistica = new EstadisticaBaloncesto(inscripcionAna, 18, 6);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsEstadisticaConjunto()
    {
        assertTrue(estadistica instanceof EstadisticaConjunto, "Una estadística de baloncesto es una estadística de conjunto.");
    }

    @Test
    void testGetters()
    {
        assertSame(inscripcionAna, estadistica.getJugador(), "El jugador no es el esperado.");
        assertEquals(18, estadistica.getPuntos(), "Los puntos no son los esperados.");
        assertEquals(6, estadistica.getRebotes(), "Los rebotes no son los esperados.");
    }

    @Test
    void testCalcularAporte()
    {
        assertEquals(42, estadistica.calcularAporte(), "El aporte debe ser dos por punto más uno por rebote.");
    }

    @Test
    void testCalcularAporteSinEstadisticas()
    {
        assertEquals(0, new EstadisticaBaloncesto(inscripcionAna, 0, 0).calcularAporte(), "Sin puntos ni rebotes el aporte es 0.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = estadistica.toJSON();

        assertEquals("EstadisticaBaloncesto", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(18, json.getInt("puntos"), "Los puntos en el JSON no son los esperados.");
        assertEquals(6, json.getInt("rebotes"), "Los rebotes en el JSON no son los esperados.");
    }

    @Test
    void testFromJSON()
    {
        EstadisticaBaloncesto restaurada = EstadisticaBaloncesto.fromJSON(estadistica.toJSON(), equipo.getInscripciones());

        assertSame(inscripcionAna, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(18, restaurada.getPuntos(), "Los puntos restaurados no son los esperados.");
        assertEquals(6, restaurada.getRebotes(), "Los rebotes restaurados no son los esperados.");
    }

    @Test
    void testFromJSONNulo()
    {
        assertNull(EstadisticaBaloncesto.fromJSON(null, equipo.getInscripciones()), "Con un JSON nulo no se debe crear una estadística.");
    }

    @Test
    void testFromJSONJugadorNoInscrito()
    {
        JSONObject json = estadistica.toJSON();
        json.put("jugador", pedro.getLogin());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> EstadisticaBaloncesto.fromJSON(json, equipo.getInscripciones()), "Debe lanzarse IllegalArgumentException si el jugador no está inscrito.");

        assertEquals("El jugador pgil no está inscrito en el equipo de la estadística", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
