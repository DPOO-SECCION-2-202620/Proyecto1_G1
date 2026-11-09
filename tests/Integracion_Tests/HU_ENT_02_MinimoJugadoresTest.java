package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import NucleoSistema.ClubSeneca;

public class HU_ENT_02_MinimoJugadoresTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
    }

    @Test
    void futbolConMinimo() throws Exception
    {
        Equipo cerros = Escenario.equipo(club, "Cerros FC", new Futbol(), Categoria.MAYORES, 7, 0);

        assertEquals(7, cerros.contarJugadoresHabilitados(), "Los 7 jugadores están habilitados.");
        assertTrue(cerros.puedeJugar(), "El partido puede disputarse.");
    }

    @Test
    void baloncestoSinMinimo() throws Exception
    {
        Equipo basket = Escenario.equipo(club, "Basket Seneca", new Baloncesto(), Categoria.MAYORES, 5, 1);

        List<InscripcionEquipo> noDisponibles = basket.getInscripcionesSuspendidas();

        assertEquals(4, basket.contarJugadoresHabilitados(), "Solo hay 4 habilitados.");
        assertFalse(basket.puedeJugar(), "El partido no puede jugarse.");
        assertEquals(1, noDisponibles.size(), "Se lista al jugador no disponible.");
        assertEquals("Basket Seneca J1", noDisponibles.get(0).getJugador().getNombre(), "Se lista al suspendido.");
    }

    @Test
    void sancionadoNoCuenta() throws Exception
    {
        Equipo andes = Escenario.equipo(club, "Andes FC", new Futbol(), Categoria.MAYORES, 8, 0);
        InscripcionEquipo primero = andes.getInscripciones().get(0);

        primero.registrarTarjetas(0, 1);

        assertEquals(7, andes.contarJugadoresHabilitados(), "El sancionado no se cuenta.");
        assertFalse(andes.estaHabilitado(primero.getJugador()), "El sancionado no está habilitado.");
        assertSame(primero, andes.getInscripcionesSuspendidas().get(0), "Aparece entre los no disponibles.");
    }
}
