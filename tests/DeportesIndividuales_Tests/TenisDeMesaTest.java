package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.TenisDeMesa;
import Instalaciones.MesaTenisDeMesa;

public class TenisDeMesaTest
{
    private TenisDeMesa tenisDeMesa;

    @BeforeEach
    void setUp() throws Exception
    {
        tenisDeMesa = new TenisDeMesa(4, "CualquierNombre");
        new MesaTenisDeMesa("Mesa 1", true, 1, tenisDeMesa);
        new MesaTenisDeMesa("Mesa 2", false, 2, tenisDeMesa);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsDisciplinaIndividual()
    {
        assertTrue(tenisDeMesa instanceof DisciplinaIndividual, "El tenis de mesa es una disciplina individual.");
    }

    @Test
    void testNombreFijo()
    {
        assertEquals("TenisDeMesa", tenisDeMesa.getNombre(), "El nombre debe ser siempre TenisDeMesa.");
    }

    @Test
    void testMaxInstalacionesSimultaneas()
    {
        assertEquals(4, tenisDeMesa.getMaxInstalacionesSimultaneas(), "El máximo de instalaciones simultáneas no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        TenisDeMesa restaurado = TenisDeMesa.fromJSON(tenisDeMesa.toJSON());

        assertEquals("TenisDeMesa", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(4, restaurado.getMaxInstalacionesSimultaneas(), "El máximo restaurado no es el esperado.");
        assertEquals(2, restaurado.getInstalaciones().size(), "Deben restaurarse las dos mesas.");
        assertTrue(restaurado.getInstalaciones().get(0).isTechado(), "La primera mesa restaurada debe estar techada.");
    }
}
