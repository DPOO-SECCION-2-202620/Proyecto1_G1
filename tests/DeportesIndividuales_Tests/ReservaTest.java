package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Reserva;
import DeportesIndividuales.Tenis;
import Instalaciones.CanchaFutbol;
import Instalaciones.CanchaTenis;
import Instalaciones.Instalacion;
import Usuarios.Administrador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class ReservaTest
{
    private Tenis tenis;
    private CanchaTenis cancha;
    private Socio socio;
    private LocalDate fecha;
    private Reserva reserva;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, Instalacion> mapaInstalaciones;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        cancha = new CanchaTenis("Tenis 1", true, 1, tenis);
        socio = new Socio(20, "Ana Torres", "ana", "1234");
        fecha = LocalDate.of(2026, 11, 10);

        reserva = new Reserva(2, LocalTime.of(10, 0), LocalTime.of(12, 0), fecha, Modalidad.SENCILLOS, socio, cancha);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("ana", socio);
        mapaUsuarios.put("admin", new Administrador("Carlos", "admin", "1"));

        mapaInstalaciones = new HashMap<String, Instalacion>();
        mapaInstalaciones.put("Tenis 1", cancha);
        mapaInstalaciones.put("Futbol 1", new CanchaFutbol("Futbol 1"));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals(2, reserva.getNumeroJugadores(), "El número de jugadores no es el esperado.");
        assertEquals(LocalTime.of(10, 0), reserva.getHoraInicio(), "La hora de inicio no es la esperada.");
        assertEquals(LocalTime.of(12, 0), reserva.getHoraFinal(), "La hora final no es la esperada.");
        assertEquals(fecha, reserva.getFecha(), "La fecha no es la esperada.");
        assertEquals(Modalidad.SENCILLOS, reserva.getModalidad(), "La modalidad no es la esperada.");
        assertSame(socio, reserva.getJugador(), "El jugador no es el esperado.");
        assertSame(cancha, reserva.getInstalacion(), "La instalación no es la esperada.");
    }

    @Test
    void testseSolapaConMismoHorario()
    {
        assertTrue(reserva.seSolapaCon(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0)), "Un horario idéntico se solapa.");
    }

    @Test
    void testseSolapaConHorarioParcial()
    {
        assertTrue(reserva.seSolapaCon(fecha, LocalTime.of(11, 0), LocalTime.of(13, 0)), "Un horario que empieza a la mitad se solapa.");
        assertTrue(reserva.seSolapaCon(fecha, LocalTime.of(9, 0), LocalTime.of(11, 0)), "Un horario que termina a la mitad se solapa.");
    }

    @Test
    void testseSolapaConHorarioContenidoOQueContiene()
    {
        assertTrue(reserva.seSolapaCon(fecha, LocalTime.of(10, 30), LocalTime.of(11, 0)), "Un horario contenido en la reserva se solapa.");
        assertTrue(reserva.seSolapaCon(fecha, LocalTime.of(8, 0), LocalTime.of(14, 0)), "Un horario que contiene la reserva se solapa.");
    }

    @Test
    void testseSolapaConHorariosContiguos()
    {
        assertFalse(reserva.seSolapaCon(fecha, LocalTime.of(12, 0), LocalTime.of(13, 0)), "Un horario que empieza cuando termina la reserva no se solapa.");
        assertFalse(reserva.seSolapaCon(fecha, LocalTime.of(9, 0), LocalTime.of(10, 0)), "Un horario que termina cuando empieza la reserva no se solapa.");
    }

    @Test
    void testseSolapaConOtraFecha()
    {
        assertFalse(reserva.seSolapaCon(fecha.plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)), "Un horario en otra fecha no se solapa.");
    }

    @Test
    void testseSolapaConParametrosNulos()
    {
        assertFalse(reserva.seSolapaCon(null, LocalTime.of(10, 0), LocalTime.of(12, 0)), "Con fecha nula no debe haber solapamiento.");
        assertFalse(reserva.seSolapaCon(fecha, null, LocalTime.of(12, 0)), "Con hora de inicio nula no debe haber solapamiento.");
        assertFalse(reserva.seSolapaCon(fecha, LocalTime.of(10, 0), null), "Con hora final nula no debe haber solapamiento.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = reserva.toJSON();

        assertEquals(2, json.getInt("numeroJugadores"), "El número de jugadores en el JSON no es el esperado.");
        assertEquals("10:00", json.getString("horaInicio"), "La hora de inicio en el JSON no es la esperada.");
        assertEquals("12:00", json.getString("horaFinal"), "La hora final en el JSON no es la esperada.");
        assertEquals("2026-11-10", json.getString("fecha"), "La fecha en el JSON no es la esperada.");
        assertEquals("SENCILLOS", json.getString("modalidad"), "La modalidad en el JSON no es la esperada.");
        assertEquals("ana", json.getString("jugador"), "El jugador en el JSON no es el esperado.");
        assertEquals("Tenis 1", json.getString("instalacion"), "La instalación en el JSON no es la esperada.");
    }

    @Test
    void testFromJSON()
    {
        Reserva restaurada = Reserva.fromJSON(reserva.toJSON(), mapaUsuarios, mapaInstalaciones);

        assertEquals(2, restaurada.getNumeroJugadores(), "El número de jugadores restaurado no es el esperado.");
        assertEquals(LocalTime.of(10, 0), restaurada.getHoraInicio(), "La hora de inicio restaurada no es la esperada.");
        assertEquals(LocalTime.of(12, 0), restaurada.getHoraFinal(), "La hora final restaurada no es la esperada.");
        assertEquals(fecha, restaurada.getFecha(), "La fecha restaurada no es la esperada.");
        assertEquals(Modalidad.SENCILLOS, restaurada.getModalidad(), "La modalidad restaurada no es la esperada.");
        assertSame(socio, restaurada.getJugador(), "El jugador restaurado no es el esperado.");
        assertSame(cancha, restaurada.getInstalacion(), "La instalación restaurada no es la esperada.");
    }

    @Test
    void testFromJSONModalidadEnMinusculas()
    {
        JSONObject json = reserva.toJSON();
        json.put("modalidad", "dobles");

        Reserva restaurada = Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones);

        assertEquals(Modalidad.DOBLES, restaurada.getModalidad(), "La modalidad debe aceptarse sin importar las mayúsculas.");
    }

    @Test
    void testFromJSONConClaveSocio()
    {
        JSONObject json = reserva.toJSON();
        json.remove("jugador");
        json.put("socio", "ana");

        Reserva restaurada = Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones);

        assertSame(socio, restaurada.getJugador(), "El jugador debe poder leerse desde la clave socio.");
    }

    @Test
    void testFromJSONConClaveUsuario()
    {
        JSONObject json = reserva.toJSON();
        json.remove("jugador");
        json.put("usuario", "ana");

        Reserva restaurada = Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones);

        assertSame(socio, restaurada.getJugador(), "El jugador debe poder leerse desde la clave usuario.");
    }

    @Test
    void testFromJSONSinJugador()
    {
        JSONObject json = reserva.toJSON();
        json.remove("jugador");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si la reserva no indica el jugador.");

        assertTrue(excepcion.getMessage().startsWith("La reserva no indica el jugador. Claves recibidas:"), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONJugadorNoEsSocio()
    {
        JSONObject json = reserva.toJSON();
        json.put("jugador", "admin");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si el jugador no es socio.");

        assertEquals("El jugador de la reserva no es un socio válido: admin", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONInstalacionNoIndividual()
    {
        JSONObject json = reserva.toJSON();
        json.put("instalacion", "Futbol 1");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si la instalación no es individual.");

        assertEquals("Instalación individual desconocida: Futbol 1", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONInstalacionInexistente()
    {
        JSONObject json = reserva.toJSON();
        json.put("instalacion", "Fantasma");

        assertThrows(IllegalArgumentException.class, () -> Reserva.fromJSON(json, mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalArgumentException si la instalación no existe.");
    }
}
