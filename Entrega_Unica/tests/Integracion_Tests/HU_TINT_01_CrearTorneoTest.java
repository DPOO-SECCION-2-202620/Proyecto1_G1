package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;

public class HU_TINT_01_CrearTorneoTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Tenis tenis;
    private LocalDate inicio;
    private LocalDate limite;

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        tenis = Escenario.tenis(club);
        inicio = LocalDate.now().plusDays(14);
        limite = LocalDate.now().plusDays(7);
    }

    @Test
    void crearTorneoCompetitivo() throws Exception
    {
        TorneoIndividual torneo = new TorneoIndividual("Copa Tenis Avanzado", 16, inicio, limite,
                new PremiacionCompetitiva(Escenario.TARIFA), tenis, NivelJugador.AVANZADO, Modalidad.SENCILLOS);

        club.agregarTorneo(torneo);

        assertEquals(1, club.getTorneos().size(), "El torneo debe quedar registrado en el club.");
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado(), "Un torneo nuevo debe tener inscripciones abiertas.");
        assertEquals(4, torneo.getCuposPreferenciales(), "El 20% de 16 es 3,2 y se redondea hacia arriba a 4.");
        assertEquals(12, torneo.getCuposLibres() - torneo.getCuposPreferencialesLibres(), "Deben quedar 12 cupos regulares.");
        assertEquals(Escenario.TARIFA, ((PremiacionCompetitiva) torneo.getPremiacion()).getTarifaBase(), 0.001,
                "La tarifa registrada no es la esperada.");
    }

    @Test
    void torneoRecreativoSinTarifa() throws Exception
    {
        TorneoIndividual torneo = Escenario.torneoTenis(club, tenis, "Copa Recreativa", 8, new PremiacionRecreativa(),
                Modalidad.SENCILLOS);

        Escenario.llenar(club, tenis, torneo, 2);

        assertEquals(0, torneo.getInscripciones().get(0).getMontoPagado(), 0.001, "La inscripción debe ser gratuita.");
        assertEquals(0, torneo.getRecaudado(), 0.001, "Un torneo recreativo no recauda dinero.");
    }

    @Test
    void competitivoSinTarifa()
    {
        assertThrows(ReglaNegocioException.class, () -> new PremiacionCompetitiva(0),
                "Una tarifa de 0 no es válida para un torneo competitivo.");
    }

    @Test
    void cuposFueraDeRango()
    {
        ReglaNegocioException pocos = assertThrows(ReglaNegocioException.class,
                () -> Escenario.torneoTenis(club, tenis, "Uno", 1, new PremiacionRecreativa(), Modalidad.SENCILLOS));
        ReglaNegocioException muchos = assertThrows(ReglaNegocioException.class,
                () -> Escenario.torneoTenis(club, tenis, "Muchos", 33, new PremiacionRecreativa(), Modalidad.SENCILLOS));

        assertTrue(pocos.getMessage().contains("2 y 32"), "El mensaje debe indicar el rango permitido.");
        assertTrue(muchos.getMessage().contains("2 y 32"), "El mensaje debe indicar el rango permitido.");
        assertEquals(0, club.getTorneos().size(), "No debe quedar ningún torneo registrado.");
    }

    @Test
    void padelSencillos()
    {
        Padel padel = new Padel(1, "Padel");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> new TorneoIndividual("Copa Padel", 8, inicio, limite, new PremiacionRecreativa(), padel,
                        NivelJugador.INTERMEDIO, Modalidad.SENCILLOS));

        assertTrue(excepcion.getMessage().contains("dobles"), "El mensaje debe indicar que el pádel es de dobles.");
    }

    @Test
    void fechaLimiteDespuesDelInicio()
    {
        assertThrows(ReglaNegocioException.class,
                () -> Escenario.torneoTenis(club, tenis, "Copa", 8, new PremiacionRecreativa(), Modalidad.SENCILLOS,
                        inicio, inicio.plusDays(1)),
                "La fecha límite no puede ser posterior al inicio.");
        assertEquals(0, club.getTorneos().size(), "No debe quedar ningún torneo registrado.");
    }

    @Test
    void torneosSimultaneos() throws Exception
    {
        club.agregarTorneo(new TorneoConjunto("Futbol Sub-17", 8, inicio, limite, new PremiacionRecreativa(),
                new Futbol(), Categoria.SUB17));
        club.agregarTorneo(new TorneoConjunto("Baloncesto Sub-15", 4, inicio, limite,
                new PremiacionCompetitiva(Escenario.TARIFA), new Baloncesto(), Categoria.SUB15));

        assertEquals(2, club.getTorneos().size(), "Ambos torneos deben quedar registrados.");
        assertEquals(2, club.getTorneos(EstadoTorneo.INSCRIPCIONES_ABIERTAS).size(), "Ambos deben estar abiertos.");
    }
}
