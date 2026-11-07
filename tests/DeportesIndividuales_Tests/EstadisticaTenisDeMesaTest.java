package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.EstadisticaPractica;
import DeportesIndividuales.EstadisticaTenisDeMesa;
import Usuarios.Socio;
import Usuarios.Usuario;

public class EstadisticaTenisDeMesaTest
{
    private Socio socio;
    private EstadisticaTenisDeMesa estadistica;
    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1234");
        estadistica = new EstadisticaTenisDeMesa(3, socio, 45);

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
        assertTrue(estadistica instanceof EstadisticaPractica, "Una estadística de tenis de mesa es una estadística de práctica.");
    }

    @Test
    void testGetters()
    {
        assertEquals(3, estadistica.getSetsGanados(), "Los sets ganados no son los esperados.");
        assertSame(socio, estadistica.getJugador(), "El jugador no es el esperado.");
        assertEquals(45, estadistica.getPuntosTotales(), "Los puntos totales no son los esperados.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = estadistica.toJSON();

        assertEquals("EstadisticaTenisDeMesa", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals(3, json.getInt("setsGanados"), "Los sets en el JSON no son los esperados.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(45, json.getInt("puntosTotales"), "Los puntos en el JSON no son los esperados.");
    }

    @Test
    void testFromJSON()
    {
        EstadisticaTenisDeMesa restaurada = EstadisticaTenisDeMesa.fromJSON(estadistica.toJSON(), mapaUsuarios);

        assertEquals(3, restaurada.getSetsGanados(), "Los sets restaurados no son los esperados.");
        assertSame(socio, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(45, restaurada.getPuntosTotales(), "Los puntos restaurados no son los esperados.");
    }

    @Test
    void testFromJSONNulo()
    {
        assertNull(EstadisticaTenisDeMesa.fromJSON(null, mapaUsuarios), "Con un JSON nulo no se debe crear una estadística.");
    }
}
