package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionCompetencia;
import DeportesConjunto_Competencias.RegistroNomina;
import DeportesConjunto_Competencias.TorneoCerros;
import Usuarios.Socio;

public class InscripcionCompetenciaTest
{
    private Futbol futbol;
    private Equipo equipo;
    private TorneoCerros competencia;
    private InscripcionCompetencia inscripcion;

    private Socio ana;
    private Socio pedro;
    private Socio luis;

    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
        equipo = new Equipo("Los Cerros FC", 10, futbol, Categoria.MAYORES);

        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");
        luis = new Socio(30, "Luis Mora", "lmora", "3");

        equipo.agregarJugador(ana, "Delantero", 9);
        equipo.agregarJugador(pedro, "Portero", 1);

        competencia = new TorneoCerros("Liga 2026", 2026, 2, Categoria.MAYORES, futbol);
        inscripcion = new InscripcionCompetencia(equipo, competencia);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Los Cerros FC", equipo);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertSame(equipo, inscripcion.getEquipo(), "El equipo no es el esperado.");
        assertSame(competencia, inscripcion.getCompetencia(), "La competencia no es la esperada.");
        assertEquals(0, inscripcion.getNomina().size(), "Una inscripción nueva no debe tener nómina.");
    }

    @Test
    void testAgregarANomina()
    {
        assertTrue(inscripcion.agregarANomina(ana), "Un jugador del equipo debe poder agregarse a la nómina.");

        assertEquals(1, inscripcion.getNomina().size(), "La nómina debe tener un registro.");
        assertSame(ana, inscripcion.getNomina().get(0).getInscripcionEquipo().getJugador(), "El jugador de la nómina no es el esperado.");
    }

    @Test
    void testAgregarANominaJugadorAjeno()
    {
        assertFalse(inscripcion.agregarANomina(luis), "Un jugador que no es del equipo no puede agregarse a la nómina.");

        assertEquals(0, inscripcion.getNomina().size(), "La nómina debe seguir vacía.");
    }

    @Test
    void testAgregarANominaRepetido()
    {
        inscripcion.agregarANomina(ana);

        assertFalse(inscripcion.agregarANomina(ana), "Un jugador no puede agregarse dos veces a la nómina.");
        assertEquals(1, inscripcion.getNomina().size(), "La nómina debe tener un solo registro.");
    }

    @Test
    void testGetRegistroDe()
    {
        inscripcion.agregarANomina(ana);

        RegistroNomina registro = inscripcion.getRegistroDe(ana);

        assertNotNull(registro, "Debe encontrarse el registro del jugador.");
        assertSame(ana, registro.getInscripcionEquipo().getJugador(), "El registro debe ser del jugador.");
        assertNull(inscripcion.getRegistroDe(pedro), "Un jugador fuera de la nómina no tiene registro.");
    }

    @Test
    void testPuedeAlinear()
    {
        inscripcion.agregarANomina(ana);

        assertTrue(inscripcion.puedeAlinear(ana), "Un jugador de la nómina sin sanciones puede alinearse.");
    }

    @Test
    void testPuedeAlinearJugadorFueraDeLaNomina()
    {
        inscripcion.agregarANomina(ana);

        assertFalse(inscripcion.puedeAlinear(pedro), "Un jugador fuera de la nómina no puede alinearse.");
        assertFalse(inscripcion.puedeAlinear(luis), "Un jugador ajeno al equipo no puede alinearse.");
    }

    @Test
    void testPuedeAlinearJugadorSuspendido()
    {
        inscripcion.agregarANomina(ana);
        inscripcion.getRegistroDe(ana).registrarTarjetas(0, 1);

        assertFalse(inscripcion.puedeAlinear(ana), "Un jugador suspendido no puede alinearse.");

        inscripcion.getRegistroDe(ana).cumplirSuspension();

        assertTrue(inscripcion.puedeAlinear(ana), "Al cumplir la suspensión el jugador puede alinearse.");
    }

    @Test
    void testToJSON()
    {
        inscripcion.agregarANomina(ana);

        JSONObject json = inscripcion.toJSON();

        assertEquals("Los Cerros FC", json.getString("equipo"), "El equipo en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("nomina").length(), "El JSON debe tener un registro de nómina.");
        assertEquals("ana", json.getJSONArray("nomina").getJSONObject(0).getString("jugador"), "El jugador de la nómina en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        inscripcion.agregarANomina(ana);
        inscripcion.agregarANomina(pedro);

        InscripcionCompetencia restaurada = InscripcionCompetencia.fromJSON(inscripcion.toJSON(), competencia, mapaEquipos);

        assertSame(equipo, restaurada.getEquipo(), "El equipo restaurado no es el esperado.");
        assertSame(competencia, restaurada.getCompetencia(), "La competencia restaurada no es la esperada.");
        assertEquals(2, restaurada.getNomina().size(), "Debe restaurarse la nómina de dos jugadores.");
        assertTrue(restaurada.puedeAlinear(pedro), "El jugador restaurado debe poder alinearse.");
    }

    @Test
    void testFromJSONEquipoInexistente()
    {
        mapaEquipos.clear();

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> InscripcionCompetencia.fromJSON(inscripcion.toJSON(), competencia, mapaEquipos), "Debe lanzarse IllegalArgumentException si el equipo no existe.");

        assertEquals("Equipo desconocido en la inscripción: Los Cerros FC", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinNomina()
    {
        JSONObject json = inscripcion.toJSON();
        json.remove("nomina");

        InscripcionCompetencia restaurada = InscripcionCompetencia.fromJSON(json, competencia, mapaEquipos);

        assertEquals(0, restaurada.getNomina().size(), "Sin nómina en el JSON la nómina debe quedar vacía.");
    }
}
