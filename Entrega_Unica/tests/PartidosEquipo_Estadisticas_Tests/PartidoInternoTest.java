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
import PartidosEquipo_Estadisticas.PartidoInterno;
import Usuarios.Socio;

public class PartidoInternoTest
{
    private Baloncesto baloncesto;
    private Equipo local;
    private Equipo visitante;
    private Equipo incompleto;

    private LocalDate fecha;
    private PartidoInterno partido;
    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        baloncesto = new Baloncesto();
        local = new Equipo("Local", 8, baloncesto, Categoria.MAYORES);
        visitante = new Equipo("Visitante", 8, baloncesto, Categoria.MAYORES);
        incompleto = new Equipo("Incompleto", 8, baloncesto, Categoria.MAYORES);

        llenarEquipo(local, "l", 5);
        llenarEquipo(visitante, "v", 5);
        llenarEquipo(incompleto, "i", 3);

        fecha = LocalDate.of(2026, 11, 10);
        partido = new PartidoInterno(fecha, local, visitante, 80, 75);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Local", local);
        mapaEquipos.put("Visitante", visitante);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private void llenarEquipo(Equipo equipo, String prefijo, int cantidad) throws Exception
    {
        for(int i = 1; i <= cantidad; i++)
        {
            equipo.agregarJugador(new Socio(20 + i, "Jugador " + prefijo + i, prefijo + i, "x"), "Alero", i);
        }
    }

    @Test
    void testEsPartido()
    {
        assertTrue(partido instanceof Partido, "Un partido interno es un partido.");
    }

    @Test
    void testGetters()
    {
        assertEquals(fecha, partido.getFecha(), "La fecha del partido no es la esperada.");
        assertSame(local, partido.getEquipoLocal(), "El equipo local no es el esperado.");
        assertSame(visitante, partido.getEquipoVisitante(), "El equipo visitante no es el esperado.");
        assertEquals(80, partido.getMarcadorLocal(), "El marcador local no es el esperado.");
        assertEquals(75, partido.getMarcadorVisitante(), "El marcador visitante no es el esperado.");
        assertEquals(0, partido.getEstadisticas().size(), "Un partido nuevo no debe tener estadísticas.");
    }

    @Test
    void testRegistrarMarcador()
    {
        partido.registrarMarcador(60, 90);

        assertEquals(60, partido.getMarcadorLocal(), "El marcador local registrado no es el esperado.");
        assertEquals(90, partido.getMarcadorVisitante(), "El marcador visitante registrado no es el esperado.");
    }

    @Test
    void testGetGanadorLocal()
    {
        assertSame(local, partido.getGanador(), "Con mayor marcador local gana el local.");
    }

    @Test
    void testGetGanadorVisitante()
    {
        partido.registrarMarcador(70, 71);

        assertSame(visitante, partido.getGanador(), "Con mayor marcador visitante gana el visitante.");
    }

    @Test
    void testGetGanadorEmpate()
    {
        partido.registrarMarcador(2, 2);

        assertNull(partido.getGanador(), "En caso de empate no hay ganador.");
    }

    @Test
    void testToString()
    {
        assertEquals("2026-11-10: Local 80 - 75 Visitante", partido.toString(), "El texto del partido no es el esperado.");
    }

    @Test
    void testPuedeJugarseConEquiposCompletos()
    {
        assertTrue(partido.puedeJugarse(), "Con ambos equipos completos el partido puede jugarse.");
    }

    @Test
    void testPuedeJugarseConEquipoIncompleto()
    {
        PartidoInterno conIncompleto = new PartidoInterno(fecha, local, incompleto, 0, 0);
        PartidoInterno conIncompletoLocal = new PartidoInterno(fecha, incompleto, visitante, 0, 0);

        assertFalse(conIncompleto.puedeJugarse(), "Con el visitante incompleto el partido no puede jugarse.");
        assertFalse(conIncompletoLocal.puedeJugarse(), "Con el local incompleto el partido no puede jugarse.");
    }

    @Test
    void testAgregarEstadistica()
    {
        partido.agregarEstadistica(new EstadisticaBaloncesto(local.getInscripciones().get(0), 20, 5));

        assertEquals(1, partido.getEstadisticas().size(), "El partido debe tener una estadística.");
    }

    @Test
    void testToJSON()
    {
        partido.agregarEstadistica(new EstadisticaBaloncesto(local.getInscripciones().get(0), 20, 5));

        JSONObject json = partido.toJSON();

        assertEquals("PartidoInterno", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("2026-11-10", json.getString("fecha"), "La fecha en el JSON no es la esperada.");
        assertEquals(80, json.getInt("marcadorLocal"), "El marcador local en el JSON no es el esperado.");
        assertEquals(75, json.getInt("marcadorVisitante"), "El marcador visitante en el JSON no es el esperado.");
        assertEquals("Local", json.getString("equipoLocal"), "El equipo local en el JSON no es el esperado.");
        assertEquals("Visitante", json.getString("equipoVisitante"), "El equipo visitante en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("estadisticasConjunto").length(), "El JSON debe tener una estadística.");
    }

    @Test
    void testFromJSON()
    {
        partido.agregarEstadistica(new EstadisticaBaloncesto(local.getInscripciones().get(0), 20, 5));
        partido.agregarEstadistica(new EstadisticaBaloncesto(visitante.getInscripciones().get(1), 12, 3));

        PartidoInterno restaurado = PartidoInterno.fromJSON(partido.toJSON(), mapaEquipos);

        assertEquals(fecha, restaurado.getFecha(), "La fecha restaurada no es la esperada.");
        assertSame(local, restaurado.getEquipoLocal(), "El equipo local restaurado no es el esperado.");
        assertSame(visitante, restaurado.getEquipoVisitante(), "El equipo visitante restaurado no es el esperado.");
        assertEquals(80, restaurado.getMarcadorLocal(), "El marcador local restaurado no es el esperado.");
        assertEquals(75, restaurado.getMarcadorVisitante(), "El marcador visitante restaurado no es el esperado.");
        assertEquals(2, restaurado.getEstadisticas().size(), "Deben restaurarse las dos estadísticas.");
        assertSame(visitante.getInscripciones().get(1), restaurado.getEstadisticas().get(1).getJugador(), "El jugador de la segunda estadística no es el esperado.");
    }

    @Test
    void testFromJSONEquipoInexistente()
    {
        mapaEquipos.remove("Visitante");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> PartidoInterno.fromJSON(partido.toJSON(), mapaEquipos), "Debe lanzarse IllegalArgumentException si un equipo no existe.");

        assertEquals("Equipo desconocido en el partido interno: Local / Visitante", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinEstadisticas()
    {
        JSONObject json = partido.toJSON();
        json.remove("estadisticasConjunto");

        PartidoInterno restaurado = PartidoInterno.fromJSON(json, mapaEquipos);

        assertEquals(0, restaurado.getEstadisticas().size(), "Sin estadísticas en el JSON el partido debe quedar sin estadísticas.");
    }
}
