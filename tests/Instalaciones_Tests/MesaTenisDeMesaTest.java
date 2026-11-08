package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.TenisDeMesa;
import Instalaciones.MesaTenisDeMesa;

public class MesaTenisDeMesaTest
{
    private TenisDeMesa tenisDeMesa;
    private MesaTenisDeMesa mesaTechada;
    private MesaTenisDeMesa mesaAbierta;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        tenisDeMesa = new TenisDeMesa(3, "TenisDeMesa");
        mesaTechada = new MesaTenisDeMesa("Mesa Interior", true, 4, tenisDeMesa);
        mesaAbierta = new MesaTenisDeMesa("Mesa Terraza", false, 0, tenisDeMesa);

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("TenisDeMesa", tenisDeMesa);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testCapacidadMaxima()
    {
        assertEquals(4, mesaTechada.getCapacidadMaxima(), "La capacidad de una mesa debe ser 4.");
    }

    @Test
    void testPermiteBebidaCalienteMesaTechada()
    {
        assertFalse(mesaTechada.permiteBebidaCaliente(), "Una mesa techada no permite bebidas calientes.");
    }

    @Test
    void testPermiteBebidaCalienteMesaAbierta()
    {
        assertTrue(mesaAbierta.permiteBebidaCaliente(), "Una mesa al aire libre permite bebidas calientes.");
    }

    @Test
    void testseRegistranEnLaDisciplina()
    {
        assertEquals(2, tenisDeMesa.getInstalaciones().size(), "La disciplina debe tener las dos mesas registradas.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = mesaTechada.toJSON();

        assertEquals("MesaTenisDeMesa", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Mesa Interior", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertTrue(json.getBoolean("techado"), "El valor techado en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONConMapa()
    {
        MesaTenisDeMesa restaurada = MesaTenisDeMesa.fromJSON(mesaTechada.toJSON(), mapaDisciplinas);

        assertEquals("Mesa Interior", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(4, restaurada.getSalon(), "El salón restaurado no es el esperado.");
        assertSame(tenisDeMesa, restaurada.getDisciplina(), "La disciplina restaurada debe ser la del mapa.");
    }

    @Test
    void testFromJSONConDisciplina()
    {
        TenisDeMesa otraDisciplina = new TenisDeMesa(1, "TenisDeMesa");

        MesaTenisDeMesa restaurada = MesaTenisDeMesa.fromJSON(mesaAbierta.toJSON(), otraDisciplina);

        assertSame(otraDisciplina, restaurada.getDisciplina(), "La disciplina restaurada debe ser la entregada.");
        assertFalse(restaurada.isTechado(), "El valor techado restaurado no es el esperado.");
    }
}
