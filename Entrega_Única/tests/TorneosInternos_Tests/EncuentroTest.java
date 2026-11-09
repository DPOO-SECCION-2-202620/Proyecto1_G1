package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Excepciones.ReglaNegocioException;
import TorneosInternos.Encuentro;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoIndividual;
import Usuarios.Socio;

public class EncuentroTest
{
    private InscripcionTorneo inscripcionA;
    private InscripcionTorneo inscripcionB;
    private InscripcionTorneo inscripcionC;
    private List<InscripcionTorneo> inscripciones;

    private Encuentro encuentro;

    @BeforeEach
    void setUp() throws Exception
    {
        inscripcionA = new InscripcionTorneoIndividual(new Socio(20, "Ana Torres", "ana", "1"), null);
        inscripcionB = new InscripcionTorneoIndividual(new Socio(25, "Pedro Gil", "pgil", "2"), null);
        inscripcionC = new InscripcionTorneoIndividual(new Socio(30, "Luis Mora", "lmora", "3"), null);

        inscripciones = new ArrayList<InscripcionTorneo>();
        inscripciones.add(inscripcionA);
        inscripciones.add(inscripcionB);
        inscripciones.add(inscripcionC);

        encuentro = new Encuentro(1, inscripcionA, inscripcionB);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEncuentroNuevo()
    {
        assertEquals(1, encuentro.getRonda(), "La ronda del encuentro no es la esperada.");
        assertSame(inscripcionA, encuentro.getParticipanteA(), "El participante A no es el esperado.");
        assertSame(inscripcionB, encuentro.getParticipanteB(), "El participante B no es el esperado.");
        assertNull(encuentro.getGanador(), "Un encuentro nuevo no debe tener ganador.");
        assertFalse(encuentro.tieneGanador(), "Un encuentro nuevo no debe tener ganador.");
        assertNull(encuentro.getPerdedor(), "Un encuentro nuevo no debe tener perdedor.");
    }

    @Test
    void testIncluye()
    {
        assertTrue(encuentro.incluye(inscripcionA), "El encuentro incluye al participante A.");
        assertTrue(encuentro.incluye(inscripcionB), "El encuentro incluye al participante B.");
        assertFalse(encuentro.incluye(inscripcionC), "El encuentro no incluye a un tercero.");
    }

    @Test
    void testRegistrarGanadorA() throws Exception
    {
        encuentro.registrarGanador(inscripcionA);

        assertTrue(encuentro.tieneGanador(), "El encuentro debe tener ganador.");
        assertSame(inscripcionA, encuentro.getGanador(), "El ganador no es el esperado.");
        assertSame(inscripcionB, encuentro.getPerdedor(), "El perdedor no es el esperado.");
    }

    @Test
    void testRegistrarGanadorB() throws Exception
    {
        encuentro.registrarGanador(inscripcionB);

        assertSame(inscripcionB, encuentro.getGanador(), "El ganador no es el esperado.");
        assertSame(inscripcionA, encuentro.getPerdedor(), "El perdedor no es el esperado.");
    }

    @Test
    void testRegistrarGanadorDosVeces() throws Exception
    {
        encuentro.registrarGanador(inscripcionA);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> encuentro.registrarGanador(inscripcionB), "Debe lanzarse ReglaNegocioException si el encuentro ya se jugó.");

        assertEquals("El encuentro ya se jugó", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertSame(inscripcionA, encuentro.getGanador(), "El ganador original no debe cambiar.");
    }

    @Test
    void testRegistrarGanadorQueNoParticipa()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> encuentro.registrarGanador(inscripcionC), "Debe lanzarse ReglaNegocioException si el ganador no participa.");

        assertEquals("El ganador debe ser uno de los dos participantes del encuentro", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertFalse(encuentro.tieneGanador(), "El encuentro no debe tener ganador.");
    }

    @Test
    void testRegistrarGanadorNulo()
    {
        assertThrows(ReglaNegocioException.class, () -> encuentro.registrarGanador(null), "Un ganador nulo no es válido.");
    }

    @Test
    void testCambiarGanador() throws Exception
    {
        encuentro.registrarGanador(inscripcionA);

        encuentro.cambiarGanador(inscripcionB);

        assertSame(inscripcionB, encuentro.getGanador(), "El nuevo ganador no es el esperado.");
        assertSame(inscripcionA, encuentro.getPerdedor(), "El nuevo perdedor no es el esperado.");
    }

    @Test
    void testCambiarGanadorSinGanadorPrevio() throws Exception
    {
        encuentro.cambiarGanador(inscripcionA);

        assertSame(inscripcionA, encuentro.getGanador(), "El ganador asignado no es el esperado.");
    }

    @Test
    void testCambiarGanadorQueNoParticipa() throws Exception
    {
        encuentro.registrarGanador(inscripcionA);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> encuentro.cambiarGanador(inscripcionC), "Debe lanzarse ReglaNegocioException si el nuevo ganador no participa.");

