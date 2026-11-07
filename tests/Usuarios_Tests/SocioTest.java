package Usuarios_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;
import Usuarios.Socio;

public class SocioTest
{
    private Socio socio;
    private Socio socioConPuntos;

    private Futbol futbol;
    private Baloncesto baloncesto;
    private Equipo equipoFutbol;

    private Tenis tenis;
    private Padel padel;

    private LocalDateTime momento;

    @BeforeEach
    void setUp() throws Exception
    {
        socio = new Socio(20, "Ana Torres", "ana", "1234");

        socioConPuntos = new Socio(30, "Pedro Gil", "pgil", "5678");
        socioConPuntos.acumularPuntos(100);

        futbol = new Futbol();
        baloncesto = new Baloncesto();
        equipoFutbol = new Equipo("Los Cerros FC", 12, futbol, Categoria.MAYORES);

        tenis = new Tenis(2, "Tenis");
        padel = new Padel(1, "Padel");

        momento = LocalDateTime.of(2026, 11, 4, 10, 0);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testsocioNuevo()
    {
        assertEquals(20, socio.getEdad(), "La edad del socio no es la esperada.");
        assertEquals("Ana Torres", socio.getNombre(), "El nombre del socio no es el esperado.");
        assertEquals("ana", socio.getLogin(), "El login del socio no es el esperado.");
        assertEquals(0, socio.getPuntosFidelidad(), "Un socio nuevo no debe tener puntos.");
        assertNull(socio.getHoraUltimaBebidaCaliente(), "Un socio nuevo no debe tener una bebida caliente registrada.");
        assertEquals(0, socio.getInscripcionesEquipo().size(), "Un socio nuevo no debe tener inscripciones a equipos.");
        assertEquals(0, socio.getInscripcionesIndividuales().size(), "Un socio nuevo no debe tener inscripciones individuales.");
    }

    @Test
    void testMinutosBebidaCaliente()
    {
        assertEquals(30, Socio.MINUTOS_BEBIDA_CALIENTE, "El tiempo de restricción por bebida caliente no es el esperado.");
    }

    @Test
    void testEsEntrenador()
    {
        assertFalse(socio.esEntrenador(), "Un socio común no es entrenador.");
    }

    @Test
    void testAcumularPuntos()
    {
        socio.acumularPuntos(50);
        assertEquals(50, socio.getPuntosFidelidad(), "Los puntos acumulados no son los esperados.");

        socio.acumularPuntos(25);
        assertEquals(75, socio.getPuntosFidelidad(), "Los puntos deben sumarse a los que ya existen.");
    }

    @Test
    void testAcumularPuntosInvalidos()
    {
        socio.acumularPuntos(0);
        assertEquals(0, socio.getPuntosFidelidad(), "Acumular 0 puntos no debe cambiar el saldo.");

        socio.acumularPuntos(-10);
        assertEquals(0, socio.getPuntosFidelidad(), "No se deben acumular puntos negativos.");
    }

    @Test
    void testRedimirPuntos()
    {
        socioConPuntos.redimirPuntos(40);
        assertEquals(60, socioConPuntos.getPuntosFidelidad(), "Los puntos redimidos deben restarse del saldo.");

        socioConPuntos.redimirPuntos(60);
        assertEquals(0, socioConPuntos.getPuntosFidelidad(), "Debe poder redimirse todo el saldo.");
    }

    @Test
    void testRedimirPuntosMasDeLosDisponibles()
    {
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> socioConPuntos.redimirPuntos(101), "Debe lanzarse IllegalArgumentException si se redimen más puntos de los disponibles.");

        assertEquals("Los puntos no pueden ser redimidos", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(100, socioConPuntos.getPuntosFidelidad(), "El saldo no debe cambiar si la redención falla.");
    }

    @Test
    void testRedimirPuntosCeroONegativos()
    {
        assertThrows(IllegalArgumentException.class, () -> socioConPuntos.redimirPuntos(0), "No se puede redimir 0 puntos.");
        assertThrows(IllegalArgumentException.class, () -> socioConPuntos.redimirPuntos(-5), "No se pueden redimir puntos negativos.");
        assertEquals(100, socioConPuntos.getPuntosFidelidad(), "El saldo no debe cambiar si la redención falla.");
    }

    @Test
    void testRedimirPuntosSinSaldo()
    {
        assertThrows(IllegalArgumentException.class, () -> socio.redimirPuntos(1), "Un socio sin puntos no puede redimir.");
    }

    @Test
    void testLlevaBebidaCalienteSinRegistro()
    {
        assertFalse(socio.llevaBebidaCaliente(momento), "Sin bebida registrada el socio no lleva bebida caliente.");
    }

    @Test
    void testRegistrarBebidaCaliente()
    {
        socio.registrarBebidaCaliente(momento);

        assertEquals(momento, socio.getHoraUltimaBebidaCaliente(), "La hora de la última bebida caliente no es la esperada.");
        assertTrue(socio.llevaBebidaCaliente(momento), "Justo al registrar la bebida el socio la lleva.");
    }

    @Test
    void testLlevaBebidaCalienteDentroDelLimite()
    {
        socio.registrarBebidaCaliente(momento);

        assertTrue(socio.llevaBebidaCaliente(momento.plusMinutes(29)), "A los 29 minutos el socio todavía lleva la bebida.");
    }

    @Test
    void testLlevaBebidaCalienteEnElLimite()
    {
        socio.registrarBebidaCaliente(momento);

        assertFalse(socio.llevaBebidaCaliente(momento.plusMinutes(30)), "A los 30 minutos el socio ya no lleva la bebida.");
        assertFalse(socio.llevaBebidaCaliente(momento.plusHours(5)), "Mucho tiempo después el socio no lleva la bebida.");
    }

    @Test
    void testLlevaBebidaCalienteMomentoAnterior()
    {
        socio.registrarBebidaCaliente(momento);

        assertFalse(socio.llevaBebidaCaliente(momento.minusMinutes(1)), "Un momento anterior al registro no cuenta como llevar la bebida.");
    }

    @Test
    void testTieneEquipoEn()
    {
        assertFalse(socio.tieneEquipoEn(futbol), "Un socio sin equipos no tiene equipo en ningún deporte.");

        assertTrue(equipoFutbol.agregarJugador(socio, "Delantero", 9), "El socio debería poder inscribirse al equipo.");

        assertTrue(socio.tieneEquipoEn(futbol), "El socio debe tener equipo en fútbol.");
        assertFalse(socio.tieneEquipoEn(baloncesto), "El socio no debe tener equipo en baloncesto.");
    }

    @Test
    void testAgregarInscripcionEquipo()
    {
        equipoFutbol.agregarJugador(socio, "Delantero", 9);

        assertEquals(1, socio.getInscripcionesEquipo().size(), "El socio debe tener una inscripción a equipo.");
        assertSame(equipoFutbol, socio.getInscripcionesEquipo().get(0).getEquipo(), "La inscripción debe ser al equipo de fútbol.");
    }

    @Test
    void testGetNivelEnSinInscripcion()
    {
        assertNull(socio.getNivelEn(tenis), "Sin inscripción el nivel debe ser nulo.");
    }

    @Test
    void testGetNivelEn()
    {
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));
        socio.agregarInscripcionIndividual(new InscripcionIndividual(padel, NivelJugador.AVANZADO));

