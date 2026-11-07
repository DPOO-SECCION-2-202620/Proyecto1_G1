package PartidosEquipo_Estadisticas_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import PartidosEquipo_Estadisticas.EstadisticaConjunto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import Usuarios.Socio;

public class EstadisticaFutbolTest
{
    private Socio ana;
    private Socio pedro;
    private Equipo equipo;
    private InscripcionEquipo inscripcionAna;
    private EstadisticaFutbol estadistica;
    private EstadisticaFutbol estadisticaVacia;

    @BeforeEach
    void setUp() throws Exception
    {
        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");

        equipo = new Equipo("Los Cerros FC", 10, new Futbol(), Categoria.MAYORES);
        equipo.agregarJugador(ana, "Delantero", 9);
        inscripcionAna = equipo.getInscripcionDe(ana);

        estadistica = new EstadisticaFutbol(inscripcionAna, 2, 1, 1, 0);
        estadisticaVacia = new EstadisticaFutbol(inscripcionAna, 0, 0, 0, 0);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsEstadisticaConjunto()
    {
        assertTrue(estadistica instanceof EstadisticaConjunto, "Una estadística de fútbol es una estadística de conjunto.");
    }

    @Test
    void testGetters()
    {
        assertSame(inscripcionAna, estadistica.getJugador(), "El jugador no es el esperado.");
        assertEquals(2, estadistica.getGoles(), "Los goles no son los esperados.");
        assertEquals(1, estadistica.getAsistencias(), "Las asistencias no son las esperadas.");
        assertEquals(1, estadistica.getTarjetasAmarillas(), "Las amarillas no son las esperadas.");
        assertEquals(0, estadistica.getTarjetasRojas(), "Las rojas no son las esperadas.");
    }

    @Test
    void testCalcularAporte()
    {
        assertEquals(5, estadistica.calcularAporte(), "El aporte debe ser dos por gol más una por asistencia.");
    }

    @Test
    void testCalcularAporteSinEstadisticas()
    {
        assertEquals(0, estadisticaVacia.calcularAporte(), "Sin goles ni asistencias el aporte es 0.");
    }

    @Test
    void testCalcularAporteNoCuentaTarjetas()
    {
        EstadisticaFutbol conTarjetas = new EstadisticaFutbol(inscripcionAna, 1, 0, 2, 1);

        assertEquals(2, conTarjetas.calcularAporte(), "Las tarjetas no deben afectar el aporte.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = estadistica.toJSON();

        assertEquals("EstadisticaFutbol", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals(2, json.getInt("goles"), "Los goles en el JSON no son los esperados.");
        assertEquals(1, json.getInt("asistencias"), "Las asistencias en el JSON no son las esperadas.");
        assertEquals(1, json.getInt("tarjetasAmarillas"), "Las amarillas en el JSON no son las esperadas.");
        assertEquals(0, json.getInt("tarjetasRojas"), "Las rojas en el JSON no son las esperadas.");
    }

    @Test
    void testFromJSON()
    {
        EstadisticaFutbol restaurada = EstadisticaFutbol.fromJSON(estadistica.toJSON(), equipo.getInscripciones());

        assertSame(inscripcionAna, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertEquals(2, restaurada.getGoles(), "Los goles restaurados no son los esperados.");
        assertEquals(1, restaurada.getAsistencias(), "Las asistencias restauradas no son las esperadas.");
        assertEquals(1, restaurada.getTarjetasAmarillas(), "Las amarillas restauradas no son las esperadas.");
        assertEquals(0, restaurada.getTarjetasRojas(), "Las rojas restauradas no son las esperadas.");
    }

    @Test
    void testFromJSONNulo()
    {
        assertNull(EstadisticaFutbol.fromJSON(null, equipo.getInscripciones()), "Con un JSON nulo no se debe crear una estadística.");
    }

    @Test
    void testFromJSONJugadorNoInscrito()
    {
        JSONObject json = estadistica.toJSON();
        json.put("jugador", pedro.getLogin());
        List<InscripcionEquipo> inscripciones = new ArrayList<InscripcionEquipo>(equipo.getInscripciones());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> EstadisticaFutbol.fromJSON(json, inscripciones), "Debe lanzarse IllegalArgumentException si el jugador no está inscrito.");

        assertEquals("El jugador pgil no está inscrito en el equipo de la estadística", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
