package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.EstadisticaPractica;
import DeportesIndividuales.EstadisticaRaqueta;
import Usuarios.Socio;
import Usuarios.Usuario;

public class EstadisticaRaquetaTest
{
    private Socio socio;
    private EstadisticaRaqueta estadistica;
    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1234");
        estadistica = new EstadisticaRaqueta(2, socio, 14, 5, 8);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", socio);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsEstadisticaPractica()
    {
        assertTrue(estadistica instanceof EstadisticaPractica, "Una estadística de raqueta es una estadística de práctica.");
    }

    @Test
    void testGetters()
    {
        assertEquals(2, estadistica.getSetsGanados(), "Los sets ganados no son los esperados.");
        assertSame(socio, estadistica.getJugador(), "El jugador no es el esperado.");
        assertEquals(14, estadistica.getGamesGanados(), "Los games ganados no son los esperados.");
        assertEquals(5, estadistica.getAces(), "Los aces no son los esperados.");
        assertEquals(8, estadistica.getErroresNoForzados(), "Los errores no forzados no son los esperados.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = estadistica.toJSON();

        assertEquals("EstadisticaRaqueta", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals(2, json.getInt("setsGanados"), "Los sets en el JSON no son los esperados.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(14, json.getInt("gamesGanados"), "Los games en el JSON no son los esperados.");
        assertEquals(5, json.getInt("aces"), "Los aces en el JSON no son los esperados.");
        assertEquals(8, json.getInt("erroresNoForzados"), "Los errores en el JSON no son los esperados.");
    }

    @Test
    void testFromJSON()
    {
        EstadisticaRaqueta restaurada = EstadisticaRaqueta.fromJSON(estadistica.toJSON(), mapaUsuarios);

        assertEquals(2, restaurada.getSetsGanados(), "Los sets restaurados no son los esperados.");
        assertSame(socio, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(14, restaurada.getGamesGanados(), "Los games restaurados no son los esperados.");
        assertEquals(5, restaurada.getAces(), "Los aces restaurados no son los esperados.");
        assertEquals(8, restaurada.getErroresNoForzados(), "Los errores restaurados no son los esperados.");
    }

    @Test
    void testFromJSONNulo()
    {
        assertNull(EstadisticaRaqueta.fromJSON(null, mapaUsuarios), "Con un JSON nulo no se debe crear una estadística.");
    }

    @Test
    void testFromJSONJugadorInexistente()
    {
        Map<String, Usuario> mapaVacio = new HashMap<String, Usuario>();

        EstadisticaRaqueta restaurada = EstadisticaRaqueta.fromJSON(estadistica.toJSON(), mapaVacio);

        assertNull(restaurada.getJugador(), "Con un jugador inexistente el jugador restaurado es nulo.");
    }
}
