package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesConjunto_Competencias.Voleibol;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaVoleibol;
import Usuarios.Socio;

public class EstadisticaVoleibolTest
{
    private Socio ana;
    private Socio pedro;
    private Equipo equipo;
    private InscripcionEquipo inscripcionAna;
    private EstadisticaVoleibol estadistica;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");

        equipo = new Equipo("Cerros Vóley", 10, new Voleibol(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Central", 3);
        inscripcionAna = equipo.getInscripcionDe(ana);

        estadistica = new EstadisticaVoleibol(inscripcionAna, 4, 3);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsEstadisticaConjunto()
    {
        assertTrue(estadistica instanceof EstadisticaConjunto, "Una estadística de voleibol es una estadística de conjunto.");
    }

    @Test
    void testGetters()
    {
        assertSame(inscripcionAna, estadistica.getJugador(), "El jugador no es el esperado.");
        assertEquals(4, estadistica.getAces(), "Los aces no son los esperados.");
        assertEquals(3, estadistica.getBloqueos(), "Los bloqueos no son los esperados.");
    }

    @Test
    void testCalcularAporte()
    {
        assertEquals(7, estadistica.calcularAporte(), "El aporte debe ser la suma de aces y bloqueos.");
    }

    @Test
    void testCalcularAporteSinEstadisticas()
    {
        assertEquals(0, new EstadisticaVoleibol(inscripcionAna, 0, 0).calcularAporte(), "Sin aces ni bloqueos el aporte es 0.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = estadistica.toJSON();

        assertEquals("EstadisticaVoleibol", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(4, json.getInt("aces"), "Los aces en el JSON no son los esperados.");
        assertEquals(3, json.getInt("bloqueos"), "Los bloqueos en el JSON no son los esperados.");
    }

    @Test
    void testFromJSON()
    {
        EstadisticaVoleibol restaurada = EstadisticaVoleibol.fromJSON(estadistica.toJSON(), equipo.getInscripciones());

        assertSame(inscripcionAna, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(4, restaurada.getAces(), "Los aces restaurados no son los esperados.");
        assertEquals(3, restaurada.getBloqueos(), "Los bloqueos restaurados no son los esperados.");
    }

    @Test
    void testFromJSONNulo()
    {
        assertNull(EstadisticaVoleibol.fromJSON(null, equipo.getInscripciones()), "Con un JSON nulo no se debe crear una estadística.");
    }

    @Test
    void testFromJSONJugadorNoInscrito()
    {
        JSONObject json = estadistica.toJSON();
        json.put("jugador", pedro.getLogin());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> EstadisticaVoleibol.fromJSON(json, equipo.getInscripciones()), "Debe lanzarse IllegalArgumentException si el jugador no está inscrito.");

        assertEquals("El jugador pgil no está inscrito en el equipo de la estadística", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
