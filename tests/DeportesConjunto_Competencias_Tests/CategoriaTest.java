package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;

public class CategoriaTest
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
    void testRangosDeEdad()
    {
        assertEquals(0, Categoria.SUB13.getEdadMinima(), "La edad mínima de SUB13 no es la esperada.");
        assertEquals(12, Categoria.SUB13.getEdadMaxima(), "La edad máxima de SUB13 no es la esperada.");
        assertEquals(13, Categoria.SUB15.getEdadMinima(), "La edad mínima de SUB15 no es la esperada.");
        assertEquals(14, Categoria.SUB15.getEdadMaxima(), "La edad máxima de SUB15 no es la esperada.");
        assertEquals(15, Categoria.SUB17.getEdadMinima(), "La edad mínima de SUB17 no es la esperada.");
        assertEquals(16, Categoria.SUB17.getEdadMaxima(), "La edad máxima de SUB17 no es la esperada.");
        assertEquals(17, Categoria.MAYORES.getEdadMinima(), "La edad mínima de MAYORES no es la esperada.");
        assertEquals(150, Categoria.MAYORES.getEdadMaxima(), "La edad máxima de MAYORES no es la esperada.");
    }

    @Test
    void testAdmiteEdadDentroDelRango()
    {
        assertTrue(Categoria.SUB13.admiteEdad(8), "SUB13 admite 8 años.");
        assertTrue(Categoria.SUB15.admiteEdad(14), "SUB15 admite 14 años.");
        assertTrue(Categoria.SUB17.admiteEdad(16), "SUB17 admite 16 años.");
        assertTrue(Categoria.MAYORES.admiteEdad(30), "MAYORES admite 30 años.");
    }

    @Test
    void testAdmiteEdadEnLosLimites()
    {
        assertTrue(Categoria.SUB13.admiteEdad(0), "SUB13 admite 0 años.");
        assertTrue(Categoria.SUB13.admiteEdad(12), "SUB13 admite 12 años.");
        assertFalse(Categoria.SUB13.admiteEdad(13), "SUB13 no admite 13 años.");
        assertFalse(Categoria.SUB15.admiteEdad(12), "SUB15 no admite 12 años.");
        assertTrue(Categoria.SUB15.admiteEdad(13), "SUB15 admite 13 años.");
        assertFalse(Categoria.SUB17.admiteEdad(14), "SUB17 no admite 14 años.");
        assertFalse(Categoria.MAYORES.admiteEdad(16), "MAYORES no admite 16 años.");
        assertTrue(Categoria.MAYORES.admiteEdad(17), "MAYORES admite 17 años.");
        assertTrue(Categoria.MAYORES.admiteEdad(150), "MAYORES admite 150 años.");
        assertFalse(Categoria.MAYORES.admiteEdad(151), "MAYORES no admite 151 años.");
    }

    @Test
    void testAdmiteEdadNegativa()
    {
        assertFalse(Categoria.SUB13.admiteEdad(-1), "Ninguna categoría admite edades negativas.");
    }
}
