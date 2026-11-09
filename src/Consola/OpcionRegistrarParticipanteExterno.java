package Consola;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.NivelJugador;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Usuarios.ParticipanteExterno;

public class OpcionRegistrarParticipanteExterno implements OpcionMenu {

	private final ClubSeneca club;
	private final LectorConsola lector;

	public OpcionRegistrarParticipanteExterno(ClubSeneca club, LectorConsola lector) {
		this.club = club;
		this.lector = lector;
	}

	@Override
	public String getTitulo() {
		return "Registrar un estudiante o una persona externa";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		List<DisciplinaIndividual> disciplinas = club.getDisciplinas();
		if (disciplinas.isEmpty()) {
			System.out.println("No hay disciplinas individuales registradas en el club.");
			return;
		}
		int tipo = lector.leerEntero("Tipo (1 = estudiante de la universidad, 2 = persona externa)", 1, 2);
		String nombre = lector.leerTexto("Nombre completo");
		int edad = lector.leerEntero("Edad", ParticipanteExterno.EDAD_MINIMA, ParticipanteExterno.EDAD_MAXIMA);
		String login = lector.leerTexto("Usuario (login)");
		while (club.existeLogin(login)) {
			System.out.println("  El login " + login + " ya se encuentra ocupado por otro usuario.");
			login = lector.leerTexto("Usuario (login)");
		}
		String password = lector.leerTexto("Contraseña");

		Map<DisciplinaIndividual, NivelJugador> niveles = new LinkedHashMap<>();
		do {
			DisciplinaIndividual disciplina = elegirDisciplina(disciplinas);
			NivelJugador nivel = elegirNivel();
			niveles.put(disciplina, nivel);
		} while (niveles.size() < disciplinas.size() && lector.confirmar("¿Registrar el nivel en otra disciplina?"));

		ParticipanteExterno participante = club.registrarParticipanteExterno(edad, nombre, login, password, tipo == 1,
				niveles);
		System.out.println("Registro exitoso (" + (participante.esEstudiante() ? "estudiante" : "persona externa")
				+ "): " + participante.getNombre() + " (usuario " + participante.getLogin()
				+ "). Ya puede iniciar sesión en la consola de jugadores.");
	}

	private DisciplinaIndividual elegirDisciplina(List<DisciplinaIndividual> disciplinas) {
		System.out.println("Disciplinas individuales:");
		for (int i = 0; i < disciplinas.size(); i++) {
			System.out.println((i + 1) + ". " + disciplinas.get(i).getNombre());
		}
		return disciplinas.get(lector.leerIndice("Elija la disciplina", disciplinas.size()));
	}

	private NivelJugador elegirNivel() {
		NivelJugador[] niveles = NivelJugador.values();
		System.out.println("Niveles:");
		for (int i = 0; i < niveles.length; i++) {
			System.out.println((i + 1) + ". " + niveles[i]);
		}
		return niveles[lector.leerIndice("Elija el nivel", niveles.length)];
	}
}
