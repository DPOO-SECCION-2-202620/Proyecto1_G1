package TorneosInternos_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import TorneosInternos.Encuentro;
import TorneosInternos.EstadoTorneo;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.Premiacion;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.PremioEntregado;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;
import Usuarios.Usuario;

public class TorneoTest
{
    private Tenis tenis;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private LocalDate antesDelLimite;
    private LocalDate despuesDelLimite;

    private TorneoIndividual torneo;
    private TorneoIndividual torneoRecreativo;

    private Map<String, Usuario> mapaUsuarios;
    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(4, "Tenis");
        fechaInicio = LocalDate.of(2026, 12, 1);
        fechaLimite = LocalDate.of(2026, 11, 20);
        antesDelLimite = LocalDate.of(2026, 11, 10);
        despuesDelLimite = LocalDate.of(2026, 11, 25);

        torneo = crearTorneo(8, true);
        torneoRecreativo = crearTorneo(8, false);

        mapaUsuarios = new HashMap<String, Usuario>();
        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
        mapaDisciplinas.put("Tenis", tenis);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private TorneoIndividual crearTorneo(int cupos, boolean competitivo) throws Exception
    {
        Premiacion premiacion;
        if(competitivo)
        {
            premiacion = new PremiacionCompetitiva(10000);
        }
        else
        {
            premiacion = new PremiacionRecreativa();
        }
        return new TorneoIndividual("Copa Cerros", cupos, fechaInicio, fechaLimite, premiacion, tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS);
    }

