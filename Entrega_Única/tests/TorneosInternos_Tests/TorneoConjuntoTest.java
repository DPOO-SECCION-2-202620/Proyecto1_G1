package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.DisciplinaIndividual;
import Excepciones.ReglaNegocioException;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class TorneoConjuntoTest
{
    private Baloncesto baloncesto;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private LocalDate hoy;

    private TorneoConjunto torneo;
    private TorneoConjunto torneoRecreativo;

    private Equipo equipoCompleto;
    private Equipo equipoOtroCompleto;
    private Equipo equipoIncompleto;
    private Equipo equipoConSuspendido;
    private Equipo equipoDeFutbol;
    private Equipo equipoDeOtraCategoria;

    @BeforeEach
    void setUp() throws Exception
    {
        baloncesto = new Baloncesto();
        fechaInicio = LocalDate.of(2026, 12, 1);
        fechaLimite = LocalDate.of(2026, 11, 20);
        hoy = LocalDate.of(2026, 11, 10);

        torneo = new TorneoConjunto("Copa Basket", 8, fechaInicio, fechaLimite, new PremiacionCompetitiva(50000), baloncesto, Categoria.MAYORES);
        torneoRecreativo = new TorneoConjunto("Copa Amigos", 8, fechaInicio, fechaLimite, new PremiacionRecreativa(), baloncesto, Categoria.MAYORES);

        equipoCompleto = crearEquipo("Cerros Basket", baloncesto, Categoria.MAYORES, "a", 5);
        equipoOtroCompleto = crearEquipo("Cerros Dos", baloncesto, Categoria.MAYORES, "b", 6);
        equipoIncompleto = crearEquipo("Cerros Pocos", baloncesto, Categoria.MAYORES, "c", 3);
        equipoConSuspendido = crearEquipo("Cerros Suspendido", baloncesto, Categoria.MAYORES, "d", 5);
        equipoConSuspendido.getInscripciones().get(0).suspender();
        equipoDeFutbol = crearEquipo("Cerros FC", new Futbol(), Categoria.MAYORES, "e", 7);
        equipoDeOtraCategoria = crearEquipo("Cerritos", baloncesto, Categoria.SUB17, "f", 5);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private Equipo crearEquipo(String nombre, DeporteConjunto deporte, Categoria categoria, String prefijo, int jugadores) throws Exception 
    { 
        Equipo equipo = new Equipo(nombre, 10, deporte, categoria);
        int edad = 25;
        if(categoria == Categoria.SUB17)
        {
            edad = 15;
        }
        for(int i = 1; i <= jugadores; i++)
        {
            equipo.agregarJugador(new Socio(edad, "Jugador " + prefijo + i, prefijo + i, "x"), "Alero", i);
        }
        return equipo;
    }

    @Test
    void testEsTorneo()
    {
        assertTrue(torneo instanceof Torneo, "Un torneo de conjunto es un torneo.");
    }

    @Test
    void testGetters()
    {
        assertSame(baloncesto, torneo.getDeporte(), "El deporte no es el esperado.");
        assertEquals(Categoria.MAYORES, torneo.getCategoria(), "La categoría no es la esperada.");
    }

    @Test
    void testGetDescripcion()
    {
        assertEquals("Baloncesto MAYORES", torneo.getDescripcion(), "La descripción del torneo no es la esperada.");
    }

    @Test
    void testInscribirEquipoCompleto() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);

        assertEquals(1, torneo.getInscripciones().size(), "El torneo debe tener una inscripción.");
        assertEquals(50000, torneo.getInscripciones().get(0).getMontoPagado(), 0.001, "El equipo paga una sola tarifa.");
    }

    @Test
    void testInscribirEquipoEnTorneoRecreativo() throws Exception
    {
        torneoRecreativo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);

        assertEquals(0, torneoRecreativo.getInscripciones().get(0).getMontoPagado(), 0.001, "Un torneo recreativo no cobra tarifa.");
    }

    @Test
    void testInscribirEquipoConMasDelMinimo() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoOtroCompleto), hoy, false);

        assertEquals(1, torneo.getInscripciones().size(), "Un equipo con más del mínimo debe poder inscribirse.");
    }

    @Test
    void testInscribirEquipoIncompleto()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoEquipo(equipoIncompleto), hoy, false), "Debe lanzarse ReglaNegocioException si faltan jugadores.");

        assertEquals("Le faltan 2 jugadores para el mínimo de 5", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(0, torneo.getInscripciones().size(), "No debe quedar ninguna inscripción.");
    }

    @Test
    void testInscribirEquipoConJugadoresSuspendidos()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoEquipo(equipoConSuspendido), hoy, false), "Debe lanzarse ReglaNegocioException si no hay suficientes jugadores habilitados.");

        assertEquals("Solo hay 4 de 5 jugadores habilitados. No disponibles: Jugador d1 (suspendido), ", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirEquipoDeOtroDeporte()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoEquipo(equipoDeFutbol), hoy, false), "Debe lanzarse ReglaNegocioException si el deporte no coincide.");

        assertEquals("Incompatibilidad: el torneo es de Baloncesto MAYORES", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirEquipoDeOtraCategoria()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoEquipo(equipoDeOtraCategoria), hoy, false), "Debe lanzarse ReglaNegocioException si la categoría no coincide.");

        assertEquals("Incompatibilidad: el torneo es de Baloncesto MAYORES", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirJugadorIndividual()
    {
        Socio socio = new Socio(25, "Ana Torres", "ana", "1");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(socio, null), hoy, false), "Debe lanzarse ReglaNegocioException si se inscribe un jugador individual.");

        assertEquals("En este torneo se inscriben equipos completos", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirEmpleadoIndividual()
    {
        Empleado empleado = new Empleado(30, "Sara Paz", "spaz", "x", "E01");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(empleado, null), hoy, false), "Debe lanzarse ReglaNegocioException si se inscribe un empleado.");

        assertEquals("Los empleados solo pueden participar en torneos individuales", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirParejaConUnEmpleado()
    {
        Socio socio = new Socio(25, "Ana Torres", "ana", "1");
        Empleado empleado = new Empleado(30, "Sara Paz", "spaz", "x", "E01");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(socio, empleado), hoy, false), "Debe lanzarse ReglaNegocioException si la pareja incluye un empleado.");

        assertEquals("Los empleados solo pueden participar en torneos individuales", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirElMismoEquipoDosVeces() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false), "Debe lanzarse ReglaNegocioException si el equipo ya está inscrito.");

        assertEquals("Jugador a1 ya está inscrito en el torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRetirarEquipo() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);
        torneo.inscribir(new InscripcionTorneoEquipo(equipoOtroCompleto), hoy, false);

        torneo.retirarEquipo(equipoCompleto);

        assertEquals(1, torneo.getInscripciones().size(), "El torneo debe tener una inscripción.");
        assertSame(equipoOtroCompleto, ((InscripcionTorneoEquipo) torneo.getInscripciones().get(0)).getEquipo(), "Debe quedar el otro equipo.");
    }

    @Test
    void testRetirarEquipoNoInscrito()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.retirarEquipo(equipoCompleto), "Debe lanzarse ReglaNegocioException si el equipo no está inscrito.");

        assertEquals("Cerros Basket no está inscrito en el torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRetirarEquipoConElTorneoIniciado() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);
        torneo.inscribir(new InscripcionTorneoEquipo(equipoOtroCompleto), hoy, false);
        torneo.iniciar();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.retirarEquipo(equipoCompleto), "Debe lanzarse ReglaNegocioException si el torneo ya inició.");

        assertEquals("El torneo ya inició", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado(), "El torneo debe seguir en curso.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = torneo.toJSON();

        assertEquals("CONJUNTO", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
        assertEquals("Baloncesto", json.getString("deporte"), "El deporte en el JSON no es el esperado.");
        assertEquals("MAYORES", json.getString("categoria"), "La categoría en el JSON no es la esperada.");
        assertEquals("Copa Basket", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
    }

    @Test
    void testFromJSON() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);
        torneo.inscribir(new InscripcionTorneoEquipo(equipoOtroCompleto), hoy, false);

        Map<String, DeporteConjunto> mapaDeportes = new HashMap<String, DeporteConjunto>();
        mapaDeportes.put("Baloncesto", baloncesto);
        Map<String, Equipo> mapaEquipos = new HashMap<String, Equipo>();
        mapaEquipos.put("Cerros Basket", equipoCompleto);
        mapaEquipos.put("Cerros Dos", equipoOtroCompleto);

        Torneo restaurado = Torneo.fromJSON(torneo.toJSON(), mapaDeportes, new HashMap<String, DisciplinaIndividual>(), new HashMap<String, Usuario>(), mapaEquipos);

        assertTrue(restaurado instanceof TorneoConjunto, "El tipo CONJUNTO debe crear un torneo de conjunto.");
        TorneoConjunto conjunto = (TorneoConjunto) restaurado;
        assertSame(baloncesto, conjunto.getDeporte(), "El deporte restaurado no es el esperado.");
        assertEquals(Categoria.MAYORES, conjunto.getCategoria(), "La categoría restaurada no es la esperada.");
        assertEquals(2, conjunto.getInscripciones().size(), "Deben restaurarse las dos inscripciones.");
        assertTrue(conjunto.esCompetitivo(), "El torneo restaurado debe ser competitivo.");
    }

    @Test
    void testFromJSONDeporteInexistente()
    {
        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> Torneo.fromJSON(torneo.toJSON(), new HashMap<String, DeporteConjunto>(), new HashMap<String, DisciplinaIndividual>(), new HashMap<String, Usuario>(), new HashMap<String, Equipo>()), "Debe lanzarse IllegalStateException si el deporte no existe.");

        assertEquals("Deporte inexistente en el torneo: Baloncesto", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONEquipoInscritoInexistente() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoEquipo(equipoCompleto), hoy, false);
        Map<String, DeporteConjunto> mapaDeportes = new HashMap<String, DeporteConjunto>();
        mapaDeportes.put("Baloncesto", baloncesto);

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> Torneo.fromJSON(torneo.toJSON(), mapaDeportes, new HashMap<String, DisciplinaIndividual>(), new HashMap<String, Usuario>(), new HashMap<String, Equipo>()), "Debe lanzarse IllegalStateException si un equipo inscrito no existe.");

        assertEquals("Equipo inexistente en el torneo: Cerros Basket", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONCategoriaInvalida()
    {
        JSONObject json = torneo.toJSON();
        json.put("categoria", "VETERANOS");
        Map<String, DeporteConjunto> mapaDeportes = new HashMap<String, DeporteConjunto>();
        mapaDeportes.put("Baloncesto", baloncesto);

        assertThrows(IllegalArgumentException.class, () -> Torneo.fromJSON(json, mapaDeportes, new HashMap<String, DisciplinaIndividual>(), new HashMap<String, Usuario>(), new HashMap<String, Equipo>()), "Una categoría desconocida debe lanzar IllegalArgumentException.");
    }
}
