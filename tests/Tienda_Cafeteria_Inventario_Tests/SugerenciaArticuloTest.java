package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.EstadoSugerencia;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;

public class SugerenciaArticuloTest
{
    private SugerenciaArticulo sugerencia;

    @BeforeEach
    void setUp() throws Exception
    {
        sugerencia = new SugerenciaArticulo("Gorra", "Accesorios", 35000.5);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals("Gorra", sugerencia.getNombrePropuesto(), "El nombre propuesto no es el esperado.");
        assertEquals("Accesorios", sugerencia.getCategoria(), "La categoría no es la esperada.");
        assertEquals(35000.5, sugerencia.getPrecioSugerido(), 0.001, "El precio sugerido no es el esperado.");
    }

    @Test
    void testEstadoInicial()
    {
        assertEquals(EstadoSugerencia.PENDIENTE, sugerencia.getEstado(), "Una sugerencia nueva debe estar pendiente.");
    }

    @Test
    void testAprobar()
    {
        sugerencia.aprobar();

        assertEquals(EstadoSugerencia.ACEPTADA, sugerencia.getEstado(), "Una sugerencia aprobada debe quedar aceptada.");
    }

    @Test
    void testRechazar()
    {
        sugerencia.rechazar();

        assertEquals(EstadoSugerencia.RECHAZADA, sugerencia.getEstado(), "Una sugerencia rechazada debe quedar rechazada.");
    }

    @Test
    void testToJSON()
    {
        sugerencia.aprobar();

        JSONObject json = sugerencia.toJSON();

        assertEquals("Gorra", json.getString("nombrePropuesto"), "El nombre en el JSON no es el esperado.");
        assertEquals("Accesorios", json.getString("categoria"), "La categoría en el JSON no es la esperada.");
        assertEquals(35000.5, json.getDouble("precioSugerido"), 0.001, "El precio en el JSON no es el esperado.");
        assertEquals("ACEPTADA", json.getString("estado"), "El estado en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        sugerencia.rechazar();

        SugerenciaArticulo restaurada = SugerenciaArticulo.fromJSON(sugerencia.toJSON());

        assertEquals("Gorra", restaurada.getNombrePropuesto(), "El nombre restaurado no es el esperado.");
        assertEquals("Accesorios", restaurada.getCategoria(), "La categoría restaurada no es la esperada.");
        assertEquals(35000.5, restaurada.getPrecioSugerido(), 0.001, "El precio restaurado no es el esperado.");
        assertEquals(EstadoSugerencia.RECHAZADA, restaurada.getEstado(), "El estado restaurado no es el esperado.");
    }

    @Test
    void testFromJSONEstadoInvalido()
    {
        JSONObject json = sugerencia.toJSON();
        json.put("estado", "ARCHIVADA");

        assertThrows(IllegalArgumentException.class, () -> SugerenciaArticulo.fromJSON(json), "Un estado desconocido debe lanzar IllegalArgumentException.");
    }
}
