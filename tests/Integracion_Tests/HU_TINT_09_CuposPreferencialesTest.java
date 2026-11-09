package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;

public class HU_TINT_09_CuposPreferencialesTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private Socio top;
    private Socio normal;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        top = Escenario.jugador(club, tenis, "Top Ranking", NivelJugador.INTERMEDIO);
        club.getRanking(tenis, top).setPuntajeElo(1300);
        normal = Escenario.jugador(club, tenis, "Sin Ranking", NivelJugador.INTERMEDIO);
    }

    private TorneoIndividual torneo(LocalDate limite) throws ReglaNegocioException
    {
        return Escenario.torneoTenis(club, tenis, "Copa", 8, new PremiacionRecreativa(), Modalidad.SENCILLOS,
                Escenario.proximo(DayOfWeek.SUNDAY), limite);
    }

    private InscripcionTorneo ultima(TorneoIndividual torneo)
    {
        return torneo.getInscripciones().get(torneo.getInscripciones().size() - 1);
    }

    @Test
    void topOcupaPreferencial() throws Exception
    {
        TorneoIndividual torneo = torneo(LocalDate.now().plusDays(7));
        Escenario.llenar(club, tenis, torneo, 6);

        Escenario.inscribir(club, torneo, top, null);

        assertTrue(ultima(torneo).esPreferencial(), "El mejor del ranking debe ocupar un cupo preferencial.");
        assertEquals(7, torneo.getInscripciones().size(), "Debe quedar inscrito.");
    }

    @Test
    void noTopRechazado() throws Exception
    {
        TorneoIndividual torneo = torneo(LocalDate.now().plusDays(7));
        Escenario.llenar(club, tenis, torneo, 6);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, normal, null));

        assertTrue(excepcion.getMessage().contains("preferenciales"), "Debe informar que solo quedan cupos preferenciales.");
        assertEquals(6, torneo.getInscripciones().size(), "No debe quedar inscrito.");
    }

    @Test
    void despuesDeLaFechaLimite() throws Exception
    {
        TorneoIndividual torneo = torneo(LocalDate.now().minusDays(1));
        Escenario.llenar(club, tenis, torneo, 7);

        Escenario.inscribir(club, torneo, normal, null);

        assertEquals(8, torneo.getInscripciones().size(), "El jugador ocupa el último cupo.");
        assertFalse(ultima(torneo).esPreferencial(), "Lo ocupa como cupo regular.");
    }

    @Test
    void topConRegularesLibres() throws Exception
    {
        TorneoIndividual torneo = torneo(LocalDate.now().plusDays(7));

        Escenario.inscribir(club, torneo, top, null);

        assertTrue(ultima(torneo).esPreferencial(), "Debe ocupar un cupo preferencial.");
        assertEquals(1, torneo.getCuposPreferencialesLibres(), "Queda un preferencial libre.");
        assertEquals(6, torneo.getCuposLibres() - torneo.getCuposPreferencialesLibres(), "Los 6 regulares siguen libres.");
    }

    @Test
    void preferencialesEnConjunto() throws Exception
    {
        Futbol futbol = new Futbol();
        Equipo cerros = Escenario.equipo(club, "Cerros FC", futbol, Categoria.MAYORES, 7, 0);
        Equipo andes = Escenario.equipo(club, "Andes FC", futbol, Categoria.MAYORES, 7, 0);
        club.registrarPartidoInterno(cerros, andes, 2, 0, null);
        TorneoConjunto torneo = new TorneoConjunto("Mini Futbol", 6, Escenario.proximo(DayOfWeek.SUNDAY),
                LocalDate.now().plusDays(7), new PremiacionRecreativa(), futbol, Categoria.MAYORES);
        club.agregarTorneo(torneo);

        club.inscribirEnTorneo(torneo, new InscripcionTorneoEquipo(cerros));
        club.inscribirEnTorneo(torneo, new InscripcionTorneoEquipo(andes));

        assertEquals(2, torneo.getCuposPreferenciales(), "El 20% de 6 es 1,2 y se redondea a 2.");
        assertTrue(torneo.getInscripciones().get(0).esPreferencial(), "El equipo con más puntos ocupa un preferencial.");
        assertFalse(torneo.getInscripciones().get(1).esPreferencial(), "Un equipo sin victorias ocupa un regular.");
    }

    @Test
    void redondeoHaciaArriba() throws Exception
    {
        assertEquals(1, Escenario.torneoTenis(club, tenis, "Cuatro", 4, new PremiacionRecreativa(), Modalidad.SENCILLOS)
                .getCuposPreferenciales(), "El 20% de 4 es 0,8: 1 cupo.");
        assertEquals(2, Escenario.torneoTenis(club, tenis, "Ocho", 8, new PremiacionRecreativa(), Modalidad.SENCILLOS)
                .getCuposPreferenciales(), "El 20% de 8 es 1,6: 2 cupos.");
        assertEquals(4, Escenario.torneoTenis(club, tenis, "Dieciseis", 16, new PremiacionRecreativa(), Modalidad.SENCILLOS)
                .getCuposPreferenciales(), "El 20% de 16 es 3,2: 4 cupos.");
    }
}
