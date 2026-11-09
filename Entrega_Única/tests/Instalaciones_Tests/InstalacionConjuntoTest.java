package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Instalaciones.CanchaBaloncesto;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaVoleibol;
import Instalaciones.Instalacion;
import Instalaciones.InstalacionConjunto;

public class InstalacionConjuntoTest
{
    private CanchaFutbol canchaFutbol;
    private CanchaBaloncesto canchaBaloncesto;
    private CanchaVoleibol canchaVoleibol;

    @BeforeEach
    void setUp() throws Exception
    {
        canchaFutbol = new CanchaFutbol("Cancha 1");
        canchaBaloncesto = new CanchaBaloncesto("Cancha 2");
        canchaVoleibol = new CanchaVoleibol("Cancha 3");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testToJSONConservaDatosBase()
    {
        JSONObject json = canchaFutbol.toJSON();

        assertEquals("Cancha 1", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(22, json.getInt("capacidadMaxima"), "La capacidad en el JSON no es la esperada.");
    }

    @Test
    void testFromJSONFutbol()
    {
        Instalacion creada = InstalacionConjunto.fromJSON(canchaFutbol.toJSON());

        assertTrue(creada instanceof CanchaFutbol, "El tipo CanchaFutbol debe crear una cancha de fútbol.");
        assertEquals("Cancha 1", creada.getNombre(), "El nombre de la cancha creada no es el esperado.");
    }

    @Test
    void testFromJSONBaloncesto()
    {
        Instalacion creada = InstalacionConjunto.fromJSON(canchaBaloncesto.toJSON());

        assertTrue(creada instanceof CanchaBaloncesto, "El tipo CanchaBaloncesto debe crear una cancha de baloncesto.");
        assertEquals(10, creada.getCapacidadMaxima(), "La capacidad de la cancha creada no es la esperada.");
    }

    @Test
    void testFromJSONVoleibol()
    {
        Instalacion creada = InstalacionConjunto.fromJSON(canchaVoleibol.toJSON());

        assertTrue(creada instanceof CanchaVoleibol, "El tipo CanchaVoleibol debe crear una cancha de voleibol.");
        assertEquals(12, creada.getCapacidadMaxima(), "La capacidad de la cancha creada no es la esperada.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = canchaFutbol.toJSON();
        json.put("tipo", "CanchaTenis");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> InstalacionConjunto.fromJSON(json), "Debe lanzarse IllegalArgumentException si el tipo no es de conjunto.");

        assertEquals("Tipo de usuario desconocido: CanchaTenis", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinTipo()
    {
        JSONObject json = canchaFutbol.toJSON();
        json.remove("tipo");

        assertThrows(org.json.JSONException.class, () -> InstalacionConjunto.fromJSON(json), "Debe lanzarse JSONException si no hay tipo.");
    }
}
