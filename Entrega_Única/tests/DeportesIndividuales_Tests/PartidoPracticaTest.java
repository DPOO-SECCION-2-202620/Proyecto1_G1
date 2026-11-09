package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.EstadisticaPractica;
import DeportesIndividuales.EstadisticaRaqueta;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaTenis;
import Instalaciones.Instalacion;
import Usuarios.Administrador;
import Usuarios.Entrenador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class PartidoPracticaTest
{
    private Tenis tenis;
    private CanchaTenis cancha;
    private LocalDate fecha;

    private Socio ana;
    private Socio pedro;
    private Socio luis;
    private Socio sofia;
    private Socio sinNivel;
    private Entrenador entrenador;

    private PartidoPractica partido;
    private PartidoPractica partidoIncompatible;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;
    private Map<String, Instalacion> mapaInstalaciones;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        cancha = new CanchaTenis("Tenis 1", true, 1, tenis);
        fecha = LocalDate.of(2026, 11, 10);

        ana = new Socio(20, "Ana Torres", "ana", "1");
        ana.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));

        pedro = new Socio(21, "Pedro Gil", "pgil", "2");
        pedro.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.AVANZADO));

        luis = new Socio(22, "Luis Mora", "lmora", "3");
        luis.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.PRINCIPIANTE));

        sofia = new Socio(23, "Sofia Vega", "svega", "4");
        sofia.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));

        sinNivel = new Socio(24, "Sin Nivel", "sinnivel", "5");

        entrenador = new Entrenador(40, "Jorge Ruiz", "jruiz", "6", "ENT01");

        partido = new PartidoPractica(fecha, tenis, crearLista(ana, pedro), cancha);
        partidoIncompatible = new PartidoPractica(fecha, tenis, crearLista(luis, pedro), cancha);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", ana);
        mapaUsuarios.put("pgil", pedro);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);

        mapaInstalaciones = new HashMap<String, Instalacion>();
        mapaInstalaciones.put("Tenis 1", cancha);
        mapaInstalaciones.put("Futbol 1", new CanchaFutbol("Futbol 1"));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private List<Socio> crearLista(Socio... socios)
    {
        List<Socio> lista = new ArrayList<Socio>();
        for(Socio socio : socios)
        {
            lista.add(socio);
        }
        return lista;
    }

    private List<EstadisticaPractica> crearEstadisticas(int setsAna, int setsPedro)
    {
        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(setsAna, ana, 12, 3, 5));
        estadisticas.add(new EstadisticaRaqueta(setsPedro, pedro, 10, 4, 6));
        return estadisticas;
    }

    @Test
    void testGetters()
    {
        assertEquals(fecha, partido.getFecha(), "La fecha del partido no es la esperada.");
        assertFalse(partido.isFinalizado(), "Un partido nuevo no debe estar finalizado.");
        assertSame(tenis, partido.getDisciplina(), "La disciplina del partido no es la esperada.");
        assertEquals(2, partido.getJugadores().size(), "El partido debe tener dos jugadores.");
        assertSame(cancha, partido.getInstalacion(), "La instalación del partido no es la esperada.");
        assertEquals(0, partido.getEstadisticas().size(), "Un partido nuevo no debe tener estadísticas.");
    }

    @Test
    void testConstructorConCuatroJugadores()
    {
        PartidoPractica dobles = new PartidoPractica(fecha, tenis, crearLista(ana, pedro, luis, sofia), cancha);

        assertEquals(4, dobles.getJugadores().size(), "Un partido de dobles debe tener cuatro jugadores.");
    }

    @Test
    void testConstructorConNumeroDeJugadoresInvalido()
    {
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> new PartidoPractica(fecha, tenis, crearLista(ana, pedro, luis), cancha), "Debe lanzarse IllegalArgumentException con tres jugadores.");

        assertEquals("Un partido de práctica tiene 2 o 4 jugadores", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertThrows(IllegalArgumentException.class, () -> new PartidoPractica(fecha, tenis, crearLista(ana), cancha), "Debe lanzarse IllegalArgumentException con un jugador.");
        assertThrows(IllegalArgumentException.class, () -> new PartidoPractica(fecha, tenis, new ArrayList<Socio>(), cancha), "Debe lanzarse IllegalArgumentException sin jugadores.");
    }

    @Test
    void testJugadoresSeCopian()
    {
        List<Socio> original = crearLista(ana, pedro);
        PartidoPractica copia = new PartidoPractica(fecha, tenis, original, cancha);

        original.add(luis);

        assertEquals(2, copia.getJugadores().size(), "Modificar la lista original no debe afectar al partido.");
    }

    @Test
    void testValidarNivelesCompatibles()
    {
        assertTrue(partido.validarNiveles(), "Intermedio y avanzado son compatibles.");
    }

    @Test
    void testValidarNivelesIncompatibles()
    {
        assertFalse(partidoIncompatible.validarNiveles(), "Principiante y avanzado no son compatibles.");
    }

    @Test
    void testValidarNivelesConEntrenador()
    {
        PartidoPractica conEntrenador = new PartidoPractica(fecha, tenis, crearLista(luis, entrenador), cancha);

        assertTrue(conEntrenador.validarNiveles(), "Un partido con un entrenador siempre es válido.");
    }

    @Test
    void testValidarNivelesJugadorSinInscripcion()
    {
        PartidoPractica conSinNivel = new PartidoPractica(fecha, tenis, crearLista(ana, sinNivel), cancha);

        assertFalse(conSinNivel.validarNiveles(), "Un jugador sin nivel en la disciplina invalida el partido.");
    }

    @Test
    void testValidarNivelesDobles()
    {
        PartidoPractica dobles = new PartidoPractica(fecha, tenis, crearLista(ana, pedro, sofia, ana), cancha);
        PartidoPractica doblesIncompatibles = new PartidoPractica(fecha, tenis, crearLista(ana, pedro, luis, sofia), cancha);

        assertTrue(dobles.validarNiveles(), "Cuatro jugadores intermedios y avanzados son compatibles.");
        assertFalse(doblesIncompatibles.validarNiveles(), "Un principiante y un avanzado en el mismo partido no son compatibles.");
    }

    @Test
    void testFinalizar() throws Exception
    {
        partido.finalizar(crearEstadisticas(2, 1));

        assertTrue(partido.isFinalizado(), "El partido debe quedar finalizado.");
        assertEquals(2, partido.getEstadisticas().size(), "El partido debe tener dos estadísticas.");
    }

    @Test
    void testFinalizarDosVeces() throws Exception
    {
        partido.finalizar(crearEstadisticas(2, 1));

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> partido.finalizar(crearEstadisticas(1, 2)), "Debe lanzarse ReglaNegocioException si el partido ya está finalizado.");

        assertEquals("El partido ya está finalizado", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(2, partido.getEstadisticas().size(), "Las estadísticas no deben duplicarse.");
    }

    @Test
    void testFinalizarConNumeroDeEstadisticasIncorrecto()
    {
        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, ana, 12, 3, 5));

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> partido.finalizar(estadisticas), "Debe lanzarse ReglaNegocioException si falta una estadística.");

        assertEquals("Debe haber una estadística por jugador", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertFalse(partido.isFinalizado(), "El partido no debe quedar finalizado.");
        assertEquals(0, partido.getEstadisticas().size(), "No debe guardarse ninguna estadística.");
    }

    @Test
    void testFinalizarConJugadorAjeno()
    {
        List<EstadisticaPractica> estadisticas = new ArrayList<EstadisticaPractica>();
        estadisticas.add(new EstadisticaRaqueta(2, ana, 12, 3, 5));
        estadisticas.add(new EstadisticaRaqueta(1, luis, 10, 4, 6));

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> partido.finalizar(estadisticas), "Debe lanzarse ReglaNegocioException si una estadística es de un jugador ajeno.");

        assertEquals("Luis Mora no juega este partido", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertFalse(partido.isFinalizado(), "El partido no debe quedar finalizado.");
    }

    @Test
    void testGetGanadorSinEstadisticas()
    {
        assertNull(partido.getGanador(), "Sin estadísticas no hay ganador.");
    }

    @Test
    void testGetGanador() throws Exception
    {
        partido.finalizar(crearEstadisticas(1, 2));

        assertSame(pedro, partido.getGanador(), "El ganador debe ser quien ganó más sets.");
    }

    @Test
    void testGetGanadorPrimeroGana() throws Exception
    {
        partido.finalizar(crearEstadisticas(3, 0));

        assertSame(ana, partido.getGanador(), "El ganador debe ser quien ganó más sets.");
    }

    @Test
    void testGetGanadorEmpate() throws Exception
    {
        partido.finalizar(crearEstadisticas(2, 2));

        assertSame(ana, partido.getGanador(), "En caso de empate gana el primero de la lista.");
    }

    @Test
    void testToJSON() throws Exception
    {
        partido.finalizar(crearEstadisticas(2, 1));

        JSONObject json = partido.toJSON();

        assertEquals("2026-11-10", json.get("fecha").toString(), "La fecha en el JSON no es la esperada.");
        assertTrue(json.getBoolean("finalizado"), "El partido en el JSON debe estar finalizado.");
        assertEquals("Tenis", json.getString("disciplina"), "La disciplina en el JSON no es la esperada.");
        assertEquals(2, json.getJSONArray("jugadores").length(), "El JSON debe tener dos jugadores.");
        assertEquals("ana", json.getJSONArray("jugadores").getString(0), "El primer jugador en el JSON no es el esperado.");
        assertEquals("Tenis 1", json.getString("instalacion"), "La instalación en el JSON no es la esperada.");
        assertEquals(2, json.getJSONArray("estadisticas").length(), "El JSON debe tener dos estadísticas.");
    }

    @Test
    void testToJSONPartidoSinFinalizar()
    {
        JSONObject json = partido.toJSON();

        assertFalse(json.getBoolean("finalizado"), "El partido en el JSON no debe estar finalizado.");
        assertEquals(0, json.getJSONArray("estadisticas").length(), "El JSON no debe tener estadísticas.");
    }

    @Test
    void testFromJSON() throws Exception
    {
        partido.finalizar(crearEstadisticas(2, 1));
        JSONObject json = new JSONObject(partido.toJSON().toString());

        PartidoPractica restaurado = PartidoPractica.fromJSON(json, mapaUsuarios, mapaDisciplinas, mapaInstalaciones);

        assertEquals(fecha, restaurado.getFecha(), "La fecha restaurada no es la esperada.");
        assertTrue(restaurado.isFinalizado(), "El partido restaurado debe estar finalizado.");
        assertSame(tenis, restaurado.getDisciplina(), "La disciplina restaurada no es la esperada.");
        assertSame(cancha, restaurado.getInstalacion(), "La instalación restaurada no es la esperada.");
        assertEquals(2, restaurado.getJugadores().size(), "Deben restaurarse los dos jugadores.");
        assertSame(ana, restaurado.getJugadores().get(0), "El primer jugador restaurado no es el esperado.");
        assertEquals(2, restaurado.getEstadisticas().size(), "Deben restaurarse las dos estadísticas.");
        assertSame(ana, restaurado.getGanador(), "El ganador restaurado no es el esperado.");
    }

    @Test
    void testFromJSONSinFinalizar()
    {
        JSONObject json = new JSONObject(partido.toJSON().toString());

        PartidoPractica restaurado = PartidoPractica.fromJSON(json, mapaUsuarios, mapaDisciplinas, mapaInstalaciones);

        assertFalse(restaurado.isFinalizado(), "El partido restaurado no debe estar finalizado.");
        assertEquals(0, restaurado.getEstadisticas().size(), "El partido restaurado no debe tener estadísticas.");
    }

    @Test
    void testFromJSONDisciplinaDesconocida()
    {
        JSONObject json = new JSONObject(partido.toJSON().toString());
        json.put("disciplina", "Golf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> PartidoPractica.fromJSON(json, mapaUsuarios, mapaDisciplinas, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si la disciplina es desconocida.");

        assertEquals("Disciplina desconocida en el partido: Golf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONInstalacionNoIndividual()
    {
        JSONObject json = new JSONObject(partido.toJSON().toString());
        json.put("instalacion", "Futbol 1");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> PartidoPractica.fromJSON(json, mapaUsuarios, mapaDisciplinas, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si la instalación no es individual.");

        assertEquals("Instalación individual desconocida: Futbol 1", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONJugadorNoEsSocio()
    {
        JSONObject json = new JSONObject(partido.toJSON().toString());
        json.getJSONArray("jugadores").put(0, "admin");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> PartidoPractica.fromJSON(json, mapaUsuarios, mapaDisciplinas, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si un jugador no es socio.");

        assertEquals("Jugador no válido en el partido: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
