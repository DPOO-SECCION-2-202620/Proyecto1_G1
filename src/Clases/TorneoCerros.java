package Clases;

import java.util.LinkedHashMap;
import java.util.Map;

public class TorneoCerros extends Competencia{

	private Map <Equipo, Integer> tablaPosiciones;
	public TorneoCerros(String nombre, String entidadOrganizadora, int anio, String formatoJuego, int nominaMinima,
			Categoria categoria, DeporteConjunto deporte) {
		super("Torneo de los Cerros", "Universidades de Bogotá", anio, "Liga", nominaMinima, categoria, deporte);
		this.tablaPosiciones = new LinkedHashMap<>();
	}

	public Map<Equipo, Integer> getTablaPosiciones() {
		return tablaPosiciones;
	}
	
	public void actualizarTabla(PartidoExterno partido) {
		Equipo equipo = partido.getEquipo();
		int puntos = getDeporte().calcularPuntos(partido.getMarcadorPropio(), partido.getMarcadorRival());
		tablaPosiciones.put(equipo, tablaPosiciones.getOrDefault(equipo,0)+puntos);
	}

	public Equipo getLider() {
		Equipo lider = null;
		for (Equipo equipo : tablaPosiciones.keySet()) {
			if (lider == null || tablaPosiciones.get(equipo) > tablaPosiciones.get(lider)) {
				lider = equipo;
			}
		}
		return lider;
	}
	
	@Override
	public void registrarPartido(PartidoExterno partido) {
		agregarALista(partido);
		actualizarTabla(partido);
	}

}
