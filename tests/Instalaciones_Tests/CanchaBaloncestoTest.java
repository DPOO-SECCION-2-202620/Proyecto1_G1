package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Instalaciones.CanchaBaloncesto;
import Instalaciones.InstalacionConjunto;

public class CanchaBaloncestoTest
{
    private CanchaBaloncesto cancha;

    @BeforeEach
    void setUp() throws Exception
    {
        cancha = new CanchaBaloncesto("Coliseo");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsInstalacionConjunto()
    {
        assertTrue(cancha instanceof InstalacionConjunto, "Una cancha de baloncesto es una instalación de conjunto.");
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Coliseo", cancha.getNombre(), "El nombre de la cancha no es el esperado.");
    }

    @Test
    void testGetCapacidadMaxima()
    {
        assertEquals(10, cancha.getCapacidadMaxima(), "La capacidad de una cancha de baloncesto debe ser 10.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = cancha.toJSON();

        assertEquals("CanchaBaloncesto", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Coliseo", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(10, json.getInt("capacidadMaxima"), "La capacidad en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        CanchaBaloncesto restaurada = CanchaBaloncesto.fromJSON(cancha.toJSON());

        assertEquals("Coliseo", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(10, restaurada.getCapacidadMaxima(), "La capacidad restaurada no es la esperada.");
    }
}
