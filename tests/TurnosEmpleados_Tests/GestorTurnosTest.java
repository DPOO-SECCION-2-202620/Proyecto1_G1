package TurnosEmpleados_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import TurnosEmpleados.GestorTurnos;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;

public class GestorTurnosTest
{
    private GestorTurnos gestor;

    private Entrenador entrenador1;
    private Entrenador entrenador2;
    private Entrenador entrenador3;
    private Fisioterapeuta fisioterapeuta;

    private Turno turnoLunesE1;
    private Turno turnoLunesE2;
    private Turno turnoLunesE3;
    private Turno turnoLunesF;

    private List<Empleado> equipoCompleto;
    private List<Empleado> equipoMinimo;

    @BeforeEach
    void setUp() throws Exception
    {
        gestor = new GestorTurnos();

        entrenador1 = new Entrenador(30, "Entrenador Uno", "e1", "1", "E1");
        entrenador2 = new Entrenador(31, "Entrenador Dos", "e2", "2", "E2");
        entrenador3 = new Entrenador(32, "Entrenador Tres", "e3", "3", "E3");
        fisioterapeuta = new Fisioterapeuta(33, "Fisio Uno", "f1", "4", "F1");

        turnoLunesE1 = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoLunesE2 = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoLunesE3 = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoLunesF = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));

        entrenador1.agregarTurno(turnoLunesE1);
        entrenador2.agregarTurno(turnoLunesE2);
        entrenador3.agregarTurno(turnoLunesE3);
        fisioterapeuta.agregarTurno(turnoLunesF);

        equipoCompleto = new ArrayList<Empleado>();
        equipoCompleto.add(entrenador1);
        equipoCompleto.add(entrenador2);
        equipoCompleto.add(entrenador3);
        equipoCompleto.add(fisioterapeuta);

        equipoMinimo = new ArrayList<Empleado>();
        equipoMinimo.add(entrenador1);
        equipoMinimo.add(entrenador2);
        equipoMinimo.add(fisioterapeuta);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testCumpleMinimo()
    {
        assertTrue(gestor.cumpleMinimo("LUNES", equipoMinimo, LocalTime.of(10, 0)), "Con dos entrenadores y un fisioterapeuta se cumple el mínimo.");
    }

    @Test
    void testCumpleMinimoEnLosLimites()
    {
        assertTrue(gestor.cumpleMinimo("LUNES", equipoMinimo, LocalTime.of(8, 0)), "En la hora de inicio se cumple el mínimo.");
        assertTrue(gestor.cumpleMinimo("LUNES", equipoMinimo, LocalTime.of(12, 0)), "En la hora final se cumple el mínimo.");
    }

    @Test
    void testCumpleMinimoFueraDeHorario()
    {
        assertFalse(gestor.cumpleMinimo("LUNES", equipoMinimo, LocalTime.of(13, 0)), "Fuera del turno no se cumple el mínimo.");
    }

    @Test
    void testCumpleMinimoOtroDia()
    {
        assertFalse(gestor.cumpleMinimo("MARTES", equipoMinimo, LocalTime.of(10, 0)), "Otro día sin turnos no cumple el mínimo.");
    }

    @Test
    void testCumpleMinimoConUnSoloEntrenador()
    {
        equipoMinimo.remove(entrenador2);

        assertFalse(gestor.cumpleMinimo("LUNES", equipoMinimo, LocalTime.of(10, 0)), "Con un solo entrenador no se cumple el mínimo.");
    }

    @Test
    void testCumpleMinimoSinFisioterapeuta()
    {
        equipoMinimo.remove(fisioterapeuta);

        assertFalse(gestor.cumpleMinimo("LUNES", equipoMinimo, LocalTime.of(10, 0)), "Sin fisioterapeuta no se cumple el mínimo.");
    }

    @Test
    void testCumpleMinimoListaVacia()
    {
        assertFalse(gestor.cumpleMinimo("LUNES", new ArrayList<Empleado>(), LocalTime.of(10, 0)), "Sin empleados no se cumple el mínimo.");
    }

    @Test
    void testPuedeAprobarConEmpleadosSobrantes() throws Exception
    {
        Turno turnoMartes = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        SolicitudCambioTurno solicitud = entrenador3.solicitarCambioTurno(turnoLunesE3, turnoMartes);

        assertTrue(gestor.puedeAprobar(solicitud, equipoCompleto), "Con un entrenador de sobra el cambio puede aprobarse.");
    }

    @Test
    void testPuedeAprobarDejaSinEntrenadores() throws Exception
    {
        Turno turnoMartes = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        SolicitudCambioTurno solicitud = entrenador2.solicitarCambioTurno(turnoLunesE2, turnoMartes);

        assertFalse(gestor.puedeAprobar(solicitud, equipoMinimo), "El cambio no puede aprobarse si deja menos de dos entrenadores.");
    }

    @Test
    void testPuedeAprobarDejaSinFisioterapeuta() throws Exception
    {
        Turno turnoMartes = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        SolicitudCambioTurno solicitud = fisioterapeuta.solicitarCambioTurno(turnoLunesF, turnoMartes);

        assertFalse(gestor.puedeAprobar(solicitud, equipoCompleto), "El cambio no puede aprobarse si deja el turno sin fisioterapeuta.");
    }

    @Test
    void testPuedeAprobarCambioParcialDeHorario() throws Exception
    {
        Turno turnoTarde = new Turno("LUNES", LocalTime.of(10, 0), LocalTime.of(14, 0));
        SolicitudCambioTurno solicitud = fisioterapeuta.solicitarCambioTurno(turnoLunesF, turnoTarde);

        assertFalse(gestor.puedeAprobar(solicitud, equipoCompleto), "El cambio no puede aprobarse si deja un tramo del turno sin fisioterapeuta.");
    }

    @Test
    void testPuedeAprobarSolicitudYaAprobada() throws Exception
    {
        Turno turnoMartes = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        SolicitudCambioTurno solicitud = entrenador3.solicitarCambioTurno(turnoLunesE3, turnoMartes);
        solicitud.aprobar();

        assertFalse(gestor.puedeAprobar(solicitud, equipoCompleto), "Una solicitud que no está pendiente no puede aprobarse.");
    }

    @Test
    void testPuedeAprobarSolicitudYaRechazada() throws Exception
    {
        Turno turnoMartes = new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        SolicitudCambioTurno solicitud = entrenador3.solicitarCambioTurno(turnoLunesE3, turnoMartes);
        solicitud.rechazar();

        assertFalse(gestor.puedeAprobar(solicitud, equipoCompleto), "Una solicitud rechazada no puede aprobarse.");
    }
}
