package NucleoSistema_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.RankingJugadorDisciplina;
import DeportesIndividuales.Tenis;
import Entrenamiento.AsistenciaEntrenamiento;
import Entrenamiento.EstadoAsistencia;
import NucleoSistema.GeneradorReportes;
import NucleoSistema.Periodo;
import PartidosEquipo_Estadisticas.Partido;
import PartidosEquipo_Estadisticas.PartidoExterno;
import PartidosEquipo_Estadisticas.PartidoInterno;
import Tienda_Cafeteria_Inventario.LineaVenta;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Socio;

public class GeneradorReportesTest
{
    private GeneradorReportes generador;

    private Socio ana;
    private Socio pedro;

    private Venta ventaTiendaLunes;
    private Venta ventaTiendaMiercoles;
    private Venta ventaCafeteriaMiercoles;
    private Venta ventaTiendaLunesSiguiente;
    private Venta ventaTiendaOctubre;
    private List<Venta> ventas;

    @BeforeEach
    void setUp() throws Exception
    {
        generador = new GeneradorReportes();

        ana = new Socio(20, "Ana Torres", "ana", "1");
        pedro = new Socio(25, "Pedro Gil", "pgil", "2");

        List<LineaVenta> sinLineas = new ArrayList<LineaVenta>();
        ventaTiendaLunes = new Venta(LocalDate.of(2026, 11, 2), 0, 1000, 0, 20, Rubro.TIENDA, ana, sinLineas, 0.19);
        ventaTiendaMiercoles = new Venta(LocalDate.of(2026, 11, 4), 0, 2000, 0, 40, Rubro.TIENDA, ana, sinLineas, 0.19);
        ventaCafeteriaMiercoles = new Venta(LocalDate.of(2026, 11, 4), 0, 500, 0, 10, Rubro.CAFETERIA, pedro, sinLineas, 0.08);
        ventaTiendaLunesSiguiente = new Venta(LocalDate.of(2026, 11, 9), 0, 4000, 0, 80, Rubro.TIENDA, pedro, sinLineas, 0.19);
        ventaTiendaOctubre = new Venta(LocalDate.of(2026, 10, 30), 0, 8000, 0, 160, Rubro.TIENDA, pedro, sinLineas, 0.19);

        ventas = new ArrayList<Venta>();
        ventas.add(ventaTiendaLunes);
        ventas.add(ventaTiendaMiercoles);
        ventas.add(ventaCafeteriaMiercoles);
        ventas.add(ventaTiendaLunesSiguiente);
        ventas.add(ventaTiendaOctubre);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testReporteVentasDiarioTodosLosRubros()
    {
        Map<LocalDate, Map<Rubro, Double>> reporte = generador.reporteVentas(ventas, null, Periodo.DIARIO);

        assertEquals(4, reporte.size(), "Debe haber cuatro días con ventas.");
        assertEquals(1000, reporte.get(LocalDate.of(2026, 11, 2)).get(Rubro.TIENDA), 0.001, "El total de tienda del 2 de noviembre no es el esperado.");
        assertEquals(2000, reporte.get(LocalDate.of(2026, 11, 4)).get(Rubro.TIENDA), 0.001, "El total de tienda del 4 de noviembre no es el esperado.");
        assertEquals(500, reporte.get(LocalDate.of(2026, 11, 4)).get(Rubro.CAFETERIA), 0.001, "El total de cafetería del 4 de noviembre no es el esperado.");
    }

    @Test
    void testReporteVentasOrdenadoPorFecha()
    {
        Map<LocalDate, Map<Rubro, Double>> reporte = generador.reporteVentas(ventas, null, Periodo.DIARIO);

        List<LocalDate> fechas = new ArrayList<LocalDate>(reporte.keySet());

        assertEquals(LocalDate.of(2026, 10, 30), fechas.get(0), "La primera fecha debe ser la más antigua.");
        assertEquals(LocalDate.of(2026, 11, 9), fechas.get(3), "La última fecha debe ser la más reciente.");
    }

    @Test
    void testReporteVentasFiltraPorRubro()
    {
        Map<LocalDate, Map<Rubro, Double>> reporte = generador.reporteVentas(ventas, Rubro.CAFETERIA, Periodo.DIARIO);

        assertEquals(1, reporte.size(), "Solo debe haber un día con ventas de cafetería.");
        assertEquals(1, reporte.get(LocalDate.of(2026, 11, 4)).size(), "Solo debe haber el rubro de cafetería.");
        assertEquals(500, reporte.get(LocalDate.of(2026, 11, 4)).get(Rubro.CAFETERIA), 0.001, "El total de cafetería no es el esperado.");
    }

    @Test
    void testReporteVentasSemanal()
    {
        Map<LocalDate, Map<Rubro, Double>> reporte = generador.reporteVentas(ventas, Rubro.TIENDA, Periodo.SEMANAL);

        assertEquals(3, reporte.size(), "Debe haber tres semanas con ventas.");
        assertEquals(3000, reporte.get(LocalDate.of(2026, 11, 2)).get(Rubro.TIENDA), 0.001, "La semana del 2 de noviembre debe sumar las dos ventas.");
        assertEquals(4000, reporte.get(LocalDate.of(2026, 11, 9)).get(Rubro.TIENDA), 0.001, "La semana del 9 de noviembre no tiene el total esperado.");
        assertEquals(8000, reporte.get(LocalDate.of(2026, 10, 26)).get(Rubro.TIENDA), 0.001, "El viernes 30 de octubre pertenece a la semana del lunes 26.");
    }

    @Test
    void testReporteVentasMensual()
    {
        Map<LocalDate, Map<Rubro, Double>> reporte = generador.reporteVentas(ventas, null, Periodo.MENSUAL);

        assertEquals(2, reporte.size(), "Debe haber dos meses con ventas.");
        assertEquals(7000, reporte.get(LocalDate.of(2026, 11, 1)).get(Rubro.TIENDA), 0.001, "El total de tienda de noviembre no es el esperado.");
        assertEquals(500, reporte.get(LocalDate.of(2026, 11, 1)).get(Rubro.CAFETERIA), 0.001, "El total de cafetería de noviembre no es el esperado.");
        assertEquals(8000, reporte.get(LocalDate.of(2026, 10, 1)).get(Rubro.TIENDA), 0.001, "El total de tienda de octubre no es el esperado.");
    }

    @Test
    void testReporteVentasSinVentas()
    {
        assertTrue(generador.reporteVentas(new ArrayList<Venta>(), null, Periodo.DIARIO).isEmpty(), "Sin ventas el reporte debe estar vacío.");
    }

    @Test
    void testReporteVentasParametrosNulos()
    {
        assertTrue(generador.reporteVentas(null, null, Periodo.DIARIO).isEmpty(), "Con ventas nulas el reporte debe estar vacío.");
        assertTrue(generador.reporteVentas(ventas, null, null).isEmpty(), "Con periodo nulo el reporte debe estar vacío.");
    }

    @Test
    void testReporteVentasIgnoraVentasNulas()
    {
        ventas.add(null);

        Map<LocalDate, Map<Rubro, Double>> reporte = generador.reporteVentas(ventas, null, Periodo.DIARIO);

        assertEquals(4, reporte.size(), "Las ventas nulas no deben afectar el reporte.");
    }

    @Test
    void testReporteVentasRubroSinVentas()
    {
        assertTrue(generador.reporteVentas(ventas, Rubro.MENSUALIDAD, Periodo.DIARIO).isEmpty(), "Un rubro sin ventas debe dar un reporte vacío.");
    }

    @Test
    void testReporteAsistencias()
    {
        List<AsistenciaEntrenamiento> asistencias = new ArrayList<AsistenciaEntrenamiento>();
        asistencias.add(new AsistenciaEntrenamiento(0, null, EstadoAsistencia.ASISTIO, ana));
        asistencias.add(new AsistenciaEntrenamiento(0, null, EstadoAsistencia.LLEGOTARDE, ana));
        asistencias.add(new AsistenciaEntrenamiento(0, null, EstadoAsistencia.FALTO, ana));
        asistencias.add(new AsistenciaEntrenamiento(0, null, EstadoAsistencia.FALTO, pedro));
        asistencias.add(new AsistenciaEntrenamiento(0, null, EstadoAsistencia.ASISTIO, pedro));

        Map<Socio, Integer> reporte = generador.reporteAsistencias(asistencias);

        assertEquals(2, reporte.size(), "Debe haber dos socios en el reporte.");
        assertEquals(2, reporte.get(ana), "Ana asistió dos veces.");
        assertEquals(1, reporte.get(pedro), "Pedro asistió una vez.");
    }

    @Test
    void testReporteAsistenciasSoloFaltas()
    {
        List<AsistenciaEntrenamiento> asistencias = new ArrayList<AsistenciaEntrenamiento>();
        asistencias.add(new AsistenciaEntrenamiento(0, null, EstadoAsistencia.FALTO, ana));

        assertTrue(generador.reporteAsistencias(asistencias).isEmpty(), "Quien solo faltó no debe aparecer en el reporte.");
    }

    @Test
    void testReporteAsistenciasNulasOVacias()
    {
        List<AsistenciaEntrenamiento> asistencias = new ArrayList<AsistenciaEntrenamiento>();
        asistencias.add(null);

        assertTrue(generador.reporteAsistencias(null).isEmpty(), "Con lista nula el reporte debe estar vacío.");
        assertTrue(generador.reporteAsistencias(new ArrayList<AsistenciaEntrenamiento>()).isEmpty(), "Con lista vacía el reporte debe estar vacío.");
        assertTrue(generador.reporteAsistencias(asistencias).isEmpty(), "Una asistencia nula no debe contarse.");
    }

    @Test
    void testReporteResultados()
    {
        Equipo local = new Equipo("Local", 8, new Baloncesto(), Categoria.MAYORES);
        Equipo visitante = new Equipo("Visitante", 8, new Baloncesto(), Categoria.MAYORES);

        List<Partido> partidos = new ArrayList<Partido>();
        partidos.add(new PartidoInterno(LocalDate.of(2026, 11, 10), local, visitante, 80, 75));
        partidos.add(new PartidoExterno(LocalDate.of(2026, 11, 11), 60, 61, "Rival", "Coliseo", local));
        partidos.add(null);

        List<String> resultados = generador.reporteResultados(partidos);

        assertEquals(2, resultados.size(), "Debe haber dos resultados.");
        assertEquals("2026-11-10: Local 80 - 75 Visitante", resultados.get(0), "El primer resultado no es el esperado.");
        assertEquals("2026-11-11: Local 60 - 61 Rival (Coliseo)", resultados.get(1), "El segundo resultado no es el esperado.");
    }

    @Test
    void testReporteResultadosNuloOVacio()
    {
        assertTrue(generador.reporteResultados(null).isEmpty(), "Con lista nula no debe haber resultados.");
        assertTrue(generador.reporteResultados(new ArrayList<Partido>()).isEmpty(), "Con lista vacía no debe haber resultados.");
    }

    @Test
    void testReporteRankings()
    {
        Tenis tenis = new Tenis(2, "Tenis");
        RankingJugadorDisciplina rankingAna = new RankingJugadorDisciplina(ana, tenis);
        rankingAna.setPuntajeElo(1100);
        RankingJugadorDisciplina rankingPedro = new RankingJugadorDisciplina(pedro, tenis);
        rankingPedro.setPuntajeElo(1300);
        RankingJugadorDisciplina rankingLuis = new RankingJugadorDisciplina(new Socio(30, "Luis", "lmora", "3"), tenis);
        rankingLuis.setPuntajeElo(900);

        List<RankingJugadorDisciplina> rankings = new ArrayList<RankingJugadorDisciplina>();
        rankings.add(rankingAna);
        rankings.add(rankingLuis);
        rankings.add(rankingPedro);

        List<RankingJugadorDisciplina> ordenados = generador.reporteRankings(rankings);

        assertEquals(3, ordenados.size(), "Debe haber tres rankings.");
        assertSame(rankingPedro, ordenados.get(0), "El primero debe ser el de mayor puntaje.");
        assertSame(rankingAna, ordenados.get(1), "El segundo debe ser el de puntaje intermedio.");
        assertSame(rankingLuis, ordenados.get(2), "El último debe ser el de menor puntaje.");
        assertSame(rankingAna, rankings.get(0), "La lista original no debe modificarse.");
    }

    @Test
    void testReporteRankingsNuloOVacio()
    {
        assertTrue(generador.reporteRankings(null).isEmpty(), "Con lista nula no debe haber rankings.");
        assertTrue(generador.reporteRankings(new ArrayList<RankingJugadorDisciplina>()).isEmpty(), "Con lista vacía no debe haber rankings.");
    }
}
