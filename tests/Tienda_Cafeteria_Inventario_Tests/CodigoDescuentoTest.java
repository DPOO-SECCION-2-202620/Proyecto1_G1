package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.CodigoDescuento;

public class CodigoDescuentoTest
{
    private CodigoDescuento codigo;

    @BeforeEach
    void setUp() throws Exception
    {
        codigo = new CodigoDescuento("DESC-E01");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetCodigo()
    {
        assertEquals("DESC-E01", codigo.getCodigo(), "El código no es el esperado.");
    }

    @Test
    void testGetPorcentajeDueno()
    {
        assertEquals(0.15, codigo.getPorcentaje(true), 0.0001, "El descuento del dueño debe ser 15%.");
    }

    @Test
    void testGetPorcentajeCompartido()
    {
        assertEquals(0.08, codigo.getPorcentaje(false), 0.0001, "El descuento compartido debe ser 8%.");
    }

    @Test
    void testToJSON()
    {
        assertEquals("DESC-E01", codigo.toJSON().getString("codigo"), "El código en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        CodigoDescuento restaurado = CodigoDescuento.fromJSON(codigo.toJSON());

        assertEquals("DESC-E01", restaurado.getCodigo(), "El código restaurado no es el esperado.");
    }

    @Test
    void testFromJSONSinCodigo()
    {
        assertThrows(org.json.JSONException.class, () -> CodigoDescuento.fromJSON(new JSONObject()), "Debe lanzarse JSONException si falta el código.");
    }
}
