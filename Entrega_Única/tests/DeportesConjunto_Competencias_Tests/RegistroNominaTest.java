package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesConjunto_Competencias.RegistroNomina;
import Usuarios.Socio;

public class RegistroNominaTest
{
    private Socio ana;
    private Socio pedro;
    private Equipo equipo;
    private InscripcionEquipo inscripcionAna;
    private RegistroNomina registro;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");

        equipo = new Equipo("Los Cerros FC", 10, new Futbol(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Delantero", 9);
        inscripcionAna = equipo.getInscripcionDe(ana);

        registro = new RegistroNomina(inscripcionAna);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetInscripcionEquipo()
    {
        assertSame(inscripcionAna, registro.getInscripcionEquipo(), "La inscripción del registro no es la esperada.");
    }

    @Test
    void testEstaHabilitadoInicialmente()
    {
        assertTrue(registro.estaHabilitado(), "Un registro nuevo debe estar habilitado.");
    }

    @Test
    void testRegistrarTarjetas()
    {
        registro.registrarTarjetas(1, 0);
        assertTrue(registro.estaHabilitado(), "Con una amarilla sigue habilitado.");

        registro.registrarTarjetas(1, 0);
        assertFalse(registro.estaHabilitado(), "Con dos amarillas queda suspendido.");
    }

    @Test
    void testRegistrarRoja()
    {
        registro.registrarTarjetas(0, 1);

        assertFalse(registro.estaHabilitado(), "Con una roja queda suspendido.");
    }

    @Test
    void testsancionesIndependientesDeLaInscripcionAlEquipo()
    {
        registro.registrarTarjetas(0, 1);

        assertFalse(registro.estaHabilitado(), "El registro de la competencia debe quedar suspendido.");
        assertTrue(inscripcionAna.estaHabilitado(), "La inscripción al equipo no debe verse afectada.");
    }

    @Test
    void testCumplirSuspension()
    {
        registro.registrarTarjetas(0, 1);

        registro.cumplirSuspension();

        assertTrue(registro.estaHabilitado(), "Al cumplir la suspensión debe quedar habilitado.");
    }

    @Test
    void testToJSON()
    {
        registro.registrarTarjetas(1, 0);

        JSONObject json = registro.toJSON();

        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(1, json.getJSONObject("controlSanciones").getInt("amarillasAcumuladas"), "Las amarillas en el JSON no son las esperadas.");
    }

    @Test
    void testFromJSON()
    {
        registro.registrarTarjetas(0, 1);

        RegistroNomina restaurado = RegistroNomina.fromJSON(registro.toJSON(), equipo);

        assertSame(inscripcionAna, restaurado.getInscripcionEquipo(), "La inscripción restaurada no es la esperada.");
        assertFalse(restaurado.estaHabilitado(), "El estado de sanción restaurado no es el esperado.");
    }

    @Test
    void testFromJSONJugadorQueNoPerteneceAlEquipo()
    {
        JSONObject json = registro.toJSON();
        json.put("jugador", pedro.getLogin());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> RegistroNomina.fromJSON(json, equipo), "Debe lanzarse IllegalArgumentException si el jugador no pertenece al equipo.");

        assertEquals("El jugador no pertenece al equipo: pgil", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
