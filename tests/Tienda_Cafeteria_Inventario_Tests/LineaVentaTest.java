package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Producto;

public class LineaVentaTest
{
    private Articulo raqueta;
    private LineaVenta linea;
    private Map<String, Producto> mapaProductos;

    @BeforeEach
    void setUp() throws Exception
    {
        raqueta = new Articulo("Raqueta", "Raquetas", 250000, 10);
        linea = new LineaVenta(2, raqueta);

        mapaProductos = new HashMap<String, Producto>();
        mapaProductos.put("Raqueta", raqueta);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals(2, linea.getCantidad(), "La cantidad de la línea no es la esperada.");
        assertSame(raqueta, linea.getProducto(), "El producto de la línea no es el esperado.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = linea.toJSON();

        assertEquals(2, json.getInt("cantidad"), "La cantidad en el JSON no es la esperada.");
        assertEquals("Raqueta", json.getString("producto"), "El producto en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        LineaVenta restaurada = LineaVenta.fromJSON(linea.toJSON(), mapaProductos);

        assertEquals(2, restaurada.getCantidad(), "La cantidad restaurada no es la esperada.");
        assertSame(raqueta, restaurada.getProducto(), "El producto restaurado debe ser el del mapa.");
    }

    @Test
    void testFromJSONProductoDesconocido()
    {
        mapaProductos.clear();

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> LineaVenta.fromJSON(linea.toJSON(), mapaProductos), "Debe lanzarse IllegalArgumentException si el producto no existe.");

        assertEquals("Producto desconocido en la línea de venta: Raqueta", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
