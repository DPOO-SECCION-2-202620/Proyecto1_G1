package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Voleibol;

public class VoleibolTest
{
    private Voleibol voleibol;

    @BeforeEach
    void setUp() throws Exception
    {
        voleibol = new Voleibol();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsDeporteConjunto()
    {
        assertTrue(voleibol instanceof DeporteConjunto, "El voleibol es un deporte de conjunto.");
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Voleibol", voleibol.getNombre(), "El nombre del deporte no es el esperado.");
    }

    @Test
    void testGetMinimoJugadores()
    {
        assertEquals(6, voleibol.getMinimoJugadores(), "El mínimo de jugadores en voleibol es 6.");
    }

    @Test
    void testAdmiteEmpate()
    {
        assertFalse(voleibol.admiteEmpate(), "El voleibol no admite empate.");
    }

    @Test
    void testCalcularPuntosVictoria()
    {
        assertEquals(1, voleibol.calcularPuntos(3, 1), "Una victoria vale 1 punto.");
    }

    @Test
    void testCalcularPuntosDerrota()
    {
        assertEquals(0, voleibol.calcularPuntos(0, 3), "Una derrota vale 0 puntos.");
    }

    @Test
    void testCalcularPuntosEmpate()
    {
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> voleibol.calcularPuntos(2, 2), "Debe lanzarse IllegalArgumentException si hay empate.");

        assertEquals("En voleibol no puede haber empate", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