        assertEquals(NivelJugador.INTERMEDIO, socio.getNivelEn(tenis), "El nivel en tenis no es el esperado.");
        assertEquals(NivelJugador.AVANZADO, socio.getNivelEn(padel), "El nivel en pádel no es el esperado.");
        assertEquals(2, socio.getInscripcionesIndividuales().size(), "El socio debe tener dos inscripciones individuales.");
    }

    @Test
    void testToJSONSinBebida()
    {
        JSONObject json = socioConPuntos.toJSON();

        assertEquals("Socio", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals(30, json.getInt("edad"), "La edad en el JSON no es la esperada.");
        assertEquals(100, json.getInt("puntosFidelidad"), "Los puntos en el JSON no son los esperados.");
        assertFalse(json.has("horaUltimaBebidaCaliente"), "Sin bebida registrada el JSON no debe incluir la hora.");
        assertEquals(0, json.getJSONArray("inscripcionesIndividuales").length(), "El JSON no debe tener inscripciones individuales.");
    }

    @Test
    void testToJSONConBebidaEInscripciones()
    {
        socio.registrarBebidaCaliente(momento);
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.PRINCIPIANTE));

        JSONObject json = socio.toJSON();

        assertEquals("2026-11-04T10:00", json.getString("horaUltimaBebidaCaliente"), "La hora de la bebida en el JSON no es la esperada.");
        assertEquals(1, json.getJSONArray("inscripcionesIndividuales").length(), "El JSON debe tener una inscripción individual.");
        assertEquals("Tenis", json.getJSONArray("inscripcionesIndividuales").getJSONObject(0).getString("disciplina"), "La disciplina en el JSON no es la esperada.");
        assertEquals("PRINCIPIANTE", json.getJSONArray("inscripcionesIndividuales").getJSONObject(0).getString("nivel"), "El nivel en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        socio.acumularPuntos(15);
        socio.registrarBebidaCaliente(momento);
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.AVANZADO));

        Map<String, DisciplinaIndividual> mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);

        Socio restaurado = Socio.fromJSON(socio.toJSON(), mapaDisciplinas);

        assertEquals("Ana Torres", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals("ana", restaurado.getLogin(), "El login restaurado no es el esperado.");
        assertEquals("1234", restaurado.getPassword(), "La contraseña restaurada no es la esperada.");
        assertEquals(20, restaurado.getEdad(), "La edad restaurada no es la esperada.");
        assertEquals(15, restaurado.getPuntosFidelidad(), "Los puntos restaurados no son los esperados.");
        assertEquals(momento, restaurado.getHoraUltimaBebidaCaliente(), "La hora de la bebida restaurada no es la esperada.");
        assertEquals(NivelJugador.AVANZADO, restaurado.getNivelEn(tenis), "El nivel restaurado no es el esperado.");
    }

    @Test
    void testFromJSONDisciplinaInexistente()
    {
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.AVANZADO));
        JSONObject json = socio.toJSON();
        Map<String, DisciplinaIndividual> mapaVacio = new HashMap<String, DisciplinaIndividual>();

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> Socio.fromJSON(json, mapaVacio), "Debe lanzarse IllegalStateException si la disciplina no existe.");

        assertEquals("Disciplina inexistente: Tenis", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
