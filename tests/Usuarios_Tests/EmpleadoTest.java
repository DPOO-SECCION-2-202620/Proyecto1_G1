package Usuarios_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Excepciones.ReglaNegocioException;
import Tienda_Cafeteria_Inventario.EstadoSugerencia;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import TurnosEmpleados.EstadoSolicitud;
import TurnosEmpleados.SolicitudCambioTurno;
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Socio;

public class EmpleadoTest
{
    private Empleado empleado;
    private Empleado empleadoSinTurnos;

    private Turno turnoLunes;
    private Turno turnoMartes;
    private Turno turnoMiercoles;
    private Turno turnoAjeno;

    @BeforeEach
    void setUp() throws Exception
    {
        turnoLunes = new Turno("LUNES", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoMartes = new Turno("MARTES", LocalTime.of(14, 0), LocalTime.of(18, 0));
        turnoMiercoles = new Turno("MIERCOLES", LocalTime.of(6, 0), LocalTime.of(10, 0));
        turnoAjeno = new Turno("VIERNES", LocalTime.of(8, 0), LocalTime.of(12, 0));

        empleado = new Empleado(30, "Luis Mora", "lmora", "abc", "E01");
        empleado.agregarTurno(turnoLunes);
        empleado.agregarTurno(turnoMartes);

        empleadoSinTurnos = new Empleado(22, "Sara Paz", "spaz", "def", "E02");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testEsUnSocio()
    {
        assertTrue(empleado instanceof Socio, "Un empleado también es un socio.");
        assertEquals(30, empleado.getEdad(), "La edad del empleado no es la esperada.");
        assertEquals("Luis Mora", empleado.getNombre(), "El nombre del empleado no es el esperado.");
    }

    @Test
    void testGetCodigoEmpleado()
    {
        assertEquals("E01", empleado.getCodigoEmpleado(), "El código del empleado no es el esperado.");
    }

    @Test
    void testGetCodigoDescuento()
    {
        assertEquals("DESC-E01", empleado.getCodigoDescuento().getCodigo(), "El código de descuento debe ser DESC- seguido del código del empleado.");
        assertEquals("DESC-E02", empleadoSinTurnos.getCodigoDescuento().getCodigo(), "El código de descuento del segundo empleado no es el esperado.");
    }

    @Test
    void testGetTurnos()
    {
        assertEquals(2, empleado.getTurnos().size(), "El empleado debe tener dos turnos.");
        assertSame(turnoLunes, empleado.getTurnos().get(0), "El primer turno debe ser el del lunes.");
        assertEquals(0, empleadoSinTurnos.getTurnos().size(), "Un empleado nuevo no debe tener turnos.");
    }

    @Test
    void testAgregarTurno()
    {
        empleadoSinTurnos.agregarTurno(turnoMiercoles);

        assertEquals(1, empleadoSinTurnos.getTurnos().size(), "El empleado debe tener un turno.");
        assertSame(turnoMiercoles, empleadoSinTurnos.getTurnos().get(0), "El turno agregado no es el esperado.");
    }

    @Test
    void testsugerirArticulo()
    {
        SugerenciaArticulo sugerencia = empleado.sugerirArticulo("Raqueta pro", "Raquetas", 250000.5);

        assertEquals("Raqueta pro", sugerencia.getNombrePropuesto(), "El nombre propuesto no es el esperado.");
        assertEquals("Raquetas", sugerencia.getCategoria(), "La categoría de la sugerencia no es la esperada.");
        assertEquals(250000.5, sugerencia.getPrecioSugerido(), 0.001, "El precio sugerido no es el esperado.");
        assertEquals(EstadoSugerencia.PENDIENTE, sugerencia.getEstado(), "Una sugerencia nueva debe estar pendiente.");
    }

    @Test
    void testsolicitarCambioTurno() throws Exception
    {
        SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(turnoLunes, turnoMiercoles);

        assertSame(turnoLunes, solicitud.getTurnoActual(), "El turno actual de la solicitud no es el esperado.");
        assertSame(turnoMiercoles, solicitud.getTurnoNuevo(), "El turno nuevo de la solicitud no es el esperado.");
        assertSame(empleado, solicitud.getSolicitante(), "El solicitante de la solicitud no es el esperado.");
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado(), "Una solicitud nueva debe estar pendiente.");
    }

    @Test
    void testsolicitarCambioTurnoAjeno()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> empleado.solicitarCambioTurno(turnoAjeno, turnoMiercoles), "Debe lanzarse ReglaNegocioException si el turno no es del empleado.");

        assertEquals("Ese turno no es de Luis Mora", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testsolicitarCambioTurnoSinTurnos()
    {
        assertThrows(ReglaNegocioException.class, () -> empleadoSinTurnos.solicitarCambioTurno(turnoLunes, turnoMiercoles), "Un empleado sin turnos no puede solicitar cambios.");
    }

    @Test
    void testCambiarTurno()
    {
        empleado.cambiarTurno(turnoLunes, turnoMiercoles);

        assertEquals(2, empleado.getTurnos().size(), "El número de turnos no debe cambiar.");
        assertSame(turnoMiercoles, empleado.getTurnos().get(0), "El nuevo turno debe quedar en la posición del anterior.");
        assertSame(turnoMartes, empleado.getTurnos().get(1), "Los demás turnos no deben cambiar.");
    }

    @Test
    void testCambiarTurnoInexistente()
    {
        empleado.cambiarTurno(turnoAjeno, turnoMiercoles);

        assertEquals(2, empleado.getTurnos().size(), "El número de turnos no debe cambiar.");
        assertSame(turnoLunes, empleado.getTurnos().get(0), "Los turnos no deben cambiar si el turno actual no existe.");
        assertSame(turnoMartes, empleado.getTurnos().get(1), "Los turnos no deben cambiar si el turno actual no existe.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = empleado.toJSON();

        assertEquals("E01", json.getString("codigoEmpleado"), "El código del empleado en el JSON no es el esperado.");
        assertEquals("DESC-E01", json.getJSONObject("codigoDescuento").getString("codigo"), "El código de descuento en el JSON no es el esperado.");
        assertEquals(2, json.getJSONArray("turnos").length(), "El JSON debe tener dos turnos.");
        assertEquals("LUNES", json.getJSONArray("turnos").getJSONObject(0).getString("diaSemana"), "El primer turno del JSON no es el esperado.");
        assertEquals(30, json.getInt("edad"), "La edad en el JSON no es la esperada.");
    }

    @Test
    void testToJSONSinTurnos()
    {
        assertEquals(0, empleadoSinTurnos.toJSON().getJSONArray("turnos").length(), "El JSON de un empleado sin turnos no debe tener turnos.");
    }
}
