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
import Usuarios.Socio;

public class PartidoExternoTest
{
    private Equipo equipoCompleto;
    private Equipo equipoIncompleto;

    private LocalDate fecha;
    private PartidoExterno partido;
    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        equipoCompleto = new Equipo("Cerros Basket", 8, new Baloncesto(), Categoria.MAYORES);
        equipoIncompleto = new Equipo("Cerros B", 8, new Baloncesto(), Categoria.MAYORES);

        for(int i = 1; i <= 5; i++)
        {
            equipoCompleto.agregarJugador(new Socio(20 + i, "Jugador " + i, "j" + i, "x"), "Alero", i);
        }
        equipoIncompleto.agregarJugador(new Socio(30, "Solo Uno", "solo", "x"), "Alero", 1);

        fecha = LocalDate.of(2026, 11, 10);
        partido = new PartidoExterno(fecha, 85, 80, "Universidad Rival", "Coliseo Norte", equipoCompleto);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Cerros Basket", equipoCompleto);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsPartido()
    {
        assertTrue(partido instanceof Partido, "Un partido externo es un partido.");
    }

    @Test
    void testGetters()
    {
        assertEquals(fecha, partido.getFecha(), "La fecha del partido no es la esperada.");
        assertEquals(85, partido.getMarcadorPropio(), "El marcador propio no es el esperado.");
        assertEquals(80, partido.getMarcadorRival(), "El marcador rival no es el esperado.");
        assertEquals("Universidad Rival", partido.getInstitucionRival(), "La institución rival no es la esperada.");
        assertEquals("Coliseo Norte", partido.getEscenario(), "El escenario no es el esperado.");
        assertSame(equipoCompleto, partido.getEquipo(), "El equipo no es el esperado.");
        assertEquals(0, partido.getEstadisticas().size(), "Un partido nuevo no debe tener estadísticas.");
    }

    @Test
    void testToString()
    {
        assertEquals("2026-11-10: Cerros Basket 85 - 80 Universidad Rival (Coliseo Norte)", partido.toString(), "El texto del partido no es el esperado.");
    }

    @Test
    void testPuedeJugarse()
    {
        assertTrue(partido.puedeJugarse(), "Con el equipo completo el partido puede jugarse.");
    }

    @Test
    void testPuedeJugarseConEquipoIncompleto()
    {
        PartidoExterno conIncompleto = new PartidoExterno(fecha, 0, 0, "Rival", "Escenario", equipoIncompleto);

        assertFalse(conIncompleto.puedeJugarse(), "Con el equipo incompleto el partido no puede jugarse.");
    }

    @Test
    void testAgregarEstadistica()
    {
        partido.agregarEstadistica(new EstadisticaBaloncesto(equipoCompleto.getInscripciones().get(0), 20, 5));

        assertEquals(1, partido.getEstadisticas().size(), "El partido debe tener una estadística.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = partido.toJSON();

        assertEquals("PartidoExterno", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("2026-11-10", json.getString("fecha"), "La fecha en el JSON no es la esperada.");
        assertEquals(85, json.getInt("marcadorPropio"), "El marcador propio en el JSON no es el esperado.");
        assertEquals(80, json.getInt("marcadorRival"), "El marcador rival en el JSON no es el esperado.");
        assertEquals("Universidad Rival", json.getString("institucionRival"), "La institución rival en el JSON no es la esperada.");
        assertEquals("Coliseo Norte", json.getString("escenario"), "El escenario en el JSON no es el esperado.");
        assertEquals("Cerros Basket", json.getString("equipo"), "El equipo en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        partido.agregarEstadistica(new EstadisticaBaloncesto(equipoCompleto.getInscripciones().get(2), 14, 7));

        PartidoExterno restaurado = PartidoExterno.fromJSON(partido.toJSON(), mapaEquipos);

        assertEquals(fecha, restaurado.getFecha(), "La fecha restaurada no es la esperada.");
        assertEquals(85, restaurado.getMarcadorPropio(), "El marcador propio restaurado no es el esperado.");
        assertEquals(80, restaurado.getMarcadorRival(), "El marcador rival restaurado no es el esperado.");
        assertEquals("Universidad Rival", restaurado.getInstitucionRival(), "La institución rival restaurada no es la esperada.");
        assertEquals("Coliseo Norte", restaurado.getEscenario(), "El escenario restaurado no es el esperado.");
        assertSame(equipoCompleto, restaurado.getEquipo(), "El equipo restaurado no es el esperado.");
        assertEquals(1, restaurado.getEstadisticas().size(), "Debe restaurarse una estadística.");
        assertSame(equipoCompleto.getInscripciones().get(2), restaurado.getEstadisticas().get(0).getJugador(), "El jugador de la estadística restaurada no es el esperado.");
    }

    @Test
    void testFromJSONEquipoInexistente()
    {
        mapaEquipos.clear();

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> PartidoExterno.fromJSON(partido.toJSON(), mapaEquipos), "Debe lanzarse IllegalArgumentException si el equipo no existe.");

        assertEquals("Equipo desconocido en el partido: Cerros Basket", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinEstadisticas()
    {
        JSONObject json = partido.toJSON();
        json.remove("estadisticasConjunto");

        PartidoExterno restaurado = PartidoExterno.fromJSON(json, mapaEquipos);

        assertEquals(0, restaurado.getEstadisticas().size(), "Sin estadísticas en el JSON el partido debe quedar sin estadísticas.");
    }
}
