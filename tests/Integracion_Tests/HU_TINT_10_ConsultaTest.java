package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoIndividual;

public class HU_TINT_10_ConsultaTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
    }

    private TorneoIndividual torneoConOcho(String nombre) throws ReglaNegocioException
    {
        TorneoIndividual torneo = Escenario.torneoSinReserva(club, tenis, nombre, 8,
                new PremiacionCompetitiva(Escenario.TARIFA), Modalidad.SENCILLOS);
        Escenario.llenar(club, tenis, torneo, 8);
        return torneo;
    }

    @Test
    void filtrarPorEstado() throws Exception
    {
        torneoConOcho("Abierto");
        TorneoIndividual enCurso = torneoConOcho("En curso");
        club.iniciarTorneo(enCurso);
        TorneoIndividual finalizado = torneoConOcho("Finalizado");
        club.iniciarTorneo(finalizado);
        Escenario.jugarHastaElFinal(club, finalizado);

        assertEquals(3, club.getTorneos(null).size(), "Sin filtro se muestran todos.");
        assertEquals(1, club.getTorneos(EstadoTorneo.EN_CURSO).size(), "Solo hay un torneo en curso.");
        assertSame(enCurso, club.getTorneos(EstadoTorneo.EN_CURSO).get(0), "El filtro devuelve el torneo en curso.");
        assertSame(finalizado, club.getTorneos(EstadoTorneo.FINALIZADO).get(0), "El filtro devuelve el finalizado.");
    }

    @Test
    void historialEnCurso() throws Exception
    {
        TorneoIndividual torneo = torneoConOcho("Copa");
        club.iniciarTorneo(torneo);
        Encuentro primero = torneo.getEncuentrosPendientes().get(0);
        club.registrarGanadorTorneo(torneo, primero, primero.getParticipanteA());

        assertEquals(8, torneo.getInscripciones().size(), "Se muestran los 8 participantes.");
        assertEquals(1, torneo.getRondaActual(), "El torneo está en la primera ronda.");
        assertEquals(1, torneo.getEncuentrosJugados().size(), "Hay un encuentro jugado.");
        assertSame(primero.getParticipanteA(), torneo.getEncuentrosJugados().get(0).getGanador(), "El jugado muestra su ganador.");
        assertEquals(3, torneo.getEncuentrosPendientes().size(), "Quedan 3 encuentros pendientes.");
    }

    @Test
    void historialFinalizado() throws Exception
    {
        TorneoIndividual torneo = torneoConOcho("Copa");
        List<InscripcionTorneo> inscritos = torneo.getInscripciones();
        club.iniciarTorneo(torneo);
        Escenario.jugarHastaElFinal(club, torneo);

        List<InscripcionTorneo> posiciones = torneo.getPosiciones();

        assertEquals(8, posiciones.size(), "Deben aparecer los 8 participantes.");
        assertSame(inscritos.get(0), posiciones.get(0), "Primero el campeón.");
        assertSame(inscritos.get(4), posiciones.get(1), "Luego el subcampeón.");
        assertEquals(2, torneo.getRondaEliminacion(posiciones.get(2)), "Después los eliminados en semifinal.");
        assertEquals(1, torneo.getRondaEliminacion(posiciones.get(7)), "Al final los eliminados en primera ronda.");
        assertEquals(2, torneo.getPremios().size(), "Se muestran los premios del 1.º y el 2.º.");
    }

    @Test
    void historialPersiste() throws Exception
    {
        TorneoIndividual finalizado = torneoConOcho("Copa Finalizada");
        club.iniciarTorneo(finalizado);
        Escenario.jugarHastaElFinal(club, finalizado);
        TorneoIndividual enCurso = torneoConOcho("Copa En Curso");
        club.iniciarTorneo(enCurso);
        Escenario.jugarRonda(club, enCurso);

        ClubSeneca recargado = Escenario.recargar(carpeta);

        assertEquals(2, recargado.getTorneos().size(), "Los dos torneos deben recargarse.");
        Torneo r1 = recargado.getTorneos(EstadoTorneo.FINALIZADO).get(0);
        assertEquals("Copa Finalizada", r1.getNombre(), "Se conserva el nombre.");
        assertEquals(7, r1.getEncuentrosJugados().size(), "Se conservan los 7 encuentros jugados.");
        assertEquals(finalizado.getPosiciones().get(0).getNombre(), r1.getPosiciones().get(0).getNombre(), "Se conserva el campeón.");
        assertEquals(2, r1.getPremios().size(), "Se conservan los premios.");
        assertEquals(finalizado.getPremios().get(0).getMontoEfectivo(), r1.getPremios().get(0).getMontoEfectivo(), 0.001,
                "Se conserva el monto del premio.");
        Torneo r2 = recargado.getTorneos(EstadoTorneo.EN_CURSO).get(0);
        assertEquals(2, r2.getRondaActual(), "Se conserva la ronda actual.");
        assertEquals(2, r2.getEncuentrosPendientes().size(), "Se conservan los encuentros pendientes.");
        assertTrue(r2.getInscripciones().get(0).getNombre().contains("Jugador"), "Se conservan los participantes.");
    }
}
