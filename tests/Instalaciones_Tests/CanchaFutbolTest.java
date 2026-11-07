package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Instalaciones.CanchaFutbol;
import Instalaciones.InstalacionConjunto;

public class CanchaFutbolTest
{
    private CanchaFutbol cancha;

    @BeforeEach
    void setUp() throws Exception
    {
        cancha = new CanchaFutbol("Cancha Principal");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsInstalacionConjunto()
    {
        assertTrue(cancha instanceof InstalacionConjunto, "Una cancha de fútbol es una instalación de conjunto.");
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Cancha Principal", cancha.getNombre(), "El nombre de la cancha no es el esperado.");
    }

    @Test
    void testGetCapacidadMaxima()
    {
        assertEquals(22, cancha.getCapacidadMaxima(), "La capacidad de una cancha de fútbol debe ser 22.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = cancha.toJSON();

        assertEquals("CanchaFutbol", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Cancha Principal", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(22, json.getInt("capacidadMaxima"), "La capacidad en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        CanchaFutbol restaurada = CanchaFutbol.fromJSON(cancha.toJSON());

        assertEquals("Cancha Principal", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(22, restaurada.getCapacidadMaxima(), "La capacidad restaurada no es la esperada.");
    }
}
