package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.TorneoConjunto;

public class HU_TINT_03_InscribirEquipoTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Futbol futbol;
    private TorneoConjunto torneo;

    @BeforeEach
    void setUp() throws Exception
    {
        club = Escenario.club(carpeta);
        futbol = new Futbol();
        torneo = new TorneoConjunto("Mini-campeonato Futbol Mayores", 8, Escenario.proximo(DayOfWeek.SUNDAY),
                LocalDate.now().plusDays(7), new PremiacionCompetitiva(Escenario.TARIFA), futbol, Categoria.MAYORES);
        club.agregarTorneo(torneo);
    }

    private void inscribir(Equipo equipo) throws ReglaNegocioException
    {
        club.inscribirEnTorneo(torneo, new InscripcionTorneoEquipo(equipo));
    }

    @Test
    void inscripcionValida() throws Exception
    {
        Equipo cerros = Escenario.equipo(club, "Cerros FC", futbol, Categoria.MAYORES, 7, 0);

        inscribir(cerros);

        assertEquals(1, torneo.getInscripciones().size(), "El equipo debe quedar inscrito.");
        assertEquals(7, torneo.getCuposLibres(), "Debe descontarse un solo cupo por el equipo.");
        assertEquals(Escenario.TARIFA, torneo.getRecaudado(), 0.001, "Un equipo paga una sola tarifa.");
    }

    @Test
    void equipoIncompatible() throws Exception
    {
        Equipo basket = Escenario.equipo(club, "Basket Seneca", new Baloncesto(), Categoria.MAYORES, 5, 0);
        Equipo sub17 = Escenario.equipo(club, "Seneca Sub17", futbol, Categoria.SUB17, 7, 0);

        ReglaNegocioException otroDeporte = assertThrows(ReglaNegocioException.class, () -> inscribir(basket));
        ReglaNegocioException otraCategoria = assertThrows(ReglaNegocioException.class, () -> inscribir(sub17));

        assertTrue(otroDeporte.getMessage().contains("Incompatibilidad"), "Debe indicar la incompatibilidad.");
        assertTrue(otraCategoria.getMessage().contains("Incompatibilidad"), "Debe indicar la incompatibilidad.");
        assertEquals(0, torneo.getInscripciones().size(), "No debe quedar ningún equipo inscrito.");
    }

    @Test
    void sinMinimoDeHabilitados() throws Exception
    {
        Equipo andes = Escenario.equipo(club, "Andes FC", futbol, Categoria.MAYORES, 8, 2);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> inscribir(andes));

        assertTrue(excepcion.getMessage().contains("Andes FC J1"), "Debe listar a los jugadores suspendidos.");
        assertEquals(0, torneo.getInscripciones().size(), "El equipo no debe quedar inscrito.");
    }

    @Test
    void sinMinimoDeInscritos() throws Exception
    {
        Equipo pequeno = Escenario.equipo(club, "Pequeno FC", futbol, Categoria.MAYORES, 5, 0);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> inscribir(pequeno));

        assertTrue(excepcion.getMessage().contains("faltan"), "Debe indicar cuántos jugadores faltan.");
    }

    @Test
    void equipoRepetido() throws Exception
    {
        Equipo cerros = Escenario.equipo(club, "Cerros FC", futbol, Categoria.MAYORES, 7, 0);
        inscribir(cerros);

        assertThrows(ReglaNegocioException.class, () -> inscribir(cerros), "El equipo ya está inscrito.");
        assertEquals(1, torneo.getInscripciones().size(), "Debe seguir habiendo una sola inscripción.");
    }
}
