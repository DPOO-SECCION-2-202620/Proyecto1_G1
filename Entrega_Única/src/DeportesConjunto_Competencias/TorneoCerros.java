package DeportesConjunto_Competencias;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import PartidosEquipo_Estadisticas.PartidoExterno;

public class TorneoCerros extends Competencia{

	private Map <Equipo, Integer> tablaPosiciones;
	
	public TorneoCerros(String nombre, int anio, int nominaMinima, Categoria categoria, DeporteConjunto deporte) {
		super(nombre, "Grupo Deportivo Universitario Los Cerros" , anio, "Liga", nominaMinima, categoria, deporte);
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

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "TorneoCerros");
		JSONArray jsonTabla = new JSONArray();
		for (Map.Entry<Equipo, Integer> fila : tablaPosiciones.entrySet()) {
			JSONObject jsonFila = new JSONObject();
			jsonFila.put("equipo", fila.getKey().getNombre());
			jsonFila.put("puntos", fila.getValue());
			jsonTabla.put(jsonFila);
		}
		json.put("tablaPosiciones", jsonTabla);
		return json;
	}

	public static TorneoCerros fromJSON(JSONObject json, Categoria categoria,
			DeporteConjunto deporte, Map<String, Equipo> mapaEquipos) {

		TorneoCerros torneo = new TorneoCerros(
				json.getString("nombre"),
				json.getInt("anio"),
				json.getInt("nominaMinima"),
				categoria,
				deporte);

		JSONArray jsonTabla = json.optJSONArray("tablaPosiciones");
		if (jsonTabla != null) {
			for (int indice = 0; indice < jsonTabla.length(); indice++) {
				JSONObject jsonFila = jsonTabla.getJSONObject(indice);
				String nombreEquipo = jsonFila.getString("equipo");
				Equipo equipo = mapaEquipos.get(nombreEquipo);
				if (equipo == null) {
					throw new IllegalArgumentException("Equipo desconocido en la tabla: " + nombreEquipo);
				}
				torneo.tablaPosiciones.put(equipo, jsonFila.getInt("puntos"));
			}
		}
		return torneo;
	}
}
