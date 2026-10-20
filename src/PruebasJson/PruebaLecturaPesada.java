package PruebasJson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class PruebaLecturaPesada {

	private interface Tarea { void ejecutar() throws Exception; }

	private static int errores = 0, advertencias = 0, comprobaciones = 0;

	private static JSONObject club;
	private static Map<String, JSONObject> usuarios = new HashMap<>();
	private static Map<String, Integer> maxPorDisciplina = new HashMap<>();
	private static Map<String, String> disciplinaDeInstalacion = new HashMap<>();
	private static Set<String> instalaciones = new HashSet<>();
	private static Set<String> productos = new HashSet<>();
	private static Set<String> codigosDescuento = new HashSet<>();
	private static Map<String, JSONObject> equipos = new HashMap<>();

	private static final Set<String> NIVELES = Set.of("PRINCIPIANTE", "INTERMEDIO", "AVANZADO");
	private static final Map<String, int[]> EDADES = Map.of(
			"SUB13", new int[] { 0, 12 }, "SUB15", new int[] { 13, 14 },
			"SUB17", new int[] { 15, 16 }, "MAYORES", new int[] { 17, 80 });

	private static void exigir(boolean condicion, String mensaje) {
		comprobaciones++;
		if (!condicion) { errores++; System.out.println("  [ERROR] " + mensaje); }
	}

	private static void avisar(boolean condicion, String mensaje) {
		comprobaciones++;
		if (!condicion) { advertencias++; System.out.println("  [AVISO] " + mensaje); }
	}

	private static boolean tiene(JSONObject objeto, String contexto, String... claves) {
		boolean todas = true;
		for (String clave : claves) {
			if (!objeto.has(clave)) {
				exigir(false, contexto + ": falta la clave '" + clave + "'");
				todas = false;
			}
		}
		return todas;
	}

	private static void seccion(String nombre, Tarea tarea) {
		System.out.println("== " + nombre);
		try {
			tarea.ejecutar();
		} catch (Exception e) {
			errores++;
			System.out.println("  [ERROR] la sección falló: " + e);
		}
	}

	public static void main(String[] args) throws Exception {
		long inicio = System.nanoTime();
		club = new JSONObject(Files.readString(Path.of("datos", "club.json")));

		seccion("Secciones obligatorias", PruebaLecturaPesada::secciones);
		seccion("Disciplinas e instalaciones", PruebaLecturaPesada::disciplinas);
		seccion("Usuarios", PruebaLecturaPesada::verUsuarios);
		seccion("Catálogo de productos", PruebaLecturaPesada::catalogo);
		seccion("Equipos", PruebaLecturaPesada::verEquipos);
		seccion("Competencias", PruebaLecturaPesada::competencias);
		seccion("Ventas", PruebaLecturaPesada::ventas);
		seccion("Solicitudes de turno", PruebaLecturaPesada::solicitudes);
		seccion("Reservas", PruebaLecturaPesada::reservas);
		seccion("Sesiones de entrenamiento", PruebaLecturaPesada::sesiones);
		seccion("Partidos de práctica", PruebaLecturaPesada::practicas);
		seccion("Partidos internos", PruebaLecturaPesada::internos);

		long ms = (System.nanoTime() - inicio) / 1_000_000;
		System.out.println("\nComprobaciones: " + comprobaciones + " | errores: " + errores
				+ " | avisos: " + advertencias + " | " + ms + " ms");
		System.out.println(errores == 0 ? "RESULTADO: OK" : "RESULTADO: HAY ERRORES");
	}

	private static void secciones() {
		tiene(club, "club", "nombre", "usuarios", "deportes", "disciplinas", "rankings", "tienda",
				"cafeteria", "inventarios", "equipos", "competencias", "ventas", "solicitudes",
				"sugerencias", "historial");
		tiene(club.getJSONObject("historial"), "historial", "reservas", "partidosPractica", "sesiones", "partidos");
	}

	private static void disciplinas() {
		for (Object o : club.getJSONArray("disciplinas")) {
			JSONObject d = (JSONObject) o;
			if (!tiene(d, "disciplina", "tipo", "nombre", "maxInstalacionesSimultaneas", "instalaciones")) continue;
			String nombre = d.getString("nombre");
			exigir(maxPorDisciplina.put(nombre, d.getInt("maxInstalacionesSimultaneas")) == null,
					"disciplina repetida: " + nombre);
			exigir(d.getInt("maxInstalacionesSimultaneas") > 0, nombre + ": máximo debe ser > 0");
			exigir(d.getJSONArray("instalaciones").length() > 0, nombre + ": necesita al menos 1 instalación");
			for (Object oi : d.getJSONArray("instalaciones")) {
				JSONObject i = (JSONObject) oi;
				if (!tiene(i, nombre, "tipo", "nombre", "techado", "salon")) continue;
				exigir(instalaciones.add(i.getString("nombre")), "instalación repetida: " + i.getString("nombre"));
				disciplinaDeInstalacion.put(i.getString("nombre"), nombre);
			}
		}
		for (Object o : club.getJSONArray("inventarios")) {
			JSONObject inventario = (JSONObject) o;
			if (tiene(inventario, "inventario", "instalacion")) {
				String nombre = inventario.getJSONObject("instalacion").getString("nombre");
				exigir(instalaciones.add(nombre), "instalación repetida: " + nombre);
			}
		}
	}

	private static void verUsuarios() {
		Set<String> tipos = Set.of("Socio", "Entrenador", "Fisioterapeuta", "Administrador");
		Set<String> codigosEmpleado = new HashSet<>();
		int entrenadores = 0, fisios = 0;

		for (Object o : club.getJSONArray("usuarios")) {
			JSONObject u = (JSONObject) o;
			if (!tiene(u, "usuario", "tipo", "login", "nombre", "password")) continue;
			String login = u.getString("login"), tipo = u.getString("tipo");
			exigir(usuarios.put(login, u) == null, "login repetido: " + login);
			exigir(tipos.contains(tipo), login + ": tipo desconocido " + tipo);
			exigir(!u.getString("password").isBlank(), login + ": password vacío");
			if (tipo.equals("Administrador")) continue;

			if (!tiene(u, login, "edad", "puntosFidelidad", "inscripcionesIndividuales")) continue;
			exigir(u.getInt("edad") >= 0, login + ": edad negativa");
			exigir(u.getInt("puntosFidelidad") >= 0, login + ": puntos negativos");

			Set<String> vistas = new HashSet<>();
			for (Object oi : u.getJSONArray("inscripcionesIndividuales")) {
				JSONObject ins = (JSONObject) oi;
				String disciplina = ins.getString("disciplina");
				exigir(maxPorDisciplina.containsKey(disciplina), login + ": disciplina inexistente " + disciplina);
				exigir(NIVELES.contains(ins.getString("nivel")), login + ": nivel inválido");
				exigir(vistas.add(disciplina), login + ": inscrito dos veces en " + disciplina);
			}

			if (tipo.equals("Entrenador") || tipo.equals("Fisioterapeuta")) {
				if (tipo.equals("Entrenador")) entrenadores++; else fisios++;
				if (!tiene(u, login, "codigoEmpleado", "codigoDescuento", "turnos")) continue;
				exigir(codigosEmpleado.add(u.getString("codigoEmpleado")), login + ": codigoEmpleado repetido");
				String codigo = u.getJSONObject("codigoDescuento").getString("codigo");
				exigir(codigosDescuento.add(codigo), login + ": código de descuento repetido " + codigo);
				for (Object ot : u.getJSONArray("turnos")) {
					JSONObject t = (JSONObject) ot;
					LocalTime inicio = LocalTime.parse(t.getString("horaInicio"));
					LocalTime fin = LocalTime.parse(t.getString("horaFinal"));
					exigir(inicio.isBefore(fin), login + ": turno con hora inicial >= final");
				}
			}
		}
		exigir(entrenadores >= 2, "se esperan al menos 2 entrenadores, hay " + entrenadores);
		exigir(fisios >= 1, "se espera al menos 1 fisioterapeuta, hay " + fisios);
		System.out.println("  usuarios: " + usuarios.size() + " (entrenadores " + entrenadores + ", fisioterapeutas " + fisios + ")");
	}

	private static void catalogo() {
		List<JSONArray> listas = List.of(club.getJSONObject("tienda").getJSONArray("articulos"),
				club.getJSONObject("cafeteria").getJSONArray("bebidas"),
				club.getJSONObject("cafeteria").getJSONArray("snacks"));
		for (JSONArray lista : listas) {
			for (Object o : lista) {
				JSONObject p = (JSONObject) o;
				if (!tiene(p, "producto", "nombre", "precio", "cantidad")) continue;
				String nombre = p.getString("nombre");
				exigir(productos.add(nombre), "producto repetido entre catálogos: " + nombre);
				exigir(p.getDouble("precio") > 0, nombre + ": precio debe ser > 0");
				exigir(p.getInt("cantidad") >= 0, nombre + ": cantidad negativa");
			}
		}
		for (Object o : club.getJSONObject("cafeteria").getJSONArray("snacks")) {
			JSONObject s = (JSONObject) o;
			Set<String> permitidos = Set.of("MANI", "GLUTEN", "LACTEOS");
			for (Object a : s.getJSONArray("alergenos")) {
				exigir(permitidos.contains((String) a), s.getString("nombre") + ": alérgeno inválido " + a);
			}
		}
		for (Object o : club.getJSONArray("inventarios")) {
			for (Object a : ((JSONObject) o).getJSONArray("articulos")) {
				exigir(productos.contains((String) a), "inventario referencia producto inexistente: " + a);
			}
		}
	}

	private static void verEquipos() {
		Map<String, Set<String>> deportesPorJugador = new HashMap<>();
		Set<String> deportes = new HashSet<>();
		for (Object o : club.getJSONArray("deportes")) deportes.add(((JSONObject) o).getString("nombre"));

		for (Object o : club.getJSONArray("equipos")) {
			JSONObject e = (JSONObject) o;
			if (!tiene(e, "equipo", "nombre", "cupoMaximo", "categoria", "deporte", "inscripciones")) continue;
			String nombre = e.getString("nombre"), deporte = e.getString("deporte"), categoria = e.getString("categoria");
			exigir(equipos.put(nombre, e) == null, "equipo repetido: " + nombre);
			exigir(deportes.contains(deporte), nombre + ": deporte inexistente " + deporte);
			exigir(EDADES.containsKey(categoria), nombre + ": categoría inválida " + categoria);
			JSONArray inscripciones = e.getJSONArray("inscripciones");
			exigir(inscripciones.length() <= e.getInt("cupoMaximo"), nombre + ": supera el cupo máximo");

			Set<Integer> camisetas = new HashSet<>();
			Set<String> jugadores = new HashSet<>();
			for (Object oi : inscripciones) {
				JSONObject i = (JSONObject) oi;
				String login = i.getString("jugador");
				JSONObject socio = usuarios.get(login);
				exigir(socio != null, nombre + ": jugador inexistente " + login);
				exigir(jugadores.add(login), nombre + ": jugador repetido " + login);
				int camiseta = i.getInt("numeroCamiseta");
				exigir(camiseta > 0 && camiseta < 100, nombre + ": camiseta fuera de rango " + camiseta);
				exigir(camisetas.add(camiseta), nombre + ": camiseta repetida " + camiseta);
				exigir(i.getInt("amarillasAcumuladas") >= 0, nombre + ": amarillas negativas");
				if (socio == null) continue;
				exigir(!socio.getString("tipo").equals("Administrador"), nombre + ": un administrador no juega");
				if (EDADES.containsKey(categoria)) {
					int edad = socio.getInt("edad");
					int[] rango = EDADES.get(categoria);
					exigir(edad >= rango[0] && edad <= rango[1],
							nombre + ": " + login + " (" + edad + " años) no cumple la categoría " + categoria);
				}
				exigir(deportesPorJugador.computeIfAbsent(login, k -> new HashSet<>()).add(deporte),
						login + " está en dos equipos de " + deporte);
			}
		}
		System.out.println("  equipos: " + equipos.size());
	}

	private static void competencias() {
		for (Object o : club.getJSONArray("competencias")) {
			JSONObject c = (JSONObject) o;
			if (!tiene(c, "competencia", "tipo", "nombre", "nominaMinima", "inscripciones", "partidos")) continue;
			String nombre = c.getString("nombre");
			for (Object oi : c.getJSONArray("inscripciones")) {
				JSONObject ins = (JSONObject) oi;
				String equipo = ins.getString("equipo");
				JSONObject e = equipos.get(equipo);
				exigir(e != null, nombre + ": equipo inexistente " + equipo);
				if (e == null) continue;
				exigir(e.getString("deporte").equals(c.getString("deporte")), nombre + ": " + equipo + " es de otro deporte");
				exigir(e.getString("categoria").equals(c.getString("categoria")), nombre + ": " + equipo + " es de otra categoría");
				Set<String> plantilla = new HashSet<>();
				for (Object x : e.getJSONArray("inscripciones")) plantilla.add(((JSONObject) x).getString("jugador"));
				JSONArray nomina = ins.getJSONArray("nomina");
				exigir(nomina.length() >= c.getInt("nominaMinima"), nombre + ": nómina de " + equipo + " bajo el mínimo");
				for (Object on : nomina) {
					JSONObject r = (JSONObject) on;
					exigir(plantilla.contains(r.getString("jugador")), nombre + ": " + r.getString("jugador") + " no está en " + equipo);
					exigir(r.getJSONObject("controlSanciones").getInt("amarillasAcumuladas") >= 0, nombre + ": amarillas negativas");
				}
			}
			for (Object op : c.getJSONArray("partidos")) {
				JSONObject p = (JSONObject) op;
				exigir(p.getInt("marcadorPropio") >= 0 && p.getInt("marcadorRival") >= 0, nombre + ": marcador negativo");
				exigir(!p.getString("institucionRival").isBlank(), nombre + ": partido sin institución rival");
				nonNegativas(p.getJSONArray("estadisticasConjunto"), nombre);
			}
		}
	}

	private static void nonNegativas(JSONArray estadisticas, String contexto) {
		for (Object o : estadisticas) {
			JSONObject e = (JSONObject) o;
			exigir(usuarios.containsKey(e.getString("jugador")), contexto + ": estadística de jugador inexistente");
			for (String clave : e.keySet()) {
				if (e.get(clave) instanceof Number) {
					exigir(e.getDouble(clave) >= 0, contexto + ": estadística negativa en " + clave);
				}
			}
		}
	}

	private static void ventas() {
		double sumaTotal = 0;
		for (Object o : club.getJSONArray("ventas")) {
			JSONObject v = (JSONObject) o;
			if (!tiene(v, "venta", "fecha", "total", "comprador", "lineas", "rubro", "puntosGenerados")) continue;
			String comprador = v.getString("comprador");
			exigir(usuarios.containsKey(comprador), "venta con comprador inexistente " + comprador);
			exigir(v.getJSONArray("lineas").length() >= 1, "venta sin líneas");
			exigir(v.getDouble("total") >= 0, "venta con total negativo");
			exigir(v.getDouble("propina") >= 0, "venta con propina negativa");
			exigir(Math.abs(v.getInt("puntosGenerados") - v.getDouble("total") * 0.02) <= 1,
					"puntos generados no son el 2% del total en venta de " + comprador);
			if (v.has("codigoDescuento")) {
				exigir(codigosDescuento.contains(v.getString("codigoDescuento")), "código de descuento inexistente");
			}
			for (Object ol : v.getJSONArray("lineas")) {
				JSONObject l = (JSONObject) ol;
				exigir(productos.contains(l.getString("producto")), "línea con producto inexistente " + l.getString("producto"));
				exigir(l.getInt("cantidad") > 0, "línea con cantidad <= 0");
			}
			sumaTotal += v.getDouble("total");
		}
		System.out.println("  ventas: " + club.getJSONArray("ventas").length() + ", recaudo total " + sumaTotal);
	}

	private static void solicitudes() {
		for (Object o : club.getJSONArray("solicitudes")) {
			JSONObject s = (JSONObject) o;
			if (!tiene(s, "solicitud", "estado", "solicitante", "turnoActual", "turnoNuevo")) continue;
			String login = s.getString("solicitante");
			JSONObject empleado = usuarios.get(login);
			exigir(empleado != null && empleado.has("turnos"), "solicitante no es un empleado: " + login);
			if (empleado == null || !empleado.has("turnos")) continue;
			boolean PENDIENTE = s.getString("estado").equals("PENDIENTE");
			Object actual = s.get("turnoActual");
			if (PENDIENTE) {
				exigir(actual instanceof Integer, login + ": solicitud PENDIENTE debe guardar el índice del turno");
				if (actual instanceof Integer) {
					int indice = (Integer) actual;
					exigir(indice >= 0 && indice < empleado.getJSONArray("turnos").length(), login + ": índice de turno fuera de rango");
				}
			} else {
				exigir(actual instanceof JSONObject, login + ": solicitud resuelta debe guardar el turno completo");
			}
			exigir(Set.of("PENDIENTE", "ACEPTADA", "RECHAZADA").contains(s.getString("estado")), login + ": estado inválido");
		}
	}

	private static boolean solapa(String i1, String f1, String i2, String f2) {
		return LocalTime.parse(i1).isBefore(LocalTime.parse(f2)) && LocalTime.parse(i2).isBefore(LocalTime.parse(f1));
	}

	private static void reservas() {
		JSONArray reservas = club.getJSONObject("historial").getJSONArray("reservas");
		for (Object o : reservas) {
			JSONObject r = (JSONObject) o;
			if (!tiene(r, "reserva", "socio", "instalacion", "fecha", "horaInicio", "horaFinal", "modalidad", "numeroJugadores")) continue;
			exigir(usuarios.containsKey(r.getString("socio")), "reserva de socio inexistente " + r.getString("socio"));
			exigir(disciplinaDeInstalacion.containsKey(r.getString("instalacion")), "reserva en instalación individual inexistente " + r.getString("instalacion"));
			exigir(LocalTime.parse(r.getString("horaInicio")).isBefore(LocalTime.parse(r.getString("horaFinal"))), "reserva con horas invertidas");
			int jugadores = r.getInt("numeroJugadores");
			exigir(jugadores % 2 == 0, "número de jugadores impar en reserva");
			exigir((r.getString("modalidad").equals("SENCILLOS") && jugadores == 2)
					|| (r.getString("modalidad").equals("DOBLES") && jugadores == 4), "modalidad y jugadores no coinciden");
		}
		for (int a = 0; a < reservas.length(); a++) {
			JSONObject r = reservas.getJSONObject(a);
			String disciplina = disciplinaDeInstalacion.get(r.getString("instalacion"));
			if (disciplina == null) continue;
			int simultaneas = 0;
			for (int b = 0; b < reservas.length(); b++) {
				JSONObject otra = reservas.getJSONObject(b);
				if (disciplina.equals(disciplinaDeInstalacion.get(otra.getString("instalacion")))
						&& r.getString("fecha").equals(otra.getString("fecha"))
						&& solapa(r.getString("horaInicio"), r.getString("horaFinal"), otra.getString("horaInicio"), otra.getString("horaFinal"))) {
					simultaneas++;
				}
			}
			exigir(simultaneas <= maxPorDisciplina.get(disciplina), "se supera el máximo simultáneo de " + disciplina + " el " + r.getString("fecha"));
			for (int b = a + 1; b < reservas.length(); b++) {
				JSONObject otra = reservas.getJSONObject(b);
				exigir(!(r.getString("instalacion").equals(otra.getString("instalacion"))
						&& r.getString("fecha").equals(otra.getString("fecha"))
						&& solapa(r.getString("horaInicio"), r.getString("horaFinal"), otra.getString("horaInicio"), otra.getString("horaFinal"))),
						"misma instalación reservada dos veces: " + r.getString("instalacion"));
			}
		}
	}

	private static void sesiones() {
		for (Object o : club.getJSONObject("historial").getJSONArray("sesiones")) {
			JSONObject s = (JSONObject) o;
			if (!tiene(s, "sesión", "entrenador", "instalacion", "convocados", "asistencias", "duracionMinutos")) continue;
			JSONObject entrenador = usuarios.get(s.getString("entrenador"));
			exigir(entrenador != null && entrenador.getString("tipo").equals("Entrenador"), "sesión con entrenador inválido " + s.getString("entrenador"));
			exigir(instalaciones.contains(s.getString("instalacion")), "sesión en instalación inexistente " + s.getString("instalacion"));
			exigir(s.getInt("duracionMinutos") > 0, "sesión con duración <= 0");
			JSONArray convocados = s.getJSONArray("convocados");
			exigir(convocados.length() > 0, "sesión con convocados vacíos");
			Set<String> lista = new HashSet<>();
			for (Object c : convocados) {
				exigir(usuarios.containsKey((String) c), "convocado inexistente " + c);
				exigir(lista.add((String) c), "convocado repetido " + c);
			}
			Set<String> registrados = new HashSet<>();
			for (Object oa : s.getJSONArray("asistencias")) {
				JSONObject a = (JSONObject) oa;
				String jugador = a.getString("jugador");
				exigir(lista.contains(jugador), "asistencia de alguien no convocado: " + jugador);
				exigir(registrados.add(jugador), "asistencia repetida de " + jugador);
				exigir(Set.of("ASISTIO", "FALTO", "LLEGOTARDE").contains(a.getString("estado")), "estado de asistencia inválido");
				int nota = a.getInt("evaluacion");
				exigir(nota == 0 || (nota >= 1 && nota <= 5), "nota fuera de 1-5 para " + jugador);
			}
		}
	}

	private static boolean compatibles(String a, String b) {
		List<String> orden = List.of("PRINCIPIANTE", "INTERMEDIO", "AVANZADO");
		return Math.abs(orden.indexOf(a) - orden.indexOf(b)) <= 1;
	}

	private static String nivelEn(JSONObject usuario, String disciplina) {
		for (Object o : usuario.getJSONArray("inscripcionesIndividuales")) {
			JSONObject i = (JSONObject) o;
			if (i.getString("disciplina").equals(disciplina)) return i.getString("nivel");
		}
		return null;
	}

	private static void practicas() {
		for (Object o : club.getJSONObject("historial").getJSONArray("partidosPractica")) {
			JSONObject p = (JSONObject) o;
			String disciplina = p.getString("disciplina");
			exigir(maxPorDisciplina.containsKey(disciplina), "práctica en disciplina inexistente");
			exigir(disciplina.equals(disciplinaDeInstalacion.get(p.getString("instalacion"))), "la instalación no pertenece a " + disciplina);
			JSONArray jugadores = p.getJSONArray("jugadores");
			exigir(jugadores.length() == 2 || jugadores.length() == 4, "práctica con " + jugadores.length() + " jugadores");
			exigir(p.getJSONArray("estadisticas").length() == jugadores.length(), "estadísticas no coinciden con jugadores");
			nonNegativas(p.getJSONArray("estadisticas"), "práctica " + p.getString("fecha"));
			for (int a = 0; a < jugadores.length(); a++) {
				for (int b = a + 1; b < jugadores.length(); b++) {
					JSONObject ua = usuarios.get(jugadores.getString(a)), ub = usuarios.get(jugadores.getString(b));
					if (ua == null || ub == null) { exigir(false, "jugador de práctica inexistente"); continue; }
					boolean entrenador = ua.getString("tipo").equals("Entrenador") || ub.getString("tipo").equals("Entrenador");
					String na = nivelEn(ua, disciplina), nb = nivelEn(ub, disciplina);
					avisar(entrenador || (na != null && nb != null && compatibles(na, nb)),
							"práctica " + p.getString("fecha") + ": " + jugadores.getString(a) + " (" + na + ") y "
									+ jugadores.getString(b) + " (" + nb + ") no son niveles compatibles");
				}
			}
		}
	}

	private static void internos() {
		for (Object o : club.getJSONObject("historial").getJSONArray("partidos")) {
			JSONObject p = (JSONObject) o;
			JSONObject local = equipos.get(p.getString("equipoLocal")), visitante = equipos.get(p.getString("equipoVisitante"));
			exigir(local != null && visitante != null, "partido interno con equipo inexistente");
			exigir(!p.getString("equipoLocal").equals(p.getString("equipoVisitante")), "un equipo juega contra sí mismo");
			if (local != null && visitante != null) {
				exigir(local.getString("deporte").equals(visitante.getString("deporte")), "partido entre deportes distintos");
				exigir(local.getString("categoria").equals(visitante.getString("categoria")), "partido entre categorías distintas");
			}
			exigir(p.getInt("marcadorLocal") >= 0 && p.getInt("marcadorVisitante") >= 0, "marcador negativo");
			nonNegativas(p.getJSONArray("estadisticasConjunto"), "partido " + p.getString("fecha"));
		}
	}
}