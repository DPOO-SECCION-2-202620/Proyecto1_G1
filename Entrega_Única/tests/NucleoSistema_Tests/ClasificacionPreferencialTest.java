package NucleoSistema_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.InscripcionIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Padel;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Tenis;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClasificacionPreferencial;
import PartidosEquipo_Estadisticas.Partido;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;
import Usuarios.Socio;

public class ClasificacionPreferencialTest
{
    private ClasificacionPreferencial clasificacion;

    private Tenis tenis;
    private Padel padel;
    private Baloncesto baloncesto;
    private Futbol futbol;

    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private LocalDate fechaPartido;

    private TorneoIndividual torneoSencillos;
    private TorneoIndividual torneoDobles;
    private TorneoConjunto torneoConjunto;

    private Socio jugador1;
    private Socio jugador2;
    private Socio jugador3;
    private Socio jugador4;
    private Socio jugadorEnElMinimo;
    private Socio jugadorBajoElMinimo;
    private List<RankingJugadorDisciplina> rankings;

    private Equipo equipoA;
    private Equipo equipoB;
    private Equipo equipoC;
    private Equipo equipoD;
    private List<Equipo> equipos;
    private List<Partido> partidos;

    @BeforeEach
    void setUp() throws Exception
    {
        clasificacion = new ClasificacionPreferencial();

        tenis = new Tenis(4, "Tenis");
        padel = new Padel(2, "Padel");
        baloncesto = new Baloncesto();
        futbol = new Futbol();

        fechaInicio = LocalDate.of(2026, 12, 1);
        fechaLimite = LocalDate.of(2026, 11, 20);
        fechaPartido = LocalDate.of(2026, 10, 15);

        torneoSencillos = crearTorneoIndividual(8, Modalidad.SENCILLOS);
        torneoDobles = crearTorneoIndividual(8, Modalidad.DOBLES);
        torneoConjunto = crearTorneoConjunto(8);

        jugador1 = crearJugador("Jugador 1", "j1", NivelJugador.INTERMEDIO);
        jugador2 = crearJugador("Jugador 2", "j2", NivelJugador.INTERMEDIO);
        jugador3 = crearJugador("Jugador 3", "j3", NivelJugador.INTERMEDIO);
        jugador4 = crearJugador("Jugador 4", "j4", NivelJugador.INTERMEDIO);
        jugadorEnElMinimo = crearJugador("Jugador Minimo", "jmin", NivelJugador.INTERMEDIO);
        jugadorBajoElMinimo = crearJugador("Jugador Bajo", "jbajo", NivelJugador.INTERMEDIO);

        rankings = new ArrayList<RankingJugadorDisciplina>();
        agregarRanking(jugador1, tenis, 1300);
        agregarRanking(jugador2, tenis, 1200);
        agregarRanking(jugador3, tenis, 1100);
        agregarRanking(jugador4, tenis, 1050);
        agregarRanking(jugadorEnElMinimo, tenis, 1000);
        agregarRanking(jugadorBajoElMinimo, tenis, 900);

        equipoA = new Equipo("Equipo A", 10, baloncesto, Categoria.MAYORES);
        equipoB = new Equipo("Equipo B", 10, baloncesto, Categoria.MAYORES);
        equipoC = new Equipo("Equipo C", 10, baloncesto, Categoria.MAYORES);
        equipoD = new Equipo("Equipo D", 10, baloncesto, Categoria.MAYORES);

        equipos = new ArrayList<Equipo>();
        equipos.add(equipoA);
        equipos.add(equipoB);
        equipos.add(equipoC);
        equipos.add(equipoD);

        partidos = new ArrayList<Partido>();
        registrarVictorias(equipoA, equipoD, 4);
        registrarVictorias(equipoB, equipoD, 3);
        registrarVictorias(equipoC, equipoD, 2);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    // ---------------------------------------------------------------------
    // Métodos auxiliares
    // ---------------------------------------------------------------------

    private TorneoIndividual crearTorneoIndividual(int cupos, Modalidad modalidad) throws ReglaNegocioException
    {
        return new TorneoIndividual("Copa Individual", cupos, fechaInicio, fechaLimite, new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, modalidad);
    }

    private TorneoConjunto crearTorneoConjunto(int cupos) throws ReglaNegocioException
    {
        return new TorneoConjunto("Copa Conjunto", cupos, fechaInicio, fechaLimite, new PremiacionRecreativa(), baloncesto, Categoria.MAYORES);
    }

    private Socio crearJugador(String nombre, String login, NivelJugador nivel)
    {
        Socio socio = new Socio(25, nombre, login, "x");
        socio.agregarInscripcionIndividual(new InscripcionIndividual(tenis, nivel));
        return socio;
    }

    private void agregarRanking(Socio jugador, Object disciplina, double puntaje)
    {
        RankingJugadorDisciplina ranking;
        if(disciplina == padel)
        {
            ranking = new RankingJugadorDisciplina(jugador, padel);
        }
        else
        {
            ranking = new RankingJugadorDisciplina(jugador, tenis);
        }
        ranking.setPuntajeElo(puntaje);
        rankings.add(ranking);
    }

    private InscripcionTorneo sencillos(Socio jugador)
    {
        return new InscripcionTorneoIndividual(jugador, null);
    }

    private InscripcionTorneo pareja(Socio jugador, Socio companero)
    {
        return new InscripcionTorneoIndividual(jugador, companero);
    }

    private void registrarVictorias(Equipo ganador, Equipo perdedor, int cantidad)
    {
        for(int i = 0; i < cantidad; i++)
        {
            partidos.add(new PartidoInterno(fechaPartido, ganador, perdedor, 2, 1));
        }
    }

    private boolean esElegible(Torneo torneo, InscripcionTorneo inscripcion)
    {
        return clasificacion.esElegible(torneo, inscripcion, rankings, partidos, equipos);
    }

    // ---------------------------------------------------------------------
    // Torneos individuales
    // ---------------------------------------------------------------------

    @Test
    void testLosMejoresDelNivelSonElegibles()
    {
        assertTrue(esElegible(torneoSencillos, sencillos(jugador1)), "El mejor jugador del nivel debe ser elegible.");
        assertTrue(esElegible(torneoSencillos, sencillos(jugador2)), "El segundo mejor jugador debe ser elegible con dos cupos preferenciales.");
    }

    @Test
    void testLosDemasJugadoresNoSonElegibles()
    {
        assertFalse(esElegible(torneoSencillos, sencillos(jugador3)), "El tercer mejor jugador no entra en dos cupos preferenciales.");
        assertFalse(esElegible(torneoSencillos, sencillos(jugador4)), "El cuarto mejor jugador no entra en dos cupos preferenciales.");
    }

    @Test
    void testJugadorConElPuntajeInicialNoEsElegible()
    {
        assertFalse(esElegible(torneoSencillos, sencillos(jugadorEnElMinimo)), "Quien solo tiene el puntaje inicial no ha demostrado nada y no es elegible.");
    }

    @Test
    void testJugadorPorDebajoDelPuntajeInicialNoEsElegible()
    {
        assertFalse(esElegible(torneoSencillos, sencillos(jugadorBajoElMinimo)), "Quien está por debajo del puntaje inicial no es elegible.");
    }

    @Test
    void testJugadorSinRankingNoEsElegible()
    {
        Socio sinRanking = crearJugador("Sin Ranking", "sinranking", NivelJugador.INTERMEDIO);

        assertFalse(esElegible(torneoSencillos, sencillos(sinRanking)), "Un jugador sin ranking no es elegible.");
    }

    @Test
    void testSinRankingsNadieEsElegible()
    {
        rankings.clear();

        assertFalse(esElegible(torneoSencillos, sencillos(jugador1)), "Sin rankings nadie es elegible.");
    }

    @Test
    void testMasCuposPreferencialesAmplianLaElite() throws Exception
    {
        TorneoIndividual grande = crearTorneoIndividual(32, Modalidad.SENCILLOS);

        assertTrue(esElegible(grande, sencillos(jugador3)), "Con siete cupos preferenciales el tercer mejor jugador es elegible.");
        assertTrue(esElegible(grande, sencillos(jugador4)), "Con siete cupos preferenciales el cuarto mejor jugador es elegible.");
        assertFalse(esElegible(grande, sencillos(jugadorEnElMinimo)), "Aun con más cupos, el puntaje inicial no basta.");
        assertFalse(esElegible(grande, sencillos(jugadorBajoElMinimo)), "Aun con más cupos, estar por debajo del puntaje inicial no basta.");
    }

    @Test
    void testConUnSoloCupoPreferencialSoloEntraElMejor() throws Exception
    {
        TorneoIndividual pequeno = crearTorneoIndividual(2, Modalidad.SENCILLOS);

        assertTrue(esElegible(pequeno, sencillos(jugador1)), "El mejor jugador ocupa el único cupo preferencial.");
        assertFalse(esElegible(pequeno, sencillos(jugador2)), "El segundo mejor no entra con un solo cupo preferencial.");
    }

    @Test
    void testLosEmpatesNoDejanFueraAQuienComparteElPuesto()
    {
        rankings.clear();
        agregarRanking(jugador1, tenis, 1300);
        agregarRanking(jugador2, tenis, 1300);
        agregarRanking(jugador3, tenis, 1200);
        agregarRanking(jugador4, tenis, 1200);

        assertTrue(esElegible(torneoSencillos, sencillos(jugador1)), "Con empate en el primer puesto el primero es elegible.");
        assertTrue(esElegible(torneoSencillos, sencillos(jugador2)), "Con empate en el primer puesto el segundo también es elegible.");
        assertFalse(esElegible(torneoSencillos, sencillos(jugador3)), "Con dos jugadores mejores y dos cupos, el tercero no es elegible.");
        assertFalse(esElegible(torneoSencillos, sencillos(jugador4)), "Con dos jugadores mejores y dos cupos, el cuarto no es elegible.");
    }

    @Test
    void testSoloCompitenJugadoresDelMismoNivel()
    {
        Socio avanzado1 = crearJugador("Avanzado 1", "av1", NivelJugador.AVANZADO);
        Socio avanzado2 = crearJugador("Avanzado 2", "av2", NivelJugador.AVANZADO);
        agregarRanking(avanzado1, tenis, 2000);
        agregarRanking(avanzado2, tenis, 1900);

        assertTrue(esElegible(torneoSencillos, sencillos(jugador2)), "Los jugadores de otro nivel no deben quitarle el cupo a un jugador del nivel del torneo.");
        assertFalse(esElegible(torneoSencillos, sencillos(jugador3)), "Los jugadores de otro nivel no deben dejar entrar a quien no está entre los mejores de su nivel.");
    }

    @Test
    void testElRankingDeOtraDisciplinaNoCuenta()
    {
        agregarRanking(jugador3, padel, 2000);
        agregarRanking(jugador4, padel, 1900);

        assertTrue(esElegible(torneoSencillos, sencillos(jugador2)), "Los rankings de otra disciplina no deben quitarle el cupo a nadie.");
        assertFalse(esElegible(torneoSencillos, sencillos(jugador3)), "El puntaje en otra disciplina no cuenta para este torneo.");
    }

    @Test
    void testJugadorConRankingSoloEnOtraDisciplinaNoEsElegible()
    {
        Socio soloPadel = crearJugador("Solo Padel", "solopadel", NivelJugador.INTERMEDIO);
        agregarRanking(soloPadel, padel, 2500);

        assertFalse(esElegible(torneoSencillos, sencillos(soloPadel)), "Sin ranking en la disciplina del torneo no es elegible.");
    }

    @Test
    void testParejaElegibleSiAmbosIntegrantesLoSon()
    {
        assertTrue(esElegible(torneoDobles, pareja(jugador1, jugador2)), "Una pareja de dos jugadores de élite es elegible.");
    }

    @Test
    void testParejaNoElegibleSiUnIntegranteNoLoEs()
    {
        assertFalse(esElegible(torneoDobles, pareja(jugador1, jugador3)), "Si un integrante no es de élite la pareja no es elegible.");
        assertFalse(esElegible(torneoDobles, pareja(jugador3, jugador1)), "El orden de los integrantes no debe importar.");
        assertFalse(esElegible(torneoDobles, pareja(jugador1, jugadorEnElMinimo)), "Si un integrante solo tiene el puntaje inicial la pareja no es elegible.");
    }

    @Test
    void testParejaNoElegibleSiAmbosFallan()
    {
        assertFalse(esElegible(torneoDobles, pareja(jugador3, jugadorBajoElMinimo)), "Si ningún integrante es de élite la pareja no es elegible.");
    }

    // ---------------------------------------------------------------------
    // Torneos de conjunto
    // ---------------------------------------------------------------------

    @Test
    void testLosEquiposConMasVictoriasSonElegibles()
    {
        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoA)), "El equipo con más victorias es elegible.");
        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoB)), "El segundo equipo con más victorias es elegible con dos cupos preferenciales.");
    }

    @Test
    void testLosDemasEquiposNoSonElegibles()
    {
        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoC)), "El tercer equipo con más victorias no entra en dos cupos preferenciales.");
    }

    @Test
    void testEquipoSinVictoriasNoEsElegible()
    {
        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoD)), "Un equipo sin victorias no es elegible.");
    }

    @Test
    void testSinPartidosNingunEquipoEsElegible()
    {
        partidos.clear();

        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoA)), "Sin partidos jugados nadie es elegible.");
    }

    @Test
    void testMasCuposPreferencialesAmplianLaEliteDeEquipos() throws Exception
    {
        TorneoConjunto grande = crearTorneoConjunto(32);

        assertTrue(esElegible(grande, new InscripcionTorneoEquipo(equipoC)), "Con siete cupos preferenciales el tercer equipo es elegible.");
        assertFalse(esElegible(grande, new InscripcionTorneoEquipo(equipoD)), "Aun con más cupos, un equipo sin victorias no es elegible.");
    }

    @Test
    void testConUnSoloCupoPreferencialSoloEntraElMejorEquipo() throws Exception
    {
        TorneoConjunto pequeno = crearTorneoConjunto(2);

        assertTrue(esElegible(pequeno, new InscripcionTorneoEquipo(equipoA)), "El mejor equipo ocupa el único cupo preferencial.");
        assertFalse(esElegible(pequeno, new InscripcionTorneoEquipo(equipoB)), "El segundo mejor equipo no entra con un solo cupo preferencial.");
    }

    @Test
    void testLosEquiposEmpatadosEnVictoriasNoSeQuitanElCupo()
    {
        partidos.clear();
        registrarVictorias(equipoA, equipoD, 4);
        registrarVictorias(equipoB, equipoD, 3);
        registrarVictorias(equipoC, equipoD, 3);

        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoA)), "El equipo con más victorias es elegible.");
        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoB)), "El primer equipo empatado es elegible.");
        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoC)), "El segundo equipo empatado también es elegible porque solo uno lo supera.");
    }

    @Test
    void testSoloCompitenEquiposDelMismoDeporteYCategoria() throws Exception
    {
        Equipo futbol1 = new Equipo("Futbol 1", 10, futbol, Categoria.MAYORES);
        Equipo futbol2 = new Equipo("Futbol 2", 10, futbol, Categoria.MAYORES);
        Equipo juvenil1 = new Equipo("Juvenil 1", 10, baloncesto, Categoria.SUB17);
        Equipo juvenil2 = new Equipo("Juvenil 2", 10, baloncesto, Categoria.SUB17);
        equipos.add(futbol1);
        equipos.add(futbol2);
        equipos.add(juvenil1);
        equipos.add(juvenil2);
        registrarVictorias(futbol1, futbol2, 10);
        registrarVictorias(juvenil1, juvenil2, 10);

        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoB)), "Los equipos de otro deporte o categoría no deben quitarle el cupo a nadie.");
        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoC)), "Los equipos de otro deporte o categoría no deben dejar entrar a quien no está entre los mejores de su grupo.");
    }

    @Test
    void testLasVictoriasEnPartidosExternosNoCuentan()
    {
        Equipo equipoE = new Equipo("Equipo E", 10, baloncesto, Categoria.MAYORES);
        equipos.add(equipoE);
        for(int i = 0; i < 10; i++)
        {
            partidos.add(new PartidoExterno(fechaPartido, 5, 1, "Rival", "Coliseo", equipoE));
        }

        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoE)), "Las victorias externas no cuentan para la clasificación preferencial.");
        assertTrue(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoB)), "Las victorias externas de otro equipo no deben quitarle el cupo a nadie.");
    }

    @Test
    void testLosEmpatesNoCuentanComoVictoria()
    {
        Equipo equipoE = new Equipo("Equipo E", 10, baloncesto, Categoria.MAYORES);
        equipos.add(equipoE);
        for(int i = 0; i < 5; i++)
        {
            partidos.add(new PartidoInterno(fechaPartido, equipoE, equipoA, 1, 1));
        }

        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoE)), "Un equipo que solo empata no tiene victorias.");
    }

    @Test
    void testLasDerrotasNoDanVictorias()
    {
        Equipo equipoE = new Equipo("Equipo E", 10, baloncesto, Categoria.MAYORES);
        equipos.add(equipoE);
        registrarVictorias(equipoA, equipoE, 5);

        assertFalse(esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoE)), "Un equipo que solo pierde no es elegible.");
    }

    @Test
    void testLaVictoriaComoVisitanteTambienCuenta() throws Exception
    {
        TorneoConjunto grande = crearTorneoConjunto(32);
        Equipo equipoE = new Equipo("Equipo E", 10, baloncesto, Categoria.MAYORES);
        equipos.add(equipoE);

        assertFalse(esElegible(grande, new InscripcionTorneoEquipo(equipoE)), "Antes de ganar, el equipo no tiene victorias.");

        partidos.add(new PartidoInterno(fechaPartido, equipoD, equipoE, 1, 2));

        assertTrue(esElegible(grande, new InscripcionTorneoEquipo(equipoE)), "Ganar como visitante cuenta como victoria.");
    }

    @Test
    void testJugadorIndividualNoEsElegibleEnUnTorneoDeConjunto()
    {
        assertFalse(esElegible(torneoConjunto, sencillos(jugador1)), "En un torneo de conjunto solo pueden ser elegibles los equipos.");
    }

    // ---------------------------------------------------------------------
    // Otros casos
    // ---------------------------------------------------------------------

    @Test
    void testTorneoDeOtroTipoNadieEsElegible() throws Exception
    {
        Torneo torneoDesconocido = new Torneo("Copa Rara", 8, fechaInicio, fechaLimite, new PremiacionRecreativa())
        {
            @Override
            protected void validarParticipante(InscripcionTorneo inscripcion) throws ReglaNegocioException
            {
            }

            @Override
            public String getDescripcion()
            {
                return "Torneo de otro tipo";
            }
        };

        assertFalse(esElegible(torneoDesconocido, sencillos(jugador1)), "En un tipo de torneo desconocido nadie es elegible.");
        assertFalse(esElegible(torneoDesconocido, new InscripcionTorneoEquipo(equipoA)), "En un tipo de torneo desconocido ningún equipo es elegible.");
    }

    @Test
    void testConsultarLaElegibilidadNoModificaLosDatos()
    {
        int rankingsAntes = rankings.size();
        int partidosAntes = partidos.size();
        int equiposAntes = equipos.size();
        double puntajeAntes = rankings.get(0).getPuntajeElo();

        esElegible(torneoSencillos, sencillos(jugador1));
        esElegible(torneoConjunto, new InscripcionTorneoEquipo(equipoA));

        assertEquals(rankingsAntes, rankings.size(), "No debe agregar ni quitar rankings.");
        assertEquals(partidosAntes, partidos.size(), "No debe agregar ni quitar partidos.");
        assertEquals(equiposAntes, equipos.size(), "No debe agregar ni quitar equipos.");
        assertEquals(puntajeAntes, rankings.get(0).getPuntajeElo(), 0.001, "No debe cambiar los puntajes.");
    }
}
