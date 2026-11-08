package TurnosEmpleados_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Usuario;

public class SolicitudCambioTurnoTest
{
    private Empleado empleado;
    private Turno turnoLunes;
    private Turno turnoMartes;
    private SolicitudCambioTurno solicitud;

    private Map<String, Usuario> mapaUsuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        turnoLunes = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoMartes = new Turno("MARTES", LocalTime.of(14, 0), LocalTime.of(18, 0));
        empleado.agregarTurno(turnoLunes);

        solicitud = new SolicitudCambioTurno(turnoLunes, turnoMartes, empleado);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("lmora", empleado);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testSolicitudNueva()
    {
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado(), "Una solicitud nueva debe estar pendiente.");
        assertSame(turnoLunes, solicitud.getTurnoActual(), "El turno actual no es el esperado.");
        assertSame(turnoMartes, solicitud.getTurnoNuevo(), "El turno nuevo no es el esperado.");
        assertSame(empleado, solicitud.getSolicitante(), "El solicitante no es el esperado.");
    }

    @Test
    void testAprobar()
    {
        solicitud.aprobar();

        assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado(), "La solicitud aprobada debe quedar aceptada.");
    }

    @Test
    void testRechazar()
    {
        solicitud.rechazar();

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado(), "La solicitud rechazada debe quedar rechazada.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = solicitud.toJSON();

        assertEquals("PENDIENTE", json.getString("estado"), "El estado en el JSON no es el esperado.");
        assertEquals("lmora", json.getString("solicitante"), "El solicitante en el JSON no es el esperado.");
        assertEquals("LUNES", json.getJSONObject("turnoActual").getString("diaSemana"), "El turno actual en el JSON no es el esperado.");
        assertEquals("MARTES", json.getJSONObject("turnoNuevo").getString("diaSemana"), "El turno nuevo en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONConTurnosCompletos()
    {
        solicitud.aprobar();

        SolicitudCambioTurno restaurada = SolicitudCambioTurno.fromJSON(solicitud.toJSON(), mapaUsuarios);

        assertEquals(EstadoSolicitud.ACEPTADA, restaurada.getEstado(), "El estado restaurado no es el esperado.");
        assertSame(empleado, restaurada.getSolicitante(), "El solicitante restaurado no es el esperado.");
        assertSame(turnoLunes, restaurada.getTurnoActual(), "El turno actual restaurado debe ser el mismo objeto del empleado.");
        assertEquals("MARTES", restaurada.getTurnoNuevo().getDiaSemana(), "El día del turno nuevo restaurado no es el esperado.");
        assertEquals(LocalTime.of(14, 0), restaurada.getTurnoNuevo().getHoraInicio(), "La hora del turno nuevo restaurado no es la esperada.");
    }

    @Test
    void testFromJSONBuscaElTurnoActualEntreVariosTurnos()
    {
        Empleado conVariosTurnos = new Empleado(31, "Sara Paz", "spaz", "def", "E02");
        Turno otroTurno = new Turno("JUEVES", LocalTime.of(6, 0), LocalTime.of(9, 0));
        Turno turnoDeLaSolicitud = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        conVariosTurnos.agregarTurno(otroTurno);
        conVariosTurnos.agregarTurno(turnoDeLaSolicitud);
        mapaUsuarios.put("spaz", conVariosTurnos);

        SolicitudCambioTurno original = new SolicitudCambioTurno(turnoDeLaSolicitud, turnoMartes, conVariosTurnos);

        SolicitudCambioTurno restaurada = SolicitudCambioTurno.fromJSON(original.toJSON(), mapaUsuarios);

        assertSame(turnoDeLaSolicitud, restaurada.getTurnoActual(), "El turno actual debe ser el turno del empleado que coincide, aunque no sea el primero.");
    }

    @Test
    void testFromJSONTurnoActualQueElEmpleadoNoTiene()
    {
        Empleado sinCoincidencia = new Empleado(31, "Sara Paz", "spaz", "def", "E02");
        sinCoincidencia.agregarTurno(new Turno("JUEVES", LocalTime.of(6, 0), LocalTime.of(9, 0)));
        mapaUsuarios.put("spaz", sinCoincidencia);

        SolicitudCambioTurno original = new SolicitudCambioTurno(turnoLunes, turnoMartes, sinCoincidencia);

        SolicitudCambioTurno restaurada = SolicitudCambioTurno.fromJSON(original.toJSON(), mapaUsuarios);

        assertNotSame(turnoLunes, restaurada.getTurnoActual(), "Sin coincidencia se debe crear un turno nuevo.");
        assertEquals("LUNES", restaurada.getTurnoActual().getDiaSemana(), "El día del turno creado no es el esperado.");
        assertEquals(LocalTime.of(8, 0), restaurada.getTurnoActual().getHoraInicio(), "La hora de inicio del turno creado no es la esperada.");
    }

    @Test
    void testFromJSONConIndicesDeTurno()
    {
        empleado.agregarTurno(turnoMartes);

        JSONObject json = new JSONObject();
        json.put("estado", "RECHAZADA");
        json.put("solicitante", "lmora");
        json.put("turnoActual", 0);
        json.put("turnoNuevo", 1);

        SolicitudCambioTurno restaurada = SolicitudCambioTurno.fromJSON(json, mapaUsuarios);

        assertEquals(EstadoSolicitud.RECHAZADA, restaurada.getEstado(), "El estado restaurado no es el esperado.");
        assertSame(turnoLunes, restaurada.getTurnoActual(), "El índice 0 debe corresponder al primer turno del empleado.");
        assertSame(turnoMartes, restaurada.getTurnoNuevo(), "El índice 1 debe corresponder al segundo turno del empleado.");
    }

    @Test
    void testFromJSONIndiceFueraDeRango()
    {
        JSONObject json = new JSONObject();
        json.put("estado", "PENDIENTE");
        json.put("solicitante", "lmora");
        json.put("turnoActual", 5);
        json.put("turnoNuevo", 0);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> SolicitudCambioTurno.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el índice está fuera de rango.");

        assertEquals("Índice de turno fuera de rango para lmora: 5", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONFormatoDeTurnoInvalido()
    {
        JSONObject json = new JSONObject();
        json.put("estado", "PENDIENTE");
        json.put("solicitante", "lmora");
        json.put("turnoActual", "LUNES");
        json.put("turnoNuevo", 0);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> SolicitudCambioTurno.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el formato del turno no es válido.");

        assertEquals("Formato de turno no válido: LUNES", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSolicitanteNoEmpleado()
    {
        JSONObject json = solicitud.toJSON();
        json.put("solicitante", "admin");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> SolicitudCambioTurno.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el solicitante no es empleado.");

        assertEquals("El solicitante no es un empleado válido: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSolicitanteInexistente()
    {
        JSONObject json = solicitud.toJSON();
        json.put("solicitante", "nadie");

        assertThrows(IllegalArgumentException.class, () -> SolicitudCambioTurno.fromJSON(json, mapaUsuarios), "Debe lanzarse IllegalArgumentException si el solicitante no existe.");
    }
}
