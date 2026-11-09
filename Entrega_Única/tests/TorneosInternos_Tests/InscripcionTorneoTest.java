package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.InscripcionTorneoIndividual;
import Usuarios.Administrador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class InscripcionTorneoTest
{
    private Socio ana;
    private Socio pedro;
    private Equipo equipo;

    private InscripcionTorneo inscripcionIndividual;
    private InscripcionTorneo inscripcionPareja;
    private InscripcionTorneo inscripcionEquipo;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");
        equipo = new Equipo("Cerros Basket", 8, new Baloncesto(), Categoria.MAYORES);

        inscripcionIndividual = new InscripcionTorneoIndividual(ana, null);
        inscripcionPareja = new InscripcionTorneoIndividual(ana, pedro);
        inscripcionEquipo = new InscripcionTorneoEquipo(equipo);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", ana);
        mapaUsuarios.put("pgil", pedro);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Cerros Basket", equipo);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testValoresIniciales()
    {
        assertFalse(inscripcionIndividual.esPreferencial(), "Una inscripción nueva no es preferencial.");
        assertEquals(0, inscripcionIndividual.getMontoPagado(), 0.001, "Una inscripción nueva no tiene monto pagado.");
    }

    @Test
    void testSetPreferencial()
    {
        inscripcionIndividual.setPreferencial(true);

        assertTrue(inscripcionIndividual.esPreferencial(), "La inscripción debe quedar como preferencial.");

        inscripcionIndividual.setPreferencial(false);

        assertFalse(inscripcionIndividual.esPreferencial(), "La inscripción debe dejar de ser preferencial.");
    }

    @Test
    void testSetMontoPagado()
    {
        inscripcionIndividual.setMontoPagado(15000.5);

        assertEquals(15000.5, inscripcionIndividual.getMontoPagado(), 0.001, "El monto pagado no es el esperado.");
    }

    @Test
    void testFromJSONIndividual()
    {
        InscripcionTorneo restaurada = InscripcionTorneo.fromJSON(inscripcionIndividual.toJSON(), mapaUsuarios, mapaEquipos);

        assertTrue(restaurada instanceof InscripcionTorneoIndividual, "El tipo INDIVIDUAL debe crear una inscripción individual.");
        assertSame(ana, ((InscripcionTorneoIndividual) restaurada).getJugador(), "El jugador restaurado no es el esperado.");
        assertFalse(((InscripcionTorneoIndividual) restaurada).esPareja(), "La inscripción restaurada no debe ser pareja.");
    }

    @Test
    void testFromJSONPareja()
    {
        InscripcionTorneo restaurada = InscripcionTorneo.fromJSON(inscripcionPareja.toJSON(), mapaUsuarios, mapaEquipos);

        InscripcionTorneoIndividual individual = (InscripcionTorneoIndividual) restaurada;
        assertTrue(individual.esPareja(), "La inscripción restaurada debe ser pareja.");
        assertSame(ana, individual.getJugador(), "El jugador restaurado no es el esperado.");
        assertSame(pedro, individual.getCompanero(), "El compañero restaurado no es el esperado.");
    }

    @Test
    void testFromJSONEquipo()
    {
        InscripcionTorneo restaurada = InscripcionTorneo.fromJSON(inscripcionEquipo.toJSON(), mapaUsuarios, mapaEquipos);

        assertTrue(restaurada instanceof InscripcionTorneoEquipo, "El tipo EQUIPO debe crear una inscripción de equipo.");
        assertSame(equipo, ((InscripcionTorneoEquipo) restaurada).getEquipo(), "El equipo restaurado no es el esperado.");
    }

    @Test
    void testFromJSONConservaPreferencialYMonto()
    {
        inscripcionIndividual.setPreferencial(true);
        inscripcionIndividual.setMontoPagado(12345.5);

        InscripcionTorneo restaurada = InscripcionTorneo.fromJSON(inscripcionIndividual.toJSON(), mapaUsuarios, mapaEquipos);

        assertTrue(restaurada.esPreferencial(), "El valor preferencial restaurado no es el esperado.");
        assertEquals(12345.5, restaurada.getMontoPagado(), 0.001, "El monto pagado restaurado no es el esperado.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = inscripcionIndividual.toJSON();
        json.put("tipo", "GRUPAL");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> InscripcionTorneo.fromJSON(json, mapaUsuarios, mapaEquipos), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de inscripción desconocido: GRUPAL", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONEquipoInexistente()
    {
        mapaEquipos.clear();

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> InscripcionTorneo.fromJSON(inscripcionEquipo.toJSON(), mapaUsuarios, mapaEquipos), "Debe lanzarse IllegalStateException si el equipo no existe.");

        assertEquals("Equipo inexistente en el torneo: Cerros Basket", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONJugadorInexistente()
    {
        mapaUsuarios.remove("ana");

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> InscripcionTorneo.fromJSON(inscripcionIndividual.toJSON(), mapaUsuarios, mapaEquipos), "Debe lanzarse IllegalStateException si el jugador no existe.");

        assertEquals("Jugador inexistente en el torneo: ana", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONCompaneroInexistente()
    {
        mapaUsuarios.remove("pgil");

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> InscripcionTorneo.fromJSON(inscripcionPareja.toJSON(), mapaUsuarios, mapaEquipos), "Debe lanzarse IllegalStateException si el compañero no existe.");

        assertEquals("Jugador inexistente en el torneo: pgil", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONUsuarioQueNoEsJugador()
    {
        JSONObject json = inscripcionIndividual.toJSON();
        json.put("jugador", "admin");

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> InscripcionTorneo.fromJSON(json, mapaUsuarios, mapaEquipos), "Debe lanzarse IllegalStateException si el usuario no es un jugador de torneo.");

        assertEquals("Jugador inexistente en el torneo: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinPreferencial()
    {
        JSONObject json = inscripcionIndividual.toJSON();
        json.remove("preferencial");

        assertThrows(org.json.JSONException.class, () -> InscripcionTorneo.fromJSON(json, mapaUsuarios, mapaEquipos), "Debe lanzarse JSONException si falta el valor preferencial.");
    }

    @Test
    void testFromJSONSinTipo()
    {
        JSONObject json = inscripcionIndividual.toJSON();
        json.remove("tipo");

        assertThrows(org.json.JSONException.class, () -> InscripcionTorneo.fromJSON(json, mapaUsuarios, mapaEquipos), "Debe lanzarse JSONException si falta el tipo.");
    }
}
