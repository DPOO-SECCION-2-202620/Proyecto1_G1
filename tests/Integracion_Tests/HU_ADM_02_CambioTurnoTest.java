package Integracion_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;

public class HU_ADM_02_CambioTurnoTest
{
    @TempDir
    File carpeta;

    private ClubSeneca club;
    private Administrador admin;
    private Entrenador luis;
    private Entrenador carla;
    private Fisioterapeuta marta;
    private Turno mananaLuis;
    private Turno mananaCarla;
    private Turno mananaMarta;

    private static Turno lunes(int desde, int hasta)
    {
        return new Turno("Lunes", LocalTime.of(desde, 0), LocalTime.of(hasta, 0));
    }

    @BeforeEach
    void setUp()
    {
        club = Escenario.club(carpeta);
        admin = new Administrador("Ana Admin", "admin", "clave");
        club.registrarUsuario(admin);
        luis = new Entrenador(30, "Luis", "luis", "clave", "E1");
        carla = new Entrenador(31, "Carla", "carla", "clave", "E2");
        Entrenador pablo = new Entrenador(32, "Pablo", "pablo", "clave", "E3");
        marta = new Fisioterapeuta(33, "Marta", "marta", "clave", "F1");
        club.registrarUsuario(luis);
        club.registrarUsuario(carla);
        club.registrarUsuario(pablo);
        club.registrarUsuario(marta);
        mananaLuis = lunes(8, 12);
        mananaCarla = lunes(8, 12);
        mananaMarta = lunes(8, 12);
        club.asignarTurno(luis, mananaLuis);
        club.asignarTurno(carla, mananaCarla);
        club.asignarTurno(pablo, lunes(8, 12));
        club.asignarTurno(marta, mananaMarta);
    }

    private SolicitudCambioTurno pedirCambio(Empleado empleado, Turno actual, Turno nuevo) throws ReglaNegocioException
    {
        club.registrarSolicitudTurno(empleado, actual, nuevo);
        List<SolicitudCambioTurno> pendientes = club.getSolicitudesPendientes();
        return pendientes.get(pendientes.size() - 1);
    }

    @Test
    void apruebaCambio() throws Exception
    {
        Turno tarde = lunes(14, 18);
        SolicitudCambioTurno solicitud = pedirCambio(luis, mananaLuis, tarde);

        admin.resolverSolicitudTurno(solicitud, true);

        assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado(), "La solicitud debe quedar aceptada.");
        assertTrue(luis.getTurnos().contains(tarde), "Luis debe tener el turno nuevo.");
        assertFalse(luis.getTurnos().contains(mananaLuis), "Luis ya no tiene el turno anterior.");
        assertEquals(0, club.getSolicitudesPendientes().size(), "No quedan solicitudes pendientes.");
    }

    @Test
    void rechazaPorEntrenadores() throws Exception
    {
        admin.resolverSolicitudTurno(pedirCambio(luis, mananaLuis, lunes(14, 18)), true);
        SolicitudCambioTurno solicitud = pedirCambio(carla, mananaCarla, lunes(14, 18));

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class,
                () -> admin.resolverSolicitudTurno(solicitud, true));

        assertEquals("La aprobación dejaría menos de 2 entrenadores o menos de 1 fisioterapeuta disponibles",
                excepcion.getMessage(), "El mensaje de rechazo no es el esperado.");
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado(), "La solicitud sigue pendiente.");
        assertTrue(carla.getTurnos().contains(mananaCarla), "Carla conserva su turno.");
    }

    @Test
    void rechazaPorFisioterapeuta() throws Exception
    {
        SolicitudCambioTurno solicitud = pedirCambio(marta, mananaMarta, lunes(14, 18));

        assertThrows(ReglaNegocioException.class, () -> club.resolverSolicitud(solicitud, true),
                "No puede quedar la mañana sin fisioterapeuta.");

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado(), "La solicitud sigue pendiente.");
        assertTrue(marta.getTurnos().contains(mananaMarta), "Marta conserva su turno.");
    }

    @Test
    void administradorRechaza() throws Exception
    {
        SolicitudCambioTurno solicitud = pedirCambio(luis, mananaLuis, lunes(14, 18));

        club.resolverSolicitud(solicitud, false);

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado(), "La solicitud debe quedar rechazada.");
        assertTrue(luis.getTurnos().contains(mananaLuis), "Luis conserva su turno.");
    }
}
