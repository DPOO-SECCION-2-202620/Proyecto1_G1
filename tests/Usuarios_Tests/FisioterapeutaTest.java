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
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Fisioterapeuta;

public class FisioterapeutaTest
{
    private Fisioterapeuta fisioterapeuta;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        fisioterapeuta = new Fisioterapeuta(35, "Marta Rios", "mrios", "xyz", "FIS01");
        fisioterapeuta.agregarTurno(new Turno("JUEVES", LocalTime.of(9, 0), LocalTime.of(13, 0)));
        fisioterapeuta.agregarTurno(new Turno("VIERNES", LocalTime.of(14, 0), LocalTime.of(18, 0)));

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsEmpleado()
    {
        assertTrue(fisioterapeuta instanceof Empleado, "Un fisioterapeuta también es un empleado.");
        assertEquals("FIS01", fisioterapeuta.getCodigoEmpleado(), "El código del fisioterapeuta no es el esperado.");
    }

    @Test
    void testNoEsEntrenador()
    {
        assertFalse(fisioterapeuta.esEntrenador(), "Un fisioterapeuta no es entrenador.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = fisioterapeuta.toJSON();

        assertEquals("Fisioterapeuta", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("FIS01", json.getString("codigoEmpleado"), "El código en el JSON no es el esperado.");
        assertEquals(2, json.getJSONArray("turnos").length(), "El JSON debe incluir los dos turnos.");
    }

    @Test
    void testFromJSON()
    {
        Fisioterapeuta restaurado = Fisioterapeuta.fromJSON(fisioterapeuta.toJSON(), mapaDisciplinas);

        assertEquals("Marta Rios", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals("mrios", restaurado.getLogin(), "El login restaurado no es el esperado.");
        assertEquals(35, restaurado.getEdad(), "La edad restaurada no es la esperada.");
        assertEquals("FIS01", restaurado.getCodigoEmpleado(), "El código restaurado no es el esperado.");
        assertEquals(2, restaurado.getTurnos().size(), "El fisioterapeuta restaurado debe tener dos turnos.");
        assertEquals("VIERNES", restaurado.getTurnos().get(1).getDiaSemana(), "El segundo turno restaurado no es el esperado.");
        assertFalse(restaurado.esEntrenador(), "El fisioterapeuta restaurado no debe ser entrenador.");
    }
}
