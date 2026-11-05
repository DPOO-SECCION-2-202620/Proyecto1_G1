package Consola;

import java.util.List;

import DeportesConjunto_Competencias.Equipo;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Usuarios.Socio;

public class OpcionInscribirseEnEquipo implements OpcionMenu {

	private final ClubSeneca club;
	private final Socio socio;
	private final LectorConsola lector;

	public OpcionInscribirseEnEquipo(ClubSeneca club, Socio socio, LectorConsola lector) {
		this.club = club;
		this.socio = socio;
		this.lector = lector;
	}

	@Override
	public String getTitulo() {
		return "Inscribirme en un equipo";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<Equipo> equipos = club.getEquipos();
		if (equipos.isEmpty()) {
			System.out.println("No hay equipos registrados.");
			return;
		}

		System.out.println("\nEquipos:");
		for (int i = 0; i < equipos.size(); i++) {
			Equipo equipo = equipos.get(i);
			int cuposLibres = equipo.getCupoMaximo() - equipo.getInscripciones().size();
			System.out.println((i + 1) + ". " + equipo.getNombre() + " (" + equipo.getDeporte().getNombre() + ", "
					+ equipo.getCategoria() + ") - cupos libres: " + cuposLibres);
		}

		Equipo equipo = equipos.get(lector.leerIndice("Elija el equipo", equipos.size()));
		String posicion = elegirPosicion(equipo);
		int dorsal = lector.leerEntero("Número de camiseta", 1, 99);

		club.inscribirJugador(socio, equipo, posicion, dorsal);

		int cuposLibres = equipo.getCupoMaximo() - equipo.getInscripciones().size();
		System.out.println("Inscripción registrada: " + socio.getNombre() + " en " + equipo.getNombre() + " como "
				+ posicion + ", camiseta " + dorsal + ".");
		System.out.println("Cupos libres que quedan: " + cuposLibres);
		
	}
	
	private String elegirPosicion(Equipo equipo) {
		List<String> posiciones = PosicionesDeporte.de(equipo.getDeporte().getNombre());
		if (posiciones.isEmpty()) {
			return lector.leerTexto("Posición");
		}
		System.out.println("Posiciones disponibles:");
		for (int i = 0; i < posiciones.size(); i++) {
			System.out.println((i + 1) + ". " + posiciones.get(i));
		}
		return posiciones.get(lector.leerIndice("Elija la posición", posiciones.size()));
	}
}