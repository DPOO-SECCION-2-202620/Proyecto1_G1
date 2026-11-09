package Entrenamiento_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Entrenamiento.EstadoAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Instalaciones.CanchaFutbol;
import Instalaciones.Instalacion;
import Usuarios.Entrenador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class SesionEntrenamientoTest
{
    private Entrenador entrenador;
    private CanchaFutbol cancha;

    private Socio socio1;
    private Socio socio2;
    private Socio socioNoConvocado;

    private List<Socio> convocados;
    private SesionEntrenamiento sesion;

    private LocalDate fecha;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, Instalacion> mapaInstalaciones;

    @BeforeEach
    void setUp() throws Exception
    {
        fecha = LocalDate.of(2026, 11, 10);

        entrenador = new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01");
        cancha = new CanchaFutbol("Cancha 1");

        socio1 = new Socio(20, "Ana Torres", "ana", "1");
        socio2 = new Socio(21, "Pedro Gil", "pgil", "2");
        socioNoConvocado = new Socio(22, "Sofia Vega", "svega", "3");

        convocados = new ArrayList<Socio>();
        convocados.add(socio1);
        convocados.add(socio2);

        sesion = new SesionEntrenamiento(fecha, LocalTime.of(10, 0), 60, entrenador, cancha, convocados);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaUsuarios.put("jruiz", entrenador);
        mapaUsuarios.put("ana", socio1);
        mapaUsuarios.put("pgil", socio2);

        mapaInstalaciones = new HashMap<String, Instalacion>();
        mapaInstalaciones.put("Cancha 1", cancha);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals(fecha, sesion.getFecha(), "La fecha de la sesión no es la esperada.");
        assertEquals(LocalTime.of(10, 0), sesion.getHora(), "La hora de la sesión no es la esperada.");
        assertEquals(60, sesion.getDuracionMinutos(), "La duración de la sesión no es la esperada.");
        assertSame(entrenador, sesion.getEntrenador(), "El entrenador de la sesión no es el esperado.");
        assertSame(cancha, sesion.getInstalacion(), "La instalación de la sesión no es la esperada.");
        assertEquals(2, sesion.getConvocados().size(), "La sesión debe tener dos convocados.");
        assertEquals(0, sesion.getAsistencias().size(), "Una sesión nueva no debe tener asistencias.");
    }

    @Test
    void testConvocadosSeCopian()
    {
        convocados.add(socioNoConvocado);

        assertEquals(2, sesion.getConvocados().size(), "Modificar la lista original no debe afectar a la sesión.");
    }

    @Test
    void testseSolapaConHorarioIgual()
    {
        assertTrue(sesion.seSolapaCon(fecha, LocalTime.of(10, 0), 60), "Un horario idéntico se solapa.");
    }

    @Test
    void testseSolapaConHorarioParcial()
    {
        assertTrue(sesion.seSolapaCon(fecha, LocalTime.of(10, 30), 60), "Un horario que empieza a la mitad se solapa.");
        assertTrue(sesion.seSolapaCon(fecha, LocalTime.of(9, 30), 60), "Un horario que termina a la mitad se solapa.");
    }

    @Test
    void testseSolapaConHorarioContenidoOQueContiene()
    {
        assertTrue(sesion.seSolapaCon(fecha, LocalTime.of(10, 15), 15), "Un horario contenido en la sesión se solapa.");
        assertTrue(sesion.seSolapaCon(fecha, LocalTime.of(9, 0), 180), "Un horario que contiene la sesión se solapa.");
    }

    @Test
    void testseSolapaConHorariosContiguos()
    {
        assertFalse(sesion.seSolapaCon(fecha, LocalTime.of(11, 0), 60), "Un horario que empieza cuando termina la sesión no se solapa.");
        assertFalse(sesion.seSolapaCon(fecha, LocalTime.of(9, 0), 60), "Un horario que termina cuando empieza la sesión no se solapa.");
    }

    @Test
    void testseSolapaConOtraFecha()
    {
        assertFalse(sesion.seSolapaCon(fecha.plusDays(1), LocalTime.of(10, 0), 60), "Un horario en otra fecha no se solapa.");
    }

    @Test
    void testRegistrarAsistenciaSinEvaluacion()
    {
        assertTrue(sesion.registrarAsistencia(socio1, EstadoAsistencia.ASISTIO, 0, null), "Debe poder registrarse la asistencia de un convocado.");

        assertEquals(1, sesion.getAsistencias().size(), "Debe haber una asistencia registrada.");
        assertSame(socio1, sesion.getAsistencias().get(0).getJugador(), "El jugador de la asistencia no es el esperado.");
        assertEquals(EstadoAsistencia.ASISTIO, sesion.getAsistencias().get(0).getEstado(), "El estado de la asistencia no es el esperado.");
        assertEquals(0, sesion.getAsistencias().get(0).getEvaluacion(), "Sin evaluación la nota debe ser 0.");
    }

    @Test
    void testRegistrarAsistenciaConEvaluacion()
    {
        assertTrue(sesion.registrarAsistencia(socio2, EstadoAsistencia.LLEGOTARDE, 4, "Llegó tarde pero rindió"), "Debe poder registrarse la asistencia con evaluación.");

        assertEquals(4, sesion.getAsistencias().get(0).getEvaluacion(), "La evaluación registrada no es la esperada.");
        assertEquals("Llegó tarde pero rindió", sesion.getAsistencias().get(0).getComentario(), "El comentario registrado no es el esperado.");
    }

    @Test
    void testRegistrarAsistenciaDeNoConvocado()
    {
        assertFalse(sesion.registrarAsistencia(socioNoConvocado, EstadoAsistencia.ASISTIO, 0, null), "No se puede registrar la asistencia de alguien no convocado.");

        assertEquals(0, sesion.getAsistencias().size(), "No debe registrarse ninguna asistencia.");
    }

    @Test
    void testRegistrarAsistenciaRepetida()
    {
        assertTrue(sesion.registrarAsistencia(socio1, EstadoAsistencia.ASISTIO, 0, null), "La primera asistencia debe registrarse.");
        assertFalse(sesion.registrarAsistencia(socio1, EstadoAsistencia.FALTO, 0, null), "No se puede registrar dos veces la asistencia del mismo socio.");

        assertEquals(1, sesion.getAsistencias().size(), "Solo debe haber una asistencia registrada.");
        assertEquals(EstadoAsistencia.ASISTIO, sesion.getAsistencias().get(0).getEstado(), "La asistencia original no debe cambiar.");
    }

    @Test
    void testRegistrarAsistenciaConEvaluacionInvalida()
    {
        assertFalse(sesion.registrarAsistencia(socio1, EstadoAsistencia.ASISTIO, 7, "Muy alta"), "No se puede registrar una evaluación fuera del rango.");

        assertEquals(0, sesion.getAsistencias().size(), "No debe registrarse la asistencia si la evaluación es inválida.");
    }

    @Test
    void testRegistrarAsistenciaFaltoConEvaluacion()
    {
        assertFalse(sesion.registrarAsistencia(socio1, EstadoAsistencia.FALTO, 3, "No vino"), "No se puede evaluar a quien faltó.");

        assertEquals(0, sesion.getAsistencias().size(), "No debe registrarse la asistencia.");
    }

    @Test
    void testRegistrarAsistenciaFaltoSinEvaluacion()
    {
        assertTrue(sesion.registrarAsistencia(socio1, EstadoAsistencia.FALTO, 0, null), "Debe poder registrarse una falta sin evaluación.");

        assertEquals(EstadoAsistencia.FALTO, sesion.getAsistencias().get(0).getEstado(), "El estado de la asistencia no es el esperado.");
    }

    @Test
    void testToJSON()
    {
        sesion.registrarAsistencia(socio1, EstadoAsistencia.ASISTIO, 5, "Excelente");

        JSONObject json = sesion.toJSON();

        assertEquals("2026-11-10", json.getString("fecha"), "La fecha en el JSON no es la esperada.");
        assertEquals("10:00", json.getString("hora"), "La hora en el JSON no es la esperada.");
        assertEquals(60, json.getInt("duracionMinutos"), "La duración en el JSON no es la esperada.");
        assertEquals("jruiz", json.getString("entrenador"), "El entrenador en el JSON no es el esperado.");
        assertEquals("Cancha 1", json.getString("instalacion"), "La instalación en el JSON no es la esperada.");
        assertEquals(2, json.getJSONArray("convocados").length(), "El JSON debe tener dos convocados.");
        assertEquals("ana", json.getJSONArray("convocados").getString(0), "El primer convocado en el JSON no es el esperado.");
        assertEquals(1, json.getJSONArray("asistencias").length(), "El JSON debe tener una asistencia.");
    }

    @Test
    void testFromJSON()
    {
        sesion.registrarAsistencia(socio1, EstadoAsistencia.ASISTIO, 5, "Excelente");

        SesionEntrenamiento restaurada = SesionEntrenamiento.fromJSON(sesion.toJSON(), mapaUsuarios, mapaInstalaciones);

        assertEquals(fecha, restaurada.getFecha(), "La fecha restaurada no es la esperada.");
        assertEquals(LocalTime.of(10, 0), restaurada.getHora(), "La hora restaurada no es la esperada.");
        assertEquals(60, restaurada.getDuracionMinutos(), "La duración restaurada no es la esperada.");
        assertSame(entrenador, restaurada.getEntrenador(), "El entrenador restaurado no es el esperado.");
        assertSame(cancha, restaurada.getInstalacion(), "La instalación restaurada no es la esperada.");
        assertEquals(2, restaurada.getConvocados().size(), "Debe haber dos convocados restaurados.");
        assertSame(socio2, restaurada.getConvocados().get(1), "El segundo convocado restaurado no es el esperado.");
        assertEquals(1, restaurada.getAsistencias().size(), "Debe haber una asistencia restaurada.");
        assertEquals(5, restaurada.getAsistencias().get(0).getEvaluacion(), "La evaluación restaurada no es la esperada.");
    }

    @Test
    void testFromJSONEntrenadorInexistente()
    {
        mapaUsuarios.remove("jruiz");

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> SesionEntrenamiento.fromJSON(sesion.toJSON(), mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalStateException si el entrenador no existe.");

        assertEquals("Sesión con referencia inexistente", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONInstalacionInexistente()
    {
        mapaInstalaciones.remove("Cancha 1");

        assertThrows(IllegalStateException.class, () -> SesionEntrenamiento.fromJSON(sesion.toJSON(), mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalStateException si la instalación no existe.");
    }

    @Test
    void testFromJSONConvocadoInexistente()
    {
        mapaUsuarios.remove("pgil");

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> SesionEntrenamiento.fromJSON(sesion.toJSON(), mapaUsuarios, mapaInstalaciones), "Debe lanzarse IllegalStateException si un convocado no existe.");

        assertEquals("Convocado inexistente: pgil", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
