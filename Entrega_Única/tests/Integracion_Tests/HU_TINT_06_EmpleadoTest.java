package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;
import TurnosEmpleados.Turno;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;

public class HU_TINT_06_EmpleadoTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private Entrenador luis;
    private Fisioterapeuta marta;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        luis = new Entrenador(38, "Luis Perez", "luis", "clave", "EMP-001");
        marta = new Fisioterapeuta(36, "Marta Diaz", "marta", "clave", "EMP-002");
        club.registrarUsuario(luis);
        club.registrarUsuario(marta);
        club.asignarTurno(luis, new Turno("Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        club.asignarTurno(marta, new Turno("Lunes", LocalTime.of(14, 0), LocalTime.of(18, 0)));
        club.inscribirEnDisciplina(luis, tenis, NivelJugador.INTERMEDIO);
        club.inscribirEnDisciplina(marta, tenis, NivelJugador.INTERMEDIO);
    }

    private TorneoIndividual competitivoEl(DayOfWeek dia) throws ReglaNegocioException
    {
        return Escenario.torneoTenis(club, tenis, "Copa del " + dia, 8, new PremiacionCompetitiva(Escenario.TARIFA),
                Modalidad.SENCILLOS, Escenario.proximo(dia), LocalDate.now().plusDays(7));
    }

    @Test
    void empleadoSinTurno() throws Exception
    {
        TorneoIndividual torneo = competitivoEl(DayOfWeek.SATURDAY);

        Escenario.inscribir(club, torneo, luis, null);

        assertEquals(1, torneo.getInscripciones().size(), "El entrenador debe quedar inscrito.");
        assertEquals(0, torneo.getInscripciones().get(0).getMontoPagado(), 0.001, "Un empleado no paga tarifa.");
    }

    @Test
    void empleadoDeTurno() throws Exception
    {
        TorneoIndividual torneo = competitivoEl(DayOfWeek.MONDAY);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, torneo, marta, null));

        assertTrue(excepcion.getMessage().contains("turno"), "Debe informar que está de turno ese día.");
        assertEquals(0, torneo.getInscripciones().size(), "La fisioterapeuta no debe quedar inscrita.");
    }

    @Test
    void empleadoEnTorneoDeConjunto() throws Exception
    {
        TorneoConjunto futbol = new TorneoConjunto("Mini Futbol", 4, Escenario.proximo(DayOfWeek.SATURDAY),
                LocalDate.now().plusDays(7), new PremiacionRecreativa(), new Futbol(), Categoria.MAYORES);
        club.agregarTorneo(futbol);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> Escenario.inscribir(club, futbol, luis, null));

        assertTrue(excepcion.getMessage().contains("individuales"),
                "Debe informar que los empleados solo participan en torneos individuales.");
    }

    @Test
    void empleadoNoSumaRecaudo() throws Exception
    {
        TorneoIndividual torneo = competitivoEl(DayOfWeek.SATURDAY);
        Socio ana = Escenario.jugador(club, tenis, "Ana Torres", NivelJugador.INTERMEDIO);

        Escenario.inscribir(club, torneo, ana, null);
        Escenario.inscribir(club, torneo, luis, null);

        assertEquals(Escenario.TARIFA, torneo.getRecaudado(), 0.001, "Solo debe contarse la tarifa de Ana.");
    }

    @Test
    void empleadoSinNivel() throws Exception
    {
        Entrenador pablo = new Entrenador(41, "Pablo Rios", "pablo", "clave", "EMP-003");
        club.registrarUsuario(pablo);
        TorneoIndividual torneo = competitivoEl(DayOfWeek.SATURDAY);

        assertThrows(ReglaNegocioException.class, () -> Escenario.inscribir(club, torneo, pablo, null),
                "Un empleado sin nivel en tenis no se puede inscribir.");
    }
}
