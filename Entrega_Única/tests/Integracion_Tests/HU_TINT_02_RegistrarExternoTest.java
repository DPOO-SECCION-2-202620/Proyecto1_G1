package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;
import Usuarios.ParticipanteExterno;
import Usuarios.Usuario;

public class HU_TINT_02_RegistrarExternoTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private Map<DisciplinaIndividual, NivelJugador> intermedioEnTenis;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        intermedioEnTenis = Map.of(tenis, NivelJugador.INTERMEDIO);
    }

    @Test
    void registrarEstudiante() throws Exception
    {
        ParticipanteExterno valentina = club.registrarParticipanteExterno(20, "Valentina Ruiz", "valentina", "clave",
                true, intermedioEnTenis);

        Usuario sesion = club.iniciarSesion("valentina", "clave");
        assertSame(valentina, sesion, "El participante debe poder iniciar sesión con su login y contraseña.");
        assertTrue(valentina.esEstudiante(), "Debe quedar registrado como estudiante.");
        assertEquals(NivelJugador.INTERMEDIO, valentina.getNivelEn(tenis), "Debe quedar registrado su nivel en tenis.");
        assertFalse(valentina.esEmpleado(), "Un participante externo no es empleado.");
    }

    @Test
    void externoSeInscribe() throws Exception
    {
        ParticipanteExterno pedro = club.registrarParticipanteExterno(30, "Pedro Externo", "pedro", "clave", false,
                intermedioEnTenis);
        TorneoIndividual torneo = Escenario.torneoTenis(club, tenis, "Copa Abierta", 8, new PremiacionRecreativa(),
                Modalidad.SENCILLOS);

        Escenario.inscribir(club, torneo, pedro, null);

        assertEquals(1, torneo.getInscripciones().size(), "La persona externa debe quedar inscrita.");
        assertFalse(torneo.getInscripciones().get(0).esPreferencial(), "Sin ranking no ocupa un cupo preferencial.");
    }

    @Test
    void registroPersiste() throws Exception
    {
        club.registrarParticipanteExterno(30, "Pedro Externo", "pedro", "clave", false, intermedioEnTenis);

        ClubSeneca recargado = Escenario.recargar(carpeta);

        Usuario sesion = recargado.iniciarSesion("pedro", "clave");
        assertTrue(sesion instanceof ParticipanteExterno, "Debe recargarse como participante externo.");
        ParticipanteExterno pedro = (ParticipanteExterno) sesion;
        assertEquals(NivelJugador.INTERMEDIO, pedro.getNivelEn(recargado.getDisciplinas().get(0)),
                "El nivel por disciplina debe conservarse.");
        assertFalse(pedro.esEstudiante(), "Debe conservarse que es una persona externa.");
    }

    @Test
    void loginOcupado()
    {
        club.crearSocio(25, "Ana Torres", "ana", "clave-ana");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.registrarParticipanteExterno(30, "Otra Ana", "ana", "otra", false, intermedioEnTenis));

        assertTrue(excepcion.getMessage().contains("ocupado"), "El mensaje debe indicar que el login está ocupado.");
        assertNull(club.iniciarSesion("ana", "otra"), "No debe quedar un segundo usuario con ese login.");
    }

    @Test
    void datosInvalidos()
    {
        ReglaNegocioException sinNombre = assertThrows(ReglaNegocioException.class,
                () -> club.registrarParticipanteExterno(30, "  ", "x1", "clave", false, intermedioEnTenis));
        ReglaNegocioException muyJoven = assertThrows(ReglaNegocioException.class,
                () -> club.registrarParticipanteExterno(4, "Nino", "x2", "clave", false, intermedioEnTenis));
        ReglaNegocioException muyMayor = assertThrows(ReglaNegocioException.class,
                () -> club.registrarParticipanteExterno(101, "Abuelo", "x3", "clave", false, intermedioEnTenis));

        assertTrue(sinNombre.getMessage().contains("nombre"), "El mensaje debe señalar el nombre.");
        assertTrue(muyJoven.getMessage().contains("edad"), "El mensaje debe señalar la edad.");
        assertTrue(muyMayor.getMessage().contains("edad"), "El mensaje debe señalar la edad.");
        assertNull(club.buscarJugadorTorneo("x2"), "No debe quedar registrado ningún participante.");
    }

    @Test
    void externoNoJuegaTorneoDeConjunto() throws Exception
    {
        ParticipanteExterno pedro = club.registrarParticipanteExterno(30, "Pedro Externo", "pedro", "clave", false,
                intermedioEnTenis);
        TorneoConjunto futbol = new TorneoConjunto("Mini Futbol", 4, Escenario.proximo(java.time.DayOfWeek.SUNDAY),
                java.time.LocalDate.now().plusDays(7), new PremiacionRecreativa(), new Futbol(), Categoria.MAYORES);
        club.agregarTorneo(futbol);

        assertThrows(ReglaNegocioException.class, () -> Escenario.inscribir(club, futbol, pedro, null),
                "En los torneos de conjunto solo se inscriben equipos.");
        assertEquals(0, futbol.getInscripciones().size(), "No debe quedar ninguna inscripción.");
    }
}
