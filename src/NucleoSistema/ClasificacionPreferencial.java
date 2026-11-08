package NucleoSistema;
 
import java.util.List;
 
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.RankingJugadorDisciplina;
import PartidosEquipo_Estadisticas.Partido;
import PartidosEquipo_Estadisticas.PartidoInterno;
import TorneosInternos.InscripcionTorneo;
import TorneosInternos.InscripcionTorneoEquipo;
import TorneosInternos.JugadorTorneo;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;
 
public class ClasificacionPreferencial {
 
	private static final double PUNTAJE_INICIAL = 1000;
 
	public boolean esElegible(Torneo torneo, InscripcionTorneo inscripcion,
			List<RankingJugadorDisciplina> rankings, List<Partido> partidos, List<Equipo> equipos) {
		int cupos = torneo.getCuposPreferenciales();
 
		if (torneo instanceof TorneoIndividual) {
			for (JugadorTorneo jugador : inscripcion.getJugadores()) {
				if (!jugadorEnLaElite(jugador, (TorneoIndividual) torneo, rankings, cupos)) {
					return false;
				}
			}
			return true;
		}
		if (torneo instanceof TorneoConjunto && inscripcion instanceof InscripcionTorneoEquipo) {
			Equipo equipo = ((InscripcionTorneoEquipo) inscripcion).getEquipo();
			return equipoEnLaElite(equipo, (TorneoConjunto) torneo, partidos, equipos, cupos);
		}
		return false;
	}
 
	private boolean jugadorEnLaElite(JugadorTorneo jugador, TorneoIndividual torneo,
			List<RankingJugadorDisciplina> rankings, int cupos) {
		double miPuntaje = 0;
		for (RankingJugadorDisciplina ranking : rankings) {
			if (ranking.getDisciplina() == torneo.getDisciplina()
					&& ranking.getJugador().getLogin().equals(jugador.getLogin())) {
				miPuntaje = ranking.getPuntajeElo();
			}
		}
		if (miPuntaje <= PUNTAJE_INICIAL) {
			return false;
		}
		int mejoresQueYo = 0;
		for (RankingJugadorDisciplina ranking : rankings) {
			boolean mismaDisciplina = ranking.getDisciplina() == torneo.getDisciplina();
			boolean mismoNivel = ranking.getJugador().getNivelEn(torneo.getDisciplina()) == torneo.getNivel();
			if (mismaDisciplina && mismoNivel && ranking.getPuntajeElo() > miPuntaje) {
				mejoresQueYo++;
			}
		}
		return mejoresQueYo < cupos;
	}
 
	private boolean equipoEnLaElite(Equipo equipo, TorneoConjunto torneo, List<Partido> partidos,
			List<Equipo> equipos, int cupos) {
		int misVictorias = contarVictorias(equipo, partidos);
		if (misVictorias == 0) {
			return false;
		}
		int mejoresQueYo = 0;
		for (Equipo otro : equipos) {
			boolean mismoDeporte = otro.getDeporte().getNombre().equals(torneo.getDeporte().getNombre());
			boolean mismaCategoria = otro.getCategoria() == torneo.getCategoria();
			if (otro != equipo && mismoDeporte && mismaCategoria && contarVictorias(otro, partidos) > misVictorias) {
				mejoresQueYo++;
			}
		}
		return mejoresQueYo < cupos;
	}
 
	private int contarVictorias(Equipo equipo, List<Partido> partidos) {
		int victorias = 0;
		for (Partido partido : partidos) {
			if (partido instanceof PartidoInterno && ((PartidoInterno) partido).getGanador() == equipo) {
				victorias++;
			}
		}
		return victorias;
	}
}