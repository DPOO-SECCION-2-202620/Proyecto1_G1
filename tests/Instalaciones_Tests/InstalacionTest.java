package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;
import DeportesIndividuales.TenisDeMesa;
import Instalaciones.CanchaBaloncesto;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaPadel;
import Instalaciones.CanchaTenis;
import Instalaciones.CanchaVoleibol;
import Instalaciones.Instalacion;
import Instalaciones.MesaTenisDeMesa;

public class InstalacionTest
{
    private Instalacion instalacion;

    private Tenis tenis;
    private Padel padel;
    private TenisDeMesa tenisDeMesa;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        instalacion = new CanchaFutbol("Cancha Principal");

        tenis = new Tenis(2, "Tenis");
        padel = new Padel(2, "Padel");
        tenisDeMesa = new TenisDeMesa(3, "TenisDeMesa");

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);
        mapaDisciplinas.put("Padel", padel);
        mapaDisciplinas.put("TenisDeMesa", tenisDeMesa);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Cancha Principal", instalacion.getNombre(), "El nombre de la instalación no es el esperado.");
    }

    @Test
    void testGetCapacidadMaxima()
    {
        assertEquals(22, instalacion.getCapacidadMaxima(), "La capacidad de la instalación no es la esperada.");
    }

    @Test
    void testsetCapacidadMaxima()
    {
        instalacion.setCapacidadMaxima(30);

        assertEquals(30, instalacion.getCapacidadMaxima(), "La capacidad modificada no es la esperada.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = instalacion.toJSON();

        assertEquals("Cancha Principal", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(22, json.getInt("capacidadMaxima"), "La capacidad en el JSON no es la esperada.");
    }

    @Test
    void testFromJSONCanchaTenis()
    {
        Instalacion creada = Instalacion.fromJSON(new CanchaTenis("Tenis 1", true, 2, tenis).toJSON(), mapaDisciplinas);

        assertTrue(creada instanceof CanchaTenis, "El tipo CanchaTenis debe crear una cancha de tenis.");
        assertEquals("Tenis 1", creada.getNombre(), "El nombre de la instalación creada no es el esperado.");
    }

    @Test
    void testFromJSONCanchaPadel()
    {
        Instalacion creada = Instalacion.fromJSON(new CanchaPadel("Padel 1", false, 1, padel).toJSON(), mapaDisciplinas);

        assertTrue(creada instanceof CanchaPadel, "El tipo CanchaPadel debe crear una cancha de pádel.");
    }

    @Test
    void testFromJSONMesaTenisDeMesa()
    {
        Instalacion creada = Instalacion.fromJSON(new MesaTenisDeMesa("Mesa 1", true, 3, tenisDeMesa).toJSON(), mapaDisciplinas);

        assertTrue(creada instanceof MesaTenisDeMesa, "El tipo MesaTenisDeMesa debe crear una mesa de tenis de mesa.");
    }

    @Test
    void testFromJSONCanchaFutbol()
    {
        Instalacion creada = Instalacion.fromJSON(new CanchaFutbol("Futbol 1").toJSON(), mapaDisciplinas);

        assertTrue(creada instanceof CanchaFutbol, "El tipo CanchaFutbol debe crear una cancha de fútbol.");
    }

    @Test
    void testFromJSONCanchaBaloncesto()
    {
        Instalacion creada = Instalacion.fromJSON(new CanchaBaloncesto("Baloncesto 1").toJSON(), mapaDisciplinas);

        assertTrue(creada instanceof CanchaBaloncesto, "El tipo CanchaBaloncesto debe crear una cancha de baloncesto.");
    }

    @Test
    void testFromJSONCanchaVoleibol()
    {
        Instalacion creada = Instalacion.fromJSON(new CanchaVoleibol("Voleibol 1").toJSON(), mapaDisciplinas);

        assertTrue(creada instanceof CanchaVoleibol, "El tipo CanchaVoleibol debe crear una cancha de voleibol.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = instalacion.toJSON();
        json.put("tipo", "Piscina");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Instalacion.fromJSON(json, mapaDisciplinas), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de Instalacion desconocido: Piscina", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinDisciplinaEnElMapa()
    {
        Map<String, DisciplinaIndividual> mapaVacio = new HashMap<String, DisciplinaIndividual>();
        JSONObject json = new CanchaTenis("Tenis 2", true, 2, tenis).toJSON();

        assertThrows(NullPointerException.class, () -> Instalacion.fromJSON(json, mapaVacio), "Sin la disciplina en el mapa no se puede crear la instalación individual.");
    }
}
