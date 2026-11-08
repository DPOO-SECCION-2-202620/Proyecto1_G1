package NucleoSistema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.RankingJugadorDisciplina;
import PartidosEquipo_Estadisticas.Partido;
import PartidosEquipo_Estadisticas.PartidoInterno;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.InscripcionTorneoIndividual;
import TorneosInternos.JugadorTorneo;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;

public class ClasificacionPreferencial {

	private static final double PUNTAJE_INICIAL = 1000;

	public boolean esElegible(Torneo torneo, InscripcionTorneo inscripcion,
			List<RankingJugadorDisciplina> rankings, List<Partido> partidos, List<Equipo> equipos) {
		int cupos = torneo.getCuposPreferenciales();
		if (torneo instanceof TorneoIndividual && inscripcion instanceof InscripcionTorneoIndividual) {
			return jugadoresEnLaElite((TorneoIndividual) torneo, inscripcion, rankings, cupos);
		}
		if (torneo instanceof TorneoConjunto && inscripcion instanceof InscripcionTorneoEquipo) {
			return equipoEnLaElite((TorneoConjunto) torneo, (InscripcionTorneoEquipo) inscripcion, partidos, equipos,
					cupos);
		}
		return false;
	}

	private boolean jugadoresEnLaElite(TorneoIndividual torneo, InscripcionTorneo inscripcion,
			List<RankingJugadorDisciplina> rankings, int cupos) {
		List<RankingJugadorDisciplina> candidatos = new ArrayList<>();
		for (RankingJugadorDisciplina ranking : rankings) {
			boolean mismaDisciplina = ranking.getDisciplina() == torneo.getDisciplina();
			if (mismaDisciplina && ranking.getJugador().getNivelEn(torneo.getDisciplina()) == torneo.getNivel()) {
				candidatos.add(ranking);
			}
		}
		candidatos.sort(Comparator.comparingDouble(RankingJugadorDisciplina::getPuntajeElo).reversed());

		List<String> elite = new ArrayList<>();
		for (int i = 0; i < Math.min(cupos, candidatos.size()); i++) {
			if (candidatos.get(i).getPuntajeElo() > PUNTAJE_INICIAL) {
				elite.add(candidatos.get(i).getJugador().getLogin());
			}
		}
		for (JugadorTorneo jugador : inscripcion.getJugadores()) {
			if (!elite.contains(jugador.getLogin())) {
				return false;
			}
		}
		return true;
	}

	private boolean equipoEnLaElite(TorneoConjunto torneo, InscripcionTorneoEquipo inscripcion,
			List<Partido> partidos, List<Equipo> equipos, int cupos) {
		Map<Equipo, Integer> victorias = new LinkedHashMap<>();
		for (Equipo equipo : equipos) {
			boolean mismoDeporte = equipo.getDeporte().getNombre().equals(torneo.getDeporte().getNombre());
			if (mismoDeporte && equipo.getCategoria() == torneo.getCategoria()) {
				victorias.put(equipo, 0);
			}
		}
		for (Partido partido : partidos) {
			if (partido instanceof PartidoInterno) {
				Equipo ganador = ((PartidoInterno) partido).getGanador();
				if (ganador != null && victorias.containsKey(ganador)) {
					victorias.merge(ganador, 1, Integer::sum);
				}
			}
		}
		List<Equipo> ordenados = new ArrayList<>(victorias.keySet());
		ordenados.sort((a, b) -> victorias.get(b) - victorias.get(a));

		Equipo propio = inscripcion.getEquipo();
		for (int i = 0; i < Math.min(cupos, ordenados.size()); i++) {
			if (ordenados.get(i) == propio && victorias.get(propio) > 0) {
				return true;
			}
		}
		return false;
	}
}