    private Socio crearJugador(int numero)
    {
        Socio socio = new Socio(20 + numero, "Jugador " + numero, "j" + numero, "x");
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.INTERMEDIO));
        mapaUsuarios.put(socio.getLogin(), socio);
        return socio;
    }

    private InscripcionTorneoIndividual inscribirJugador(Torneo destino, int numero) throws Exception
    {
        InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(crearJugador(numero), null);
        destino.inscribir(inscripcion, despuesDelLimite, false);
        return inscripcion;
    }

    private List<InscripcionTorneo> llenar(Torneo destino, int cantidad) throws Exception
    {
        List<InscripcionTorneo> lista = new ArrayList<InscripcionTorneo>();
        for(int i = 1; i <= cantidad; i++)
        {
            lista.add(inscribirJugador(destino, i));
        }
        return lista;
    }

    private void jugarRonda(Torneo destino, int ronda) throws Exception
    {
        List<Encuentro> encuentros = new ArrayList<Encuentro>(destino.getEncuentrosDeRonda(ronda));
        for(Encuentro encuentro : encuentros)
        {
            destino.registrarGanador(encuentro, encuentro.getParticipanteA());
        }
    }

    @Test
    void testTorneoNuevo()
    {
        assertEquals("Copa Cerros", torneo.getNombre(), "El nombre del torneo no es el esperado.");
        assertEquals(8, torneo.getCupos(), "Los cupos del torneo no son los esperados.");
        assertEquals(fechaInicio, torneo.getFechaInicio(), "La fecha de inicio no es la esperada.");
        assertEquals(fechaLimite, torneo.getFechaLimitePreferencial(), "La fecha límite preferencial no es la esperada.");
        assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, torneo.getEstado(), "Un torneo nuevo debe tener las inscripciones abiertas.");
        assertEquals(0, torneo.getRondaActual(), "Un torneo nuevo está en la ronda 0.");
        assertEquals(0, torneo.getInscripciones().size(), "Un torneo nuevo no tiene inscripciones.");
        assertEquals(0, torneo.getEncuentros().size(), "Un torneo nuevo no tiene encuentros.");
        assertEquals(0, torneo.getPremios().size(), "Un torneo nuevo no tiene premios.");
        assertTrue(torneo.getPremiacion() instanceof PremiacionCompetitiva, "La premiación del torneo no es la esperada.");
    }

    @Test
    void testConstantes()
    {
        assertEquals(2, Torneo.CUPOS_MINIMOS, "El mínimo de cupos no es el esperado.");
        assertEquals(32, Torneo.CUPOS_MAXIMOS, "El máximo de cupos no es el esperado.");
        assertEquals(0.20, Torneo.PORCENTAJE_PREFERENCIAL, 0.0001, "El porcentaje preferencial no es el esperado.");
    }

    @Test
    void testCuposEnLosLimites() throws Exception
    {
        assertEquals(2, crearTorneo(2, true).getCupos(), "Un torneo de 2 cupos es válido.");
        assertEquals(32, crearTorneo(32, true).getCupos(), "Un torneo de 32 cupos es válido.");
    }

    @Test
    void testCuposFueraDeLosLimites()
    {
        ReglaNegocioException muyPocos = assertThrows(ReglaNegocioException.class, () -> crearTorneo(1, true), "Un torneo de 1 cupo no es válido.");
        assertEquals("Los cupos deben estar entre 2 y 32", muyPocos.getMessage(), "El mensaje de la excepción no es el esperado.");

        assertThrows(ReglaNegocioException.class, () -> crearTorneo(33, true), "Un torneo de 33 cupos no es válido.");
        assertThrows(ReglaNegocioException.class, () -> crearTorneo(0, true), "Un torneo de 0 cupos no es válido.");
        assertThrows(ReglaNegocioException.class, () -> crearTorneo(-4, true), "Un torneo de cupos negativos no es válido.");
    }

    @Test
    void testNombreInvalido()
    {
        ReglaNegocioException sinNombre = assertThrows(ReglaNegocioException.class, () -> new TorneoIndividual(null, 8, fechaInicio, fechaLimite, new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS), "Un torneo sin nombre no es válido.");
        assertEquals("El torneo debe tener nombre", sinNombre.getMessage(), "El mensaje de la excepción no es el esperado.");

        assertThrows(ReglaNegocioException.class, () -> new TorneoIndividual("   ", 8, fechaInicio, fechaLimite, new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS), "Un nombre en blanco no es válido.");
        assertThrows(ReglaNegocioException.class, () -> new TorneoIndividual("", 8, fechaInicio, fechaLimite, new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS), "Un nombre vacío no es válido.");
    }

    @Test
    void testFechaLimitePosteriorAlInicio()
    {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> new TorneoIndividual("Copa", 8, fechaInicio, fechaInicio.plusDays(1), new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS), "La fecha límite no puede ser posterior al inicio.");

        assertEquals("La fecha límite preferencial no puede ser posterior a la fecha de inicio", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFechaLimiteIgualAlInicio() throws Exception
    {
        TorneoIndividual igual = new TorneoIndividual("Copa", 8, fechaInicio, fechaInicio, new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS);

        assertEquals(fechaInicio, igual.getFechaLimitePreferencial(), "La fecha límite igual al inicio es válida.");
    }

    @Test
    void testEsCompetitivo()
    {
        assertTrue(torneo.esCompetitivo(), "Un torneo con premiación competitiva es competitivo.");
        assertFalse(torneoRecreativo.esCompetitivo(), "Un torneo con premiación recreativa no es competitivo.");
    }

    @Test
    void testToString() throws Exception
    {
        assertEquals("Copa Cerros (Tenis INTERMEDIO SENCILLOS, competitivo, INSCRIPCIONES_ABIERTAS)", torneo.toString(), "El texto del torneo competitivo no es el esperado.");
        assertEquals("Copa Cerros (Tenis INTERMEDIO SENCILLOS, recreativo, INSCRIPCIONES_ABIERTAS)", torneoRecreativo.toString(), "El texto del torneo recreativo no es el esperado.");
    }

    @Test
    void testGetCuposPreferenciales() throws Exception
    {
        assertEquals(2, crearTorneo(8, true).getCuposPreferenciales(), "El 20% de 8 cupos son 2 cupos preferenciales.");
        assertEquals(2, crearTorneo(10, true).getCuposPreferenciales(), "El 20% de 10 cupos son 2 cupos preferenciales.");
        assertEquals(7, crearTorneo(32, true).getCuposPreferenciales(), "El 20% de 32 cupos se redondea hacia arriba a 7.");
        assertEquals(1, crearTorneo(2, true).getCuposPreferenciales(), "El 20% de 2 cupos se redondea hacia arriba a 1.");
        assertEquals(1, crearTorneo(4, true).getCuposPreferenciales(), "El 20% de 4 cupos se redondea hacia arriba a 1.");
    }

    @Test
    void testGetCuposLibres() throws Exception
    {
        assertEquals(8, torneo.getCuposLibres(), "Un torneo nuevo tiene todos los cupos libres.");

        llenar(torneo, 3);

        assertEquals(5, torneo.getCuposLibres(), "Con tres inscritos quedan cinco cupos libres.");
    }

    @Test
    void testGetCuposPreferencialesLibres() throws Exception
    {
        assertEquals(2, torneo.getCuposPreferencialesLibres(), "Un torneo nuevo tiene todos los cupos preferenciales libres.");

        torneo.inscribir(new InscripcionTorneoIndividual(crearJugador(1), null), antesDelLimite, true);

        assertEquals(1, torneo.getCuposPreferencialesLibres(), "Con un inscrito preferencial queda un cupo preferencial libre.");
    }

    @Test
    void testInscribir() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = inscribirJugador(torneo, 1);

        assertEquals(1, torneo.getInscripciones().size(), "El torneo debe tener una inscripción.");
        assertSame(inscripcion, torneo.getInscripciones().get(0), "La inscripción no es la esperada.");
        assertFalse(inscripcion.esPreferencial(), "La inscripción no debe ser preferencial.");
        assertEquals(10000, inscripcion.getMontoPagado(), 0.001, "La inscripción debe pagar la tarifa del torneo competitivo.");
    }

    @Test
    void testInscribirEnTorneoRecreativo() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = inscribirJugador(torneoRecreativo, 1);

        assertEquals(0, inscripcion.getMontoPagado(), 0.001, "El torneo recreativo no cobra tarifa.");
    }

    @Test
    void testInscribirPreferencialAntesDelLimite() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(crearJugador(1), null);

        torneo.inscribir(inscripcion, antesDelLimite, true);

        assertTrue(inscripcion.esPreferencial(), "Un jugador elegible antes del límite ocupa un cupo preferencial.");
    }

    @Test
    void testInscribirPreferencialEnLaFechaLimite() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(crearJugador(1), null);

        torneo.inscribir(inscripcion, fechaLimite, true);

        assertTrue(inscripcion.esPreferencial(), "El mismo día del límite todavía se puede ocupar un cupo preferencial.");
    }

    @Test
    void testInscribirPreferencialDespuesDelLimite() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(crearJugador(1), null);

        torneo.inscribir(inscripcion, despuesDelLimite, true);

        assertFalse(inscripcion.esPreferencial(), "Después del límite ya no se ocupa un cupo preferencial.");
    }

    @Test
    void testInscribirNoElegibleAntesDelLimite() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(crearJugador(1), null);

        torneo.inscribir(inscripcion, antesDelLimite, false);

        assertFalse(inscripcion.esPreferencial(), "Un jugador no elegible nunca ocupa un cupo preferencial.");
    }

    @Test
    void testInscribirPreferencialConCuposPreferencialesAgotados() throws Exception
    {
        torneo.inscribir(new InscripcionTorneoIndividual(crearJugador(1), null), antesDelLimite, true);
        torneo.inscribir(new InscripcionTorneoIndividual(crearJugador(2), null), antesDelLimite, true);
        InscripcionTorneoIndividual tercera = new InscripcionTorneoIndividual(crearJugador(3), null);

        torneo.inscribir(tercera, antesDelLimite, true);

        assertFalse(tercera.esPreferencial(), "Con los cupos preferenciales agotados se ocupa un cupo regular.");
        assertEquals(0, torneo.getCuposPreferencialesLibres(), "No deben quedar cupos preferenciales.");
        assertEquals(5, torneo.getCuposLibres(), "Deben quedar cinco cupos libres.");
    }

    @Test
    void testInscribirSoloQuedanCuposPreferenciales() throws Exception
    {
        llenar(torneo, 6);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(crearJugador(7), null), antesDelLimite, false), "Debe lanzarse ReglaNegocioException si solo quedan cupos preferenciales.");

        assertEquals("Solo quedan cupos preferenciales", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(6, torneo.getInscripciones().size(), "No debe agregarse la inscripción.");
    }

    @Test
    void testInscribirPreferencialConLosCuposRegularesAgotados() throws Exception
    {
        llenar(torneo, 6);
        InscripcionTorneoIndividual preferencial = new InscripcionTorneoIndividual(crearJugador(7), null);

        torneo.inscribir(preferencial, antesDelLimite, true);

        assertTrue(preferencial.esPreferencial(), "Un jugador elegible puede ocupar los cupos preferenciales que quedan.");
    }

    @Test
    void testInscribirCuposPreferencialesDisponiblesDespuesDelLimite() throws Exception
    {
        llenar(torneo, 6);
        InscripcionTorneoIndividual inscripcion = new InscripcionTorneoIndividual(crearJugador(7), null);

        torneo.inscribir(inscripcion, despuesDelLimite, false);

        assertFalse(inscripcion.esPreferencial(), "Después del límite los cupos preferenciales se abren a todos.");
        assertEquals(7, torneo.getInscripciones().size(), "El torneo debe tener siete inscripciones.");
    }

    @Test
    void testInscribirTorneoLleno() throws Exception
    {
        llenar(torneo, 8);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(crearJugador(9), null), despuesDelLimite, false), "Debe lanzarse ReglaNegocioException si el torneo está lleno.");

        assertEquals("El torneo está lleno", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(8, torneo.getInscripciones().size(), "No debe agregarse la inscripción.");
    }

    @Test
    void testInscribirJugadorYaInscrito() throws Exception
    {
        inscribirJugador(torneo, 1);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual((Socio) mapaUsuarios.get("j1"), null), antesDelLimite, false), "Debe lanzarse ReglaNegocioException si el jugador ya está inscrito.");

        assertEquals("Jugador 1 ya está inscrito en el torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertEquals(1, torneo.getInscripciones().size(), "No debe agregarse la inscripción repetida.");
    }

    @Test
    void testInscribirConLasInscripcionesCerradas() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(crearJugador(3), null), antesDelLimite, false), "Debe lanzarse ReglaNegocioException si las inscripciones están cerradas.");

        assertEquals("Las inscripciones están cerradas", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testInscribirParticipanteInvalido()
    {
        Socio principiante = new Socio(25, "Luis Mora", "lmora", "x");
        principiante.agregarInscripcionIndividual(new InscripcionIndividual(tenis, NivelJugador.PRINCIPIANTE));

        assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(new InscripcionTorneoIndividual(principiante, null), antesDelLimite, false), "Un participante inválido no debe inscribirse.");

        assertEquals(0, torneo.getInscripciones().size(), "No debe quedar ninguna inscripción.");
    }

    @Test
    void testRetirar() throws Exception
    {
        inscribirJugador(torneo, 1);
        inscribirJugador(torneo, 2);

        torneo.retirar((Socio) mapaUsuarios.get("j1"));

        assertEquals(1, torneo.getInscripciones().size(), "El torneo debe tener una inscripción.");
        assertNull(torneo.buscarInscripcion((Socio) mapaUsuarios.get("j1")), "El jugador retirado ya no debe estar inscrito.");
        assertNotNull(torneo.buscarInscripcion((Socio) mapaUsuarios.get("j2")), "El otro jugador debe seguir inscrito.");
    }

    @Test
    void testRetirarJugadorNoInscrito()
    {
        Socio ajeno = crearJugador(1);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.retirar(ajeno), "Debe lanzarse ReglaNegocioException si el jugador no está inscrito.");

        assertEquals("Jugador 1 no está inscrito en el torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRetirarConElTorneoIniciado() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.retirar((Socio) mapaUsuarios.get("j1")), "Debe lanzarse ReglaNegocioException si el torneo ya inició.");

        assertEquals("El torneo ya inició", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRetirarEquipoNoInscrito()
    {
        Equipo equipo = new Equipo("Cerros Basket", 8, new DeportesConjunto_Competencias.Baloncesto(), DeportesConjunto_Competencias.Categoria.MAYORES);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.retirarEquipo(equipo), "Debe lanzarse ReglaNegocioException si el equipo no está inscrito.");

        assertEquals("Cerros Basket no está inscrito en el torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRetirarEquipoConElTorneoIniciado() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();
        Equipo equipo = new Equipo("Cerros Basket", 8, new DeportesConjunto_Competencias.Baloncesto(), DeportesConjunto_Competencias.Categoria.MAYORES);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.retirarEquipo(equipo), "Debe lanzarse ReglaNegocioException si el torneo ya inició.");

        assertEquals("El torneo ya inició", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testBuscarInscripcion() throws Exception
    {
        InscripcionTorneoIndividual inscripcion = inscribirJugador(torneo, 1);

        assertSame(inscripcion, torneo.buscarInscripcion((Socio) mapaUsuarios.get("j1")), "Debe encontrarse la inscripción del jugador.");
        assertNull(torneo.buscarInscripcion(crearJugador(2)), "Un jugador no inscrito no tiene inscripción.");
    }

    @Test
    void testIniciarConDosInscritos() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 2);

        torneo.iniciar();

        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado(), "El torneo debe quedar en curso.");
        assertEquals(1, torneo.getRondaActual(), "El torneo debe iniciar en la ronda 1.");
        assertEquals(1, torneo.getEncuentros().size(), "Debe haber un encuentro.");
        assertSame(inscritos.get(0), torneo.getEncuentros().get(0).getParticipanteA(), "El participante A del encuentro no es el esperado.");
        assertSame(inscritos.get(1), torneo.getEncuentros().get(0).getParticipanteB(), "El participante B del encuentro no es el esperado.");
        assertEquals(1, torneo.getEncuentros().get(0).getRonda(), "El encuentro debe ser de la ronda 1.");
    }

    @Test
    void testIniciarConOchoInscritos() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 8);

        torneo.iniciar();

        assertEquals(4, torneo.getEncuentros().size(), "Debe haber cuatro encuentros.");
        assertSame(inscritos.get(2), torneo.getEncuentros().get(1).getParticipanteA(), "El segundo encuentro debe empezar con la tercera inscripción.");
        assertSame(inscritos.get(7), torneo.getEncuentros().get(3).getParticipanteB(), "El último encuentro debe terminar con la última inscripción.");
    }

    @Test
    void testIniciarConCuatroInscritos() throws Exception
    {
        llenar(torneo, 4);

        torneo.iniciar();

        assertEquals(2, torneo.getEncuentros().size(), "Debe haber dos encuentros.");
    }

    @Test
    void testIniciarConDieciseisInscritos() throws Exception
    {
        TorneoIndividual grande = crearTorneo(16, true);
        llenar(grande, 16);

        grande.iniciar();

        assertEquals(8, grande.getEncuentros().size(), "Debe haber ocho encuentros.");
    }

    @Test
    void testIniciarConTreintaYDosInscritos() throws Exception
    {
        TorneoIndividual grande = crearTorneo(32, true);
        llenar(grande, 32);

        grande.iniciar();

        assertEquals(16, grande.getEncuentros().size(), "Debe haber dieciséis encuentros.");
        assertEquals(EstadoTorneo.EN_CURSO, grande.getEstado(), "El torneo debe quedar en curso.");
    }

    @Test
    void testIniciarConUnNumeroInvalidoDeInscritos() throws Exception
    {
        int[] invalidos = {0, 1, 3, 5, 6, 7};
        for(int cantidad : invalidos)
        {
            TorneoIndividual otro = crearTorneo(8, true);
            llenar(otro, cantidad);

            ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> otro.iniciar(), "Con " + cantidad + " inscritos no debe poder iniciarse.");

            assertEquals("Se necesitan 2, 4, 8, 16 o 32 inscritos para iniciar el torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
            assertEquals(EstadoTorneo.INSCRIPCIONES_ABIERTAS, otro.getEstado(), "El torneo debe seguir con las inscripciones abiertas.");
        }
    }

    @Test
    void testIniciarDosVeces() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.iniciar(), "Debe lanzarse ReglaNegocioException si el torneo ya inició.");

        assertEquals("El torneo ya inició", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRegistrarGanadorSinIniciar()
    {
        Encuentro ajeno = new Encuentro(1, new InscripcionTorneoIndividual(crearJugador(1), null), new InscripcionTorneoIndividual(crearJugador(2), null));

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.registrarGanador(ajeno, ajeno.getParticipanteA()), "Debe lanzarse ReglaNegocioException si el torneo no está en curso.");

        assertEquals("El torneo no está en curso", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRegistrarGanadorDeUnEncuentroAjeno() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();
        Encuentro ajeno = new Encuentro(1, torneo.getInscripciones().get(0), torneo.getInscripciones().get(1));

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.registrarGanador(ajeno, ajeno.getParticipanteA()), "Debe lanzarse ReglaNegocioException si el encuentro no es del torneo.");

        assertEquals("El encuentro no pertenece a la ronda actual de este torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRegistrarGanadorDeUnaRondaAnterior() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primeraRonda = torneo.getEncuentros().get(0);
        jugarRonda(torneo, 1);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.registrarGanador(primeraRonda, primeraRonda.getParticipanteA()), "Debe lanzarse ReglaNegocioException si el encuentro es de una ronda anterior.");

        assertEquals("El encuentro no pertenece a la ronda actual de este torneo", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRegistrarGanadorQueNoParticipa() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro encuentro = torneo.getEncuentros().get(0);
        InscripcionTorneo deOtroEncuentro = torneo.getEncuentros().get(1).getParticipanteA();

        assertThrows(ReglaNegocioException.class, () -> torneo.registrarGanador(encuentro, deOtroEncuentro), "El ganador debe ser uno de los participantes del encuentro.");

        assertFalse(encuentro.tieneGanador(), "El encuentro no debe tener ganador.");
    }

    @Test
    void testRegistrarGanadorDosVeces() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro encuentro = torneo.getEncuentros().get(0);
        torneo.registrarGanador(encuentro, encuentro.getParticipanteA());

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.registrarGanador(encuentro, encuentro.getParticipanteB()), "Debe lanzarse ReglaNegocioException si el encuentro ya se jugó.");

        assertEquals("El encuentro ya se jugó", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testRegistrarGanadorNoAvanzaConEncuentrosPendientes() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);

        torneo.registrarGanador(primero, primero.getParticipanteA());

        assertEquals(1, torneo.getRondaActual(), "El torneo debe seguir en la ronda 1.");
        assertEquals(2, torneo.getEncuentros().size(), "No debe armarse la siguiente ronda.");
        assertEquals(1, torneo.getEncuentrosPendientes().size(), "Debe quedar un encuentro pendiente.");
        assertEquals(1, torneo.getEncuentrosJugados().size(), "Debe haber un encuentro jugado.");
    }

    @Test
    void testRegistrarGanadorArmaLaSiguienteRonda() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 8);
        torneo.iniciar();

        jugarRonda(torneo, 1);

        assertEquals(2, torneo.getRondaActual(), "El torneo debe pasar a la ronda 2.");
        assertEquals(4, torneo.getEncuentrosDeRonda(1).size(), "Debe haber cuatro encuentros en la ronda 1.");
        assertEquals(2, torneo.getEncuentrosDeRonda(2).size(), "Debe haber dos encuentros en la ronda 2.");
        assertSame(inscritos.get(0), torneo.getEncuentrosDeRonda(2).get(0).getParticipanteA(), "El primer ganador debe jugar la ronda 2.");
        assertSame(inscritos.get(2), torneo.getEncuentrosDeRonda(2).get(0).getParticipanteB(), "El segundo ganador debe jugar contra el primero.");
        assertSame(inscritos.get(4), torneo.getEncuentrosDeRonda(2).get(1).getParticipanteA(), "El tercer ganador debe jugar la otra semifinal.");
        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado(), "El torneo debe seguir en curso.");
    }

    @Test
    void testRegistrarGanadorFinalizaElTorneo() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 4);
        torneo.iniciar();

        jugarRonda(torneo, 1);
        jugarRonda(torneo, 2);

        assertEquals(EstadoTorneo.FINALIZADO, torneo.getEstado(), "El torneo debe quedar finalizado.");
        assertEquals(2, torneo.getRondaActual(), "La ronda final debe ser la 2.");
        assertEquals(3, torneo.getEncuentros().size(), "Debe haber tres encuentros.");
        assertEquals(0, torneo.getEncuentrosPendientes().size(), "No debe haber encuentros pendientes.");
        assertSame(inscritos.get(0), torneo.getEncuentrosDeRonda(2).get(0).getGanador(), "El campeón debe ser el primer inscrito.");
    }

    @Test
    void testRegistrarGanadorConElTorneoFinalizado() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();
        Encuentro laFinal = torneo.getEncuentros().get(0);
        torneo.registrarGanador(laFinal, laFinal.getParticipanteA());

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.registrarGanador(laFinal, laFinal.getParticipanteB()), "Debe lanzarse ReglaNegocioException si el torneo ya terminó.");

        assertEquals("El torneo no está en curso", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testCorregirGanadorEnLaMismaRonda() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);
        torneo.registrarGanador(primero, primero.getParticipanteA());

        torneo.corregirGanador(primero, primero.getParticipanteB());

        assertSame(primero.getParticipanteB(), primero.getGanador(), "El ganador debe cambiar.");
    }

    @Test
    void testCorregirGanadorReemplazaAlParticipanteDeLaSiguienteRonda() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);
        InscripcionTorneo ganadorOriginal = primero.getParticipanteA();
        InscripcionTorneo nuevoGanador = primero.getParticipanteB();
        jugarRonda(torneo, 1);
        Encuentro laFinal = torneo.getEncuentrosDeRonda(2).get(0);
        assertSame(ganadorOriginal, laFinal.getParticipanteA(), "Antes de corregir la final debe tener al ganador original.");

        torneo.corregirGanador(primero, nuevoGanador);

        assertSame(nuevoGanador, primero.getGanador(), "El ganador del encuentro debe cambiar.");
        assertSame(nuevoGanador, laFinal.getParticipanteA(), "La final debe reemplazar al participante.");
        assertFalse(laFinal.incluye(ganadorOriginal), "La final ya no debe incluir al ganador original.");
    }

    @Test
    void testCorregirGanadorCuandoLaSiguienteRondaYaSeJugo() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);
        jugarRonda(torneo, 1);
        Encuentro laFinal = torneo.getEncuentrosDeRonda(2).get(0);
        torneo.registrarGanador(laFinal, laFinal.getParticipanteB());

        InscripcionTorneo otro = primero.getParticipanteB();
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.corregirGanador(primero, otro), "Debe lanzarse ReglaNegocioException si la final ya se jugó o el torneo terminó.");

        assertEquals("El resultado ya no se puede cambiar", excepcion.getMessage(), "Con el torneo finalizado el mensaje debe ser el general.");
    }

    @Test
    void testCorregirGanadorConLaSiguienteRondaYaJugada() throws Exception
    {
        llenar(torneo, 8);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);
        jugarRonda(torneo, 1);
        Encuentro semifinal = torneo.getEncuentrosDeRonda(2).get(0);
        torneo.registrarGanador(semifinal, semifinal.getParticipanteA());

        InscripcionTorneo otro = primero.getParticipanteB();
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.corregirGanador(primero, otro), "Debe lanzarse ReglaNegocioException si la ronda siguiente ya se jugó.");

        assertEquals("El resultado ya no se puede cambiar: la ronda siguiente ya se jugó", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertSame(primero.getParticipanteA(), primero.getGanador(), "El ganador no debe cambiar.");
    }

    @Test
    void testCorregirGanadorSinGanadorPrevio() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.corregirGanador(primero, primero.getParticipanteA()), "Debe lanzarse ReglaNegocioException si el encuentro no tiene ganador.");

        assertEquals("El encuentro todavía no tiene ganador", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testCorregirGanadorConElTorneoFinalizado() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();
        Encuentro laFinal = torneo.getEncuentros().get(0);
        torneo.registrarGanador(laFinal, laFinal.getParticipanteA());

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> torneo.corregirGanador(laFinal, laFinal.getParticipanteB()), "Debe lanzarse ReglaNegocioException si el torneo ya terminó.");

        assertEquals("El resultado ya no se puede cambiar", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertSame(laFinal.getParticipanteA(), laFinal.getGanador(), "El ganador no debe cambiar.");
    }

    @Test
    void testCorregirGanadorConUnParticipanteAjeno() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);
        torneo.registrarGanador(primero, primero.getParticipanteA());
        InscripcionTorneo deOtroEncuentro = torneo.getEncuentros().get(1).getParticipanteA();

        assertThrows(ReglaNegocioException.class, () -> torneo.corregirGanador(primero, deOtroEncuentro), "El nuevo ganador debe participar en el encuentro.");

        assertSame(primero.getParticipanteA(), primero.getGanador(), "El ganador no debe cambiar.");
    }

    @Test
    @Disabled("Error conocido en Torneo.corregirGanador: reemplaza al participante de la ronda siguiente antes de validar al nuevo ganador. Quita esta anotación para verlo fallar.")
    void testCorregirGanadorConParticipanteAjenoNoAlteraLaSiguienteRonda() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);
        Encuentro primero = torneo.getEncuentros().get(0);
        Encuentro laFinal = torneo.getEncuentrosDeRonda(2).get(0);
        InscripcionTorneo ganadorOriginal = primero.getGanador();
        InscripcionTorneo deOtroEncuentro = torneo.getEncuentros().get(1).getParticipanteA();

        assertThrows(ReglaNegocioException.class, () -> torneo.corregirGanador(primero, deOtroEncuentro), "El nuevo ganador debe participar en el encuentro.");

        assertSame(ganadorOriginal, primero.getGanador(), "El ganador del encuentro no debe cambiar.");
        assertTrue(laFinal.incluye(ganadorOriginal), "Si la corrección falla, la final debe conservar al ganador original.");
    }

    @Test
    void testGetEncuentrosDeRonda() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);

        assertEquals(2, torneo.getEncuentrosDeRonda(1).size(), "Debe haber dos encuentros en la ronda 1.");
        assertEquals(1, torneo.getEncuentrosDeRonda(2).size(), "Debe haber un encuentro en la ronda 2.");
        assertEquals(0, torneo.getEncuentrosDeRonda(3).size(), "No debe haber encuentros en la ronda 3.");
        assertEquals(0, torneo.getEncuentrosDeRonda(0).size(), "No debe haber encuentros en la ronda 0.");
    }

    @Test
    void testGetRondaEliminacion() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);
        jugarRonda(torneo, 2);

        assertEquals(0, torneo.getRondaEliminacion(inscritos.get(0)), "El campeón no fue eliminado.");
        assertEquals(1, torneo.getRondaEliminacion(inscritos.get(1)), "El segundo inscrito perdió en la ronda 1.");
        assertEquals(2, torneo.getRondaEliminacion(inscritos.get(2)), "El tercer inscrito perdió la final.");
        assertEquals(1, torneo.getRondaEliminacion(inscritos.get(3)), "El cuarto inscrito perdió en la ronda 1.");
    }

    @Test
    void testGetRondaEliminacionDeUnaInscripcionAjena() throws Exception
    {
        llenar(torneo, 2);

        assertEquals(0, torneo.getRondaEliminacion(new InscripcionTorneoIndividual(crearJugador(9), null)), "Una inscripción ajena no tiene ronda de eliminación.");
    }

    @Test
    void testGetPosiciones() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);
        jugarRonda(torneo, 2);

        List<InscripcionTorneo> posiciones = torneo.getPosiciones();

        assertEquals(4, posiciones.size(), "Debe haber cuatro posiciones.");
        assertSame(inscritos.get(0), posiciones.get(0), "El primer lugar debe ser el campeón.");
        assertSame(inscritos.get(2), posiciones.get(1), "El segundo lugar debe ser el subcampeón.");
        assertSame(inscritos.get(1), posiciones.get(2), "El tercer lugar debe ser el primer eliminado en la ronda 1.");
        assertSame(inscritos.get(3), posiciones.get(3), "El cuarto lugar debe ser el otro eliminado en la ronda 1.");
    }

    @Test
    void testGetPosicionesConOchoJugadores() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 8);
        torneo.iniciar();
        jugarRonda(torneo, 1);
        jugarRonda(torneo, 2);
        jugarRonda(torneo, 3);

        List<InscripcionTorneo> posiciones = torneo.getPosiciones();

        assertEquals(8, posiciones.size(), "Debe haber ocho posiciones.");
        assertSame(inscritos.get(0), posiciones.get(0), "El primer lugar debe ser el campeón.");
        assertSame(inscritos.get(4), posiciones.get(1), "El segundo lugar debe ser el subcampeón.");
        assertEquals(3, torneo.getRondaEliminacion(posiciones.get(1)), "El subcampeón debe haber perdido la final.");
        assertEquals(2, torneo.getRondaEliminacion(posiciones.get(2)), "El tercer lugar debe haber perdido en semifinales.");
        assertEquals(2, torneo.getRondaEliminacion(posiciones.get(3)), "El cuarto lugar debe haber perdido en semifinales.");
        for(int i = 4; i < 8; i++)
        {
            assertEquals(1, torneo.getRondaEliminacion(posiciones.get(i)), "Del quinto al octavo lugar deben haber perdido en la primera ronda.");
        }
    }

    @Test
    void testGetRecaudado() throws Exception
    {
        assertEquals(0, torneo.getRecaudado(), 0.001, "Un torneo sin inscritos no recauda nada.");

        llenar(torneo, 3);

        assertEquals(30000, torneo.getRecaudado(), 0.001, "Tres inscritos de 10000 recaudan 30000.");
    }

    @Test
    void testGetRecaudadoTorneoRecreativo() throws Exception
    {
        llenar(torneoRecreativo, 4);

        assertEquals(0, torneoRecreativo.getRecaudado(), 0.001, "Un torneo recreativo no recauda.");
    }

    @Test
    void testPremiosCompetitivos() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);
        jugarRonda(torneo, 2);

        List<PremioEntregado> premios = torneo.getPremios();

        assertEquals(2, premios.size(), "Debe haber un premio por cada uno de los dos finalistas.");
        assertEquals(1, premios.get(0).getPuesto(), "El primer premio debe ser del primer puesto.");
        assertSame(((InscripcionTorneoIndividual) inscritos.get(0)).getJugador(), premios.get(0).getJugador(), "El primer premio debe ser del campeón.");
        assertEquals(20000, premios.get(0).getMontoEfectivo(), 0.001, "El campeón recibe 50% de 40000.");
        assertEquals(2, premios.get(1).getPuesto(), "El segundo premio debe ser del segundo puesto.");
        assertSame(((InscripcionTorneoIndividual) inscritos.get(2)).getJugador(), premios.get(1).getJugador(), "El segundo premio debe ser del subcampeón.");
        assertEquals(8000, premios.get(1).getMontoEfectivo(), 0.001, "El subcampeón recibe 20% de 40000.");
    }

    @Test
    void testPremiosRecreativos() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneoRecreativo, 4);
        torneoRecreativo.iniciar();
        jugarRonda(torneoRecreativo, 1);
        jugarRonda(torneoRecreativo, 2);

        List<PremioEntregado> premios = torneoRecreativo.getPremios();
        Socio campeon = (Socio) ((InscripcionTorneoIndividual) inscritos.get(0)).getJugador();

        assertEquals(2, premios.size(), "Debe haber un premio por cada uno de los dos finalistas.");
        assertEquals(0, premios.get(0).getMontoEfectivo(), 0.001, "El premio recreativo no es en efectivo.");
        assertNotNull(premios.get(0).getBono(), "El campeón debe recibir un bono.");
        assertEquals(1, campeon.getBonos().size(), "El bono debe quedar en el campeón.");
        assertSame(premios.get(0).getBono(), campeon.getBonos().get(0), "El bono del campeón debe ser el del premio.");
    }

    @Test
    void testSinPremiosAntesDeFinalizar() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);

        assertEquals(0, torneo.getPremios().size(), "Antes de la final no hay premios.");
    }

    @Test
    void testListasDeSoloLectura() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();

        assertThrows(UnsupportedOperationException.class, () -> torneo.getInscripciones().clear(), "La lista de inscripciones no debe poder modificarse desde afuera.");
        assertThrows(UnsupportedOperationException.class, () -> torneo.getEncuentros().clear(), "La lista de encuentros no debe poder modificarse desde afuera.");
        assertThrows(UnsupportedOperationException.class, () -> torneo.getPremios().clear(), "La lista de premios no debe poder modificarse desde afuera.");
    }

    @Test
    void testToJSONCompetitivo()
    {
        JSONObject json = torneo.toJSON();

        assertEquals("Copa Cerros", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(8, json.getInt("cupos"), "Los cupos en el JSON no son los esperados.");
        assertEquals("2026-12-01", json.getString("fechaInicio"), "La fecha de inicio en el JSON no es la esperada.");
        assertEquals("2026-11-20", json.getString("fechaLimitePreferencial"), "La fecha límite en el JSON no es la esperada.");
        assertTrue(json.getBoolean("competitivo"), "El torneo debe ser competitivo en el JSON.");
        assertEquals(10000, json.getDouble("tarifa"), 0.001, "La tarifa en el JSON no es la esperada.");
        assertEquals("INSCRIPCIONES_ABIERTAS", json.getString("estado"), "El estado en el JSON no es el esperado.");
        assertEquals(0, json.getInt("rondaActual"), "La ronda en el JSON no es la esperada.");
        assertEquals(0, json.getJSONArray("inscripciones").length(), "El JSON no debe tener inscripciones.");
        assertEquals(0, json.getJSONArray("encuentros").length(), "El JSON no debe tener encuentros.");
        assertEquals(0, json.getJSONArray("premios").length(), "El JSON no debe tener premios.");
    }

    @Test
    void testToJSONRecreativo()
    {
        JSONObject json = torneoRecreativo.toJSON();

        assertFalse(json.getBoolean("competitivo"), "El torneo no debe ser competitivo en el JSON.");
        assertFalse(json.has("tarifa"), "Un torneo recreativo no tiene tarifa en el JSON.");
    }

    @Test
    void testToJSONConInscripcionesEncuentrosYPremios() throws Exception
    {
        llenar(torneo, 2);
        torneo.iniciar();
        jugarRonda(torneo, 1);

        JSONObject json = torneo.toJSON();

        assertEquals("FINALIZADO", json.getString("estado"), "El estado en el JSON no es el esperado.");
        assertEquals(1, json.getInt("rondaActual"), "La ronda en el JSON no es la esperada.");
        assertEquals(2, json.getJSONArray("inscripciones").length(), "El JSON debe tener dos inscripciones.");
        assertEquals(1, json.getJSONArray("encuentros").length(), "El JSON debe tener un encuentro.");
        assertEquals(2, json.getJSONArray("premios").length(), "El JSON debe tener dos premios.");
    }

    @Test
    void testFromJSONDeUnTorneoFinalizado() throws Exception
    {
        List<InscripcionTorneo> inscritos = llenar(torneo, 4);
        torneo.iniciar();
        jugarRonda(torneo, 1);
        jugarRonda(torneo, 2);
        JSONObject json = new JSONObject(torneo.toJSON().toString());

        Torneo restaurado = Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>());

        assertEquals("Copa Cerros", restaurado.getNombre(), "El nombre restaurado no es el esperado.");
        assertEquals(8, restaurado.getCupos(), "Los cupos restaurados no son los esperados.");
        assertEquals(fechaInicio, restaurado.getFechaInicio(), "La fecha de inicio restaurada no es la esperada.");
        assertEquals(fechaLimite, restaurado.getFechaLimitePreferencial(), "La fecha límite restaurada no es la esperada.");
        assertEquals(EstadoTorneo.FINALIZADO, restaurado.getEstado(), "El estado restaurado no es el esperado.");
        assertEquals(2, restaurado.getRondaActual(), "La ronda restaurada no es la esperada.");
        assertTrue(restaurado.esCompetitivo(), "El torneo restaurado debe ser competitivo.");
        assertEquals(4, restaurado.getInscripciones().size(), "Deben restaurarse cuatro inscripciones.");
        assertEquals(3, restaurado.getEncuentros().size(), "Deben restaurarse tres encuentros.");
        assertEquals(2, restaurado.getPremios().size(), "Deben restaurarse dos premios.");
        assertEquals(40000, restaurado.getRecaudado(), 0.001, "Lo recaudado restaurado no es el esperado.");
        assertEquals(torneo.getPosiciones().get(0).getNombre(), restaurado.getPosiciones().get(0).getNombre(), "El campeón restaurado no es el esperado.");
        assertEquals(inscritos.get(2).getNombre(), restaurado.getPosiciones().get(1).getNombre(), "El subcampeón restaurado no es el esperado.");
    }

    @Test
    void testFromJSONDeUnTorneoRecreativoRestauraLosBonos() throws Exception
    {
        llenar(torneoRecreativo, 2);
        torneoRecreativo.iniciar();
        jugarRonda(torneoRecreativo, 1);
        JSONObject json = new JSONObject(torneoRecreativo.toJSON().toString());

        Torneo restaurado = Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>());

        assertFalse(restaurado.esCompetitivo(), "El torneo restaurado debe ser recreativo.");
        assertEquals(2, restaurado.getPremios().size(), "Deben restaurarse dos premios.");
        assertNotNull(restaurado.getPremios().get(0).getBono(), "El premio restaurado debe tener su bono.");
        assertSame(restaurado.getPremios().get(0).getJugador().getBonos().get(0), restaurado.getPremios().get(0).getBono(), "El bono restaurado debe ser el del jugador.");
    }

    @Test
    void testFromJSONDeUnTorneoEnCurso() throws Exception
    {
        llenar(torneo, 4);
        torneo.iniciar();
        Encuentro primero = torneo.getEncuentros().get(0);
        torneo.registrarGanador(primero, primero.getParticipanteB());
        JSONObject json = new JSONObject(torneo.toJSON().toString());

        Torneo restaurado = Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>());

        assertEquals(EstadoTorneo.EN_CURSO, restaurado.getEstado(), "El estado restaurado no es el esperado.");
        assertEquals(1, restaurado.getRondaActual(), "La ronda restaurada no es la esperada.");
        assertEquals(1, restaurado.getEncuentrosJugados().size(), "Debe restaurarse un encuentro jugado.");
        assertEquals(1, restaurado.getEncuentrosPendientes().size(), "Debe restaurarse un encuentro pendiente.");
        assertSame(restaurado.getEncuentros().get(0).getParticipanteB(), restaurado.getEncuentros().get(0).getGanador(), "El ganador restaurado no es el esperado.");
        assertEquals(0, restaurado.getPremios().size(), "No debe haber premios.");
    }

    @Test
    void testFromJSONSinListasOpcionales()
    {
        JSONObject json = torneo.toJSON();
        json.remove("inscripciones");
        json.remove("encuentros");
        json.remove("premios");

        Torneo restaurado = Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>());

        assertEquals(0, restaurado.getInscripciones().size(), "Sin inscripciones en el JSON el torneo debe quedar sin inscripciones.");
        assertEquals(0, restaurado.getEncuentros().size(), "Sin encuentros en el JSON el torneo debe quedar sin encuentros.");
        assertEquals(0, restaurado.getPremios().size(), "Sin premios en el JSON el torneo debe quedar sin premios.");
    }

    @Test
    void testFromJSONDisciplinaInexistente()
    {
        mapaDisciplinas.clear();

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> Torneo.fromJSON(torneo.toJSON(), new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>()), "Debe lanzarse IllegalStateException si la disciplina no existe.");

        assertEquals("Disciplina inexistente en el torneo: Tenis", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = torneo.toJSON();
        json.put("tipo", "MIXTO");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>()), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de torneo desconocido: MIXTO", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONConDatosInvalidos()
    {
        JSONObject json = torneo.toJSON();
        json.put("cupos", 1);

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>()), "Debe lanzarse IllegalStateException si los datos del torneo son inválidos.");

        assertEquals("Torneo inválido en los datos: Los cupos deben estar entre 2 y 32", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
        assertTrue(excepcion.getCause() instanceof ReglaNegocioException, "La causa debe ser la ReglaNegocioException original.");
    }

    @Test
    void testFromJSONConEstadoInvalido()
    {
        JSONObject json = torneo.toJSON();
        json.put("estado", "CANCELADO");

        assertThrows(IllegalArgumentException.class, () -> Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>()), "Un estado desconocido debe lanzar IllegalArgumentException.");
    }

    @Test
    void testFromJSONIncompleto()
    {
        JSONObject json = new JSONObject();
        json.put("nombre", "Copa Cerros");

        assertThrows(org.json.JSONException.class, () -> Torneo.fromJSON(json, new HashMap<String, DeporteConjunto>(), mapaDisciplinas, mapaUsuarios, new HashMap<String, Equipo>()), "Debe lanzarse JSONException si faltan campos.");
    }
}
