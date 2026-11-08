package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Futbol;

public class FutbolTest
{
    private Futbol futbol;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsDeporteConjunto()
    {
        assertTrue(futbol instanceof DeporteConjunto, "El fútbol es un deporte de conjunto.");
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Futbol", futbol.getNombre(), "El nombre del deporte no es el esperado.");
    }

    @Test
    void testGetMinimoJugadores()
    {
        assertEquals(7, futbol.getMinimoJugadores(), "El mínimo de jugadores en fútbol es 7.");
    }

    @Test
    void testAdmiteEmpate()
    {
        assertTrue(futbol.admiteEmpate(), "El fútbol admite empate.");
    }

    @Test
    void testCalcularPuntosVictoria()
    {
        assertEquals(3, futbol.calcularPuntos(2, 1), "Una victoria vale 3 puntos.");
    }

    @Test
    void testCalcularPuntosEmpate()
    {
        assertEquals(1, futbol.calcularPuntos(1, 1), "Un empate vale 1 punto.");
        assertEquals(1, futbol.calcularPuntos(0, 0), "Un empate sin goles vale 1 punto.");
    }

    @Test
    void testCalcularPuntosDerrota()
    {
        assertEquals(0, futbol.calcularPuntos(0, 3), "Una derrota vale 0 puntos.");
    }
}
