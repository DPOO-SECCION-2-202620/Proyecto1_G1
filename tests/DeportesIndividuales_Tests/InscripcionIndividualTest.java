package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;

public class InscripcionIndividualTest
{
    private Tenis tenis;
    private Padel padel;
    private InscripcionIndividual inscripcion;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        padel = new Padel(1, "Padel");
        inscripcion = new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetDisciplina()
    {
        assertSame(tenis, inscripcion.getDisciplina(), "La disciplina de la inscripción no es la esperada.");
    }

    @Test
    void testGetNivel()
    {
        assertEquals(NivelJugador.INTERMEDIO, inscripcion.getNivel(), "El nivel de la inscripción no es el esperado.");
    }

    @Test
    void testCambiarNivel()
    {
        inscripcion.cambiarNivel(NivelJugador.AVANZADO);

        assertEquals(NivelJugador.AVANZADO, inscripcion.getNivel(), "El nivel cambiado no es el esperado.");
    }

    @Test
    void testCambiarNivelANulo()
    {
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> inscripcion.cambiarNivel(null), "Debe lanzarse IllegalArgumentException si el nivel es nulo.");

        assertEquals("El nivel no puede ser nulo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(NivelJugador.INTERMEDIO, inscripcion.getNivel(), "El nivel no debe cambiar si el nuevo nivel es nulo.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = inscripcion.toJSON();

        assertEquals("Tenis", json.getString("disciplina"), "La disciplina en el JSON no es la esperada.");
        assertEquals("INTERMEDIO", json.getString("nivel"), "El nivel en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        InscripcionIndividual restaurada = InscripcionIndividual.fromJSON(inscripcion.toJSON(), padel);

        assertSame(padel, restaurada.getDisciplina(), "La disciplina restaurada debe ser la entregada.");
        assertEquals(NivelJugador.INTERMEDIO, restaurada.getNivel(), "El nivel restaurado no es el esperado.");
    }

    @Test
    void testFromJSONNivelInvalido()
    {
        JSONObject json = inscripcion.toJSON();
        json.put("nivel", "EXPERTO");

        assertThrows(IllegalArgumentException.class, () -> InscripcionIndividual.fromJSON(json, tenis), "Un nivel desconocido debe lanzar IllegalArgumentException.");
    }
}
