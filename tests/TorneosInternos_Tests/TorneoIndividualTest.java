package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoIndividual;
import TurnosEmpleados.Turno;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class TorneoIndividualTest
{
    private Tenis tenis;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private LocalDate hoy;

    private TorneoIndividual torneoSencillos;
    private TorneoIndividual torneoDobles;

    private Socio ana;
    private Socio pedro;
    private Socio principiante;
    private Socio sinNivel;
    private Empleado empleadoLibre;
    private Empleado empleadoDeTurno;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(4, "Tenis");
        fechaInicio = LocalDate.of(2026, 12, 1);
        fechaLimite = LocalDate.of(2026, 11, 20);
        hoy = LocalDate.of(2026, 11, 10);

        torneoSencillos = new TorneoIndividual("Copa Sencillos", 8, fechaInicio, fechaLimite, new PremiacionCompetitiva(10000), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS);
        torneoDobles = new TorneoIndividual("Copa Dobles", 8, fechaInicio, fechaLimite, new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.DOBLES);

        ana = crearSocio("Ana Torres", "ana", NivelJugador.INTERMEDIO);
        pedro = crearSocio("Pedro Gil", "pgil", NivelJugador.INTERMEDIO);
        principiante = crearSocio("Luis Mora", "lmora", NivelJugador.PRINCIPIANTE);
        sinNivel = new Socio(22, "Sin Nivel", "sinnivel", "x");

        empleadoLibre = new Empleado(30, "Sara Paz", "spaz", "x", "E01");
        empleadoLibre.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));
        empleadoLibre.agregarTurno(new Turno("JUEVES", LocalTime.of(8, 0), LocalTime.of(12, 0)));

        empleadoDeTurno = new Empleado(31, "Tomas Rios", "trios", "x", "E02");
        empleadoDeTurno.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));
        empleadoDeTurno.agregarTurno(new Turno("MARTES", LocalTime.of(8, 0), LocalTime.of(12, 0)));
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private Socio crearSocio(String nombre, String login, NivelJugador nivel)
    {
        Socio socio = new Socio(25, nombre, login, "x");
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, nivel));
        return socio;
    }

    @Test
    void testEsTorneo()
    {
        assertTrue(torneoSencillos instanceof Torneo, "Un torneo individual es un torneo.");
    }

    @Test
    void testGetters()
    {
        assertSame(tenis, torneoSencillos.getDisciplina(), "La disciplina no es la esperada.");
        assertEquals(NivelJugador.INTERMEDIO, torneoSencillos.getNivel(), "El nivel no es el esperado.");
        assertEquals(Modalidad.SENCILLOS, torneoSencillos.getModalidad(), "La modalidad no es la esperada.");
    }

    @Test
    void testEsDobles()
    {
        assertFalse(torneoSencillos.esDobles(), "Un torneo de sencillos no es de dobles.");
        assertTrue(torneoDobles.esDobles(), "Un torneo de dobles es de dobles.");
    }

    @Test
    void testGetDescripcion()
    {
        assertEquals("Tenis INTERMEDIO SENCILLOS", torneoSencillos.getDescripcion(), "La descripción del torneo no es la esperada.");
        assertEquals("Tenis INTERMEDIO DOBLES", torneoDobles.getDescripcion(), "La descripción del torneo no es la esperada.");
    }

    @Test
    void testInscribirJugadorConElNivel() throws Exception
    {
        torneoSencillos.inscribir(new InscripcionTorneoIndividual(ana, null), hoy, false);

        assertEquals(1, torneoSencillos.getInscripciones().size(), "El torneo debe tener una inscripción.");
    }

    @Test
    void testInscribirJugadorConOtroNivel()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoSencillos.inscribir(new InscripcionTorneoIndividual(principiante, null), hoy, false), "Debe lanzarse ReglaNegocioException si el jugador no tiene el nivel.");

        assertEquals("Luis Mora no tiene el nivel del torneo. Nivel requerido: INTERMEDIO", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(0, torneoSencillos.getInscripciones().size(), "No debe quedar ninguna inscripción.");
    }

    @Test
    void testInscribirJugadorSinNivelEnLaDisciplina()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoSencillos.inscribir(new InscripcionTorneoIndividual(sinNivel, null), hoy, false), "Debe lanzarse ReglaNegocioException si el jugador no está inscrito en la disciplina.");

        assertEquals("Sin Nivel no tiene el nivel del torneo. Nivel requerido: INTERMEDIO", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirEquipoEnTorneoIndividual()
    {
        Equipo equipo = new Equipo("Cerros Basket", 8, new Baloncesto(), Categoria.MAYORES);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoSencillos.inscribir(new InscripcionTorneoEquipo(equipo), hoy, false), "Debe lanzarse ReglaNegocioException si se inscribe un equipo.");

        assertEquals("En este torneo se inscriben jugadores", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirParejaEnTorneoDeSencillos()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoSencillos.inscribir(new InscripcionTorneoIndividual(ana, pedro), hoy, false), "Debe lanzarse ReglaNegocioException si se inscribe una pareja en sencillos.");

        assertEquals("Este torneo es de sencillos: no se admite compañero", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirJugadorSoloEnTorneoDeDobles()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoDobles.inscribir(new InscripcionTorneoIndividual(ana, null), hoy, false), "Debe lanzarse ReglaNegocioException si falta el compañero en dobles.");

        assertEquals("Este torneo es de dobles: debe indicar un compañero", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirParejaEnTorneoDeDobles() throws Exception
    {
        torneoDobles.inscribir(new InscripcionTorneoIndividual(ana, pedro), hoy, false);

        assertEquals(1, torneoDobles.getInscripciones().size(), "El torneo debe tener una inscripción.");
        assertEquals(0, torneoDobles.getInscripciones().get(0).getMontoPagado(), 0.001, "Un torneo recreativo no cobra tarifa.");
    }

    @Test
    void testInscribirParejaConUnCompaneroDeOtroNivel()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoDobles.inscribir(new InscripcionTorneoIndividual(ana, principiante), hoy, false), "Debe lanzarse ReglaNegocioException si el compañero no tiene el nivel.");

        assertEquals("Luis Mora no tiene el nivel del torneo. Nivel requerido: INTERMEDIO", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirEmpleadoSinTurnoElDiaDelTorneo() throws Exception
    {
        torneoSencillos.inscribir(new InscripcionTorneoIndividual(empleadoLibre, null), hoy, false);

        assertEquals(1, torneoSencillos.getInscripciones().size(), "El empleado sin turno ese día debe poder inscribirse.");
        assertEquals(0, torneoSencillos.getInscripciones().get(0).getMontoPagado(), 0.001, "El empleado no paga tarifa.");
    }

    @Test
    void testInscribirEmpleadoDeTurnoElDiaDelTorneo()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneoSencillos.inscribir(new InscripcionTorneoIndividual(empleadoDeTurno, null), hoy, false), "Debe lanzarse ReglaNegocioException si el empleado está de turno.");

        assertEquals("El empleado está de turno el día del torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(0, torneoSencillos.getInscripciones().size(), "No debe quedar ninguna inscripción.");
    }

    @Test
    void testInscribirEmpleadoSinTurnos() throws Exception
    {
        Empleado sinTurnos = new Empleado(32, "Sin Turnos", "sinturnos", "x", "E03");
        sinTurnos.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));

        torneoSencillos.inscribir(new InscripcionTorneoIndividual(sinTurnos, null), hoy, false);

        assertEquals(1, torneoSencillos.getInscripciones().size(), "Un empleado sin turnos puede inscribirse.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = torneoSencillos.toJSON();

        assertEquals("INDIVIDUAL", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Tenis", json.getString("disciplina"), "La disciplina en el JSON no es la esperada.");
        assertEquals("INTERMEDIO", json.getString("nivel"), "El nivel en el JSON no es el esperado.");
        assertEquals("SENCILLOS", json.getString("modalidad"), "La modalidad en el JSON no es la esperada.");
        assertEquals("Copa Sencillos", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON()
    {
        Map<String, DisciplinaIndividual> mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);

        Torneo restaurado = Torneo.fromJSON(torneoDobles.toJSON(), new HashMap<String, DeporteConjunto>(), mapaDisciplinas, new HashMap<String, Usuario>(), new HashMap<String, Equipo>());

        assertTrue(restaurado instanceof TorneoIndividual, "El tipo INDIVIDUAL debe crear un torneo individual.");
        TorneoIndividual individual = (TorneoIndividual) restaurado;
        assertSame(tenis, individual.getDisciplina(), "La disciplina restaurada no es la esperada.");
        assertEquals(NivelJugador.INTERMEDIO, individual.getNivel(), "El nivel restaurado no es el esperado.");
        assertTrue(individual.esDobles(), "La modalidad restaurada no es la esperada.");
        assertFalse(individual.esCompetitivo(), "El torneo restaurado debe ser recreativo.");
    }
}
