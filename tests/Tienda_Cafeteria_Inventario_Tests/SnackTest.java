package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Alergeno;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Snack;

public class SnackTest
{
    private Snack galletas;
    private Snack frutas;

    @BeforeEach
    void setUp() throws Exception
    {
        List<Alergeno> alergenosGalletas = new ArrayList<Alergeno>();
        alergenosGalletas.add(Alergeno.GLUTEN);
        alergenosGalletas.add(Alergeno.LACTEOS);

        galletas = new Snack("Galletas", "Snacks", 3000, 15, alergenosGalletas);
        frutas = new Snack("Frutas", "Saludable", 5000, 8, new ArrayList<Alergeno>());
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsProducto()
    {
        assertTrue(galletas instanceof Producto, "Un snack es un producto.");
    }

    @Test
    void testGetters()
    {
        assertEquals("Galletas", galletas.getNombre(), "El nombre del snack no es el esperado.");
        assertEquals("Snacks", galletas.getCategoria(), "La categoría del snack no es la esperada.");
        assertEquals(3000, galletas.getPrecio(), 0.001, "El precio del snack no es el esperado.");
        assertEquals(15, galletas.getCantidad(), "La cantidad del snack no es la esperada.");
    }

    @Test
    void testGetAlergenos()
    {
        assertEquals(2, galletas.getAlergenos().size(), "Las galletas deben tener dos alérgenos.");
        assertTrue(galletas.getAlergenos().contains(Alergeno.GLUTEN), "Las galletas deben contener gluten.");
        assertTrue(galletas.getAlergenos().contains(Alergeno.LACTEOS), "Las galletas deben contener lácteos.");
        assertFalse(galletas.getAlergenos().contains(Alergeno.MANI), "Las galletas no deben contener maní.");
        assertEquals(0, frutas.getAlergenos().size(), "Las frutas no deben tener alérgenos.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = galletas.toJSON();

        assertEquals("Snack", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals(2, json.getJSONArray("alergenos").length(), "El JSON debe tener dos alérgenos.");
        assertEquals("GLUTEN", json.getJSONArray("alergenos").getString(0), "El primer alérgeno en el JSON no es el esperado.");
        assertEquals("LACTEOS", json.getJSONArray("alergenos").getString(1), "El segundo alérgeno en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        Snack restaurado = Snack.fromJSON(galletas.toJSON());

        assertEquals("Galletas", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(3000, restaurado.getPrecio(), 0.001, "El precio restaurado no es el esperado.");
        assertEquals(15, restaurado.getCantidad(), "La cantidad restaurada no es la esperada.");
        assertEquals(2, restaurado.getAlergenos().size(), "Deben restaurarse los dos alérgenos.");
        assertEquals(Alergeno.GLUTEN, restaurado.getAlergenos().get(0), "El primer alérgeno restaurado no es el esperado.");
    }

    @Test
    void testFromJSONSinAlergenos()
    {
        JSONObject json = frutas.toJSON();
        json.remove("alergenos");

        Snack restaurado = Snack.fromJSON(json);

        assertEquals(0, restaurado.getAlergenos().size(), "Sin alérgenos en el JSON la lista debe quedar vacía.");
    }

    @Test
    void testFromJSONAlergenoInvalido()
    {
        JSONObject json = galletas.toJSON();
        json.put("alergenos", new JSONArray().put("SOYA"));

        assertThrows(IllegalArgumentException.class, () -> Snack.fromJSON(json), "Un alérgeno desconocido debe lanzar IllegalArgumentException.");
    }
}
