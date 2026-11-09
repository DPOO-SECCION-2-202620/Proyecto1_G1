package Consola;

import java.text.Normalizer;
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
		String clave = Normalizer.normalize(nombreDeporte.trim().toLowerCase(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		return POSICIONES.getOrDefault(clave, List.of());
	}
}