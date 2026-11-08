package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Padel;
import Instalaciones.CanchaPadel;

public class PadelTest
{
    private Padel padel;

    @BeforeEach
    void setUp() throws Exception
    {
        padel = new Padel(2, "CualquierNombre");
        new CanchaPadel("Padel 1", true, 1, padel);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsDisciplinaIndividual()
    {
        assertTrue(padel instanceof DisciplinaIndividual, "El pádel es una disciplina individual.");
    }

    @Test
    void testNombreFijo()
    {
        assertEquals("Padel", padel.getNombre(), "El nombre del pádel debe ser siempre Padel.");
    }

    @Test
    void testMaxInstalacionesSimultaneas()
    {
        assertEquals(2, padel.getMaxInstalacionesSimultaneas(), "El máximo de instalaciones simultáneas no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        Padel restaurado = Padel.fromJSON(padel.toJSON());

        assertEquals("Padel", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(2, restaurado.getMaxInstalacionesSimultaneas(), "El máximo restaurado no es el esperado.");
        assertEquals(1, restaurado.getInstalaciones().size(), "Debe restaurarse la cancha.");
        assertEquals("Padel 1", restaurado.getInstalaciones().get(0).getNombre(), "El nombre de la cancha restaurada no es el esperado.");
    }
}
