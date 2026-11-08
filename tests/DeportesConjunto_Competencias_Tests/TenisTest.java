package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Tenis;
import Instalaciones.CanchaTenis;

public class TenisTest
{
    private Tenis tenis;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(3, "CualquierNombre");
        new CanchaTenis("Tenis 1", true, 1, tenis);
        new CanchaTenis("Tenis 2", false, 2, tenis);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsDisciplinaIndividual()
    {
        assertTrue(tenis instanceof DisciplinaIndividual, "El tenis es una disciplina individual.");
    }

    @Test
    void testNombreFijo()
    {
        assertEquals("Tenis", tenis.getNombre(), "El nombre del tenis debe ser siempre Tenis.");
    }

    @Test
    void testMaxInstalacionesSimultaneas()
    {
        assertEquals(3, tenis.getMaxInstalacionesSimultaneas(), "El máximo de instalaciones simultáneas no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        Tenis restaurado = Tenis.fromJSON(tenis.toJSON());

        assertEquals("Tenis", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(3, restaurado.getMaxInstalacionesSimultaneas(), "El máximo restaurado no es el esperado.");
        assertEquals(2, restaurado.getInstalaciones().size(), "Deben restaurarse las dos canchas.");
        assertEquals("Tenis 2", restaurado.getInstalaciones().get(1).getNombre(), "El nombre de la segunda cancha restaurada no es el esperado.");
        assertFalse(restaurado.getInstalaciones().get(1).isTechado(), "La segunda cancha restaurada no debe estar techada.");
    }

    @Test
    void testFromJSONSinMaximo()
    {
        JSONObject json = new JSONObject();
        json.put("nombre", "Tenis");

        assertThrows(org.json.JSONException.class, () -> Tenis.fromJSON(json), "Debe lanzarse JSONException si faltan campos.");
    }
}
