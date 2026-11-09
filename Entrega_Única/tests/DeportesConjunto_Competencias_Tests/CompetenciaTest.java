package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.ASCUN;
import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.FaseASCUN;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.Voleibol;
import DeportesConjunto_Competencias.InscripcionCompetencia;
import DeportesConjunto_Competencias.TorneoCerros;
import PartidosEquipo_Estadisticas.EstadisticaBaloncesto;
import PartidosEquipo_Estadisticas.EstadisticaFutbol;
import PartidosEquipo_Estadisticas.EstadisticaVoleibol;
import PartidosEquipo_Estadisticas.PartidoExterno;
import Usuarios.Socio;

public class CompetenciaTest
{
    private Futbol futbol;
    private Baloncesto baloncesto;

    private Equipo equipoCompleto;
    private Equipo equipoIncompleto;
    private Equipo equipoOtroDeporte;
    private Equipo equipoOtraCategoria;

    private Socio ana;
    private Socio pedro;

    private Competencia competencia;
    private LocalDate fecha;

    private Map<String, DeporteConjunto> mapaDeportes;
    private Map<String, Equipo> mapaEquipos;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
        baloncesto = new Baloncesto();
        fecha = LocalDate.of(2026, 11, 10);

        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");

        equipoCompleto = new Equipo("Los Cerros FC", 10, futbol, Categoria.MAYORES);
        equipoCompleto.agregarJugador(ana, "Delantero", 9);
        equipoCompleto.agregarJugador(pedro, "Portero", 1);
        equipoCompleto.agregarJugador(new Socio(26, "Tercero", "t3", "x"), "Defensa", 3);

        equipoIncompleto = new Equipo("Los Pocos FC", 10, futbol, Categoria.MAYORES);
        equipoIncompleto.agregarJugador(new Socio(27, "Cuarto", "t4", "x"), "Defensa", 4);

        equipoOtroDeporte = new Equipo("Cerros Basket", 10, baloncesto, Categoria.MAYORES);
        equipoOtraCategoria = new Equipo("Cerritos", 10, futbol, Categoria.SUB15);

        competencia = new TorneoCerros("Liga 2026", 2026, 3, Categoria.MAYORES, futbol);

