package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

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
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.PremioEntregado;
import Usuarios.Socio;

public class PremiacionRecreativaTest
{
    private PremiacionRecreativa premiacion;

    private Socio ana;
    private Socio pedro;
    private Socio luis;

    private InscripcionTorneoIndividual sencillosAna;
    private InscripcionTorneoIndividual parejaAnaPedro;
    private InscripcionTorneoIndividual sencillosLuis;
    private InscripcionTorneoEquipo inscripcionEquipo;

    @BeforeEach
    void setUp() throws Exception
    {
        premiacion = new PremiacionRecreativa();

        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");
        luis = new Socio(30, "Luis Mora", "lmora", "3");

        sencillosAna = new InscripcionTorneoIndividual(ana, null);
        parejaAnaPedro = new InscripcionTorneoIndividual(ana, pedro);
        sencillosLuis = new InscripcionTorneoIndividual(luis, null);

        Equipo equipo = new Equipo("Cerros Basket", 8, new Baloncesto(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Alero", 7);
        equipo.agregarJugador(pedro, "Pivot", 5);
        inscripcionEquipo = new InscripcionTorneoEquipo(equipo);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsPremiacion()
    {
        assertTrue(premiacion instanceof Premiacion, "La premiación recreativa es una premiación.");
    }

    @Test
    void testConstantes()
    {
        assertEquals(0.25, PremiacionRecreativa.BONO_PRIMERO, 0.0001, "El bono del primer puesto no es el esperado.");
        assertEquals(0.1, PremiacionRecreativa.BONO_SEGUNDO, 0.0001, "El bono del segundo puesto no es el esperado.");
    }

    @Test
    void testGetTarifaEsGratis()
    {
        assertEquals(0, premiacion.getTarifa(sencillosAna), 0.001, "Un torneo recreativo no cobra tarifa a un jugador.");
        assertEquals(0, premiacion.getTarifa(parejaAnaPedro), 0.001, "Un torneo recreativo no cobra tarifa a una pareja.");
        assertEquals(0, premiacion.getTarifa(inscripcionEquipo), 0.001, "Un torneo recreativo no cobra tarifa a un equipo.");
    }

    @Test
    void testEntregarPremiosSencillos()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosAna, sencillosLuis, 5000, "Copa");

        assertEquals(2, premios.size(), "Debe haber dos premios.");
        assertEquals(1, premios.get(0).getPuesto(), "El primer premio debe ser del primer puesto.");
        assertSame(ana, premios.get(0).getJugador(), "El primer premio debe ser de Ana.");
        assertEquals(0, premios.get(0).getMontoEfectivo(), 0.001, "El premio recreativo no es en efectivo.");
        assertNotNull(premios.get(0).getBono(), "El premio recreativo debe tener un bono.");
        assertEquals(2, premios.get(1).getPuesto(), "El segundo premio debe ser del segundo puesto.");
        assertSame(luis, premios.get(1).getJugador(), "El segundo premio debe ser de Luis.");
        assertNotNull(premios.get(1).getBono(), "El premio recreativo debe tener un bono.");
    }

    @Test
    void testEntregarPremiosAgregaElBonoAlJugador()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosAna, sencillosLuis, 5000, "Copa");

        assertEquals(1, ana.getBonos().size(), "Ana debe recibir un bono.");
        assertSame(premios.get(0).getBono(), ana.getBonos().get(0), "El bono del jugador debe ser el del premio.");
        assertEquals(1, luis.getBonos().size(), "Luis debe recibir un bono.");
        assertSame(premios.get(1).getBono(), luis.getBonos().get(0), "El bono del jugador debe ser el del premio.");
    }

    @Test
    void testEntregarPremiosAUnaPareja()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(parejaAnaPedro, sencillosLuis, 5000, "Copa");

        assertEquals(3, premios.size(), "Debe haber un premio por jugador.");
        assertSame(ana, premios.get(0).getJugador(), "El primer premio debe ser de Ana.");
        assertSame(pedro, premios.get(1).getJugador(), "El segundo premio debe ser de Pedro.");
        assertEquals(1, premios.get(0).getPuesto(), "Los dos primeros premios son del primer puesto.");
        assertEquals(1, premios.get(1).getPuesto(), "Los dos primeros premios son del primer puesto.");
        assertEquals(1, ana.getBonos().size(), "Ana debe recibir un bono.");
        assertEquals(1, pedro.getBonos().size(), "Pedro debe recibir un bono.");
        assertNotSame(premios.get(0).getBono(), premios.get(1).getBono(), "Cada jugador recibe su propio bono.");
    }

    @Test
    void testEntregarPremiosAUnEquipo()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(inscripcionEquipo, sencillosLuis, 5000, "Copa");

        assertEquals(3, premios.size(), "Debe haber un premio por jugador del equipo y uno del segundo puesto.");
        assertEquals(1, ana.getBonos().size(), "Ana debe recibir un bono.");
        assertEquals(1, pedro.getBonos().size(), "Pedro debe recibir un bono.");
        assertEquals(1, luis.getBonos().size(), "Luis debe recibir un bono.");
    }

    @Test
    void testEntregarPremiosAcumulaBonos()
    {
        premiacion.entregarPremios(sencillosAna, sencillosLuis, 0, "Copa 1");
        premiacion.entregarPremios(sencillosAna, sencillosLuis, 0, "Copa 2");

        assertEquals(2, ana.getBonos().size(), "Los bonos de distintos torneos deben acumularse.");
    }

    @Test
    void testEntregarPremiosIgnoraLoRecaudado()
    {
        List<PremioEntregado> premios = premiacion.entregarPremios(sencillosAna, sencillosLuis, 999999, "Copa");

        assertEquals(0, premios.get(0).getMontoEfectivo(), 0.001, "Lo recaudado no afecta los premios recreativos.");
    }

    @Test
    void testEntregarPremiosSinSegundoLugarFalla()
    {
        assertThrows(NullPointerException.class, () -> premiacion.entregarPremios(sencillosAna, null, 5000, "Copa"), "Con el segundo lugar nulo la premiación recreativa falla, a diferencia de la competitiva.");
    }
}
