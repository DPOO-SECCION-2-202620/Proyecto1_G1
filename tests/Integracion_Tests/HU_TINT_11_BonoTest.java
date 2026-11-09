package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.BonoDescuento;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Socio;

public class HU_TINT_11_BonoTest
{
    private static final double IVA = 1.19;

    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Socio ana;
    private Articulo raqueta;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        ana = club.crearSocio(25, "Ana Torres", "ana", "clave");
        raqueta = new Articulo("Raqueta", "Tenis", 40000, 10);
    }

    private Map<Producto, Integer> unaRaqueta()
    {
        Map<Producto, Integer> compra = new HashMap<>();
        compra.put(raqueta, 1);
        return compra;
    }

    @Test
    void usarBono() throws Exception
    {
        BonoDescuento bono = new BonoDescuento(0.25, "Copa Recreativa");
        ana.agregarBono(bono);

        Venta venta = club.registrarVentaConBono(ana, unaRaqueta(), Rubro.TIENDA, null, bono, 0);

        assertEquals(30000 * IVA, venta.getTotal(), 0.001, "El bono descuenta el 25%.");
        assertTrue(bono.estaUsado(), "El bono debe quedar usado.");
        assertEquals(9, raqueta.getCantidad(), "La compra descuenta el inventario.");
    }

    @Test
    void bonoUsado() throws Exception
    {
        BonoDescuento bono = new BonoDescuento(0.25, "Copa Recreativa");
        ana.agregarBono(bono);
        club.registrarVentaConBono(ana, unaRaqueta(), Rubro.TIENDA, null, bono, 0);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.registrarVentaConBono(ana, unaRaqueta(), Rubro.TIENDA, null, bono, 0));

        assertTrue(excepcion.getMessage().contains("ya fue usado"), "Debe informar que el bono ya fue usado.");
        assertEquals(9, raqueta.getCantidad(), "La segunda compra no se registra.");
    }

    @Test
    void noAcumulableConPuntos() throws Exception
    {
        club.registrarVenta(ana, unaRaqueta(), Rubro.TIENDA, null, null, 0, 0);
        int puntosAntes = ana.getPuntosFidelidad();
        assertTrue(puntosAntes > 0, "Ana debe tener puntos acumulados por la primera compra.");
        BonoDescuento bono = new BonoDescuento(0.25, "Copa Recreativa");
        ana.agregarBono(bono);

        Venta venta = club.registrarVentaConBono(ana, unaRaqueta(), Rubro.TIENDA, null, bono, 0);

        assertEquals(0, venta.getPuntosRedimidos(), "Una compra con bono no redime puntos.");
        assertEquals(30000 * IVA, venta.getTotal(), 0.001, "Solo se aplica el descuento del bono.");
        assertTrue(ana.getPuntosFidelidad() >= puntosAntes, "Los puntos de Ana no se gastan en la compra con bono.");
    }

    @Test
    void bonoAjeno()
    {
        BonoDescuento bonoDeOtro = new BonoDescuento(0.25, "Copa Recreativa");

        assertThrows(ReglaNegocioException.class,
                () -> club.registrarVentaConBono(ana, unaRaqueta(), Rubro.TIENDA, null, bonoDeOtro, 0),
                "Solo se pueden usar bonos propios.");
        assertFalse(bonoDeOtro.estaUsado(), "El bono no debe quedar usado.");
        assertEquals(10, raqueta.getCantidad(), "No se registra la compra.");
    }

    @Test
    void dosBonos() throws Exception
    {
        BonoDescuento bono25 = new BonoDescuento(0.25, "Copa 1");
        BonoDescuento bono10 = new BonoDescuento(0.10, "Copa 2");
        ana.agregarBono(bono25);
        ana.agregarBono(bono10);

        club.registrarVentaConBono(ana, unaRaqueta(), Rubro.TIENDA, null, bono25, 0);

        assertTrue(bono25.estaUsado(), "El bono usado queda marcado.");
        assertFalse(bono10.estaUsado(), "El otro bono sigue disponible.");
    }

    @Test
    void bonoPersiste() throws Exception
    {
        ana.agregarBono(new BonoDescuento(0.25, "Copa Recreativa"));
        club.guardarDatos();

        ClubSeneca recargado = Escenario.recargar(carpeta);
        Socio anaRecargada = (Socio) recargado.iniciarSesion("ana", "clave");

        assertEquals(1, anaRecargada.getBonos().size(), "El bono debe seguir en la cuenta.");
        assertEquals(0.25, anaRecargada.getBonos().get(0).getPorcentaje(), 0.0001, "Se conserva el porcentaje.");
        assertFalse(anaRecargada.getBonos().get(0).estaUsado(), "Sigue sin usar.");
    }
}
