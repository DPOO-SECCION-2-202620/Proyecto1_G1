package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Excepciones.ReglaNegocioException;
import Tienda_Cafeteria_Inventario.BonoDescuento;

public class BonoDescuentoTest
{
    private BonoDescuento bonoPrimerPuesto;
    private BonoDescuento bonoSegundoPuesto;
    private BonoDescuento bonoTotal;
    private BonoDescuento bonoSinDescuento;

    @BeforeEach
    void setUp() throws Exception
    {
        bonoPrimerPuesto = new BonoDescuento(0.25, "Copa Cerros");
        bonoSegundoPuesto = new BonoDescuento(0.1, "Copa Amigos");
        bonoTotal = new BonoDescuento(1, "Copa Total");
        bonoSinDescuento = new BonoDescuento(0, "Copa Nula");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testBonoNuevo()
    {
        assertEquals(0.25, bonoPrimerPuesto.getPorcentaje(), 0.0001, "El porcentaje del bono no es el esperado.");
        assertEquals("Copa Cerros", bonoPrimerPuesto.getTorneoOrigen(), "El torneo de origen no es el esperado.");
        assertFalse(bonoPrimerPuesto.estaUsado(), "Un bono nuevo no debe estar usado.");
    }

    @Test
    void testCadaBonoGuardaSusPropiosDatos()
    {
        assertEquals(0.1, bonoSegundoPuesto.getPorcentaje(), 0.0001, "El porcentaje del segundo bono no es el esperado.");
        assertEquals("Copa Amigos", bonoSegundoPuesto.getTorneoOrigen(), "El torneo de origen del segundo bono no es el esperado.");
        assertNotEquals(bonoPrimerPuesto.getPorcentaje(), bonoSegundoPuesto.getPorcentaje(), 0.0001, "Los bonos no deben compartir el porcentaje.");
    }

    @Test
    void testAplicarDescuentaElPorcentaje() throws Exception
    {
        assertEquals(75000, bonoPrimerPuesto.aplicar(100000), 0.001, "Un bono del 25% sobre 100000 debe dejar 75000.");
        assertEquals(90000, bonoSegundoPuesto.aplicar(100000), 0.001, "Un bono del 10% sobre 100000 debe dejar 90000.");
    }

    @Test
    void testAplicarMarcaElBonoComoUsado() throws Exception
    {
        bonoPrimerPuesto.aplicar(100000);

        assertTrue(bonoPrimerPuesto.estaUsado(), "Después de aplicarlo el bono debe quedar usado.");
    }

    @Test
    void testAplicarNoAfectaAOtrosBonos() throws Exception
    {
        bonoPrimerPuesto.aplicar(100000);

        assertFalse(bonoSegundoPuesto.estaUsado(), "Usar un bono no debe marcar a los demás como usados.");
    }

    @Test
    void testAplicarBonoUsado() throws Exception
    {
        bonoPrimerPuesto.aplicar(100000);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> bonoPrimerPuesto.aplicar(50000), "Un bono solo puede usarse una vez.");

        assertTrue(excepcion.getMessage().toLowerCase().contains("usado"), "El mensaje debe indicar que el bono ya fue usado.");
        assertTrue(bonoPrimerPuesto.estaUsado(), "El bono debe seguir usado.");
    }

    @Test
    void testAplicarSubtotalCero() throws Exception
    {
        assertEquals(0, bonoPrimerPuesto.aplicar(0), 0.001, "El descuento sobre un subtotal cero es cero.");
        assertTrue(bonoPrimerPuesto.estaUsado(), "Aplicar el bono a un subtotal cero igual lo consume.");
    }

    @Test
    void testAplicarBonoDelCienPorCiento() throws Exception
    {
        assertEquals(0, bonoTotal.aplicar(80000), 0.001, "Un bono del 100% deja el subtotal en cero.");
    }

    @Test
    void testAplicarBonoSinDescuento() throws Exception
    {
        assertEquals(80000, bonoSinDescuento.aplicar(80000), 0.001, "Un bono del 0% no cambia el subtotal.");
    }

