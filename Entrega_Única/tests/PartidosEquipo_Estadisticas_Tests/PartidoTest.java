package PartidosEquipo_Estadisticas_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.Partido;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Usuarios.Socio;

public class PartidoTest
{
    private Equipo equipoA;
    private Equipo equipoB;
    private Partido partidoInterno;
    private Partido partidoExterno;

    private LocalDate fecha;
    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        equipoA = new Equipo("Equipo A", 8, new Baloncesto(), Categoria.MAYORES);
        equipoB = new Equipo("Equipo B", 8, new Baloncesto(), Categoria.MAYORES);
        equipoA.agregarJugador(new Socio(21, "Jugador A1", "a1", "x"), "Alero", 1);

        fecha = LocalDate.of(2026, 11, 10);
        partidoInterno = new PartidoInterno(fecha, equipoA, equipoB, 70, 60);
        partidoExterno = new PartidoExterno(fecha, 70, 60, "Rival", "Escenario", equipoA);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Equipo A", equipoA);
        mapaEquipos.put("Equipo B", equipoB);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetFecha()
    {
        assertEquals(fecha, partidoInterno.getFecha(), "La fecha del partido no es la esperada.");
    }

    @Test
    void testAgregarEstadistica()
    {
        partidoInterno.agregarEstadistica(new EstadisticaBaloncesto(equipoA.getInscripciones().get(0), 10, 2));

        assertEquals(1, partidoInterno.getEstadisticas().size(), "El partido debe tener una estadística.");
    }

    @Test
    void testToJSON()
    {
        partidoInterno.agregarEstadistica(new EstadisticaBaloncesto(equipoA.getInscripciones().get(0), 10, 2));

        JSONObject json = partidoInterno.toJSON();

        assertEquals("2026-11-10", json.getString("fecha"), "La fecha en el JSON no es la esperada.");
        assertEquals(1, json.getJSONArray("estadisticasConjunto").length(), "El JSON debe tener una estadística.");
    }

    @Test
    void testFromJSONPartidoInterno()
    {
        Partido creado = Partido.fromJSON(partidoInterno.toJSON(), mapaEquipos);

        assertTrue(creado instanceof PartidoInterno, "El tipo PartidoInterno debe crear un partido interno.");
    }

    @Test
    void testFromJSONPartidoExterno()
    {
        Partido creado = Partido.fromJSON(partidoExterno.toJSON(), mapaEquipos);

        assertTrue(creado instanceof PartidoExterno, "El tipo PartidoExterno debe crear un partido externo.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = partidoInterno.toJSON();
        json.put("tipo", "PartidoAmistoso");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Partido.fromJSON(json, mapaEquipos), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de partido desconocido: PartidoAmistoso", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
