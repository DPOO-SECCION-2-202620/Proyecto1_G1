package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import Instalaciones.CanchaBaloncesto;
import Instalaciones.CanchaFutbol;
import Instalaciones.Instalacion;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.Inventario;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Ubicacion;

public class InventarioTest
{
    private CanchaFutbol canchaFutbol;
    private CanchaBaloncesto canchaBaloncesto;
    private Ubicacion ubicacion;
    private Inventario inventario;

    private Articulo conos;
    private Articulo balones;

    private Map<String, Producto> mapaProductos;
    private Map<String, Instalacion> mapaInstalaciones;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        canchaFutbol = new CanchaFutbol("Futbol 1");
        canchaBaloncesto = new CanchaBaloncesto("Basket 1");

        List<Instalacion> instalaciones = new ArrayList<Instalacion>();
        instalaciones.add(canchaFutbol);
        instalaciones.add(canchaBaloncesto);

        ubicacion = new Ubicacion("Bodega Norte");
        inventario = new Inventario(instalaciones, ubicacion);

        conos = new Articulo("Conos", "Entrenamiento", 2000, 40);
        balones = new Articulo("Balones", "Entrenamiento", 50000, 12);

        mapaProductos = new HashMap<String, Producto>();
        mapaProductos.put("Conos", conos);
        mapaProductos.put("Balones", balones);
        mapaProductos.put("Gaseosa", new Bebida("Gaseosa", "Frias", 3000, 5, false));

        mapaInstalaciones = new HashMap<String, Instalacion>();
        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertSame(ubicacion, inventario.getUbicacion(), "La ubicación del inventario no es la esperada.");
        assertEquals(2, inventario.getInstalaciones().size(), "El inventario debe tener dos instalaciones.");
        assertSame(canchaFutbol, inventario.getInstalaciones().get(0), "La primera instalación no es la esperada.");
        assertEquals(0, inventario.getArticulos().size(), "Un inventario nuevo no debe tener artículos.");
    }

    @Test
    void testAgregarArticulo()
    {
        inventario.agregarArticulo(conos);

        assertEquals(1, inventario.getArticulos().size(), "El inventario debe tener un artículo.");
        assertTrue(inventario.tieneArticulo(conos), "El inventario debe tener los conos.");
        assertFalse(inventario.tieneArticulo(balones), "El inventario no debe tener los balones.");
    }

    @Test
    void testQuitarArticulo()
    {
        inventario.agregarArticulo(conos);
        inventario.agregarArticulo(balones);

        inventario.quitarArticulo(conos);

        assertFalse(inventario.tieneArticulo(conos), "El inventario ya no debe tener los conos.");
        assertTrue(inventario.tieneArticulo(balones), "El inventario debe conservar los balones.");
        assertEquals(1, inventario.getArticulos().size(), "El inventario debe tener un artículo.");
    }

    @Test
    void testQuitarArticuloInexistente()
    {
        inventario.agregarArticulo(conos);

        inventario.quitarArticulo(balones);

        assertEquals(1, inventario.getArticulos().size(), "Quitar un artículo que no está no debe cambiar el inventario.");
    }

    @Test
    void testToJSON()
    {
        inventario.agregarArticulo(conos);

        JSONObject json = inventario.toJSON();

        assertEquals(2, json.getJSONArray("instalaciones").length(), "El JSON debe tener dos instalaciones.");
        assertEquals("Futbol 1", json.getJSONArray("instalaciones").getJSONObject(0).getString("nombre"), "La primera instalación en el JSON no es la esperada.");
        assertEquals("Bodega Norte", json.getJSONObject("ubicacion").getString("nombre"), "La ubicación en el JSON no es la esperada.");
        assertEquals(1, json.getJSONArray("articulos").length(), "El JSON debe tener un artículo.");
        assertEquals("Conos", json.getJSONArray("articulos").getString(0), "El artículo en el JSON debe estar identificado por su nombre.");
    }

    @Test
    void testFromJSON()
    {
        inventario.agregarArticulo(conos);
        inventario.agregarArticulo(balones);

        Inventario restaurado = Inventario.fromJSON(inventario.toJSON(), mapaProductos, mapaInstalaciones, mapaDisciplinas);

        assertEquals("Bodega Norte", restaurado.getUbicacion().getNombre(), "La ubicación restaurada no es la esperada.");
        assertEquals(2, restaurado.getInstalaciones().size(), "Deben restaurarse las dos instalaciones.");
        assertEquals("Basket 1", restaurado.getInstalaciones().get(1).getNombre(), "La segunda instalación restaurada no es la esperada.");
        assertEquals(2, restaurado.getArticulos().size(), "Deben restaurarse los dos artículos.");
        assertSame(conos, restaurado.getArticulos().get(0), "Los artículos restaurados deben ser los del mapa de productos.");
        assertEquals(2, mapaInstalaciones.size(), "Las instalaciones restauradas deben quedar en el mapa.");
    }

    @Test
    void testFromJSONReutilizaInstalacionesDelMapa()
    {
        mapaInstalaciones.put("Futbol 1", canchaFutbol);

        Inventario restaurado = Inventario.fromJSON(inventario.toJSON(), mapaProductos, mapaInstalaciones, mapaDisciplinas);

        assertSame(canchaFutbol, restaurado.getInstalaciones().get(0), "La instalación existente en el mapa debe reutilizarse.");
    }

    @Test
    void testFromJSONConClaveUbicaciones()
    {
        JSONObject json = inventario.toJSON();
        json.remove("ubicacion");
        json.put("ubicaciones", new JSONArray().put(ubicacion.toJSON()));

        Inventario restaurado = Inventario.fromJSON(json, mapaProductos, mapaInstalaciones, mapaDisciplinas);

        assertEquals("Bodega Norte", restaurado.getUbicacion().getNombre(), "La ubicación debe poder leerse desde la clave ubicaciones.");
    }

    @Test
    void testFromJSONSinArticulos()
    {
        JSONObject json = inventario.toJSON();
        json.remove("articulos");

        Inventario restaurado = Inventario.fromJSON(json, mapaProductos, mapaInstalaciones, mapaDisciplinas);

        assertEquals(0, restaurado.getArticulos().size(), "Sin artículos en el JSON el inventario debe quedar vacío.");
    }

    @Test
    void testFromJSONArticuloInexistente()
    {
        inventario.agregarArticulo(conos);
        mapaProductos.remove("Conos");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Inventario.fromJSON(inventario.toJSON(), mapaProductos, mapaInstalaciones, mapaDisciplinas), "Debe lanzarse IllegalArgumentException si un artículo no existe.");

        assertEquals("Artículo no encontrado para el inventario: Conos", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONProductoQueNoEsArticulo()
    {
        JSONObject json = inventario.toJSON();
        json.put("articulos", new JSONArray().put("Gaseosa"));

        assertThrows(IllegalArgumentException.class, () -> Inventario.fromJSON(json, mapaProductos, mapaInstalaciones, mapaDisciplinas), "Un producto que no es artículo no puede estar en el inventario.");
    }
}
