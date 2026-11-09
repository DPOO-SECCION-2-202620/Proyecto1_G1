package Integracion_Tests;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import NucleoSistema.Persistencia;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.JugadorTorneo;
import TorneosInternos.Premiacion;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;

public final class Escenario
{
    public static final double TARIFA = 15000;

    private static int contador = 0;

    private Escenario()
    {
    }

    public static ClubSeneca club(File carpeta)
    {
        ClubSeneca club = new ClubSeneca("Club Seneca");
        club.setPersistencia(new Persistencia(carpeta.getAbsolutePath()));
        return club;
    }

    public static ClubSeneca recargar(File carpeta) throws Exception
    {
        return new Persistencia(carpeta.getAbsolutePath()).cargar();
    }

    public static Tenis tenis(ClubSeneca club)
    {
        Tenis tenis = new Tenis(2, "Tenis");
        club.registrarDisciplina(tenis);
        return tenis;
    }

    public static LocalDate proximo(DayOfWeek dia)
    {
        LocalDate fecha = LocalDate.now().plusDays(8);
        while (fecha.getDayOfWeek() != dia)
        {
            fecha = fecha.plusDays(1);
        }
        return fecha;
    }

    public static TorneoIndividual torneoTenis(ClubSeneca club, Tenis tenis, String nombre, int cupos,
            Premiacion premiacion, Modalidad modalidad) throws ReglaNegocioException
    {
        return torneoTenis(club, tenis, nombre, cupos, premiacion, modalidad, proximo(DayOfWeek.SUNDAY),
                LocalDate.now().plusDays(7));
    }

    public static TorneoIndividual torneoSinReserva(ClubSeneca club, Tenis tenis, String nombre, int cupos,
            Premiacion premiacion, Modalidad modalidad) throws ReglaNegocioException
    {
        return torneoTenis(club, tenis, nombre, cupos, premiacion, modalidad, proximo(DayOfWeek.SUNDAY),
                LocalDate.now().minusDays(1));
    }

    public static TorneoIndividual torneoTenis(ClubSeneca club, Tenis tenis, String nombre, int cupos,
            Premiacion premiacion, Modalidad modalidad, LocalDate inicio, LocalDate limite) throws ReglaNegocioException
    {
        TorneoIndividual torneo = new TorneoIndividual(nombre, cupos, inicio, limite, premiacion, tenis,
                NivelJugador.INTERMEDIO, modalidad);
        club.agregarTorneo(torneo);
        return torneo;
    }

    public static Socio jugador(ClubSeneca club, Tenis tenis, String nombre, NivelJugador nivel)
    {
        contador++;
        Socio socio = club.crearSocio(25, nombre, "jugador" + contador, "clave");
        if (nivel != null)
        {
            club.inscribirEnDisciplina(socio, tenis, nivel);
        }
        return socio;
    }

    public static List<Socio> jugadores(ClubSeneca club, Tenis tenis, int cantidad)
    {
        List<Socio> jugadores = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++)
        {
            jugadores.add(jugador(club, tenis, "Jugador " + i, NivelJugador.INTERMEDIO));
        }
        return jugadores;
    }

    public static void inscribir(ClubSeneca club, Torneo torneo, JugadorTorneo jugador, JugadorTorneo companero)
            throws ReglaNegocioException
    {
        club.inscribirEnTorneo(torneo, new InscripcionTorneoIndividual(jugador, companero));
    }

    public static List<Socio> llenar(ClubSeneca club, Tenis tenis, Torneo torneo, int cantidad)
            throws ReglaNegocioException
    {
        List<Socio> jugadores = jugadores(club, tenis, cantidad);
        for (Socio jugador : jugadores)
        {
            inscribir(club, torneo, jugador, null);
        }
        return jugadores;
    }

    public static void jugarHastaElFinal(ClubSeneca club, Torneo torneo) throws ReglaNegocioException
    {
        while (torneo.getEstado() == EstadoTorneo.EN_CURSO)
        {
            Encuentro encuentro = torneo.getEncuentrosPendientes().get(0);
            club.registrarGanadorTorneo(torneo, encuentro, encuentro.getParticipanteA());
        }
    }

    public static void jugarRonda(ClubSeneca club, Torneo torneo) throws ReglaNegocioException
    {
        int ronda = torneo.getRondaActual();
        for (Encuentro encuentro : torneo.getEncuentrosDeRonda(ronda))
        {
            if (!encuentro.tieneGanador())
            {
                club.registrarGanadorTorneo(torneo, encuentro, encuentro.getParticipanteA());
            }
        }
    }

    public static Equipo equipo(ClubSeneca club, String nombre, DeporteConjunto deporte, Categoria categoria,
            int jugadores, int suspendidos) throws ReglaNegocioException
    {
        Equipo equipo = club.crearEquipo(nombre, 20, deporte, categoria);
        int edad = categoria.getEdadMinima() + 1;
        for (int i = 1; i <= jugadores; i++)
        {
            contador++;
            Socio socio = club.crearSocio(edad, nombre + " J" + i, "equipo" + contador, "clave");
            club.inscribirJugador(socio, equipo, "Posicion " + i, i);
            if (i <= suspendidos)
            {
                equipo.getInscripcionDe(socio).registrarTarjetas(0, 1);
            }
        }
        return equipo;
    }
}
