package DeportesConjunto_Competencias_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.ASCUN;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.FaseASCUN;
import DeportesConjunto_Competencias.Futbol;
import PartidosEquipo_Estadisticas.PartidoExterno;
import Usuarios.Socio;

public class ASCUNTest
{
    private Futbol futbol;
    private Equipo equipoGanador;
    private Equipo equipoPerdedor;
    private Equipo equipoParejo;
    private ASCUN ascun;
    private LocalDate fecha;

    @BeforeEach
    void setUp() throws Exception
    {
        futbol = new Futbol();
        fecha = LocalDate.of(2026, 11, 10);

        equipoGanador = crearEquipo("Ganador", "g");
        equipoPerdedor = crearEquipo("Perdedor", "p");
        equipoParejo = crearEquipo("Parejo", "e");

        ascun = new ASCUN("ASCUN 2026", 2026, 1, Categoria.MAYORES, futbol, FaseASCUN.GRUPOS);
        ascun.inscribirEquipo(equipoGanador);
        ascun.inscribirEquipo(equipoPerdedor);
        ascun.inscribirEquipo(equipoParejo);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private Equipo crearEquipo(String nombre, String prefijo)
    {
        Equipo equipo = new Equipo(nombre, 10, futbol, Categoria.MAYORES);
        equipo.agregarJugador(new Socio(20, "Jugador " + prefijo, prefijo, "x"), "Delantero", 9);
        return equipo;
    }

    private PartidoExterno crearPartido(Equipo equipo, int propio, int rival)
    {
        return new PartidoExterno(fecha, propio, rival, "Rival", "Estadio", equipo);
    }

    @Test
    void testEsCompetencia()
    {
        assertTrue(ascun instanceof Competencia, "ASCUN es una competencia.");
    }

    @Test
    void testDatosFijos()
    {
        assertEquals("ASCUN", ascun.getEntidadOrganizadora(), "La entidad organizadora no es la esperada.");
        assertEquals("GRUPOS y eliminación directa", ascun.getFormatoJuego(), "El formato de juego no es el esperado.");
        assertEquals("ASCUN 2026", ascun.getNombre(), "El nombre no es el esperado.");
        assertEquals(2026, ascun.getAnio(), "El año no es el esperado.");
    }

    @Test
    void testAvanzarFase()
    {
        ascun.avanzarFase();

        assertEquals(FaseASCUN.ELIMINACION_DIRECTA, ascun.getFase(), "Después de avanzar la fase debe ser eliminación directa.");
    }

    @Test
    void testAvanzarFaseDosVeces()
    {
        ascun.avanzarFase();
        ascun.avanzarFase();

        assertEquals(FaseASCUN.ELIMINACION_DIRECTA, ascun.getFase(), "La fase de eliminación directa es la última.");
    }

    @Test
    void testRegistrarPartido()
    {
        PartidoExterno partido = crearPartido(equipoGanador, 2, 0);

        ascun.registrarPartido(partido);

        assertEquals(1, ascun.getListaPartidos().size(), "El partido debe quedar en la lista.");
        assertSame(partido, ascun.getListaPartidos().get(0), "El partido registrado no es el esperado.");
    }

    @Test
    void testDeterminarClasificadosSinPartidos()
    {
        assertEquals(0, ascun.determinarClasificados().size(), "Sin partidos ningún equipo clasifica.");
    }

    @Test
    void testDeterminarClasificados()
    {
        ascun.registrarPartido(crearPartido(equipoGanador, 2, 0));
        ascun.registrarPartido(crearPartido(equipoGanador, 1, 0));
        ascun.registrarPartido(crearPartido(equipoGanador, 0, 1));
        ascun.registrarPartido(crearPartido(equipoPerdedor, 0, 3));
        ascun.registrarPartido(crearPartido(equipoParejo, 1, 0));
        ascun.registrarPartido(crearPartido(equipoParejo, 0, 1));

        List<Equipo> clasificados = ascun.determinarClasificados();

        assertEquals(1, clasificados.size(), "Solo debe clasificar un equipo.");
        assertSame(equipoGanador, clasificados.get(0), "Clasifica el equipo con más victorias que derrotas.");
    }

    @Test
    void testDeterminarClasificadosIgnoraEmpates()
    {
        ascun.registrarPartido(crearPartido(equipoParejo, 1, 1));
        ascun.registrarPartido(crearPartido(equipoParejo, 2, 2));
        ascun.registrarPartido(crearPartido(equipoParejo, 3, 2));

        List<Equipo> clasificados = ascun.determinarClasificados();

        assertEquals(1, clasificados.size(), "Los empates no cuentan como derrota.");
        assertSame(equipoParejo, clasificados.get(0), "Con una victoria y dos empates el equipo clasifica.");
    }

    @Test
    void testDeterminarClasificadosSoloEquiposInscritos()
    {
        Equipo noInscrito = crearEquipo("No Inscrito", "n");
        ascun.registrarPartido(crearPartido(noInscrito, 5, 0));

        assertEquals(0, ascun.determinarClasificados().size(), "Un equipo no inscrito no puede clasificar.");
    }

    @Test
    void testToJSON()
    {
        ascun.avanzarFase();

        JSONObject json = ascun.toJSON();

        assertEquals("ASCUN", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("ELIMINACION_DIRECTA", json.getString("fase"), "La fase en el JSON no es la esperada.");
        assertEquals("ASCUN 2026", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(3, json.getJSONArray("inscripciones").length(), "El JSON debe tener tres inscripciones.");
    }

    @Test
    void testFromJSON()
    {
        ascun.avanzarFase();

        ASCUN restaurado = ASCUN.fromJSON(ascun.toJSON(), Categoria.MAYORES, futbol);

        assertEquals("ASCUN 2026", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(2026, restaurado.getAnio(), "El año restaurado no es el esperado.");
        assertEquals(1, restaurado.getNominaMinima(), "La nómina mínima restaurada no es la esperada.");
        assertEquals(FaseASCUN.ELIMINACION_DIRECTA, restaurado.getFase(), "La fase restaurada no es la esperada.");
        assertSame(futbol, restaurado.getDeporte(), "El deporte restaurado no es el esperado.");
    }

    @Test
    void testFromJSONFaseInvalida()
    {
        JSONObject json = ascun.toJSON();
        json.put("fase", "FINAL");

        assertThrows(IllegalArgumentException.class, () -> ASCUN.fromJSON(json, Categoria.MAYORES, futbol), "Una fase desconocida debe lanzar IllegalArgumentException.");
    }
}
