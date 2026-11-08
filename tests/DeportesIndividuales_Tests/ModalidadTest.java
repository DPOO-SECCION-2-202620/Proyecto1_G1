package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.Modalidad;

public class ModalidadTest
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
        assertEquals(2, Modalidad.values().length, "Debe haber dos modalidades.");
        assertEquals(Modalidad.SENCILLOS, Modalidad.values()[0], "La primera modalidad debe ser sencillos.");
        assertEquals(Modalidad.DOBLES, Modalidad.values()[1], "La segunda modalidad debe ser dobles.");
    }

    @Test
    void testValueOf()
    {
        assertEquals(Modalidad.DOBLES, Modalidad.valueOf("DOBLES"), "La modalidad obtenida por nombre no es la esperada.");
        assertThrows(IllegalArgumentException.class, () -> Modalidad.valueOf("TRIOS"), "Una modalidad inexistente debe lanzar IllegalArgumentException.");
    }
}
