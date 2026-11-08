package PartidosEquipo_Estadisticas_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import PartidosEquipo_Estadisticas.EstadisticaVoleibol;
import Usuarios.Socio;

public class EstadisticaConjuntoTest
{
    private Socio ana;
    private Equipo equipo;
    private InscripcionEquipo inscripcionAna;

    private EstadisticaConjunto estadisticaFutbol;
    private EstadisticaConjunto estadisticaBaloncesto;
    private EstadisticaConjunto estadisticaVoleibol;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        equipo = new Equipo("Los Cerros FC", 10, new Futbol(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Delantero", 9);
        inscripcionAna = equipo.getInscripcionDe(ana);

        estadisticaFutbol = new EstadisticaFutbol(inscripcionAna, 1, 1, 0, 0);
        estadisticaBaloncesto = new EstadisticaBaloncesto(inscripcionAna, 10, 2);
        estadisticaVoleibol = new EstadisticaVoleibol(inscripcionAna, 2, 2);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetJugador()
    {
        assertSame(inscripcionAna, estadisticaFutbol.getJugador(), "El jugador no es el esperado.");
    }

    @Test
    void testToJSON()
    {
        assertEquals("ana", estadisticaFutbol.toJSON().getString("jugador"), "El jugador en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONFutbol()
    {
        EstadisticaConjunto creada = EstadisticaConjunto.fromJSON(estadisticaFutbol.toJSON(), equipo.getInscripciones());

        assertTrue(creada instanceof EstadisticaFutbol, "El tipo EstadisticaFutbol debe crear una estadística de fútbol.");
    }

    @Test
    void testFromJSONBaloncesto()
    {
        EstadisticaConjunto creada = EstadisticaConjunto.fromJSON(estadisticaBaloncesto.toJSON(), equipo.getInscripciones());

        assertTrue(creada instanceof EstadisticaBaloncesto, "El tipo EstadisticaBaloncesto debe crear una estadística de baloncesto.");
    }

    @Test
    void testFromJSONVoleibol()
    {
        EstadisticaConjunto creada = EstadisticaConjunto.fromJSON(estadisticaVoleibol.toJSON(), equipo.getInscripciones());

        assertTrue(creada instanceof EstadisticaVoleibol, "El tipo EstadisticaVoleibol debe crear una estadística de voleibol.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = estadisticaFutbol.toJSON();
        json.put("tipo", "EstadisticaGolf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> EstadisticaConjunto.fromJSON(json, equipo.getInscripciones()), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de estadística desconocido: EstadisticaGolf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
