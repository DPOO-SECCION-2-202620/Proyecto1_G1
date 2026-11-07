package Consola;

import java.time.LocalDate;
import java.util.List;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import TorneosInternos.Premiacion;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.Torneo;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;

public class OpcionCrearTorneo extends OpcionTorneo {

	private static final String[] DEPORTES_CONJUNTO = { "Futbol", "Baloncesto", "Voleibol" };

	public OpcionCrearTorneo(ClubSeneca club, LectorConsola lector) {
		super(club, lector);
	}

	@Override
	public String getTitulo() {
		return "Crear un torneo";
	}

	@Override
	public void ejecutar() throws ReglaNegocioException {
		String nombre = lector.leerTexto("Nombre del torneo");
		int tipo = lector.leerEntero("Tipo de deporte (1 = de conjunto, 2 = individual, 0 = cancelar)", 0, 2);
		if (tipo == 0) {
			System.out.println("Operación cancelada.");
			return;
		}

		DeporteConjunto deporte = null;
		Categoria categoria = null;
		DisciplinaIndividual disciplina = null;
		NivelJugador nivel = null;
		Modalidad modalidad = null;
		if (tipo == 1) {
			deporte = elegirDeporte();
			categoria = elegirOpcion("Categoría", Categoria.values());
		} else {
			List<DisciplinaIndividual> disciplinas = club.getDisciplinas();
			if (disciplinas.isEmpty()) {
				System.out.println("No hay disciplinas individuales registradas en el club.");
				return;
			}
			disciplina = elegirDisciplina(disciplinas);
			nivel = elegirOpcion("Nivel", NivelJugador.values());
			modalidad = elegirOpcion("Modalidad", Modalidad.values());
		}

		int cupos = lector.leerEntero("Número de cupos", Torneo.CUPOS_MINIMOS, Torneo.CUPOS_MAXIMOS);
		int clase = lector.leerEntero("Clase de torneo (1 = recreativo, 2 = competitivo)", 1, 2);
		Premiacion premiacion;
		if (clase == 2) {
			double tarifa = lector.leerDecimal("Tarifa de inscripción por jugador (COP)", 1, 10000000);
			premiacion = new PremiacionCompetitiva(tarifa);
		} else {
			premiacion = new PremiacionRecreativa();
		}
		LocalDate hoy = LocalDate.now();
		LocalDate inicio = lector.leerFecha("Fecha de inicio", hoy.plusDays(1), hoy.plusYears(2));
		LocalDate limite = lector.leerFecha("Fecha límite para los cupos preferenciales", hoy, inicio);

		Torneo torneo;
		if (tipo == 1) {
			torneo = new TorneoConjunto(nombre, cupos, inicio, limite, premiacion, deporte, categoria);
		} else {
			torneo = new TorneoIndividual(nombre, cupos, inicio, limite, premiacion, disciplina, nivel, modalidad);
		}
		club.agregarTorneo(torneo);

		System.out.println("Torneo creado: " + FormatoTorneo.linea(torneo));
		System.out.println("Cupos preferenciales reservados: " + torneo.getCuposPreferenciales());
		System.out.println("Para iniciarlo se necesitan exactamente 2, 4, 8, 16 o 32 inscritos.");
	}

	private DeporteConjunto elegirDeporte() {
		System.out.println("Deportes de conjunto:");
		for (int i = 0; i < DEPORTES_CONJUNTO.length; i++) {
			System.out.println((i + 1) + ". " + DEPORTES_CONJUNTO[i]);
		}
		String nombre = DEPORTES_CONJUNTO[lector.leerIndice("Elija el deporte", DEPORTES_CONJUNTO.length)];
		return club.obtenerDeporte(nombre);
	}

	private DisciplinaIndividual elegirDisciplina(List<DisciplinaIndividual> disciplinas) {
		System.out.println("Disciplinas individuales:");
		for (int i = 0; i < disciplinas.size(); i++) {
			System.out.println((i + 1) + ". " + disciplinas.get(i).getNombre());
		}
		return disciplinas.get(lector.leerIndice("Elija la disciplina", disciplinas.size()));
	}

	private <T> T elegirOpcion(String titulo, T[] valores) {
		System.out.println(titulo + ":");
		for (int i = 0; i < valores.length; i++) {
			System.out.println((i + 1) + ". " + valores[i]);
		}
		return valores[lector.leerIndice("Elija " + titulo.toLowerCase(), valores.length)];
	}
}
