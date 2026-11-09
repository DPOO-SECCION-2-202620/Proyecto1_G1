package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoIndividual;

public class HU_TINT_07_CuadroTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private TorneoIndividual torneo;

    @BeforeEach
    void setUp() throws Exception
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        torneo = Escenario.torneoSinReserva(club, tenis, "Copa", 8, new PremiacionRecreativa(), Modalidad.SENCILLOS);
    }

    private List<InscripcionTorneo> conOchoIniciado() throws ReglaNegocioException
    {
        Escenario.llenar(club, tenis, torneo, 8);
        club.iniciarTorneo(torneo);
        return torneo.getInscripciones();
    }

    @Test
    void iniciarTorneo() throws Exception
    {
        List<InscripcionTorneo> inscritos = conOchoIniciado();

        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado(), "El torneo debe pasar a En curso.");
        List<Encuentro> primeraRonda = torneo.getEncuentrosDeRonda(1);
        assertEquals(4, primeraRonda.size(), "Con 8 inscritos debe haber 4 encuentros.");
        for (int i = 0; i < 4; i++)
        {
            assertSame(inscritos.get(2 * i), primeraRonda.get(i).getParticipanteA(), "Los encuentros siguen el orden de inscripción.");
            assertSame(inscritos.get(2 * i + 1), primeraRonda.get(i).getParticipanteB(), "Los encuentros siguen el orden de inscripción.");
        }
    }

    @Test
    void cantidadInvalida() throws Exception
    {
        Escenario.llenar(club, tenis, torneo, 6);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> club.iniciarTorneo(torneo));

        assertTrue(excepcion.getMessage().contains("2, 4, 8, 16 o 32"), "Debe indicar las cantidades válidas.");
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado(), "El torneo debe seguir abierto.");
    }

    @Test
    void perdedorEliminado() throws Exception
    {
        conOchoIniciado();
        Encuentro cuartos = torneo.getEncuentrosDeRonda(1).get(0);
        InscripcionTorneo perdedor = cuartos.getParticipanteB();

        Escenario.jugarRonda(club, torneo);

        assertEquals(1, torneo.getRondaEliminacion(perdedor), "Debe quedar eliminado en la primera ronda.");
        for (Encuentro semifinal : torneo.getEncuentrosDeRonda(2))
        {
            assertFalse(semifinal.incluye(perdedor), "El perdedor no debe estar en la ronda siguiente.");
        }
    }

    @Test
    void avanzarHastaLaFinal() throws Exception
    {
        conOchoIniciado();

        Escenario.jugarRonda(club, torneo);
        assertEquals(2, torneo.getRondaActual(), "Debe armarse la semifinal.");
        assertEquals(2, torneo.getEncuentrosDeRonda(2).size(), "La semifinal tiene 2 encuentros.");

        Escenario.jugarRonda(club, torneo);
        assertEquals(1, torneo.getEncuentrosDeRonda(3).size(), "La final es un solo encuentro.");

        Escenario.jugarRonda(club, torneo);
        assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado(), "Al jugar la final el torneo termina.");
    }

    @Test
    void ganadorAjeno() throws Exception
    {
        List<InscripcionTorneo> inscritos = conOchoIniciado();
        Encuentro primero = torneo.getEncuentrosDeRonda(1).get(0);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.registrarGanadorTorneo(torneo, primero, inscritos.get(5)));

        assertTrue(excepcion.getMessage().contains("uno de los dos participantes"), "Debe explicar el motivo del rechazo.");
        assertNull(primero.getGanador(), "El encuentro debe seguir sin ganador.");
    }

    @Test
    void corregirGanador() throws Exception
    {
        conOchoIniciado();
        Encuentro cuartos = torneo.getEncuentrosDeRonda(1).get(0);
        InscripcionTorneo a = cuartos.getParticipanteA();
        InscripcionTorneo b = cuartos.getParticipanteB();
        Escenario.jugarRonda(club, torneo);
        Encuentro semifinal = torneo.getEncuentrosDeRonda(2).get(0);
        assertTrue(semifinal.incluye(a), "A gana por error y pasa a la semifinal.");

        club.corregirGanadorTorneo(torneo, cuartos, b);

        assertSame(b, cuartos.getGanador(), "B debe quedar como ganador.");
        assertEquals(1, torneo.getRondaEliminacion(a), "A queda eliminado en cuartos.");
        assertTrue(semifinal.incluye(b), "B ocupa el lugar de A en la semifinal.");
        assertFalse(semifinal.incluye(a), "A ya no está en la semifinal.");
    }

    @Test
    void noCorregirSiYaJugo() throws Exception
    {
        conOchoIniciado();
        Encuentro cuartos = torneo.getEncuentrosDeRonda(1).get(0);
        InscripcionTorneo a = cuartos.getParticipanteA();
        Escenario.jugarRonda(club, torneo);
        Encuentro semifinal = torneo.getEncuentrosDeRonda(2).get(0);
        club.registrarGanadorTorneo(torneo, semifinal, semifinal.getParticipanteA());

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.corregirGanadorTorneo(torneo, cuartos, cuartos.getParticipanteB()));

        assertTrue(excepcion.getMessage().contains("ya no se puede cambiar"), "Debe informar que no se puede cambiar.");
        assertSame(a, cuartos.getGanador(), "El ganador original se mantiene.");
    }

    @Test
    void noCorregirTorneoFinalizado() throws Exception
    {
        conOchoIniciado();
        Escenario.jugarHastaElFinal(club, torneo);
        Encuentro laFinal = torneo.getEncuentrosDeRonda(3).get(0);

        assertThrows(ReglaNegocioException.class,
                () -> club.corregirGanadorTorneo(torneo, laFinal, laFinal.getParticipanteB()),
                "Un torneo finalizado no admite correcciones.");
    }
}
