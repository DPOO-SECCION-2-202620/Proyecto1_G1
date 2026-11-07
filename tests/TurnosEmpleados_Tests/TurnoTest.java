package TurnosEmpleados_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import TurnosEmpleados.Turno;

public class TurnoTest
{
    private Turno turnoLunes;
    private Turno turnoNocturno;

    @BeforeEach
    void setUp() throws Exception
    {
        turnoLunes = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoNocturno = new Turno("Sabado", LocalTime.of(18, 30), LocalTime.of(22, 45));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetDiaSemana()
    {
        assertEquals("LUNES", turnoLunes.getDiaSemana(), "El día del turno no es el esperado.");
    }

    @Test
    void testGetHoraInicio()
    {
        assertEquals(LocalTime.of(8, 0), turnoLunes.getHoraInicio(), "La hora de inicio del turno no es la esperada.");
    }

    @Test
    void testGetHoraFinal()
    {
        assertEquals(LocalTime.of(12, 0), turnoLunes.getHoraFinal(), "La hora final del turno no es la esperada.");
    }

    @Test
    void testCubreHoraDentroDelTurno()
    {
        assertTrue(turnoLunes.cubre("LUNES", LocalTime.of(10, 0)), "Una hora dentro del turno debe estar cubierta.");
    }

    @Test
    void testCubreLimitesInclusivos()
    {
        assertTrue(turnoLunes.cubre("LUNES", LocalTime.of(8, 0)), "La hora de inicio debe estar cubierta.");
        assertTrue(turnoLunes.cubre("LUNES", LocalTime.of(12, 0)), "La hora final debe estar cubierta.");
    }

    @Test
    void testCubreHoraFueraDelTurno()
    {
        assertFalse(turnoLunes.cubre("LUNES", LocalTime.of(7, 59)), "Un minuto antes del inicio no debe estar cubierto.");
        assertFalse(turnoLunes.cubre("LUNES", LocalTime.of(12, 1)), "Un minuto después del final no debe estar cubierto.");
    }

    @Test
    void testCubreOtroDia()
    {
        assertFalse(turnoLunes.cubre("MARTES", LocalTime.of(10, 0)), "Otro día no debe estar cubierto.");
    }

    @Test
    void testCubreIgnoraMayusculas()
    {
        assertTrue(turnoLunes.cubre("lunes", LocalTime.of(10, 0)), "El día debe compararse sin distinguir mayúsculas.");
        assertTrue(turnoNocturno.cubre("SABADO", LocalTime.of(20, 0)), "El día debe compararse sin distinguir mayúsculas.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = turnoNocturno.toJSON();

        assertEquals("Sabado", json.getString("diaSemana"), "El día en el JSON no es el esperado.");
        assertEquals("18:30", json.getString("horaInicio"), "La hora de inicio en el JSON no es la esperada.");
        assertEquals("22:45", json.getString("horaFinal"), "La hora final en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        Turno restaurado = Turno.fromJSON(turnoNocturno.toJSON());

        assertEquals("Sabado", restaurado.getDiaSemana(), "El día restaurado no es el esperado.");
        assertEquals(LocalTime.of(18, 30), restaurado.getHoraInicio(), "La hora de inicio restaurada no es la esperada.");
        assertEquals(LocalTime.of(22, 45), restaurado.getHoraFinal(), "La hora final restaurada no es la esperada.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject json = new JSONObject();
        json.put("diaSemana", "LUNES");

        assertThrows(org.json.JSONException.class, () -> Turno.fromJSON(json), "Debe lanzarse JSONException si faltan campos.");
    }
}
