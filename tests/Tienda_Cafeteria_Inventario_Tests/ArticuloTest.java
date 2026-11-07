package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Producto;

public class ArticuloTest
{
    private Articulo articulo;

    @BeforeEach
    void setUp() throws Exception
    {
        articulo = new Articulo("Pelotas de tenis", "Pelotas", 18000, 30);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsProducto()
    {
        assertTrue(articulo instanceof Producto, "Un artículo es un producto.");
    }

    @Test
    void testGetters()
    {
        assertEquals("Pelotas de tenis", articulo.getNombre(), "El nombre del artículo no es el esperado.");
        assertEquals("Pelotas", articulo.getCategoria(), "La categoría del artículo no es la esperada.");
        assertEquals(18000, articulo.getPrecio(), 0.001, "El precio del artículo no es el esperado.");
        assertEquals(30, articulo.getCantidad(), "La cantidad del artículo no es la esperada.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = articulo.toJSON();

        assertEquals("Articulo", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Pelotas de tenis", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        Articulo restaurado = Articulo.fromJSON(articulo.toJSON());

        assertEquals("Pelotas de tenis", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals("Pelotas", restaurado.getCategoria(), "La categoría restaurada no es la esperada.");
        assertEquals(18000, restaurado.getPrecio(), 0.001, "El precio restaurado no es el esperado.");
        assertEquals(30, restaurado.getCantidad(), "La cantidad restaurada no es la esperada.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject json = new JSONObject();
        json.put("nombre", "Solo nombre");

        assertThrows(org.json.JSONException.class, () -> Articulo.fromJSON(json), "Debe lanzarse JSONException si faltan campos.");
    }
}
