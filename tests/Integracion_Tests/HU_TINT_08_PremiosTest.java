package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import NucleoSistema.ClubSeneca;
import TorneosInternos.JugadorTorneo;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.PremioEntregado;
import TorneosInternos.TorneoIndividual;
import Usuarios.Entrenador;
import Usuarios.Socio;

public class HU_TINT_08_PremiosTest
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

    private static double efectivoDe(TorneoIndividual torneo, JugadorTorneo jugador)
    {
        double total = 0;
        for (PremioEntregado premio : torneo.getPremios())
        {
            if (premio.getJugador() == jugador)
            {
                total += premio.getMontoEfectivo();
            }
        }
        return total;
    }

    @Test
    void premiosCompetitivo() throws Exception
    {
        TorneoIndividual torneo = Escenario.torneoSinReserva(club, tenis, "Copa", 8,
                new PremiacionCompetitiva(Escenario.TARIFA), Modalidad.SENCILLOS);
        List<Socio> jugadores = Escenario.llenar(club, tenis, torneo, 8);
        club.iniciarTorneo(torneo);

        Escenario.jugarHastaElFinal(club, torneo);

        assertEquals(120000, torneo.getRecaudado(), 0.001, "Se recaudan 8 x $15.000.");
        assertEquals(60000, efectivoDe(torneo, jugadores.get(0)), 0.001, "El campeón recibe el 50%.");
        assertEquals(24000, efectivoDe(torneo, jugadores.get(4)), 0.001, "El subcampeón recibe el 20%.");
        double entregado = 0;
        for (PremioEntregado premio : torneo.getPremios())
        {
            entregado += premio.getMontoEfectivo();
        }
        assertEquals(36000, torneo.getRecaudado() - entregado, 0.001, "Al club le queda el 30%.");
    }

    @Test
    void campeonEmpleado() throws Exception
    {
        Entrenador luis = new Entrenador(38, "Luis Perez", "luis", "clave", "EMP-001");
        club.registrarUsuario(luis);
        club.inscribirEnDisciplina(luis, tenis, NivelJugador.INTERMEDIO);
        TorneoIndividual torneo = Escenario.torneoTenis(club, tenis, "Copa", 8,
                new PremiacionCompetitiva(Escenario.TARIFA), Modalidad.SENCILLOS, Escenario.proximo(DayOfWeek.SATURDAY),
                LocalDate.now().minusDays(1));
        Escenario.inscribir(club, torneo, luis, null);
        List<Socio> socios = Escenario.llenar(club, tenis, torneo, 7);
        club.iniciarTorneo(torneo);

        Escenario.jugarHastaElFinal(club, torneo);

        assertEquals(105000, torneo.getRecaudado(), 0.001, "El empleado no paga: se recaudan 7 x $15.000.");
        assertEquals(0, efectivoDe(torneo, luis), 0.001, "El empleado campeón no recibe dinero.");
        assertEquals(21000, efectivoDe(torneo, socios.get(3)), 0.001, "El subcampeón recibe el 20% de lo recaudado.");
    }

    @Test
    void premiosRecreativo() throws Exception
    {
        TorneoIndividual torneo = Escenario.torneoSinReserva(club, tenis, "Copa Recreativa", 8,
                new PremiacionRecreativa(), Modalidad.SENCILLOS);
        List<Socio> jugadores = Escenario.llenar(club, tenis, torneo, 8);
        club.iniciarTorneo(torneo);

        Escenario.jugarHastaElFinal(club, torneo);

        assertEquals(1, jugadores.get(0).getBonos().size(), "El campeón recibe un bono.");
        assertEquals(0.25, jugadores.get(0).getBonos().get(0).getPorcentaje(), 0.0001, "El bono del campeón es del 25%.");
        assertEquals(0.10, jugadores.get(4).getBonos().get(0).getPorcentaje(), 0.0001, "El bono del subcampeón es del 10%.");
        assertEquals(0, jugadores.get(1).getBonos().size(), "Los demás no reciben bono.");
        for (PremioEntregado premio : torneo.getPremios())
        {
            assertEquals(0, premio.getMontoEfectivo(), 0.001, "Un torneo recreativo no entrega dinero.");
        }
    }

    @Test
    void doblesCompetitivo() throws Exception
    {
        TorneoIndividual torneo = Escenario.torneoTenis(club, tenis, "Copa Dobles", 4,
                new PremiacionCompetitiva(Escenario.TARIFA), Modalidad.DOBLES, Escenario.proximo(DayOfWeek.SUNDAY),
                LocalDate.now().minusDays(1));
        List<Socio> jugadores = Escenario.jugadores(club, tenis, 8);
        for (int i = 0; i < 8; i += 2)
        {
            Escenario.inscribir(club, torneo, jugadores.get(i), jugadores.get(i + 1));
        }
        club.iniciarTorneo(torneo);

        Escenario.jugarHastaElFinal(club, torneo);

        assertEquals(120000, torneo.getRecaudado(), 0.001, "4 parejas x 2 tarifas.");
        assertEquals(30000, efectivoDe(torneo, jugadores.get(0)), 0.001, "Cada campeón recibe la mitad de $60.000.");
        assertEquals(30000, efectivoDe(torneo, jugadores.get(1)), 0.001, "Cada campeón recibe la mitad de $60.000.");
        assertEquals(12000, efectivoDe(torneo, jugadores.get(4)), 0.001, "Cada subcampeón recibe la mitad de $24.000.");
        assertEquals(12000, efectivoDe(torneo, jugadores.get(5)), 0.001, "Cada subcampeón recibe la mitad de $24.000.");
    }

    @Test
    void doblesRecreativo() throws Exception
    {
        TorneoIndividual torneo = Escenario.torneoTenis(club, tenis, "Copa Dobles", 2, new PremiacionRecreativa(),
                Modalidad.DOBLES, Escenario.proximo(DayOfWeek.SUNDAY), LocalDate.now().minusDays(1));
        List<Socio> jugadores = Escenario.jugadores(club, tenis, 4);
        Escenario.inscribir(club, torneo, jugadores.get(0), jugadores.get(1));
        Escenario.inscribir(club, torneo, jugadores.get(2), jugadores.get(3));
        club.iniciarTorneo(torneo);

        Escenario.jugarHastaElFinal(club, torneo);

        for (int i = 0; i < 2; i++)
        {
            assertEquals(0.25, jugadores.get(i).getBonos().get(0).getPorcentaje(), 0.0001, "Cada campeón recibe su bono.");
            assertEquals(0.10, jugadores.get(i + 2).getBonos().get(0).getPorcentaje(), 0.0001, "Cada subcampeón recibe su bono.");
        }
        assertTrue(jugadores.get(0).getBonos().get(0) != jugadores.get(1).getBonos().get(0),
                "Cada integrante debe tener su propio bono.");
        assertFalse(torneo.getPremios().get(0).getBono().estaUsado(), "Los bonos nuevos no están usados.");
    }
}