    @Test
    void testSetUsado()
    {
        bonoPrimerPuesto.setUsado(true);
        assertTrue(bonoPrimerPuesto.estaUsado(), "El bono debe quedar usado.");

        bonoPrimerPuesto.setUsado(false);
        assertFalse(bonoPrimerPuesto.estaUsado(), "El bono debe quedar disponible.");
    }

    @Test
    void testBonoMarcadoComoUsadoNoSePuedeAplicar()
    {
        bonoPrimerPuesto.setUsado(true);

        assertThrows(ReglaNegocioException.class, () -> bonoPrimerPuesto.aplicar(100000), "Un bono marcado como usado no puede aplicarse.");
    }

    @Test
    void testBonoRehabilitadoSePuedeAplicar() throws Exception
    {
        bonoPrimerPuesto.aplicar(100000);
        bonoPrimerPuesto.setUsado(false);

        assertEquals(75000, bonoPrimerPuesto.aplicar(100000), 0.001, "Un bono rehabilitado debe poder aplicarse otra vez.");
    }

    @Test
    void testToJSONBonoNuevo()
    {
        JSONObject json = bonoPrimerPuesto.toJSON();

        assertEquals(0.25, json.getDouble("porcentaje"), 0.0001, "El porcentaje en el JSON no es el esperado.");
        assertFalse(json.getBoolean("usado"), "El estado usado en el JSON no es el esperado.");
        assertEquals("Copa Cerros", json.getString("torneoOrigen"), "El torneo de origen en el JSON no es el esperado.");
    }

    @Test
    void testToJSONBonoUsado() throws Exception
    {
        bonoPrimerPuesto.aplicar(100000);

        assertTrue(bonoPrimerPuesto.toJSON().getBoolean("usado"), "El JSON debe reflejar que el bono fue usado.");
    }

    @Test
    void testFromJSONBonoNuevo() throws Exception
    {
        BonoDescuento restaurado = BonoDescuento.fromJSON(bonoSegundoPuesto.toJSON());

        assertEquals(0.1, restaurado.getPorcentaje(), 0.0001, "El porcentaje restaurado no es el esperado.");
        assertEquals("Copa Amigos", restaurado.getTorneoOrigen(), "El torneo de origen restaurado no es el esperado.");
        assertFalse(restaurado.estaUsado(), "El bono restaurado debe estar disponible.");
        assertEquals(90000, restaurado.aplicar(100000), 0.001, "El bono restaurado debe poder aplicarse.");
    }

    @Test
    void testFromJSONBonoUsado() throws Exception
    {
        bonoPrimerPuesto.aplicar(100000);

        BonoDescuento restaurado = BonoDescuento.fromJSON(bonoPrimerPuesto.toJSON());

        assertTrue(restaurado.estaUsado(), "El bono restaurado debe seguir usado.");
        assertThrows(ReglaNegocioException.class, () -> restaurado.aplicar(100000), "Un bono restaurado como usado no puede aplicarse.");
    }

    @Test
    void testFromJSONNoModificaElOriginal() throws Exception
    {
        BonoDescuento restaurado = BonoDescuento.fromJSON(bonoPrimerPuesto.toJSON());
        restaurado.aplicar(100000);

        assertFalse(bonoPrimerPuesto.estaUsado(), "Usar el bono restaurado no debe afectar al original.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject sinUsado = bonoPrimerPuesto.toJSON();
        sinUsado.remove("usado");
        JSONObject sinPorcentaje = bonoPrimerPuesto.toJSON();
        sinPorcentaje.remove("porcentaje");
        JSONObject sinOrigen = bonoPrimerPuesto.toJSON();
        sinOrigen.remove("torneoOrigen");

        assertThrows(org.json.JSONException.class, () -> BonoDescuento.fromJSON(sinUsado), "Debe lanzarse JSONException si falta el estado usado.");
        assertThrows(org.json.JSONException.class, () -> BonoDescuento.fromJSON(sinPorcentaje), "Debe lanzarse JSONException si falta el porcentaje.");
        assertThrows(org.json.JSONException.class, () -> BonoDescuento.fromJSON(sinOrigen), "Debe lanzarse JSONException si falta el torneo de origen.");
    }
}
