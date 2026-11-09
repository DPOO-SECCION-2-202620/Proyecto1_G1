package Consola;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import Tienda_Cafeteria_Inventario.Alergeno;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.Bebida;
import Tienda_Cafeteria_Inventario.Snack;
import DeportesConjunto_Competencias.Baloncesto;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.Futbol;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import DeportesIndividuales.Tenis;
import Instalaciones.CanchaTenis;
import NucleoSistema.ClubSeneca;
import TorneosInternos.PremiacionCompetitiva;
import TorneosInternos.PremiacionRecreativa;
import TorneosInternos.TorneoConjunto;
import TorneosInternos.TorneoIndividual;
import TurnosEmpleados.Turno;
import Usuarios.Administrador;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;

public class DatosIniciales {

	public static void main(String[] args) throws Exception {
		ClubSeneca club = new ClubSeneca("Club Seneca");

		// Administrador y empleados
		club.registrarUsuario(new Administrador("Admin Seneca", "admin", "admin123"));
		Entrenador luis = new Entrenador(38, "Luis Perez", "luis", "clave-luis", "EMP-001");
		Entrenador carla = new Entrenador(34, "Carla Gomez", "carla", "clave-carla", "EMP-002");
		Entrenador pablo = new Entrenador(41, "Pablo Rios", "pablo", "clave-pablo", "EMP-003");
		Fisioterapeuta marta = new Fisioterapeuta(36, "Marta Diaz", "marta", "clave-marta", "EMP-004");
		Fisioterapeuta diego = new Fisioterapeuta(39, "Diego Castro", "diego", "clave-diego", "EMP-005");
		club.registrarUsuario(luis);
		club.registrarUsuario(carla);
		club.registrarUsuario(pablo);
		club.registrarUsuario(marta);
		club.registrarUsuario(diego);

		// Turnos: tres entrenadores y una fisioterapeuta en la mañana del lunes
		Turno turnoLuis = turno(8, 12);
		Turno turnoCarla = turno(8, 12);
		Turno turnoPablo = turno(8, 12);
		Turno turnoMarta = turno(8, 12);
		club.asignarTurno(luis, turnoLuis);
		club.asignarTurno(carla, turnoCarla);
		club.asignarTurno(pablo, turnoPablo);
		club.asignarTurno(marta, turnoMarta);
		club.asignarTurno(diego, turno(14, 18));

		// Solicitudes pendientes: Luis se puede aprobar; Marta nunca (HU-ADM-02 CA-1 y CA-2)
		club.registrarSolicitudTurno(luis, turnoLuis, turno(14, 18));
		club.registrarSolicitudTurno(marta, turnoMarta, turno(14, 18));

		// Socios
		Socio[] jugadores = new Socio[10];
		for (int numero = 1; numero <= 9; numero++) {
			jugadores[numero] = club.crearSocio(19 + numero, "Jugador " + numero,
					String.format("jugador%02d", numero), "clave" + numero);
		}
		club.crearSocio(24, "Ana Torres", "ana", "clave-ana");
		club.crearSocio(15, "Nino Sub17", "nino", "clave-nino");

		// Equipos (HU-ENT-02 y HU-SOC-02)
		Equipo cerros = club.crearEquipo("Cerros FC", 10, new Futbol(), Categoria.MAYORES);
		for (int numero = 1; numero <= 8; numero++) {
			club.inscribirJugador(jugadores[numero], cerros, "Posicion " + numero, numero);
		}
		cerros.getInscripcionDe(jugadores[8]).registrarTarjetas(0, 1); // suspendido: no cuenta (CA-3)

		club.crearEquipo("Andes FC", 8, new Futbol(), Categoria.MAYORES); // vacío (CA-4 de SOC-02)
		club.crearEquipo("Seneca Sub17", 8, new Futbol(), Categoria.SUB17); // vacío (CA-2 de SOC-02)

		Equipo basket = club.crearEquipo("Basket Seneca", 6, new Baloncesto(), Categoria.MAYORES);
		for (int numero = 1; numero <= 5; numero++) {
			club.inscribirJugador(jugadores[numero], basket, "Posicion " + numero, numero);
		}
		basket.getInscripcionDe(jugadores[5]).registrarTarjetas(0, 1); // 4 habilitados (CA-2 de ENT-02)

		// Torneos internos: disciplina individual, jugadores con nivel y torneos de ejemplo
		Tenis tenis = new Tenis(2, "Tenis");
		new CanchaTenis("Cancha Tenis 1", false, 1, tenis);
		new CanchaTenis("Cancha Tenis 2", true, 1, tenis);
		club.registrarDisciplina(tenis);
		for (int numero = 1; numero <= 3; numero++) {
			club.inscribirEnDisciplina(jugadores[numero], tenis, NivelJugador.INTERMEDIO);
		}
		Socio ana = club.buscarSocio("ana");
		club.inscribirEnDisciplina(ana, tenis, NivelJugador.INTERMEDIO);
		club.getRanking(tenis, ana).setPuntajeElo(1100); // mejor ranking: puede tomar el cupo preferencial
		club.inscribirEnDisciplina(carla, tenis, NivelJugador.INTERMEDIO); // un empleado que puede jugar

		// Un domingo (ningun empleado tiene turno ese dia), al menos 14 dias adelante
		LocalDate domingo = LocalDate.now().plusDays(14);
		while (domingo.getDayOfWeek() != DayOfWeek.SUNDAY) {
			domingo = domingo.plusDays(1);
		}
		club.agregarTorneo(new TorneoIndividual("Copa Tenis Intermedio", 4, domingo, domingo.minusDays(7),
				new PremiacionRecreativa(), tenis, NivelJugador.INTERMEDIO, Modalidad.SENCILLOS));
		club.agregarTorneo(new TorneoConjunto("Mini-campeonato Futbol Mayores", 4, domingo, domingo.minusDays(7),
				new PremiacionCompetitiva(50000), cerros.getDeporte(), Categoria.MAYORES));
		
		club.reabastecerTienda(new Articulo("Pelotas de tenis (x3)", "Equipamiento", 15000, 20), 1);
		club.reabastecerTienda(new Articulo("Camiseta Seneca", "Ropa", 45000, 10), 1);
		club.reabasteceCafeteriaSocios(new Bebida("Agua", "Bebida fría", 3000, 30, false), 1);
		club.reabasteceCafeteriaSocios(new Snack("Barra de cereal", "Snack", 4000, 25, List.of(Alergeno.GLUTEN)), 1);

		club.guardarDatos();
		System.out.println("Datos iniciales creados en datos/club.json");
	}

	private static Turno turno(int horaInicio, int horaFin) {
		return new Turno("Lunes", LocalTime.of(horaInicio, 0), LocalTime.of(horaFin, 0));
	}
}