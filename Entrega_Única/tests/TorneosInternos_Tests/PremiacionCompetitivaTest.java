package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.Premiacion;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremioEntregado;
import Usuarios.Empleado;
import Usuarios.Socio;

public class PremiacionCompetitivaTest
{
    private PremiacionCompetitiva premiacion;

    private Socio ana;
    private Socio pedro;
    private Socio luis;
    private Empleado empleado;

    private InscripcionTorneoIndividual sencillosAna;
    private InscripcionTorneoIndividual sencillosEmpleado;
    private InscripcionTorneoIndividual parejaAnaPedro;
    private InscripcionTorneoIndividual parejaLuisEmpleado;
    private InscripcionTorneoEquipo inscripcionEquipo;

    @BeforeEach
    void setUp() throws Exception
    {
        premiacion = new PremiacionCompetitiva(10000);

        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");
        luis = new Socio(30, "Luis Mora", "lmora", "3");
        empleado = new Empleado(35, "Sara Paz", "spaz", "4", "E01");

        sencillosAna = new InscripcionTorneoIndividual(ana, null);
        sencillosEmpleado = new InscripcionTorneoIndividual(empleado, null);
        parejaAnaPedro = new InscripcionTorneoIndividual(ana, pedro);
        parejaLuisEmpleado = new InscripcionTorneoIndividual(luis, empleado);

        Equipo equipo = new Equipo("Cerros Basket", 8, new Baloncesto(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Alero", 7);
        equipo.agregarJugador(pedro, "Pivot", 5);
        equipo.agregarJugador(luis, "Base", 3);
        inscripcionEquipo = new InscripcionTorneoEquipo(equipo);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsPremiacion()
    {
        assertTrue(premiacion instanceof Premiacion, "La premiación competitiva es una premiación.");
    }

    @Test
    void testConstantes()
    {
        assertEquals(0.5, PremiacionCompetitiva.PORC_PRIMERO, 0.0001, "El porcentaje del primer puesto no es el esperado.");
        assertEquals(0.2, PremiacionCompetitiva.PORC_SEGUNDO, 0.0001, "El porcentaje del segundo puesto no es el esperado.");
    }

    @Test
    void testGetTarifaBase()
    {
        assertEquals(10000, premiacion.getTarifaBase(), 0.001, "La tarifa base no es la esperada.");
    }

    @Test
    void testGetTarifaEquipo()
    {
        assertEquals(10000, premiacion.getTarifa(inscripcionEquipo), 0.001, "Un equipo paga una sola tarifa.");
    }

    @Test
    void testGetTarifaSencillos()
    {
        assertEquals(10000, premiacion.getTarifa(sencillosAna), 0.001, "Un jugador paga una tarifa.");
    }

    @Test
    void testGetTarifaPareja()
    {
        assertEquals(20000, premiacion.getTarifa(parejaAnaPedro), 0.001, "Una pareja paga una tarifa por jugador.");
    }

    @Test
    void testGetTarifaEmpleadoNoPaga()
    {
        assertEquals(0, premiacion.getTarifa(sencillosEmpleado), 0.001, "Un empleado no paga tarifa.");
        assertEquals(10000, premiacion.getTarifa(parejaLuisEmpleado), 0.001, "En una pareja solo paga el jugador que no es empleado.");
    }

    @Test
    void testEntregarPremiosSencillos()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosAna, new InscripcionTorneoIndividual(luis, null), 1000, "Copa");

        assertEquals(2, premios.size(), "Debe haber dos premios.");
        assertEquals(1, premios.get(0).getPuesto(), "El primer premio debe ser del primer puesto.");
        assertSame(ana, premios.get(0).getJugador(), "El primer premio debe ser de Ana.");
        assertEquals(500, premios.get(0).getMontoEfectivo(), 0.001, "El primer puesto recibe 50% de lo recaudado.");
        assertNull(premios.get(0).getBono(), "El premio competitivo no tiene bono.");
        assertEquals(2, premios.get(1).getPuesto(), "El segundo premio debe ser del segundo puesto.");
        assertSame(luis, premios.get(1).getJugador(), "El segundo premio debe ser de Luis.");
        assertEquals(200, premios.get(1).getMontoEfectivo(), 0.001, "El segundo puesto recibe 20% de lo recaudado.");
    }

    @Test
    void testEntregarPremiosPareja()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(parejaAnaPedro, sencillosEmpleado, 1000, "Copa");

        assertEquals(3, premios.size(), "Debe haber un premio por jugador.");
        assertSame(ana, premios.get(0).getJugador(), "El primer premio debe ser de Ana.");
        assertEquals(250, premios.get(0).getMontoEfectivo(), 0.001, "El premio del primer puesto se reparte entre la pareja.");
        assertSame(pedro, premios.get(1).getJugador(), "El segundo premio debe ser de Pedro.");
        assertEquals(250, premios.get(1).getMontoEfectivo(), 0.001, "El premio del primer puesto se reparte entre la pareja.");
    }

    @Test
    void testEntregarPremiosEquipo()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(inscripcionEquipo, sencillosAna, 900, "Copa");

        assertEquals(4, premios.size(), "Debe haber tres premios del equipo y uno del segundo puesto.");
        assertEquals(150, premios.get(0).getMontoEfectivo(), 0.001, "El premio del equipo se reparte entre sus tres jugadores.");
        assertEquals(150, premios.get(2).getMontoEfectivo(), 0.001, "El premio del equipo se reparte entre sus tres jugadores.");
        assertEquals(180, premios.get(3).getMontoEfectivo(), 0.001, "El segundo puesto recibe 20% de lo recaudado.");
    }

    @Test
    void testEntregarPremiosEmpleadoNoRecibeEfectivo()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosEmpleado, parejaLuisEmpleado, 1000, "Copa");

        assertEquals(3, premios.size(), "Debe haber un premio por jugador.");
        assertSame(empleado, premios.get(0).getJugador(), "El primer premio debe ser del empleado.");
        assertEquals(0, premios.get(0).getMontoEfectivo(), 0.001, "El empleado no recibe efectivo.");
        assertSame(luis, premios.get(1).getJugador(), "El segundo premio debe ser de Luis.");
        assertEquals(100, premios.get(1).getMontoEfectivo(), 0.001, "Luis recibe la mitad del premio del segundo puesto.");
        assertSame(empleado, premios.get(2).getJugador(), "El tercer premio debe ser del empleado.");
        assertEquals(0, premios.get(2).getMontoEfectivo(), 0.001, "El empleado no recibe efectivo.");
    }

    @Test
    void testEntregarPremiosSinSegundoLugar()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosAna, null, 1000, "Copa");

        assertEquals(1, premios.size(), "Sin segundo lugar solo hay un premio.");
        assertEquals(500, premios.get(0).getMontoEfectivo(), 0.001, "El primer puesto recibe 50% de lo recaudado.");
    }

    @Test
    void testEntregarPremiosSinGanadores()
    {
        assertEquals(0, premiacion.entregarPremios(null, null, 1000, "Copa").size(), "Sin ganadores no hay premios.");
    }

    @Test
    void testEntregarPremiosSinRecaudo()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosAna, new InscripcionTorneoIndividual(luis, null), 0, "Copa");

        assertEquals(0, premios.get(0).getMontoEfectivo(), 0.001, "Sin recaudo el premio es cero.");
        assertEquals(0, premios.get(1).getMontoEfectivo(), 0.001, "Sin recaudo el premio es cero.");
    }
}
