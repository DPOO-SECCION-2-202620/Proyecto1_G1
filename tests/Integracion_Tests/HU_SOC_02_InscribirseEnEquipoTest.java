package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesConjunto_Competencias.InscripcionEquipo;
import DeportesConjunto_Competencias.Voleibol;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Usuarios.Socio;

public class HU_SOC_02_InscribirseEnEquipoTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Futbol futbol;
    private Equipo sub15;
    private Socio laura;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        futbol = new Futbol();
        sub15 = club.crearEquipo("Seneca Sub-15", 11, futbol, Categoria.SUB15);
        laura = club.crearSocio(14, "Laura", "laura", "clave");
    }

    @Test
    void inscripcionValida() throws Exception
    {
        club.inscribirJugador(laura, sub15, "Delantera", 9);

        InscripcionEquipo inscripcion = sub15.getInscripcionDe(laura);
        assertNotNull(inscripcion, "Laura debe quedar inscrita.");
        assertEquals(9, inscripcion.getNumeroCamiseta(), "Debe quedar con su dorsal.");
        assertEquals("Delantera", inscripcion.getPosicion(), "Debe quedar con su posición.");
        assertEquals(1, sub15.getInscripciones().size(), "Se ocupa un cupo del equipo.");
        assertTrue(laura.tieneEquipoEn(futbol), "Laura ya tiene equipo de fútbol.");
    }

    @Test
    void edadIncompatible()
    {
        Socio carlos = club.crearSocio(20, "Carlos", "carlos", "clave");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.inscribirJugador(carlos, sub15, "Defensa", 4));

        assertEquals("La edad del socio no corresponde a la categoría del equipo", excepcion.getMessage(),
                "Debe indicar la incompatibilidad de categoría.");
        assertNull(sub15.getInscripcionDe(carlos), "Carlos no debe quedar inscrito.");
    }

    @Test
    void dorsalOcupado() throws Exception
    {
        Socio sofia = club.crearSocio(13, "Sofia", "sofia", "clave");
        club.inscribirJugador(sofia, sub15, "Portera", 9);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.inscribirJugador(laura, sub15, "Delantera", 9));

        assertEquals("El dorsal ya fue asignado a otro jugador del equipo; elija un dorsal libre", excepcion.getMessage(),
                "Debe pedir un dorsal libre.");
        assertNull(sub15.getInscripcionDe(laura), "Laura no debe quedar inscrita.");
    }

    @Test
    void unSoloEquipoPorDisciplina() throws Exception
    {
        Equipo otro = club.crearEquipo("Andes Sub-15", 11, futbol, Categoria.SUB15);
        club.inscribirJugador(laura, sub15, "Delantera", 9);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> club.inscribirJugador(laura, otro, "Delantera", 7));

        assertEquals("El socio ya pertenece a un equipo de este deporte; solo puede pertenecer a un único equipo por disciplina",
                excepcion.getMessage(), "Debe indicar que solo puede tener un equipo por disciplina.");
        assertNull(otro.getInscripcionDe(laura), "Laura no debe quedar en el segundo equipo.");
    }

    @Test
    void equiposDeDistintoDeporte() throws Exception
    {
        Equipo voleibol = club.crearEquipo("Seneca Voley Sub-15", 12, new Voleibol(), Categoria.SUB15);
        club.inscribirJugador(laura, sub15, "Delantera", 9);

        club.inscribirJugador(laura, voleibol, "Libero", 9);

        assertNotNull(voleibol.getInscripcionDe(laura), "Laura puede jugar fútbol y voleibol.");
    }

    @Test
    void inscripcionPersiste() throws Exception
    {
        club.inscribirJugador(laura, sub15, "Delantera", 9);

        ClubSeneca recargado = Escenario.recargar(carpeta);

        assertEquals(1, recargado.getEquipos().get(0).getInscripciones().size(), "La inscripción debe recargarse.");
        assertEquals(9, recargado.getEquipos().get(0).getInscripciones().get(0).getNumeroCamiseta(),
                "Se conserva el dorsal.");
    }
}
