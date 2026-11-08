package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Tenis;
import Usuarios.Administrador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class RankingJugadorDisciplinaTest
{
    private Socio socio;
    private Tenis tenis;
    private RankingJugadorDisciplina ranking;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1234");
        tenis = new Tenis(2, "Tenis");
        ranking = new RankingJugadorDisciplina(socio, tenis);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", socio);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testRankingNuevo()
    {
        assertEquals(1000, ranking.getPuntajeElo(), 0.001, "El puntaje inicial debe ser 1000.");
        assertSame(socio, ranking.getJugador(), "El jugador del ranking no es el esperado.");
        assertSame(tenis, ranking.getDisciplina(), "La disciplina del ranking no es la esperada.");
    }

    @Test
    void testsetPuntajeElo()
    {
        ranking.setPuntajeElo(1234.5);

        assertEquals(1234.5, ranking.getPuntajeElo(), 0.001, "El puntaje modificado no es el esperado.");
    }

    @Test
    void testCalcularPuntajeEloGanaContraRivalIgual()
    {
        ranking.calcularPuntajeElo(1000, true);

        assertEquals(1016, ranking.getPuntajeElo(), 0.001, "Ganar contra un rival igual debe sumar 16 puntos.");
    }

    @Test
    void testCalcularPuntajeEloPierdeContraRivalIgual()
    {
        ranking.calcularPuntajeElo(1000, false);

        assertEquals(984, ranking.getPuntajeElo(), 0.001, "Perder contra un rival igual debe restar 16 puntos.");
    }

    @Test
    void testCalcularPuntajeEloGanaContraRivalMasFuerte()
    {
        ranking.calcularPuntajeElo(1400, true);

        assertEquals(1029.0909, ranking.getPuntajeElo(), 0.001, "Ganar contra un rival más fuerte debe sumar más puntos.");
    }

    @Test
    void testCalcularPuntajeEloPierdeContraRivalMasFuerte()
    {
        ranking.calcularPuntajeElo(1400, false);

        assertEquals(997.0909, ranking.getPuntajeElo(), 0.001, "Perder contra un rival más fuerte debe restar pocos puntos.");
    }

    @Test
    void testCalcularPuntajeEloGanaContraRivalMasDebil()
    {
        ranking.calcularPuntajeElo(600, true);

        assertEquals(1002.9091, ranking.getPuntajeElo(), 0.001, "Ganar contra un rival más débil debe sumar pocos puntos.");
    }

    @Test
    void testCalcularPuntajeEloVariasVeces()
    {
        ranking.calcularPuntajeElo(1000, true);
        ranking.calcularPuntajeElo(1000, false);

        assertEquals(1016 + 32 * (0 - 1.0 / (1 + Math.pow(10, (1000 - 1016) / 400.0))), ranking.getPuntajeElo(), 0.001, "El puntaje acumulado no es el esperado.");
    }

    @Test
    void testToJSON()
    {
        ranking.setPuntajeElo(1100.25);

        JSONObject json = ranking.toJSON();

        assertEquals(1100.25, json.getDouble("puntajeElo"), 0.001, "El puntaje en el JSON no es el esperado.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals("Tenis", json.getString("disciplina"), "La disciplina en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        ranking.setPuntajeElo(1100.25);

        RankingJugadorDisciplina restaurado = RankingJugadorDisciplina.fromJSON(ranking.toJSON(), mapaUsuarios, mapaDisciplinas);

        assertEquals(1100.25, restaurado.getPuntajeElo(), 0.001, "El puntaje restaurado no es el esperado.");
        assertSame(socio, restaurado.getJugador(), "El jugador restaurado no es el esperado.");
        assertSame(tenis, restaurado.getDisciplina(), "La disciplina restaurada no es la esperada.");
    }

    @Test
    void testFromJSONJugadorNoEsSocio()
    {
        JSONObject json = ranking.toJSON();
        json.put("jugador", "admin");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> RankingJugadorDisciplina.fromJSON(json, mapaUsuarios, mapaDisciplinas), "Debe lanzarse IllegalArgumentException si el jugador no es socio.");

        assertEquals("El jugador del ranking no es un socio válido: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONDisciplinaDesconocida()
    {
        JSONObject json = ranking.toJSON();
        json.put("disciplina", "Golf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> RankingJugadorDisciplina.fromJSON(json, mapaUsuarios, mapaDisciplinas), "Debe lanzarse IllegalArgumentException si la disciplina es desconocida.");

        assertEquals("Disciplina desconocida en ranking: Golf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
