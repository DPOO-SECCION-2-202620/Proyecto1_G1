package Consola;

import java.util.List;
import java.util.Map;

public final class PosicionesDeporte {

	private static final Map<String, List<String>> POSICIONES = Map.of(
			"futbol", List.of("Portero", "Defensa", "Mediocampista", "Delantero"),
			"baloncesto", List.of("Base", "Escolta", "Alero", "Ala-pívot", "Pívot"),
			"voleibol", List.of("Armador", "Central", "Opuesto", "Receptor", "Líbero"));

	private PosicionesDeporte() {
	}

	public static List<String> de(String nombreDeporte) {
		if (nombreDeporte == null) {
			return List.of();
		}
		String clave = nombreDeporte.trim().toLowerCase();
		if (POSICIONES.containsKey(clave)) {
			return POSICIONES.get(clave);
		}
		return List.of();
	}
}