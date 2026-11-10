package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Tienda;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Socio;

public class TiendaTest
{
    private Tienda tienda;
    private Articulo raqueta;
    private Articulo pelotas;
    private Socio socio;

    private Map<String, Producto> mapaProductos;

    @BeforeEach
    void setUp() throws Exception
    {
        tienda = new Tienda();
        raqueta = new Articulo("Raqueta", "Raquetas", 100000, 10);
        pelotas = new Articulo("Pelotas", "Pelotas", 20000, 30);
        socio = new Socio(20, "Ana Torres", "ana", "1");

        mapaProductos = new HashMap<String, Producto>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testTiendaNuevaVacia()
    {
        assertEquals(0, tienda.toJSON().getJSONArray("articulos").length(), "Una tienda nueva no debe tener artículos.");
    }

    @Test
    void testAgregarArticulo()
    {
        tienda.agregarArticulo(raqueta);
        tienda.agregarArticulo(pelotas);

        JSONObject json = tienda.toJSON();

        assertEquals(2, json.getJSONArray("articulos").length(), "La tienda debe tener dos artículos.");
        assertEquals("Raqueta", json.getJSONArray("articulos").getJSONObject(0).getString("nombre"), "El primer artículo no es el esperado.");
    }

    @Test
    void testReabastecer()
    {
        tienda.reabastecer(raqueta, 3);

        assertEquals(3, tienda.toJSON().getJSONArray("articulos").length(), "El reabastecimiento debe agregar el artículo tantas veces como la cantidad.");
    }

    @Test
    void testReabastecerCantidadCero()
    {
        tienda.reabastecer(raqueta, 0);

        assertEquals(0, tienda.toJSON().getJSONArray("articulos").length(), "Reabastecer cero unidades no debe agregar artículos.");
    }

    @Test
    void testVender()
    {
        List<LineaVenta> lineas = new ArrayList<LineaVenta>();
        lineas.add(new LineaVenta(2, pelotas));

        Venta venta = tienda.vender(socio, lineas, null, null, 0, 0);

        assertNotNull(venta, "La venta no debe ser nula.");
        assertEquals(Rubro.TIENDA, venta.getRubro(), "El rubro de la venta debe ser tienda.");
        assertEquals(0.19, venta.getTasaImpuestos(), 0.0001, "La tienda cobra IVA del 19%.");
        assertEquals(40000 * 1.19, venta.getTotal(), 0.001, "El total de la venta no es el esperado.");
    }

    @Test
    void testVenderConPropinaYPuntos()
    {
        List<LineaVenta> lineas = new ArrayList<LineaVenta>();
        lineas.add(new LineaVenta(1, raqueta));

        Venta venta = tienda.vender(socio, lineas, null, null, 1000, 500);

        assertEquals(100000 * 1.19 + 500 - 1000, venta.getTotal(), 0.001, "El total debe incluir la propina y descontar los puntos.");
        assertEquals(500, venta.getPropina(), 0.001, "La propina de la venta no es la esperada.");
        assertEquals(1000, venta.getPuntosRedimidos(), "Los puntos redimidos no son los esperados.");
    }

    @Test
    void testToJSON()
    {
        tienda.agregarArticulo(raqueta);

        JSONObject json = tienda.toJSON();

        assertEquals("Articulo", json.getJSONArray("articulos").getJSONObject(0).getString("tipo"), "El tipo del artículo en el JSON no es el esperado.");
        assertEquals(100000, json.getJSONArray("articulos").getJSONObject(0).getDouble("precio"), 0.001, "El precio del artículo en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONCreaArticulosYLosRegistraEnElMapa()
    {
        tienda.agregarArticulo(raqueta);
        tienda.agregarArticulo(pelotas);

        Tienda restaurada = Tienda.fromJSON(tienda.toJSON(), mapaProductos);

        assertEquals(2, restaurada.toJSON().getJSONArray("articulos").length(), "La tienda restaurada debe tener dos artículos.");
        assertEquals(2, mapaProductos.size(), "El mapa de productos debe tener los dos artículos.");
        assertTrue(mapaProductos.get("Raqueta") instanceof Articulo, "El producto del mapa debe ser un artículo.");
    }

    @Test
    void testFromJSONReutilizaProductosDelMapa()
    {
        mapaProductos.put("Raqueta", raqueta);
        tienda.agregarArticulo(raqueta);

        Tienda restaurada = Tienda.fromJSON(tienda.toJSON(), mapaProductos);

        assertSame(raqueta, mapaProductos.get("Raqueta"), "El producto existente en el mapa debe reutilizarse.");
        assertEquals(1, restaurada.toJSON().getJSONArray("articulos").length(), "La tienda restaurada debe tener un artículo.");
    }

    @Test
    void testFromJSONSinArticulos()
    {
        Tienda restaurada = Tienda.fromJSON(new JSONObject(), mapaProductos);

        assertEquals(0, restaurada.toJSON().getJSONArray("articulos").length(), "Sin artículos en el JSON la tienda debe quedar vacía.");
    }
}
