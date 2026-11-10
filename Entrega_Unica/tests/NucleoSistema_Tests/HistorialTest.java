package NucleoSistema_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.PartidoPractica;
import DeportesIndividuales.Reserva;
import DeportesIndividuales.Tenis;
import Entrenamiento.SesionEntrenamiento;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaTenis;
import Instalaciones.Instalacion;
import NucleoSistema.Historial;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Usuarios.Entrenador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class HistorialTest
{
    private Historial historial;

    private Tenis tenis;
    private CanchaTenis canchaTenis;
    private CanchaFutbol canchaFutbol;
    private Socio ana;
    private Socio pedro;
    private Entrenador entrenador;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;

    private Reserva reserva;
    private PartidoPractica partidoPractica;
    private SesionEntrenamiento sesion;
    private PartidoInterno partidoInterno;
    private PartidoExterno partidoExterno;

    private LocalDate fecha;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, Equipo> mapaEquipos;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;
    private Map<String, Instalacion> mapaInstalaciones;

    @BeforeEach
    void setUp() throws Exception
    {
        historial = new Historial();
        fecha = LocalDate.of(2026, 11, 10);

        tenis = new Tenis(2, "Tenis");
        canchaTenis = new CanchaTenis("Tenis 1", true, 1, tenis);
        canchaFutbol = new CanchaFutbol("Futbol 1");

        ana = new Socio(20, "Ana Torres", "ana", "1");
        ana.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");
        pedro.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.AVANZADO));
        entrenador = new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01");

        equipoLocal = new Equipo("Local", 8, new Baloncesto(), Categoria.MAYORES);
        equipoVisitante = new Equipo("Visitante", 8, new Baloncesto(), Categoria.MAYORES);

        List<Socio> jugadores = new ArrayList<Socio>();
        jugadores.add(ana);
        jugadores.add(pedro);

        reserva = new Reserva(2, LocalTime.of(10, 0), LocalTime.of(11, 0), fecha, Modalidad.SENCILLOS, ana, canchaTenis);
        partidoPractica = new PartidoPractica(fecha, tenis, jugadores, canchaTenis);
        sesion = new SesionEntrenamiento(fecha, LocalTime.of(8, 0), 60, entrenador, canchaFutbol, jugadores);
        partidoInterno = new PartidoInterno(fecha, equipoLocal, equipoVisitante, 70, 60);
        partidoExterno = new PartidoExterno(fecha, 55, 50, "Rival", "Coliseo", equipoLocal);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", ana);
        mapaUsuarios.put("pgil", pedro);
        mapaUsuarios.put("jruiz", entrenador);

        mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Local", equipoLocal);
        mapaEquipos.put("Visitante", equipoVisitante);

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);

        mapaInstalaciones = new HashMap<String, Instalacion>();
        mapaInstalaciones.put("Tenis 1", canchaTenis);
        mapaInstalaciones.put("Futbol 1", canchaFutbol);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testHistorialNuevoVacio()
    {
        assertEquals(0, historial.getPartidos().size(), "Un historial nuevo no debe tener partidos.");
        assertEquals(0, historial.getPartidosPractica().size(), "Un historial nuevo no debe tener partidos de práctica.");
        assertEquals(0, historial.getReservas().size(), "Un historial nuevo no debe tener reservas.");
        assertEquals(0, historial.getSesiones().size(), "Un historial nuevo no debe tener sesiones.");
    }

    @Test
    void testAgregarReserva()
    {
        historial.agregarReserva(reserva);

        assertEquals(1, historial.getReservas().size(), "El historial debe tener una reserva.");
        assertSame(reserva, historial.getReservas().get(0), "La reserva agregada no es la esperada.");
    }

    @Test
    void testAgregarPartido()
    {
        historial.agregarPartido(partidoInterno);
        historial.agregarPartido(partidoExterno);

        assertEquals(2, historial.getPartidos().size(), "El historial debe tener dos partidos.");
        assertSame(partidoExterno, historial.getPartidos().get(1), "El segundo partido no es el esperado.");
    }

    @Test
    void testAgregarPartidoPractica()
    {
        historial.agregarPartidosPractica(partidoPractica);

        assertEquals(1, historial.getPartidosPractica().size(), "El historial debe tener un partido de práctica.");
        assertSame(partidoPractica, historial.getPartidosPractica().get(0), "El partido de práctica agregado no es el esperado.");
    }

    @Test
    void testAgregarSesion()
    {
        historial.agregarSesion(sesion);

        assertEquals(1, historial.getSesiones().size(), "El historial debe tener una sesión.");
        assertSame(sesion, historial.getSesiones().get(0), "La sesión agregada no es la esperada.");
    }

    @Test
    void testToJSONVacio()
    {
        JSONObject json = historial.toJSON();

        assertEquals(0, json.getJSONArray("reservas").length(), "El JSON no debe tener reservas.");
        assertEquals(0, json.getJSONArray("partidos").length(), "El JSON no debe tener partidos.");
        assertEquals(0, json.getJSONArray("partidosPractica").length(), "El JSON no debe tener partidos de práctica.");
        assertEquals(0, json.getJSONArray("sesiones").length(), "El JSON no debe tener sesiones.");
    }

    @Test
    void testToJSON()
    {
        historial.agregarReserva(reserva);
        historial.agregarPartido(partidoInterno);
        historial.agregarPartido(partidoExterno);
        historial.agregarPartidosPractica(partidoPractica);
        historial.agregarSesion(sesion);

        JSONObject json = historial.toJSON();

        assertEquals(1, json.getJSONArray("reservas").length(), "El JSON debe tener una reserva.");
        assertEquals(2, json.getJSONArray("partidos").length(), "El JSON debe tener dos partidos.");
        assertEquals(1, json.getJSONArray("partidosPractica").length(), "El JSON debe tener un partido de práctica.");
        assertEquals(1, json.getJSONArray("sesiones").length(), "El JSON debe tener una sesión.");
    }

    @Test
    void testFromJSON()
    {
        historial.agregarReserva(reserva);
        historial.agregarPartido(partidoInterno);
        historial.agregarPartido(partidoExterno);
        historial.agregarPartidosPractica(partidoPractica);
        historial.agregarSesion(sesion);
        JSONObject json = new JSONObject(historial.toJSON().toString());

        Historial restaurado = Historial.fromJSON(json, mapaUsuarios, mapaEquipos, mapaDisciplinas, mapaInstalaciones);

        assertEquals(1, restaurado.getReservas().size(), "Debe restaurarse una reserva.");
        assertSame(ana, restaurado.getReservas().get(0).getJugador(), "El jugador de la reserva restaurada no es el esperado.");
        assertEquals(2, restaurado.getPartidos().size(), "Deben restaurarse dos partidos.");
        assertTrue(restaurado.getPartidos().get(0) instanceof PartidoInterno, "El primer partido restaurado debe ser interno.");
        assertTrue(restaurado.getPartidos().get(1) instanceof PartidoExterno, "El segundo partido restaurado debe ser externo.");
        assertEquals(1, restaurado.getPartidosPractica().size(), "Debe restaurarse un partido de práctica.");
        assertEquals(1, restaurado.getSesiones().size(), "Debe restaurarse una sesión.");
        assertSame(entrenador, restaurado.getSesiones().get(0).getEntrenador(), "El entrenador de la sesión restaurada no es el esperado.");
    }

    @Test
    void testFromJSONSinPartidos()
    {
        JSONObject json = historial.toJSON();
        json.remove("partidos");

        Historial restaurado = Historial.fromJSON(json, mapaUsuarios, mapaEquipos, mapaDisciplinas, mapaInstalaciones);

        assertEquals(0, restaurado.getPartidos().size(), "Sin partidos en el JSON el historial debe quedar sin partidos.");
    }

    @Test
    void testFromJSONSinReservas()
    {
        JSONObject json = historial.toJSON();
        json.remove("reservas");

        assertThrows(org.json.JSONException.class, () -> Historial.fromJSON(json, mapaUsuarios, mapaEquipos, mapaDisciplinas, mapaInstalaciones), "Debe lanzarse JSONException si faltan las reservas.");
    }
}
