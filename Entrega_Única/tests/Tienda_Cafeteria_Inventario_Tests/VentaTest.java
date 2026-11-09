package Tienda_Cafeteria_Inventario_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.CodigoDescuento;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class VentaTest
{
    private Socio socio;
    private Empleado empleado;
    private Articulo pelotas;
    private Articulo gorra;

    private List<LineaVenta> lineas;
    private CodigoDescuento codigoDelEmpleado;
    private CodigoDescuento codigoAjeno;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, Producto> mapaProductos;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1");
        empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");

        pelotas = new Articulo("Pelotas", "Pelotas", 10000, 50);
        gorra = new Articulo("Gorra", "Accesorios", 30000, 20);

        lineas = new ArrayList<LineaVenta>();
        lineas.add(new LineaVenta(2, pelotas));

        codigoDelEmpleado = empleado.getCodigoDescuento();
        codigoAjeno = new CodigoDescuento("OTRO");

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", socio);
        mapaUsuarios.put("lmora", empleado);

        mapaProductos = new HashMap<String, Producto>();
        mapaProductos.put("Pelotas", pelotas);
        mapaProductos.put("Gorra", gorra);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private double totalConImpuesto(double base, double tasa)
    {
        return base * (1 + tasa);
    }

    @Test
    void testVentaSimple()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19), venta.getTotal(), 0.001, "El total debe sumar el IVA.");
        assertEquals(0, venta.getPropina(), 0.001, "La propina no es la esperada.");
        assertEquals(0, venta.getPuntosRedimidos(), "No debe haber puntos redimidos.");
        assertEquals(Rubro.TIENDA, venta.getRubro(), "El rubro no es el esperado.");
        assertEquals(0.19, venta.getTasaImpuestos(), 0.0001, "La tasa de impuestos no es la esperada.");
        assertEquals(LocalDate.now(), venta.getFecha(), "La fecha de la venta debe ser la de hoy.");
    }

    @Test
    void testVentaConVariasLineas()
    {
        lineas.add(new LineaVenta(1, gorra));

        Venta venta = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);

        assertEquals(totalConImpuesto(50000, 0.19), venta.getTotal(), 0.001, "El total debe sumar todas las líneas.");
    }

    @Test
    void testVentaSinLineas()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, null, socio, new ArrayList<LineaVenta>(), 0.19);

        assertEquals(0, venta.getTotal(), 0.001, "Una venta sin líneas debe costar 0.");
        assertEquals(0, venta.getPuntosGenerados(), "Una venta sin líneas no genera puntos.");
    }

    @Test
    void testVentaIgnoraLineasNulas()
    {
        lineas.add(null);
        lineas.add(new LineaVenta(3, null));

        Venta venta = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19), venta.getTotal(), 0.001, "Las líneas nulas no deben afectar el total.");
    }

    @Test
    void testVentaConPropina()
    {
        Venta venta = new Venta(1500, 0, Rubro.CAFETERIA, null, socio, lineas, 0.08);

        assertEquals(totalConImpuesto(20000, 0.08) + 1500, venta.getTotal(), 0.001, "La propina debe sumarse al total.");
        assertEquals(1500, venta.getPropina(), 0.001, "La propina no es la esperada.");
    }

    @Test
    void testVentaConCodigoDeEmpleadoDueno()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, codigoDelEmpleado, empleado, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19) * 0.85, venta.getTotal(), 0.001, "El dueño del código recibe 15% de descuento.");
    }

    @Test
    void testVentaConCodigoCompartido()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, codigoDelEmpleado, socio, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19) * 0.92, venta.getTotal(), 0.001, "Un socio con el código de un empleado recibe 8% de descuento.");
    }

    @Test
    void testVentaEmpleadoConCodigoAjeno()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, codigoAjeno, empleado, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19) * 0.92, venta.getTotal(), 0.001, "Un empleado con un código que no es suyo recibe 8% de descuento.");
    }

    @Test
    void testVentaDescuentoNoAfectaLaPropina()
    {
        Venta venta = new Venta(1000, 0, Rubro.TIENDA, codigoAjeno, socio, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19) * 0.92 + 1000, venta.getTotal(), 0.001, "El descuento se aplica antes de sumar la propina.");
    }

    @Test
    void testVentaRedimiendoPuntosParcialmente()
    {
        Venta venta = new Venta(0, 1000, Rubro.TIENDA, null, socio, lineas, 0.19);

        assertEquals(totalConImpuesto(20000, 0.19) - 1000, venta.getTotal(), 0.001, "Cada punto redimido descuenta una unidad del total.");
        assertEquals(1000, venta.getPuntosRedimidos(), "Los puntos redimidos no son los esperados.");
    }

    @Test
    void testVentaRedimiendoPuntosSuficientesParaTodo()
    {
        Venta venta = new Venta(0, 50000, Rubro.TIENDA, null, socio, lineas, 0.19);

        int puntosNecesarios = (int) Math.ceil(totalConImpuesto(20000, 0.19));

        assertEquals(0, venta.getTotal(), 0.001, "Con puntos suficientes el total debe ser 0.");
        assertEquals(puntosNecesarios, venta.getPuntosRedimidos(), "Solo deben redimirse los puntos necesarios.");
        assertEquals(0, venta.getPuntosGenerados(), "Una venta pagada con puntos no genera puntos.");
    }

    @Test
    void testVentaRedimiendoExactamenteLosPuntosNecesarios()
    {
        List<LineaVenta> unaLinea = new ArrayList<LineaVenta>();
        unaLinea.add(new LineaVenta(1, new Articulo("Barato", "Varios", 1000, 5)));

        Venta venta = new Venta(0, 1000, Rubro.TIENDA, null, socio, unaLinea, 0);

        assertEquals(0, venta.getTotal(), 0.001, "Con los puntos exactos el total debe ser 0.");
        assertEquals(1000, venta.getPuntosRedimidos(), "Los puntos redimidos no son los esperados.");
    }

    @Test
    void testPuntosGenerados()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);

        long esperados = Math.round(venta.getTotal() * 0.02);

        assertEquals(esperados, venta.getPuntosGenerados(), "Los puntos generados deben ser el 2% del total redondeado.");
        assertEquals(476, venta.getPuntosGenerados(), "Los puntos generados por 23800 deben ser 476.");
    }

    @Test
    void testConstructorExplicito()
    {
        LocalDate fecha = LocalDate.of(2025, 5, 20);

        Venta venta = new Venta(fecha, 500, 12345.5, 10, 247, Rubro.CAFETERIA, socio, lineas, 0.08);

        assertEquals(fecha, venta.getFecha(), "La fecha no es la esperada.");
        assertEquals(500, venta.getPropina(), 0.001, "La propina no es la esperada.");
        assertEquals(12345.5, venta.getTotal(), 0.001, "El total no debe recalcularse.");
        assertEquals(10, venta.getPuntosRedimidos(), "Los puntos redimidos no son los esperados.");
        assertEquals(247, venta.getPuntosGenerados(), "Los puntos generados no son los esperados.");
        assertEquals(Rubro.CAFETERIA, venta.getRubro(), "El rubro no es el esperado.");
        assertEquals(0.08, venta.getTasaImpuestos(), 0.0001, "La tasa de impuestos no es la esperada.");
    }

    @Test
    void testToJSON()
    {
        Venta venta = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);

        JSONObject json = venta.toJSON();

        assertEquals(LocalDate.now().toString(), json.getString("fecha"), "La fecha en el JSON no es la esperada.");
        assertEquals(0, json.getDouble("propina"), 0.001, "La propina en el JSON no es la esperada.");
        assertEquals(venta.getTotal(), json.getDouble("total"), 0.001, "El total en el JSON no es el esperado.");
        assertEquals(0, json.getInt("puntosRedimidos"), "Los puntos redimidos en el JSON no son los esperados.");
        assertEquals(476, json.getInt("puntosGenerados"), "Los puntos generados en el JSON no son los esperados.");
        assertEquals("TIENDA", json.getString("rubro"), "El rubro en el JSON no es el esperado.");
        assertEquals(0.19, json.getDouble("tasaImpuestos"), 0.0001, "La tasa en el JSON no es la esperada.");
        assertEquals("ana", json.getString("comprador"), "El comprador en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("lineas").length(), "El JSON debe tener una línea.");
    }

    @Test
    void testFromJSON()
    {
        Venta original = new Venta(750, 0, Rubro.CAFETERIA, null, socio, lineas, 0.08);

        Venta restaurada = Venta.fromJSON(original.toJSON(), mapaUsuarios, mapaProductos);

        assertEquals(original.getFecha(), restaurada.getFecha(), "La fecha restaurada no es la esperada.");
        assertEquals(750, restaurada.getPropina(), 0.001, "La propina restaurada no es la esperada.");
        assertEquals(original.getTotal(), restaurada.getTotal(), 0.001, "El total restaurado no es el esperado.");
        assertEquals(original.getPuntosGenerados(), restaurada.getPuntosGenerados(), "Los puntos generados restaurados no son los esperados.");
        assertEquals(Rubro.CAFETERIA, restaurada.getRubro(), "El rubro restaurado no es el esperado.");
        assertEquals(0.08, restaurada.getTasaImpuestos(), 0.0001, "La tasa restaurada no es la esperada.");
    }

    @Test
    void testFromJSONCompradorDesconocido()
    {
        Venta original = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);
        mapaUsuarios.clear();

        Venta restaurada = Venta.fromJSON(original.toJSON(), mapaUsuarios, mapaProductos);

        assertEquals(original.getTotal(), restaurada.getTotal(), 0.001, "El total restaurado no es el esperado.");
    }

    @Test
    void testFromJSONProductoDesconocido()
    {
        Venta original = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);
        mapaProductos.clear();

        assertThrows(IllegalArgumentException.class, () -> Venta.fromJSON(original.toJSON(), mapaUsuarios, mapaProductos), "Debe lanzarse IllegalArgumentException si un producto no existe.");
    }

    @Test
    void testFromJSONSinLineas()
    {
        Venta original = new Venta(0, 0, Rubro.TIENDA, null, socio, lineas, 0.19);
        JSONObject json = original.toJSON();
        json.remove("lineas");

        Venta restaurada = Venta.fromJSON(json, mapaUsuarios, mapaProductos);

        assertEquals(original.getTotal(), restaurada.getTotal(), 0.001, "El total restaurado no es el esperado.");
    }
}
