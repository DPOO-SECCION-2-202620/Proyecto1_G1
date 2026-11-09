package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;

public class HU_TINT_04_InscripcionIndividualTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private Socio ana;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        ana = Escenario.jugador(club, tenis, "Ana Torres", NivelJugador.INTERMEDIO);
    }

    private TorneoIndividual competitivo(Modalidad modalidad) throws ReglaNegocioException
    {
        return Escenario.torneoTenis(club, tenis, "Copa " + modalidad, 8, new PremiacionCompetitiva(Escenario.TARIFA),
                modalidad);
    }

    @Test
    void inscripcionValida() throws Exception
    {
        TorneoIndividual torneo = competitivo(Modalidad.SENCILLOS);

        Escenario.inscribir(club, torneo, ana, null);

        assertEquals(1, torneo.getInscripciones().size(), "Ana debe quedar inscrita.");
        assertEquals(7, torneo.getCuposLibres(), "Debe descontarse un cupo.");
        assertEquals(Escenario.TARIFA, torneo.getInscripciones().get(0).getMontoPagado(), 0.001, "Debe pagar la tarifa.");
        assertEquals(Escenario.TARIFA, torneo.getRecaudado(), 0.001, "La tarifa debe sumar a lo recaudado.");
    }

    @Test
    void inscripcionEnDobles() throws Exception
    {
        TorneoIndividual torneo = competitivo(Modalidad.DOBLES);
        Socio pedro = Escenario.jugador(club, tenis, "Pedro Gil", NivelJugador.INTERMEDIO);

        Escenario.inscribir(club, torneo, ana, pedro);

        assertEquals(1, torneo.getInscripciones().size(), "La pareja es una sola inscripción.");
        assertEquals(7, torneo.getCuposLibres(), "La pareja ocupa un solo cupo.");
        assertEquals(2 * Escenario.TARIFA, torneo.getRecaudado(), 0.001, "Cada integrante paga su tarifa.");
        assertTrue(torneo.getInscripciones().get(0).incluye(pedro), "Pedro debe quedar inscrito con Ana.");
    }

    @Test
    void companeroYaInscrito() throws Exception
    {
        TorneoIndividual torneo = competitivo(Modalidad.DOBLES);
        Socio pedro = Escenario.jugador(club, tenis, "Pedro Gil", NivelJugador.INTERMEDIO);
        Socio luis = Escenario.jugador(club, tenis, "Luis Mora", NivelJugador.INTERMEDIO);
        Escenario.inscribir(club, torneo, pedro, luis);

        assertThrows(ReglaNegocioException.class, () -> Escenario.inscribir(club, torneo, ana, pedro),
                "Pedro ya está inscrito con otra pareja.");
        assertEquals(1, torneo.getInscripciones().size(), "Debe seguir habiendo una sola pareja.");
    }

    @Test
    void nivelIncorrecto() throws Exception
    {
        TorneoIndividual torneo = competitivo(Modalidad.SENCILLOS);
        Socio avanzado = Escenario.jugador(club, tenis, "Carlos Avanzado", NivelJugador.AVANZADO);
        Socio sinNivel = Escenario.jugador(club, tenis, "Sofia Sin Nivel", null);

        ReglaNegocioException otroNivel = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, avanzado, null));
        ReglaNegocioException ninguno = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, sinNivel, null));

        assertTrue(otroNivel.getMessage().contains("INTERMEDIO"), "Debe indicar el nivel requerido.");
        assertTrue(ninguno.getMessage().contains("INTERMEDIO"), "Debe indicar el nivel requerido.");
        assertEquals(0, torneo.getInscripciones().size(), "Nadie debe quedar inscrito.");
    }

    @Test
    void inscripcionRepetida() throws Exception
    {
        TorneoIndividual torneo = competitivo(Modalidad.SENCILLOS);
        Escenario.inscribir(club, torneo, ana, null);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, ana, null));

        assertTrue(excepcion.getMessage().contains("ya está inscrito"), "Debe informar que ya está inscrito.");
        assertEquals(1, torneo.getInscripciones().size(), "Debe seguir habiendo una inscripción.");
    }

    @Test
    void torneoLleno() throws Exception
    {
        TorneoIndividual torneo = Escenario.torneoTenis(club, tenis, "Copa Llena", 2, new PremiacionRecreativa(),
                Modalidad.SENCILLOS, Escenario.proximo(DayOfWeek.SUNDAY), LocalDate.now().minusDays(1));
        List<Socio> inscritos = Escenario.llenar(club, tenis, torneo, 2);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, ana, null));

        assertTrue(excepcion.getMessage().contains("lleno"), "Debe informar que el torneo está lleno.");
        assertEquals(inscritos.size(), torneo.getInscripciones().size(), "No debe sumarse ninguna inscripción.");
    }

    @Test
    void torneoEnCurso() throws Exception
    {
        TorneoIndividual torneo = competitivo(Modalidad.SENCILLOS);
        Escenario.llenar(club, tenis, torneo, 2);
        club.iniciarTorneo(torneo);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, ana, null));

        assertTrue(excepcion.getMessage().contains("cerradas"), "Debe informar que las inscripciones están cerradas.");
    }
}
