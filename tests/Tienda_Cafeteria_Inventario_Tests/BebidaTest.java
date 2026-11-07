package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.Producto;

public class BebidaTest
{
    private Bebida cafe;
    private Bebida limonada;

    @BeforeEach
    void setUp() throws Exception
    {
        cafe = new Bebida("Cafe", "Bebidas calientes", 4500, 20, true);
        limonada = new Bebida("Limonada", "Bebidas frias", 6000, 12, false);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsProducto()
    {
        assertTrue(cafe instanceof Producto, "Una bebida es un producto.");
    }

    @Test
    void testGetters()
    {
        assertEquals("Cafe", cafe.getNombre(), "El nombre de la bebida no es el esperado.");
        assertEquals("Bebidas calientes", cafe.getCategoria(), "La categoría de la bebida no es la esperada.");
        assertEquals(4500, cafe.getPrecio(), 0.001, "El precio de la bebida no es el esperado.");
        assertEquals(20, cafe.getCantidad(), "La cantidad de la bebida no es la esperada.");
    }

    @Test
    void testGetCaliente()
    {
        assertTrue(cafe.getCaliente(), "El café es una bebida caliente.");
        assertFalse(limonada.getCaliente(), "La limonada no es una bebida caliente.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = cafe.toJSON();

        assertEquals("Bebida", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertTrue(json.getBoolean("caliente"), "El valor caliente en el JSON no es el esperado.");
        assertEquals("Cafe", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        Bebida restaurada = Bebida.fromJSON(limonada.toJSON());

        assertEquals("Limonada", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals("Bebidas frias", restaurada.getCategoria(), "La categoría restaurada no es la esperada.");
        assertEquals(6000, restaurada.getPrecio(), 0.001, "El precio restaurado no es el esperado.");
        assertEquals(12, restaurada.getCantidad(), "La cantidad restaurada no es la esperada.");
        assertFalse(restaurada.getCaliente(), "El valor caliente restaurado no es el esperado.");
    }

    @Test
    void testFromJSONSinCaliente()
    {
        JSONObject json = cafe.toJSON();
        json.remove("caliente");

        assertThrows(org.json.JSONException.class, () -> Bebida.fromJSON(json), "Debe lanzarse JSONException si falta el valor caliente.");
    }
}