        mapaDeportes = new HashMap<String, DeporteConjunto>();
        mapaDeportes.put("Futbol", futbol);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Los Cerros FC", equipoCompleto);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals("Liga 2026", competencia.getNombre(), "El nombre de la competencia no es el esperado.");
        assertEquals("Grupo Deportivo Universitario Los Cerros", competencia.getEntidadOrganizadora(), "La entidad organizadora no es la esperada.");
        assertEquals(2026, competencia.getAnio(), "El año de la competencia no es el esperado.");
        assertEquals("Liga", competencia.getFormatoJuego(), "El formato de juego no es el esperado.");
        assertEquals(3, competencia.getNominaMinima(), "La nómina mínima no es la esperada.");
        assertEquals(Categoria.MAYORES, competencia.getCategoria(), "La categoría no es la esperada.");
        assertSame(futbol, competencia.getDeporte(), "El deporte no es el esperado.");
        assertEquals(0, competencia.getInscripciones().size(), "Una competencia nueva no debe tener inscripciones.");
        assertEquals(0, competencia.getListaPartidos().size(), "Una competencia nueva no debe tener partidos.");
    }

    @Test
    void testEquipoCumpleRequisitos()
    {
        assertTrue(competencia.equipoCumpleRequisitos(equipoCompleto), "Un equipo del deporte, categoría y tamaño correctos cumple los requisitos.");
    }

    @Test
    void testEquipoNoCumpleRequisitosPorDeporte()
    {
        assertFalse(competencia.equipoCumpleRequisitos(equipoOtroDeporte), "Un equipo de otro deporte no cumple los requisitos.");
    }

    @Test
    void testEquipoNoCumpleRequisitosPorCategoria()
    {
        assertFalse(competencia.equipoCumpleRequisitos(equipoOtraCategoria), "Un equipo de otra categoría no cumple los requisitos.");
    }

    @Test
    void testEquipoNoCumpleRequisitosPorNomina()
    {
        assertFalse(competencia.equipoCumpleRequisitos(equipoIncompleto), "Un equipo con pocos jugadores habilitados no cumple los requisitos.");
    }

    @Test
    void testEquipoNoCumpleRequisitosPorSuspendidos()
    {
        equipoCompleto.getInscripcionDe(ana).suspender();

        assertFalse(competencia.equipoCumpleRequisitos(equipoCompleto), "Los jugadores suspendidos no cuentan para la nómina mínima.");
    }

    @Test
    void testInscribirEquipo()
    {
        assertTrue(competencia.inscribirEquipo(equipoCompleto), "El equipo debe poder inscribirse.");

        assertEquals(1, competencia.getInscripciones().size(), "La competencia debe tener una inscripción.");
        assertSame(equipoCompleto, competencia.getInscripciones().get(0).getEquipo(), "El equipo inscrito no es el esperado.");
        assertSame(competencia, competencia.getInscripciones().get(0).getCompetencia(), "La competencia de la inscripción no es la esperada.");
    }

    @Test
    void testInscribirEquipoRepetido()
    {
        competencia.inscribirEquipo(equipoCompleto);

        assertFalse(competencia.inscribirEquipo(equipoCompleto), "Un equipo no puede inscribirse dos veces.");
        assertEquals(1, competencia.getInscripciones().size(), "Debe haber una sola inscripción.");
    }

    @Test
    void testInscribirEquipoQueNoCumple()
    {
        assertFalse(competencia.inscribirEquipo(equipoIncompleto), "Un equipo que no cumple los requisitos no puede inscribirse.");
        assertFalse(competencia.inscribirEquipo(equipoOtroDeporte), "Un equipo de otro deporte no puede inscribirse.");

        assertEquals(0, competencia.getInscripciones().size(), "No debe haber inscripciones.");
    }

    @Test
    void testGetInscripcionDe()
    {
        competencia.inscribirEquipo(equipoCompleto);

        InscripcionCompetencia inscripcion = competencia.getInscripcionDe(equipoCompleto);

        assertNotNull(inscripcion, "Debe encontrarse la inscripción del equipo.");
        assertSame(equipoCompleto, inscripcion.getEquipo(), "La inscripción debe ser del equipo.");
        assertNull(competencia.getInscripcionDe(equipoIncompleto), "Un equipo no inscrito no tiene inscripción.");
    }

    @Test
    void testRegistrarPartido()
    {
        PartidoExterno partido = new PartidoExterno(fecha, 2, 1, "Rival", "Estadio", equipoCompleto);

        competencia.registrarPartido(partido);

        assertEquals(1, competencia.getListaPartidos().size(), "La competencia debe tener un partido.");
        assertSame(partido, competencia.getListaPartidos().get(0), "El partido registrado no es el esperado.");
    }

    @Test
    void testGetGoleadorSinPartidos()
    {
        assertNull(competencia.getGoleador(), "Sin partidos no hay goleador.");
    }

    @Test
    void testGetGoleadorFutbol()
    {
        PartidoExterno primero = new PartidoExterno(fecha, 3, 0, "Rival 1", "Estadio", equipoCompleto);
        primero.agregarEstadistica(new EstadisticaFutbol(equipoCompleto.getInscripcionDe(ana), 1, 0, 0, 0));
        primero.agregarEstadistica(new EstadisticaFutbol(equipoCompleto.getInscripcionDe(pedro), 2, 0, 0, 0));

        PartidoExterno segundo = new PartidoExterno(fecha.plusDays(7), 2, 1, "Rival 2", "Estadio", equipoCompleto);
        segundo.agregarEstadistica(new EstadisticaFutbol(equipoCompleto.getInscripcionDe(ana), 3, 0, 0, 0));

        competencia.registrarPartido(primero);
        competencia.registrarPartido(segundo);

        assertSame(ana, competencia.getGoleador(), "Ana suma cuatro goles y debe ser la goleadora.");
    }

    @Test
    void testGetGoleadorBaloncestoCuentaPuntos() throws Exception
    {
        Equipo basket = new Equipo("Cerros Basket", 10, baloncesto, Categoria.MAYORES);
        basket.agregarJugador(ana, "Alero", 7);
        basket.agregarJugador(pedro, "Pivot", 5);
        Competencia liga = new TorneoCerros("Liga Basket", 2026, 2, Categoria.MAYORES, baloncesto);

        PartidoExterno partido = new PartidoExterno(fecha, 80, 70, "Rival", "Coliseo", basket);
        partido.agregarEstadistica(new EstadisticaBaloncesto(basket.getInscripcionDe(ana), 12, 10));
        partido.agregarEstadistica(new EstadisticaBaloncesto(basket.getInscripcionDe(pedro), 20, 1));
        liga.registrarPartido(partido);

        assertSame(pedro, liga.getGoleador(), "El goleador debe ser quien más puntos anotó.");
    }

    @Test
    void testGetGoleadorVoleibolCuentaAcesYBloqueos() throws Exception
    {
        Equipo voley = new Equipo("Cerros Voley", 10, new Voleibol(), Categoria.MAYORES);
        voley.agregarJugador(ana, "Central", 3);
        voley.agregarJugador(pedro, "Libero", 4);
        Competencia liga = new TorneoCerros("Liga Voley", 2026, 2, Categoria.MAYORES, new Voleibol());

        PartidoExterno partido = new PartidoExterno(fecha, 3, 1, "Rival", "Coliseo", voley);
        partido.agregarEstadistica(new EstadisticaVoleibol(voley.getInscripcionDe(ana), 2, 2));
        partido.agregarEstadistica(new EstadisticaVoleibol(voley.getInscripcionDe(pedro), 4, 3));
        liga.registrarPartido(partido);

        assertSame(pedro, liga.getGoleador(), "El máximo anotador debe ser quien más aces y bloqueos suma.");
    }

    @Test
    void testGetJugadorDestacadoSinPartidos()
    {
        assertNull(competencia.getJugadorDestacado(), "Sin partidos no hay jugador destacado.");
    }

    @Test
    void testGetJugadorDestacado()
    {
        PartidoExterno partido = new PartidoExterno(fecha, 3, 0, "Rival", "Estadio", equipoCompleto);
        partido.agregarEstadistica(new EstadisticaFutbol(equipoCompleto.getInscripcionDe(ana), 1, 0, 0, 0));
        partido.agregarEstadistica(new EstadisticaFutbol(equipoCompleto.getInscripcionDe(pedro), 1, 3, 0, 0));
        competencia.registrarPartido(partido);

        assertSame(pedro, competencia.getJugadorDestacado(), "Pedro aporta cinco contra dos de Ana y debe ser el destacado.");
    }

    @Test
    void testToJSON()
    {
        competencia.inscribirEquipo(equipoCompleto);
        competencia.registrarPartido(new PartidoExterno(fecha, 2, 1, "Rival", "Estadio", equipoCompleto));

        JSONObject json = competencia.toJSON();

        assertEquals("TorneoCerros", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Liga 2026", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals("Grupo Deportivo Universitario Los Cerros", json.getString("entidadOrganizadora"), "La entidad en el JSON no es la esperada.");
        assertEquals(2026, json.getInt("anio"), "El año en el JSON no es el esperado.");
        assertEquals("Liga", json.getString("formatoJuego"), "El formato en el JSON no es el esperado.");
        assertEquals(3, json.getInt("nominaMinima"), "La nómina mínima en el JSON no es la esperada.");
        assertEquals("MAYORES", json.getString("categoria"), "La categoría en el JSON no es la esperada.");
        assertEquals("Futbol", json.getJSONObject("deporte").getString("nombre"), "El deporte en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("inscripciones").length(), "El JSON debe tener una inscripción.");
        assertEquals(1, json.getJSONArray("listaPartidos").length(), "El JSON debe tener un partido.");
    }

    @Test
    void testFromJSONTorneoCerros()
    {
        competencia.inscribirEquipo(equipoCompleto);
        competencia.registrarPartido(new PartidoExterno(fecha, 2, 1, "Rival", "Estadio", equipoCompleto));

        Competencia restaurada = Competencia.fromJSON(competencia.toJSON(), mapaDeportes, mapaEquipos);

        assertTrue(restaurada instanceof TorneoCerros, "El tipo TorneoCerros debe crear un torneo.");
        assertEquals("Liga 2026", restaurada.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(2026, restaurada.getAnio(), "El año restaurado no es el esperado.");
        assertEquals(3, restaurada.getNominaMinima(), "La nómina mínima restaurada no es la esperada.");
        assertEquals(Categoria.MAYORES, restaurada.getCategoria(), "La categoría restaurada no es la esperada.");
        assertSame(futbol, restaurada.getDeporte(), "El deporte restaurado debe ser el del mapa.");
        assertEquals(1, restaurada.getInscripciones().size(), "Debe restaurarse una inscripción.");
        assertEquals(1, restaurada.getListaPartidos().size(), "Debe restaurarse un partido.");
        assertEquals(1, ((TorneoCerros) restaurada).getTablaPosiciones().size(), "Debe restaurarse la tabla de posiciones con un equipo.");
        assertEquals(3, ((TorneoCerros) restaurada).getTablaPosiciones().get(equipoCompleto), "Los puntos restaurados del equipo no son los esperados.");
    }

    @Test
    void testFromJSONASCUN()
    {
        Competencia ascun = new ASCUN("ASCUN 2026", 2026, 3, Categoria.MAYORES, futbol, FaseASCUN.ELIMINACION_DIRECTA);

        Competencia restaurada = Competencia.fromJSON(ascun.toJSON(), mapaDeportes, mapaEquipos);

        assertTrue(restaurada instanceof ASCUN, "El tipo ASCUN debe crear una competencia ASCUN.");
        assertEquals(FaseASCUN.ELIMINACION_DIRECTA, ((ASCUN) restaurada).getFase(), "La fase restaurada no es la esperada.");
    }

    @Test
    void testFromJSONDeporteComoTexto()
    {
        JSONObject json = competencia.toJSON();
        json.put("deporte", "Futbol");

        Competencia restaurada = Competencia.fromJSON(json, mapaDeportes, mapaEquipos);

        assertSame(futbol, restaurada.getDeporte(), "El deporte restaurado debe ser el del mapa.");
    }

    @Test
    void testFromJSONDeporteTextoInexistente()
    {
        JSONObject json = competencia.toJSON();
        json.put("deporte", "Golf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Competencia.fromJSON(json, mapaDeportes, mapaEquipos), "Debe lanzarse IllegalArgumentException si el deporte no existe.");

        assertEquals("Deporte no encontrado para la competencia: Golf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = competencia.toJSON();
        json.put("tipo", "Olimpiadas");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Competencia.fromJSON(json, mapaDeportes, mapaEquipos), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de competencia desconocido: Olimpiadas", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONPartidosConClaveAlternativa()
    {
        competencia.registrarPartido(new PartidoExterno(fecha, 2, 1, "Rival", "Estadio", equipoCompleto));
        JSONObject json = competencia.toJSON();
        JSONArray partidos = json.getJSONArray("listaPartidos");
        json.remove("listaPartidos");
        json.put("partidos", partidos);

        Competencia restaurada = Competencia.fromJSON(json, mapaDeportes, mapaEquipos);

        assertEquals(1, restaurada.getListaPartidos().size(), "Los partidos deben poder leerse desde la clave partidos.");
    }
}
