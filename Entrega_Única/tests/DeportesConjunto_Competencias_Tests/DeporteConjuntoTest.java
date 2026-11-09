package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.Voleibol;

public class DeporteConjuntoTest
{
    private DeporteConjunto futbol;
    private Map<String, DeporteConjunto> mapaDeportes;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
        mapaDeportes = new HashMap<String, DeporteConjunto>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testToString()
    {
        assertEquals("Futbol", futbol.toString(), "El texto del deporte debe ser su nombre.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = futbol.toJSON();

        assertEquals("Futbol", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(7, json.getInt("minimoJugadores"), "El mínimo de jugadores en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONCreaFutbol()
    {
        DeporteConjunto creado = DeporteConjunto.fromJSON(futbol.toJSON(), mapaDeportes);

        assertTrue(creado instanceof Futbol, "El nombre Futbol debe crear un Futbol.");
        assertSame(creado, mapaDeportes.get("Futbol"), "El deporte creado debe quedar en el mapa.");
    }

    @Test
    void testFromJSONCreaVoleibol()
    {
        DeporteConjunto creado = DeporteConjunto.fromJSON(new Voleibol().toJSON(), mapaDeportes);

        assertTrue(creado instanceof Voleibol, "El nombre Voleibol debe crear un Voleibol.");
        assertEquals(1, mapaDeportes.size(), "El mapa debe tener un deporte.");
    }

    @Test
    void testFromJSONCreaBaloncesto()
    {
        DeporteConjunto creado = DeporteConjunto.fromJSON(new Baloncesto().toJSON(), mapaDeportes);

        assertTrue(creado instanceof Baloncesto, "El nombre Baloncesto debe crear un Baloncesto.");
    }

    @Test
    void testFromJSONReutilizaExistente()
    {
        mapaDeportes.put("Futbol", futbol);

        DeporteConjunto obtenido = DeporteConjunto.fromJSON(futbol.toJSON(), mapaDeportes);

        assertSame(futbol, obtenido, "Si el deporte ya está en el mapa debe reutilizarse.");
        assertEquals(1, mapaDeportes.size(), "El mapa no debe crecer.");
    }

    @Test
    void testFromJSONDeporteDesconocido()
    {
        JSONObject json = futbol.toJSON();
        json.put("nombre", "Golf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> DeporteConjunto.fromJSON(json, mapaDeportes), "Debe lanzarse IllegalArgumentException si el deporte es desconocido.");

        assertEquals("Deporte de conjunto desconocido: Golf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