        assertEquals("El ganador debe ser uno de los dos participantes del encuentro", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertSame(inscripcionA, encuentro.getGanador(), "El ganador no debe cambiar.");
    }

    @Test
    void testReemplazarParticipanteA()
    {
        encuentro.reemplazarParticipante(inscripcionA, inscripcionC);

        assertSame(inscripcionC, encuentro.getParticipanteA(), "El participante A debe reemplazarse.");
        assertSame(inscripcionB, encuentro.getParticipanteB(), "El participante B no debe cambiar.");
    }

    @Test
    void testReemplazarParticipanteB()
    {
        encuentro.reemplazarParticipante(inscripcionB, inscripcionC);

        assertSame(inscripcionA, encuentro.getParticipanteA(), "El participante A no debe cambiar.");
        assertSame(inscripcionC, encuentro.getParticipanteB(), "El participante B debe reemplazarse.");
    }

    @Test
    void testReemplazarParticipanteInexistente()
    {
        encuentro.reemplazarParticipante(inscripcionC, inscripcionA);

        assertSame(inscripcionA, encuentro.getParticipanteA(), "El participante A no debe cambiar.");
        assertSame(inscripcionB, encuentro.getParticipanteB(), "El participante B no debe cambiar.");
    }

    @Test
    void testToStringPendiente()
    {
        assertEquals("Ronda 1: Ana Torres vs Pedro Gil (pendiente)", encuentro.toString(), "El texto del encuentro pendiente no es el esperado.");
    }

    @Test
    void testToStringConGanador() throws Exception
    {
        encuentro.registrarGanador(inscripcionB);

        assertEquals("Ronda 1: Ana Torres vs Pedro Gil -> ganó Pedro Gil", encuentro.toString(), "El texto del encuentro jugado no es el esperado.");
    }

    @Test
    void testToJSONSinGanador()
    {
        JSONObject json = encuentro.toJSON(inscripciones);

        assertEquals(1, json.getInt("ronda"), "La ronda en el JSON no es la esperada.");
        assertEquals(0, json.getInt("participanteA"), "El índice del participante A no es el esperado.");
        assertEquals(1, json.getInt("participanteB"), "El índice del participante B no es el esperado.");
        assertFalse(json.has("ganador"), "Sin ganador el JSON no debe incluirlo.");
    }

    @Test
    void testToJSONConGanador() throws Exception
    {
        encuentro.registrarGanador(inscripcionB);

        JSONObject json = encuentro.toJSON(inscripciones);

        assertEquals(1, json.getInt("ganador"), "El índice del ganador no es el esperado.");
    }

    @Test
    void testToJSONConParticipanteFueraDeLaLista()
    {
        Encuentro otro = new Encuentro(2, inscripcionA, new InscripcionTorneoIndividual(new Socio(40, "Ajeno", "ajeno", "x"), null));

        JSONObject json = otro.toJSON(inscripciones);

        assertEquals(-1, json.getInt("participanteB"), "Un participante fuera de la lista debe quedar con índice -1.");
    }

    @Test
    void testFromJSON() throws Exception
    {
        Encuentro restaurado = Encuentro.fromJSON(encuentro.toJSON(inscripciones), inscripciones);

        assertEquals(1, restaurado.getRonda(), "La ronda restaurada no es la esperada.");
        assertSame(inscripcionA, restaurado.getParticipanteA(), "El participante A restaurado no es el esperado.");
        assertSame(inscripcionB, restaurado.getParticipanteB(), "El participante B restaurado no es el esperado.");
        assertFalse(restaurado.tieneGanador(), "El encuentro restaurado no debe tener ganador.");
    }

    @Test
    void testFromJSONConGanador() throws Exception
    {
        encuentro.registrarGanador(inscripcionB);

        Encuentro restaurado = Encuentro.fromJSON(encuentro.toJSON(inscripciones), inscripciones);

        assertSame(inscripcionB, restaurado.getGanador(), "El ganador restaurado no es el esperado.");
        assertSame(inscripcionA, restaurado.getPerdedor(), "El perdedor restaurado no es el esperado.");
    }

    @Test
    void testFromJSONConGanadorQueNoParticipa()
    {
        JSONObject json = encuentro.toJSON(inscripciones);
        json.put("ganador", 2);

        assertThrows(ReglaNegocioException.class, () -> Encuentro.fromJSON(json, inscripciones), "Un ganador que no participa debe lanzar ReglaNegocioException.");
    }

    @Test
    void testFromJSONConIndiceFueraDeRango()
    {
        JSONObject json = encuentro.toJSON(inscripciones);
        json.put("participanteB", 10);

        assertThrows(IndexOutOfBoundsException.class, () -> Encuentro.fromJSON(json, inscripciones), "Un índice fuera de rango debe lanzar IndexOutOfBoundsException.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject json = new JSONObject();
        json.put("ronda", 1);

        assertThrows(org.json.JSONException.class, () -> Encuentro.fromJSON(json, inscripciones), "Debe lanzarse JSONException si faltan campos.");
    }
}
