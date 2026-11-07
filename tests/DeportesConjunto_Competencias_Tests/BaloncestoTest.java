package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.DeporteConjunto;

public class BaloncestoTest
{
    private Baloncesto baloncesto;

    @BeforeEach
    void setUp() throws Exception
    {
        baloncesto = new Baloncesto();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsDeporteConjunto()
    {
        assertTrue(baloncesto instanceof DeporteConjunto, "El baloncesto es un deporte de conjunto.");
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Baloncesto", baloncesto.getNombre(), "El nombre del deporte no es el esperado.");
    }

    @Test
    void testGetMinimoJugadores()
    {
        assertEquals(5, baloncesto.getMinimoJugadores(), "El mínimo de jugadores en baloncesto es 5.");
    }

    @Test
    void testAdmiteEmpate()
    {
        assertFalse(baloncesto.admiteEmpate(), "El baloncesto no admite empate.");
    }

    @Test
    void testCalcularPuntosVictoria()
    {
        assertEquals(1, baloncesto.calcularPuntos(80, 75), "Una victoria vale 1 punto.");
    }

    @Test
    void testCalcularPuntosDerrota()
    {
        assertEquals(0, baloncesto.calcularPuntos(70, 71), "Una derrota vale 0 puntos.");
    }

    @Test
    void testCalcularPuntosEmpate()
    {
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> baloncesto.calcularPuntos(60, 60), "Debe lanzarse IllegalArgumentException si hay empate.");

        assertEquals("En baloncesto no puede haber empate", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
