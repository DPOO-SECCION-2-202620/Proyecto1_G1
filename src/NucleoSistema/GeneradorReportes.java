package NucleoSistema;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import DeportesIndividuales.RankingJugadorDisciplina;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Venta;
import Entrenamiento.AsistenciaEntrenamiento;
import PartidosEquipo_Estadisticas.Partido;
import Usuarios.Socio;

public class GeneradorReportes {

	public GeneradorReportes() {
		super();
	}

	public Map<Rubro, Double> reporteVentas(List<Venta> ventas) {
		Map<Rubro, Double> totalesPorRubro = new HashMap<>();
		
		if (ventas == null) {
			return totalesPorRubro;
		}

		for (Venta venta : ventas) {
			if (venta != null && venta.getRubro() != null) {
				Rubro rubro = venta.getRubro();
				double totalVenta = venta.getTotal();
				
				double acumulado = totalesPorRubro.getOrDefault(rubro, 0.0);
				totalesPorRubro.put(rubro, acumulado + totalVenta);
			}
		}

		return totalesPorRubro;
	}
	
	public Map<Socio, Integer> reporteAsistencias(List<AsistenciaEntrenamiento> asistencias) {
		Map<Socio, Integer> asistenciasPorSocio = new HashMap<>();

		if (asistencias == null) {
			return asistenciasPorSocio;
		}

		for (AsistenciaEntrenamiento asistencia : asistencias) {
			if (asistencia != null && asistencia.getJugador() != null) {
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
