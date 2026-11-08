package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Tenis;
import Instalaciones.CanchaTenis;

public class CanchaTenisTest
{
    private Tenis tenis;
    private CanchaTenis cancha;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        cancha = new CanchaTenis("Tenis Central", true, 3, tenis);

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testCapacidadMaxima()
    {
        assertEquals(4, cancha.getCapacidadMaxima(), "La capacidad de una cancha de tenis debe ser 4.");
    }

    @Test
    void testPermiteBebidaCaliente()
    {
        assertTrue(cancha.permiteBebidaCaliente(), "La cancha de tenis permite bebidas calientes.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = cancha.toJSON();

        assertEquals("CanchaTenis", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Tenis Central", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONConMapa()
    {
        CanchaTenis restaurada = CanchaTenis.fromJSON(cancha.toJSON(), mapaDisciplinas);

        assertEquals("Tenis Central", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertTrue(restaurada.isTechado(), "El valor techado restaurado no es el esperado.");
        assertEquals(3, restaurada.getSalon(), "El salón restaurado no es el esperado.");
        assertSame(tenis, restaurada.getDisciplina(), "La disciplina restaurada debe ser la del mapa.");
    }

    @Test
    void testFromJSONConDisciplina()
    {
        Tenis otroTenis = new Tenis(1, "Tenis");

        CanchaTenis restaurada = CanchaTenis.fromJSON(cancha.toJSON(), otroTenis);

        assertSame(otroTenis, restaurada.getDisciplina(), "La disciplina restaurada debe ser la entregada.");
        assertEquals(1, otroTenis.getInstalaciones().size(), "La disciplina debe registrar la cancha restaurada.");
    }

    @Test
    void testFromJSONConMapaSinTenis()
    {
        JSONObject json = cancha.toJSON();
        Map<String, DisciplinaIndividual> mapaVacio = new HashMap<String, DisciplinaIndividual>();

        assertThrows(NullPointerException.class, () -> CanchaTenis.fromJSON(json, mapaVacio), "Sin la disciplina en el mapa no se puede restaurar la cancha.");
    }
}
