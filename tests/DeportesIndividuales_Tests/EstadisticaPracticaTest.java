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
import DeportesIndividuales.EstadisticaTenisDeMesa;
import Usuarios.Socio;
import Usuarios.Usuario;

public class EstadisticaPracticaTest
{
    private Socio socio;
    private EstadisticaPractica estadisticaRaqueta;
    private EstadisticaPractica estadisticaTenisDeMesa;

    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1234");

        estadisticaRaqueta = new EstadisticaRaqueta(2, socio, 14, 5, 8);
        estadisticaTenisDeMesa = new EstadisticaTenisDeMesa(3, socio, 45);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", socio);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetSetsGanados()
    {
        assertEquals(2, estadisticaRaqueta.getSetsGanados(), "Los sets ganados no son los esperados.");
        assertEquals(3, estadisticaTenisDeMesa.getSetsGanados(), "Los sets ganados no son los esperados.");
    }

    @Test
    void testGetJugador()
    {
        assertSame(socio, estadisticaRaqueta.getJugador(), "El jugador no es el esperado.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = estadisticaRaqueta.toJSON();

        assertEquals(2, json.getInt("setsGanados"), "Los sets ganados en el JSON no son los esperados.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONRaqueta()
    {
        EstadisticaPractica restaurada = EstadisticaPractica.fromJSON(estadisticaRaqueta.toJSON(), mapaUsuarios);

        assertTrue(restaurada instanceof EstadisticaRaqueta, "El tipo EstadisticaRaqueta debe crear una estadística de raqueta.");
        assertEquals(14, ((EstadisticaRaqueta) restaurada).getGamesGanados(), "Los games restaurados no son los esperados.");
    }

    @Test
    void testFromJSONTenisDeMesa()
    {
        EstadisticaPractica restaurada = EstadisticaPractica.fromJSON(estadisticaTenisDeMesa.toJSON(), mapaUsuarios);

        assertTrue(restaurada instanceof EstadisticaTenisDeMesa, "El tipo EstadisticaTenisDeMesa debe crear una estadística de tenis de mesa.");
        assertEquals(45, ((EstadisticaTenisDeMesa) restaurada).getPuntosTotales(), "Los puntos restaurados no son los esperados.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = estadisticaRaqueta.toJSON();
        json.put("tipo", "EstadisticaGolf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> EstadisticaPractica.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de estadística de práctica desconocido: EstadisticaGolf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
