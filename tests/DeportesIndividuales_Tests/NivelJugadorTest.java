package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.NivelJugador;

public class NivelJugadorTest
{
    @BeforeEach
    void setUp() throws Exception
    {
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testValores()
    {
        assertEquals(3, NivelJugador.values().length, "Debe haber tres niveles de jugador.");
        assertEquals(NivelJugador.PRINCIPIANTE, NivelJugador.values()[0], "El primer nivel debe ser principiante.");
        assertEquals(NivelJugador.INTERMEDIO, NivelJugador.values()[1], "El segundo nivel debe ser intermedio.");
        assertEquals(NivelJugador.AVANZADO, NivelJugador.values()[2], "El tercer nivel debe ser avanzado.");
    }

    @Test
    void testEsCompatibleConElMismoNivel()
    {
        assertTrue(NivelJugador.PRINCIPIANTE.esCompatibleCon(NivelJugador.PRINCIPIANTE), "Un nivel es compatible consigo mismo.");
        assertTrue(NivelJugador.INTERMEDIO.esCompatibleCon(NivelJugador.INTERMEDIO), "Un nivel es compatible consigo mismo.");
        assertTrue(NivelJugador.AVANZADO.esCompatibleCon(NivelJugador.AVANZADO), "Un nivel es compatible consigo mismo.");
    }

    @Test
    void testEsCompatibleConNivelesContiguos()
    {
        assertTrue(NivelJugador.PRINCIPIANTE.esCompatibleCon(NivelJugador.INTERMEDIO), "Principiante e intermedio son compatibles.");
        assertTrue(NivelJugador.INTERMEDIO.esCompatibleCon(NivelJugador.PRINCIPIANTE), "Intermedio y principiante son compatibles.");
        assertTrue(NivelJugador.INTERMEDIO.esCompatibleCon(NivelJugador.AVANZADO), "Intermedio y avanzado son compatibles.");
        assertTrue(NivelJugador.AVANZADO.esCompatibleCon(NivelJugador.INTERMEDIO), "Avanzado e intermedio son compatibles.");
    }

    @Test
    void testNoEsCompatibleConNivelesLejanos()
    {
        assertFalse(NivelJugador.PRINCIPIANTE.esCompatibleCon(NivelJugador.AVANZADO), "Principiante y avanzado no son compatibles.");
        assertFalse(NivelJugador.AVANZADO.esCompatibleCon(NivelJugador.PRINCIPIANTE), "Avanzado y principiante no son compatibles.");
    }

    @Test
    void testValueOf()
    {
        assertEquals(NivelJugador.INTERMEDIO, NivelJugador.valueOf("INTERMEDIO"), "El nivel obtenido por nombre no es el esperado.");
        assertThrows(IllegalArgumentException.class, () -> NivelJugador.valueOf("EXPERTO"), "Un nivel inexistente debe lanzar IllegalArgumentException.");
    }
}
