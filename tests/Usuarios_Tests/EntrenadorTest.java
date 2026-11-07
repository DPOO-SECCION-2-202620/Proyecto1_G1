package Usuarios_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Tenis;
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Entrenador;

public class EntrenadorTest
{
    private Entrenador entrenador;
    private Tenis tenis;
    private Turno turnoLunes;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        turnoLunes = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));

        entrenador = new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01");
        entrenador.agregarTurno(turnoLunes);
        entrenador.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.AVANZADO));
        entrenador.acumularPuntos(10);

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsEmpleado()
    {
        assertTrue(entrenador instanceof Empleado, "Un entrenador también es un empleado.");
        assertEquals("ENT01", entrenador.getCodigoEmpleado(), "El código del entrenador no es el esperado.");
    }

    @Test
    void testEsEntrenador()
    {
        assertTrue(entrenador.esEntrenador(), "Un entrenador debe responder que es entrenador.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = entrenador.toJSON();

        assertEquals("Entrenador", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("ENT01", json.getString("codigoEmpleado"), "El código en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("turnos").length(), "El JSON debe incluir el turno del entrenador.");
    }

    @Test
    void testFromJSON()
    {
        Entrenador restaurado = Entrenador.fromJSON(entrenador.toJSON(), mapaDisciplinas);

        assertEquals("Jorge Ruiz", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals("jruiz", restaurado.getLogin(), "El login restaurado no es el esperado.");
        assertEquals("pass", restaurado.getPassword(), "La contraseña restaurada no es la esperada.");
        assertEquals(40, restaurado.getEdad(), "La edad restaurada no es la esperada.");
        assertEquals("ENT01", restaurado.getCodigoEmpleado(), "El código restaurado no es el esperado.");
        assertEquals(10, restaurado.getPuntosFidelidad(), "Los puntos restaurados no son los esperados.");
        assertEquals(NivelJugador.AVANZADO, restaurado.getNivelEn(tenis), "El nivel restaurado no es el esperado.");
        assertEquals(1, restaurado.getTurnos().size(), "El entrenador restaurado debe tener un turno.");
        assertEquals("LUNES", restaurado.getTurnos().get(0).getDiaSemana(), "El día del turno restaurado no es el esperado.");
        assertEquals(LocalTime.of(8, 0), restaurado.getTurnos().get(0).getHoraInicio(), "La hora de inicio restaurada no es la esperada.");
        assertTrue(restaurado.esEntrenador(), "El entrenador restaurado debe seguir siendo entrenador.");
    }
}
