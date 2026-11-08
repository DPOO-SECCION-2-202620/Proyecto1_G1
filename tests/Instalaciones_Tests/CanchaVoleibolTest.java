package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Instalaciones.CanchaVoleibol;
import Instalaciones.InstalacionConjunto;

public class CanchaVoleibolTest
{
    private CanchaVoleibol cancha;

    @BeforeEach
    void setUp() throws Exception
    {
        cancha = new CanchaVoleibol("Cancha de Arena");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsInstalacionConjunto()
    {
        assertTrue(cancha instanceof InstalacionConjunto, "Una cancha de voleibol es una instalación de conjunto.");
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Cancha de Arena", cancha.getNombre(), "El nombre de la cancha no es el esperado.");
    }

    @Test
    void testGetCapacidadMaxima()
    {
        assertEquals(12, cancha.getCapacidadMaxima(), "La capacidad de una cancha de voleibol debe ser 12.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = cancha.toJSON();

        assertEquals("CanchaVoleibol", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Cancha de Arena", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(12, json.getInt("capacidadMaxima"), "La capacidad en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        CanchaVoleibol restaurada = CanchaVoleibol.fromJSON(cancha.toJSON());

        assertEquals("Cancha de Arena", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(12, restaurada.getCapacidadMaxima(), "La capacidad restaurada no es la esperada.");
    }
}
