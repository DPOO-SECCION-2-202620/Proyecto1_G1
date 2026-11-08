package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Padel;
import Instalaciones.CanchaPadel;

public class CanchaPadelTest
{
    private Padel padel;
    private CanchaPadel cancha;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        padel = new Padel(2, "Padel");
        cancha = new CanchaPadel("Padel Norte", false, 1, padel);

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Padel", padel);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testCapacidadMaxima()
    {
        assertEquals(4, cancha.getCapacidadMaxima(), "La capacidad de una cancha de pádel debe ser 4.");
    }

    @Test
    void testAdmiteModalidadDobles()
    {
        assertTrue(cancha.admiteModalidad(Modalidad.DOBLES, 4), "El pádel admite dobles con cuatro jugadores.");
    }

    @Test
    void testNoAdmiteSencillos()
    {
        assertFalse(cancha.admiteModalidad(Modalidad.SENCILLOS, 2), "El pádel no admite sencillos.");
    }

    @Test
    void testNoAdmiteDoblesConOtroNumeroDeJugadores()
    {
        assertFalse(cancha.admiteModalidad(Modalidad.DOBLES, 2), "El pádel no admite dobles con dos jugadores.");
        assertFalse(cancha.admiteModalidad(Modalidad.DOBLES, 3), "El pádel no admite dobles con tres jugadores.");
    }

    @Test
    void testPermiteBebidaCaliente()
    {
        assertFalse(cancha.permiteBebidaCaliente(), "La cancha de pádel no permite bebidas calientes.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = cancha.toJSON();

        assertEquals("CanchaPadel", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Padel Norte", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONConMapa()
    {
        CanchaPadel restaurada = CanchaPadel.fromJSON(cancha.toJSON(), mapaDisciplinas);

        assertEquals("Padel Norte", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertFalse(restaurada.isTechado(), "El valor techado restaurado no es el esperado.");
        assertEquals(1, restaurada.getSalon(), "El salón restaurado no es el esperado.");
        assertSame(padel, restaurada.getDisciplina(), "La disciplina restaurada debe ser la del mapa.");
    }

    @Test
    void testFromJSONConDisciplina()
    {
        Padel otroPadel = new Padel(1, "Padel");

        CanchaPadel restaurada = CanchaPadel.fromJSON(cancha.toJSON(), otroPadel);

        assertSame(otroPadel, restaurada.getDisciplina(), "La disciplina restaurada debe ser la entregada.");
    }
}
