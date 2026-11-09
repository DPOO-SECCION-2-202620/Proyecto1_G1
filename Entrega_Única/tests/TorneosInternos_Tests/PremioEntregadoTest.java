package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.BonoDescuento;
import TorneosInternos.PremioEntregado;
import Usuarios.Administrador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class PremioEntregadoTest
{
    private Socio ana;
    private BonoDescuento primerBono;
    private BonoDescuento segundoBono;
    private BonoDescuento bonoAjeno;

    private PremioEntregado premioEfectivo;
    private PremioEntregado premioConBono;
    private PremioEntregado premioConBonoAjeno;

    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        primerBono = new BonoDescuento(0.25, "Copa Cerros");
        segundoBono = new BonoDescuento(0.1, "Copa Cerros");
        bonoAjeno = new BonoDescuento(0.5, "Otro Torneo");
        ana.agregarBono(primerBono);
        ana.agregarBono(segundoBono);

        premioEfectivo = new PremioEntregado(1, ana, 5000.5, null);
        premioConBono = new PremioEntregado(2, ana, 0, segundoBono);
        premioConBonoAjeno = new PremioEntregado(2, ana, 0, bonoAjeno);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", ana);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals(1, premioEfectivo.getPuesto(), "El puesto no es el esperado.");
        assertSame(ana, premioEfectivo.getJugador(), "El jugador no es el esperado.");
        assertEquals(5000.5, premioEfectivo.getMontoEfectivo(), 0.001, "El monto en efectivo no es el esperado.");
        assertNull(premioEfectivo.getBono(), "El premio en efectivo no tiene bono.");
        assertSame(segundoBono, premioConBono.getBono(), "El bono del premio no es el esperado.");
    }

    @Test
    void testToJSONSinBono()
    {
        JSONObject json = premioEfectivo.toJSON();

        assertEquals(1, json.getInt("puesto"), "El puesto en el JSON no es el esperado.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(5000.5, json.getDouble("montoEfectivo"), 0.001, "El monto en el JSON no es el esperado.");
        assertFalse(json.has("indiceBono"), "Sin bono el JSON no debe incluir el índice.");
    }

    @Test
    void testToJSONConBono()
    {
        JSONObject json = premioConBono.toJSON();

        assertEquals(2, json.getInt("puesto"), "El puesto en el JSON no es el esperado.");
        assertEquals(1, json.getInt("indiceBono"), "El índice del bono debe ser su posición en los bonos del jugador.");
    }

    @Test
    void testToJSONConBonoQueElJugadorNoTiene()
    {
        assertEquals(-1, premioConBonoAjeno.toJSON().getInt("indiceBono"), "Un bono que el jugador no tiene queda con índice -1.");
    }

    @Test
    void testFromJSONSinBono()
    {
        PremioEntregado restaurado = PremioEntregado.fromJSON(premioEfectivo.toJSON(), mapaUsuarios);

        assertEquals(1, restaurado.getPuesto(), "El puesto restaurado no es el esperado.");
        assertSame(ana, restaurado.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(5000.5, restaurado.getMontoEfectivo(), 0.001, "El monto restaurado no es el esperado.");
        assertNull(restaurado.getBono(), "Sin índice de bono el bono restaurado debe ser nulo.");
    }

    @Test
    void testFromJSONConBono()
    {
        PremioEntregado restaurado = PremioEntregado.fromJSON(premioConBono.toJSON(), mapaUsuarios);

        assertSame(segundoBono, restaurado.getBono(), "El bono restaurado debe ser el del jugador en ese índice.");
    }

    @Test
    void testFromJSONConIndiceNegativo()
    {
        PremioEntregado restaurado = PremioEntregado.fromJSON(premioConBonoAjeno.toJSON(), mapaUsuarios);

        assertNull(restaurado.getBono(), "Un índice negativo no corresponde a ningún bono.");
    }

    @Test
    void testFromJSONConIndiceFueraDeRango()
    {
        JSONObject json = premioConBono.toJSON();
        json.put("indiceBono", 7);

        PremioEntregado restaurado = PremioEntregado.fromJSON(json, mapaUsuarios);

        assertNull(restaurado.getBono(), "Un índice fuera de rango no corresponde a ningún bono.");
    }

    @Test
    void testFromJSONJugadorInexistente()
    {
        mapaUsuarios.remove("ana");

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> PremioEntregado.fromJSON(premioEfectivo.toJSON(), mapaUsuarios), "Debe lanzarse IllegalStateException si el jugador no existe.");

        assertEquals("Jugador inexistente en el torneo: ana", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONUsuarioQueNoEsJugador()
    {
        JSONObject json = premioEfectivo.toJSON();
        json.put("jugador", "admin");

        assertThrows(IllegalStateException.class, () -> PremioEntregado.fromJSON(json, mapaUsuarios), "Un usuario que no es jugador de torneo no puede recibir premios.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject json = new JSONObject();
        json.put("puesto", 1);

        assertThrows(org.json.JSONException.class, () -> PremioEntregado.fromJSON(json, mapaUsuarios), "Debe lanzarse JSONException si faltan campos.");
    }
}
