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

import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;
import DeportesIndividuales.TenisDeMesa;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaPadel;
import Instalaciones.CanchaTenis;
import Instalaciones.MesaTenisDeMesa;
import Tienda_Cafeteria_Inventario.Alergeno;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.CafeteriaSocios;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Snack;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Socio;

public class CafeteriaSociosTest
{
    private CafeteriaSocios cafeteria;

    private Bebida cafe;
    private Bebida limonada;
    private Snack galletas;

    private CanchaPadel canchaPadel;
    private CanchaTenis canchaTenis;
    private MesaTenisDeMesa mesaTechada;
    private CanchaFutbol canchaFutbol;

    private Socio socio;
    private Map<String, Producto> mapaProductos;

    @BeforeEach
    void setUp() throws Exception
    {
        cafeteria = new CafeteriaSocios();

        cafe = new Bebida("Cafe", "Calientes", 4000, 20, true);
        limonada = new Bebida("Limonada", "Frias", 6000, 20, false);
        galletas = new Snack("Galletas", "Snacks", 3000, 15, new ArrayList<Alergeno>());

        canchaPadel = new CanchaPadel("Padel 1", false, 1, new Padel(1, "Padel"));
        canchaTenis = new CanchaTenis("Tenis 1", true, 1, new Tenis(1, "Tenis"));
        mesaTechada = new MesaTenisDeMesa("Mesa 1", true, 1, new TenisDeMesa(1, "TenisDeMesa"));
        canchaFutbol = new CanchaFutbol("Futbol 1");

        socio = new Socio(20, "Ana Torres", "ana", "1");
        mapaProductos = new HashMap<String, Producto>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private List<LineaVenta> crearLineas(Producto... productos)
    {
        List<LineaVenta> lineas = new ArrayList<LineaVenta>();
        for(Producto producto : productos)
        {
            lineas.add(new LineaVenta(1, producto));
        }
        return lineas;
    }

    @Test
    void testPropinaSugerida()
    {
        assertEquals(0.1, CafeteriaSocios.getPropinaSugerida(), 0.0001, "La propina sugerida debe ser del 10%.");
    }

    @Test
    void testCafeteriaNuevaVacia()
    {
        JSONObject json = cafeteria.toJSON();

        assertEquals(0, json.getJSONArray("bebidas").length(), "Una cafetería nueva no debe tener bebidas.");
        assertEquals(0, json.getJSONArray("snacks").length(), "Una cafetería nueva no debe tener snacks.");
    }

    @Test
    void testAgregarProducto()
    {
        cafeteria.agregarProducto(cafe);
        cafeteria.agregarProducto(limonada);
        cafeteria.agregarProducto(galletas);

        JSONObject json = cafeteria.toJSON();

        assertEquals(2, json.getJSONArray("bebidas").length(), "La cafetería debe tener dos bebidas.");
        assertEquals(1, json.getJSONArray("snacks").length(), "La cafetería debe tener un snack.");
    }

    @Test
    void testAgregarProductoDeOtroTipoSeIgnora()
    {
        cafeteria.agregarProducto(new Articulo("Raqueta", "Raquetas", 100000, 5));

        JSONObject json = cafeteria.toJSON();

        assertEquals(0, json.getJSONArray("bebidas").length(), "Un artículo no debe quedar entre las bebidas.");
        assertEquals(0, json.getJSONArray("snacks").length(), "Un artículo no debe quedar entre los snacks.");
    }

    @Test
    void testReabastecer()
    {
        cafeteria.reabastecer(galletas, 4);

        assertEquals(4, cafeteria.toJSON().getJSONArray("snacks").length(), "El reabastecimiento debe agregar el producto tantas veces como la cantidad.");
    }

    @Test
    void testPuedeDespacharBebidaCalienteEnCanchaRestringida()
    {
        assertFalse(cafeteria.puedeDespachar(cafe, canchaPadel), "No se puede despachar bebida caliente en la cancha de pádel.");
        assertFalse(cafeteria.puedeDespachar(cafe, mesaTechada), "No se puede despachar bebida caliente en una mesa techada.");
    }

    @Test
    void testPuedeDespacharBebidaCalienteEnCanchaPermitida()
    {
        assertTrue(cafeteria.puedeDespachar(cafe, canchaTenis), "Se puede despachar bebida caliente en la cancha de tenis.");
    }

    @Test
    void testPuedeDespacharBebidaCalienteEnInstalacionDeConjunto()
    {
        assertTrue(cafeteria.puedeDespachar(cafe, canchaFutbol), "Se puede despachar bebida caliente en una cancha de conjunto.");
    }

    @Test
    void testPuedeDespacharBebidaCalienteSinInstalacion()
    {
        assertTrue(cafeteria.puedeDespachar(cafe, null), "Sin instalación se puede despachar bebida caliente.");
    }

    @Test
    void testPuedeDespacharBebidaFriaEnCualquierLugar()
    {
        assertTrue(cafeteria.puedeDespachar(limonada, canchaPadel), "Se puede despachar bebida fría en la cancha de pádel.");
        assertTrue(cafeteria.puedeDespachar(limonada, mesaTechada), "Se puede despachar bebida fría en una mesa techada.");
    }

    @Test
    void testVender()
    {
        Venta venta = cafeteria.vender(socio, crearLineas(limonada, galletas), canchaPadel, null, 0, 0);

        assertNotNull(venta, "La venta no debe ser nula.");
        assertEquals(Rubro.CAFETERIA, venta.getRubro(), "El rubro de la venta debe ser cafetería.");
        assertEquals(0.08, venta.getTasaImpuestos(), 0.0001, "La cafetería cobra impuesto al consumo del 8%.");
        assertEquals(9000 * 1.08, venta.getTotal(), 0.001, "El total de la venta no es el esperado.");
    }

    @Test
    void testVenderBebidaCalienteEnCanchaRestringida()
    {
        Venta venta = cafeteria.vender(socio, crearLineas(limonada, cafe), canchaPadel, null, 0, 0);

        assertNull(venta, "No se debe vender una bebida caliente en una cancha restringida.");
    }

    @Test
    void testVenderBebidaCalienteEnCanchaPermitida()
    {
        Venta venta = cafeteria.vender(socio, crearLineas(cafe), canchaTenis, null, 0, 0);

        assertNotNull(venta, "Se debe poder vender una bebida caliente en la cancha de tenis.");
    }

    @Test
    void testVenderSnackEnCanchaRestringida()
    {
        Venta venta = cafeteria.vender(socio, crearLineas(galletas), canchaPadel, null, 0, 0);

        assertNotNull(venta, "Los snacks se pueden vender en cualquier instalación.");
    }

    @Test
    void testVenderConPropina()
    {
        Venta venta = cafeteria.vender(socio, crearLineas(limonada), null, null, 0, 600);

        assertEquals(6000 * 1.08 + 600, venta.getTotal(), 0.001, "El total debe incluir la propina.");
    }

    @Test
    void testToJSON()
    {
        cafeteria.agregarProducto(cafe);
        cafeteria.agregarProducto(galletas);

        JSONObject json = cafeteria.toJSON();

        assertEquals("Cafe", json.getJSONArray("bebidas").getJSONObject(0).getString("nombre"), "El nombre de la bebida en el JSON no es el esperado.");
        assertEquals("Galletas", json.getJSONArray("snacks").getJSONObject(0).getString("nombre"), "El nombre del snack en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        cafeteria.agregarProducto(cafe);
        cafeteria.agregarProducto(galletas);

        CafeteriaSocios restaurada = CafeteriaSocios.fromJSON(cafeteria.toJSON(), mapaProductos);

        assertEquals(1, restaurada.toJSON().getJSONArray("bebidas").length(), "La cafetería restaurada debe tener una bebida.");
        assertEquals(1, restaurada.toJSON().getJSONArray("snacks").length(), "La cafetería restaurada debe tener un snack.");
        assertTrue(mapaProductos.get("Cafe") instanceof Bebida, "La bebida debe quedar en el mapa de productos.");
        assertTrue(mapaProductos.get("Galletas") instanceof Snack, "El snack debe quedar en el mapa de productos.");
    }

    @Test
    void testFromJSONReutilizaProductosDelMapa()
    {
        mapaProductos.put("Cafe", cafe);
        cafeteria.agregarProducto(cafe);

        CafeteriaSocios.fromJSON(cafeteria.toJSON(), mapaProductos);

        assertSame(cafe, mapaProductos.get("Cafe"), "El producto existente en el mapa debe reutilizarse.");
    }

    @Test
    void testFromJSONSinProductos()
    {
        CafeteriaSocios restaurada = CafeteriaSocios.fromJSON(new JSONObject(), mapaProductos);

        assertEquals(0, restaurada.toJSON().getJSONArray("bebidas").length(), "Sin bebidas en el JSON la lista debe quedar vacía.");
        assertEquals(0, restaurada.toJSON().getJSONArray("snacks").length(), "Sin snacks en el JSON la lista debe quedar vacía.");
    }
}
