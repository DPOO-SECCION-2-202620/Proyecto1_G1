package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.TorneoCerros;
import PartidosEquipo_Estadisticas.PartidoExterno;

public class TorneoCerrosTest
{
    private Futbol futbol;
    private Equipo equipoA;
    private Equipo equipoB;
    private TorneoCerros torneo;
    private LocalDate fecha;

    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
        equipoA = new Equipo("Equipo A", 10, futbol, Categoria.MAYORES);
        equipoB = new Equipo("Equipo B", 10, futbol, Categoria.MAYORES);

        torneo = new TorneoCerros("Liga 2026", 2026, 0, Categoria.MAYORES, futbol);
        fecha = LocalDate.of(2026, 11, 10);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Equipo A", equipoA);
        mapaEquipos.put("Equipo B", equipoB);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private PartidoExterno crearPartido(Equipo equipo, int propio, int rival)
    {
        return new PartidoExterno(fecha, propio, rival, "Rival", "Estadio", equipo);
    }

    @Test
    void testEsCompetencia()
    {
        assertTrue(torneo instanceof Competencia, "El torneo de los cerros es una competencia.");
    }

    @Test
    void testDatosFijos()
    {
        assertEquals("Grupo Deportivo Universitario Los Cerros", torneo.getEntidadOrganizadora(), "La entidad organizadora no es la esperada.");
        assertEquals("Liga", torneo.getFormatoJuego(), "El formato del torneo no es el esperado.");
    }

    @Test
    void testTablaInicialVacia()
    {
        assertEquals(0, torneo.getTablaPosiciones().size(), "Un torneo nuevo no debe tener tabla de posiciones.");
        assertNull(torneo.getLider(), "Sin partidos no hay líder.");
    }

    @Test
    void testActualizarTablaVictoria()
    {
        torneo.actualizarTabla(crearPartido(equipoA, 2, 0));

        assertEquals(3, torneo.getTablaPosiciones().get(equipoA), "Una victoria en fútbol suma 3 puntos.");
    }

    @Test
    void testActualizarTablaEmpate()
    {
        torneo.actualizarTabla(crearPartido(equipoA, 1, 1));

        assertEquals(1, torneo.getTablaPosiciones().get(equipoA), "Un empate en fútbol suma 1 punto.");
    }

    @Test
    void testActualizarTablaDerrota()
    {
        torneo.actualizarTabla(crearPartido(equipoA, 0, 1));

        assertTrue(torneo.getTablaPosiciones().containsKey(equipoA), "El equipo debe aparecer en la tabla aunque pierda.");
        assertEquals(0, torneo.getTablaPosiciones().get(equipoA), "Una derrota suma 0 puntos.");
    }

    @Test
    void testActualizarTablaAcumula()
    {
        torneo.actualizarTabla(crearPartido(equipoA, 2, 0));
        torneo.actualizarTabla(crearPartido(equipoA, 1, 1));
        torneo.actualizarTabla(crearPartido(equipoA, 0, 3));

        assertEquals(4, torneo.getTablaPosiciones().get(equipoA), "Los puntos deben acumularse en la tabla.");
    }

    @Test
    void testActualizarTablaConDeporteSinEmpate()
    {
        TorneoCerros torneoBasket = new TorneoCerros("Liga Basket", 2026, 0, Categoria.MAYORES, new Baloncesto());
        Equipo basket = new Equipo("Basket", 10, new Baloncesto(), Categoria.MAYORES);

        assertThrows(IllegalArgumentException.class, () -> torneoBasket.actualizarTabla(new PartidoExterno(fecha, 50, 50, "Rival", "Coliseo", basket)), "Un empate en baloncesto debe lanzar IllegalArgumentException.");
    }

    @Test
    void testGetLider()
    {
        torneo.actualizarTabla(crearPartido(equipoA, 1, 1));
        torneo.actualizarTabla(crearPartido(equipoB, 3, 0));

        assertSame(equipoB, torneo.getLider(), "El líder debe ser el equipo con más puntos.");
    }

    @Test
    void testGetLiderConUnSoloEquipo()
    {
        torneo.actualizarTabla(crearPartido(equipoA, 0, 5));

        assertSame(equipoA, torneo.getLider(), "Con un solo equipo en la tabla ese equipo es el líder.");
    }

    @Test
    void testRegistrarPartido()
    {
        PartidoExterno partido = crearPartido(equipoA, 4, 2);

        torneo.registrarPartido(partido);

        assertEquals(1, torneo.getListaPartidos().size(), "El partido debe quedar en la lista.");
        assertEquals(3, torneo.getTablaPosiciones().get(equipoA), "El partido debe actualizar la tabla.");
    }

    @Test
    void testToJSON()
    {
        torneo.registrarPartido(crearPartido(equipoA, 4, 2));
        torneo.registrarPartido(crearPartido(equipoB, 1, 1));

        JSONObject json = torneo.toJSON();

        assertEquals("TorneoCerros", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals(2, json.getJSONArray("tablaPosiciones").length(), "La tabla en el JSON debe tener dos filas.");
        assertEquals("Equipo A", json.getJSONArray("tablaPosiciones").getJSONObject(0).getString("equipo"), "El primer equipo de la tabla no es el esperado.");
        assertEquals(3, json.getJSONArray("tablaPosiciones").getJSONObject(0).getInt("puntos"), "Los puntos del primer equipo no son los esperados.");
        assertEquals(1, json.getJSONArray("tablaPosiciones").getJSONObject(1).getInt("puntos"), "Los puntos del segundo equipo no son los esperados.");
    }

    @Test
    void testFromJSON()
    {
        torneo.registrarPartido(crearPartido(equipoA, 4, 2));
        torneo.registrarPartido(crearPartido(equipoB, 1, 1));

        TorneoCerros restaurado = TorneoCerros.fromJSON(torneo.toJSON(), Categoria.MAYORES, futbol, mapaEquipos);

        assertEquals("Liga 2026", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(2026, restaurado.getAnio(), "El año restaurado no es el esperado.");
        assertEquals(2, restaurado.getTablaPosiciones().size(), "La tabla restaurada debe tener dos equipos.");
        assertEquals(3, restaurado.getTablaPosiciones().get(equipoA), "Los puntos restaurados de A no son los esperados.");
        assertEquals(1, restaurado.getTablaPosiciones().get(equipoB), "Los puntos restaurados de B no son los esperados.");
        assertSame(equipoA, restaurado.getLider(), "El líder restaurado no es el esperado.");
    }

    @Test
    void testFromJSONSinTabla()
    {
        JSONObject json = torneo.toJSON();
        json.remove("tablaPosiciones");

        TorneoCerros restaurado = TorneoCerros.fromJSON(json, Categoria.MAYORES, futbol, mapaEquipos);

        assertEquals(0, restaurado.getTablaPosiciones().size(), "Sin tabla en el JSON la tabla debe quedar vacía.");
    }

    @Test
    void testFromJSONEquipoInexistenteEnLaTabla()
    {
        torneo.registrarPartido(crearPartido(equipoA, 4, 2));
        mapaEquipos.remove("Equipo A");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> TorneoCerros.fromJSON(torneo.toJSON(), Categoria.MAYORES, futbol, mapaEquipos), "Debe lanzarse IllegalArgumentException si un equipo de la tabla no existe.");

        assertEquals("Equipo desconocido en la tabla: Equipo A", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
