package NucleoSistema;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import DeportesIndividuales.RankingJugadorDisciplina;
import Entrenamiento.AsistenciaEntrenamiento;
import Entrenamiento.EstadoAsistencia;
import PartidosEquipo_Estadisticas.Partido;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Socio;

public class GeneradorReportes {

	public GeneradorReportes() {
		super();
	}

	public Map<LocalDate, Map<Rubro, Double>> reporteVentas(List<Venta> ventas, Rubro rubro, Periodo periodo) {
		Map<LocalDate, Map<Rubro, Double>> ventasDiscriminadas = new TreeMap<>();

		if (ventas == null || periodo == null) {
			return ventasDiscriminadas;
		}

		for (Venta venta : ventas) {
			if (venta == null || venta.getRubro() == null) {
				continue;
			}
			if (rubro != null && venta.getRubro() != rubro) {
				continue;
			}

			if (venta.getFecha() == null) {
				continue;
			}
			LocalDate fecha = venta.getFecha();

			LocalDate clave;
			switch (periodo) {
				case SEMANAL:
					clave = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
					break;
				case MENSUAL:
					clave = fecha.withDayOfMonth(1);
					break;
				default:
					clave = fecha;
					break;
			}

			Map<Rubro, Double> totalesPorRubro = ventasDiscriminadas.get(clave);
			if (totalesPorRubro == null) {
				totalesPorRubro = new HashMap<>();
				ventasDiscriminadas.put(clave, totalesPorRubro);
			}
			double acumulado = totalesPorRubro.getOrDefault(venta.getRubro(), 0.0);
			totalesPorRubro.put(venta.getRubro(), acumulado + venta.getTotal());
		}

		return ventasDiscriminadas;
	}

	public Map<Socio, Integer> reporteAsistencias(List<AsistenciaEntrenamiento> asistencias) {
		Map<Socio, Integer> asistenciasPorSocio = new HashMap<>();

		if (asistencias == null) {
			return asistenciasPorSocio;
		}

		for (AsistenciaEntrenamiento asistencia : asistencias) {
			if (asistencia != null && asistencia.getJugador() != null
					&& asistencia.getEstado() != EstadoAsistencia.FALTO) {
				Socio socio = asistencia.getJugador();

				int totalAsistencias = asistenciasPorSocio.getOrDefault(socio, 0);
				asistenciasPorSocio.put(socio, totalAsistencias + 1);
			}
		}

		return asistenciasPorSocio;
	}

	public List<String> reporteResultados(List<Partido> partidos) {
		List<String> resultadosFormateados = new ArrayList<>();

		if (partidos == null) {
			return resultadosFormateados;
		}

		for (Partido partido : partidos) {
			if (partido != null) {
				resultadosFormateados.add(partido.toString());
			}
		}

		return resultadosFormateados;
	}

	public List<RankingJugadorDisciplina> reporteRankings(List<RankingJugadorDisciplina> rankingsGlobales) {
		List<RankingJugadorDisciplina> listaOrdenada = new ArrayList<>();

		if (rankingsGlobales == null) {
			return listaOrdenada;
		}

		listaOrdenada.addAll(rankingsGlobales);

		listaOrdenada.sort((r1, r2) -> {
			if (r1 == null || r2 == null) return 0;
			return Double.compare(r2.getPuntajeElo(), r1.getPuntajeElo());
		});

		return listaOrdenada;
	}

}