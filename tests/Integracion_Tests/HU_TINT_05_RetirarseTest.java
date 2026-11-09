package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;

public class HU_TINT_05_RetirarseTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private Socio ana;
    private TorneoIndividual torneo;

    @BeforeEach
    void setUp() throws Exception
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        ana = Escenario.jugador(club, tenis, "Ana Torres", NivelJugador.INTERMEDIO);
        torneo = Escenario.torneoTenis(club, tenis, "Copa", 8, new PremiacionCompetitiva(Escenario.TARIFA),
                Modalidad.SENCILLOS);
    }

    @Test
    void retirarse() throws Exception
    {
        Escenario.inscribir(club, torneo, ana, null);
        Escenario.llenar(club, tenis, torneo, 1);

        club.retirarJugadorDeTorneo(torneo, ana);

        assertNull(torneo.buscarInscripcion(ana), "Ana no debe seguir en la lista de participantes.");
        assertEquals(7, torneo.getCuposLibres(), "El cupo de Ana debe quedar libre.");
        assertEquals(Escenario.TARIFA, torneo.getRecaudado(), 0.001, "Solo debe contarse la tarifa del otro jugador.");
    }

    @Test
    void retirarPareja() throws Exception
    {
        TorneoIndividual dobles = Escenario.torneoTenis(club, tenis, "Copa Dobles", 8,
                new PremiacionCompetitiva(Escenario.TARIFA), Modalidad.DOBLES);
        Socio pedro = Escenario.jugador(club, tenis, "Pedro Gil", NivelJugador.INTERMEDIO);
        Escenario.inscribir(club, dobles, ana, pedro);

        club.retirarJugadorDeTorneo(dobles, pedro);

        assertEquals(0, dobles.getInscripciones().size(), "La pareja completa debe salir del torneo.");
        assertEquals(8, dobles.getCuposLibres(), "El cupo de la pareja debe quedar libre.");
        assertEquals(0, dobles.getRecaudado(), 0.001, "No debe quedar dinero recaudado.");
    }

    @Test
    void liberaCupoPreferencial() throws Exception
    {
        club.getRanking(tenis, ana).setPuntajeElo(1300);
        Escenario.inscribir(club, torneo, ana, null);
        assertTrue(torneo.getInscripciones().get(0).esPreferencial(), "Con el mejor ranking, Ana ocupa un preferencial.");
        assertEquals(1, torneo.getCuposPreferencialesLibres(), "Debe quedar un cupo preferencial libre.");

        club.retirarJugadorDeTorneo(torneo, ana);

        assertEquals(2, torneo.getCuposPreferencialesLibres(), "El cupo preferencial debe volver a estar disponible.");
    }

    @Test
    void torneoIniciado() throws Exception
    {
        Escenario.inscribir(club, torneo, ana, null);
        Escenario.llenar(club, tenis, torneo, 1);
        club.iniciarTorneo(torneo);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.retirarJugadorDeTorneo(torneo, ana));

        assertTrue(excepcion.getMessage().contains("ya inició"), "Debe informar que el torneo ya inició.");
        assertEquals(2, torneo.getInscripciones().size(), "Ana debe seguir inscrita.");
    }

    @Test
    void noInscrito()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.retirarJugadorDeTorneo(torneo, ana));

        assertTrue(excepcion.getMessage().contains("no está inscrito"), "Debe informar que no está inscrito.");
    }
}
