package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Alergeno;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Snack;

public class ProductoTest
{
    private Producto raqueta;
    private Producto cafe;
    private Producto galletas;

    @BeforeEach
    void setUp() throws Exception
    {
        raqueta = new Articulo("Raqueta", "Raquetas", 250000.5, 10);
        cafe = new Bebida("Cafe", "Bebidas calientes", 4500, 20, true);

        ArrayList<Alergeno> alergenos = new ArrayList<Alergeno>();
        alergenos.add(Alergeno.GLUTEN);
        galletas = new Snack("Galletas", "Snacks", 3000, 15, alergenos);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals("Raqueta", raqueta.getNombre(), "El nombre del producto no es el esperado.");
        assertEquals("Raquetas", raqueta.getCategoria(), "La categoría del producto no es la esperada.");
        assertEquals(250000.5, raqueta.getPrecio(), 0.001, "El precio del producto no es el esperado.");
        assertEquals(10, raqueta.getCantidad(), "La cantidad del producto no es la esperada.");
    }

    @Test
    void testReducirCantidad()
    {
        assertTrue(raqueta.reducirCantidad(4), "Debe poder reducirse la cantidad si hay existencias.");

        assertEquals(6, raqueta.getCantidad(), "La cantidad después de reducir no es la esperada.");
    }

    @Test
    void testReducirCantidadHastaCero()
    {
        assertTrue(raqueta.reducirCantidad(10), "Debe poder reducirse toda la cantidad disponible.");

        assertEquals(0, raqueta.getCantidad(), "La cantidad debe quedar en 0.");
    }

    @Test
    void testReducirCantidadMasDeLaDisponible()
    {
        assertFalse(raqueta.reducirCantidad(11), "No debe poder reducirse más de lo disponible.");

        assertEquals(10, raqueta.getCantidad(), "La cantidad no debe cambiar.");
    }

    @Test
    void testReducirCantidadCero()
    {
        assertTrue(raqueta.reducirCantidad(0), "Reducir cero unidades siempre es posible.");

        assertEquals(10, raqueta.getCantidad(), "La cantidad no debe cambiar.");
    }

    @Test
    void testAgregarCantidad()
    {
        raqueta.agregarCantidad(5);

        assertEquals(15, raqueta.getCantidad(), "La cantidad después de agregar no es la esperada.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = raqueta.toJSON();

        assertEquals("Raqueta", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals("Raquetas", json.getString("categoria"), "La categoría en el JSON no es la esperada.");
        assertEquals(250000.5, json.getDouble("precio"), 0.001, "El precio en el JSON no es el esperado.");
        assertEquals(10, json.getInt("cantidad"), "La cantidad en el JSON no es la esperada.");
    }

    @Test
    void testFromJSONArticulo()
    {
        Producto creado = Producto.fromJSON(raqueta.toJSON());

        assertTrue(creado instanceof Articulo, "El tipo Articulo debe crear un artículo.");
        assertEquals("Raqueta", creado.getNombre(), "El nombre del producto creado no es el esperado.");
    }

    @Test
    void testFromJSONBebida()
    {
        Producto creado = Producto.fromJSON(cafe.toJSON());

        assertTrue(creado instanceof Bebida, "El tipo Bebida debe crear una bebida.");
        assertTrue(((Bebida) creado).getCaliente(), "La bebida creada debe ser caliente.");
    }

    @Test
    void testFromJSONSnack()
    {
        Producto creado = Producto.fromJSON(galletas.toJSON());

        assertTrue(creado instanceof Snack, "El tipo Snack debe crear un snack.");
        assertEquals(1, ((Snack) creado).getAlergenos().size(), "El snack creado debe tener un alérgeno.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = raqueta.toJSON();
        json.put("tipo", "Servicio");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Producto.fromJSON(json), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de producto desconocido", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
