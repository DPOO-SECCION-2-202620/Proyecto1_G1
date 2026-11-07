package TorneosInternos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import Usuarios.Usuario;

import Excepciones.ReglaNegocioException;

public abstract class Torneo {

	public static final int CUPOS_MINIMOS = 2;
	public static final int CUPOS_MAXIMOS = 32;
	public static final double PORCENTAJE_PREFERENCIAL = 0.20;
	
	private String nombre;
	private int cupos;
	private LocalDate fechaInicio;
	private LocalDate fechaLimitePreferencial;
	private EstadoTorneo estado;
	private int rondaActual;
	private Premiacion premiacion;
	private List<InscripcionTorneo> inscripciones;
	private List<Encuentro> encuentros;
	private List<PremioEntregado> premios;
	
	public Torneo(String nombre, int cupos, LocalDate fechaInicio, LocalDate fechaLimitePreferencial, Premiacion premiacion)
			throws ReglaNegocioException {
		this.nombre = nombre;
		this.cupos = cupos;
		this.fechaInicio = fechaInicio;
		this.fechaLimitePreferencial = fechaLimitePreferencial;
		this.premiacion = premiacion;
		this.estado = EstadoTorneo.INSCRIPCIONES_ABIERTAS;
		this.rondaActual = 0;
		this.inscripciones = new ArrayList<>();
		this.encuentros = new ArrayList<>();
		this.premios = new ArrayList<>();
		validarDatos();
	}
	
	private void validarDatos() throws ReglaNegocioException {
	    if (nombre == null || nombre.isBlank()) {
	        throw new ReglaNegocioException("El torneo debe tener nombre");
	    }
	    if (cupos < CUPOS_MINIMOS || cupos > CUPOS_MAXIMOS) {
	        throw new ReglaNegocioException("Los cupos deben estar entre 2 y 32");
	    }
	    if (fechaLimitePreferencial.isAfter(fechaInicio)) {
	        throw new ReglaNegocioException("La fecha límite preferencial no puede ser posterior a la fecha de inicio");
	    }
	}
	
	
	public int getCuposPreferenciales() {
	    return (int) Math.ceil(cupos * PORCENTAJE_PREFERENCIAL);
	}

	public int getCuposLibres() {
	    return cupos - inscripciones.size();
	}
	
	public int getCuposPreferencialesLibres() {
		int ocupados = 0;
		for (InscripcionTorneo i : inscripciones) {
			if(i.esPreferencial()) {
				ocupados += 1;
			}
		}
		return getCuposPreferenciales()-ocupados;
	}
	
	public void inscribir(InscripcionTorneo inscripcion, LocalDate hoy, boolean elegiblePreferencial)
			throws ReglaNegocioException {
		 if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
		        throw new ReglaNegocioException("Las inscripciones están cerradas");
		    }
		    for (InscripcionTorneo i : inscripciones) {
		        for (JugadorTorneo j : inscripcion.getJugadores()) {
		            if (i.incluye(j)) {
		                throw new ReglaNegocioException(j.getNombre() + " ya está inscrito en el torneo");
		            }
		        }
		    }
		    validarParticipante(inscripcion);
		    if (getCuposLibres() == 0) {
		        throw new ReglaNegocioException("El torneo está lleno");
		    }
		    boolean antesDelLimite = !hoy.isAfter(fechaLimitePreferencial);
		    boolean ocupaPreferencial = false;

		    if (elegiblePreferencial && antesDelLimite && getCuposPreferencialesLibres() > 0) {   
		        ocupaPreferencial = true;
		    } 
		    else {
		        int regularesLibres;
		        if (antesDelLimite) {
		            regularesLibres = getCuposLibres() - getCuposPreferencialesLibres();          
		        } 
		        else {
		            regularesLibres = getCuposLibres();                                           
		        }
		        if (regularesLibres <= 0) {
		            throw new ReglaNegocioException("Solo quedan cupos preferenciales");
		        }
		    }
		    inscripcion.setPreferencial(ocupaPreferencial);
		    inscripcion.setMontoPagado(premiacion.getTarifa(inscripcion));

		    inscripciones.add(inscripcion); 
}

	protected abstract void validarParticipante(InscripcionTorneo inscripcion) throws ReglaNegocioException;

	public void retirar(JugadorTorneo jugador) throws ReglaNegocioException {
		if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
			throw new ReglaNegocioException("El torneo ya inició");
		}
		InscripcionTorneo encontrada = buscarInscripcion(jugador);
	    if (encontrada == null) {
	        throw new ReglaNegocioException(jugador.getNombre() + " no está inscrito en el torneo");
	    }
	    inscripciones.remove(encontrada);
		
	}

	public void retirarEquipo(DeportesConjunto_Competencias.Equipo equipo) throws ReglaNegocioException {
		if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
			throw new ReglaNegocioException("El torneo ya inició");
	}
		for (InscripcionTorneo i : inscripciones) {
	        if (i instanceof InscripcionTorneoEquipo && ((InscripcionTorneoEquipo) i).getEquipo() == equipo) {
	            inscripciones.remove(i);
	            return;
	        }
	    }
	    throw new ReglaNegocioException(equipo.getNombre() + " no está inscrito en el torneo");
	}

	public InscripcionTorneo buscarInscripcion(JugadorTorneo jugador) {
		for (InscripcionTorneo inscripcion : inscripciones) {
			if (inscripcion.incluye(jugador)) {
				return inscripcion;
			}
		}
		return null;
	}

	public void iniciar() throws ReglaNegocioException {
		if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
			throw new ReglaNegocioException("El torneo ya inició");
		}
		if (!(inscripciones.size() == 2 || inscripciones.size() == 4 || inscripciones.size() == 8 || inscripciones.size() == 16 || inscripciones.size() == 32)) {
			throw new ReglaNegocioException("Se necesitan 2, 4, 8, 16 o 32 inscritos para iniciar el torneo");
		}
		estado = EstadoTorneo.EN_CURSO;
		rondaActual = 1;
		for (int k = 0; k < inscripciones.size(); k += 2) {
		    InscripcionTorneo a = inscripciones.get(k);
		    InscripcionTorneo b = inscripciones.get(k + 1);  
		    encuentros.add(new Encuentro(1, a, b));
		}
		
	}

	
	public void registrarGanador(Encuentro encuentro, InscripcionTorneo ganador) throws ReglaNegocioException {
		if (estado != EstadoTorneo.EN_CURSO) {
			throw new ReglaNegocioException("El torneo no está en curso");
		}
		if (!encuentros.contains(encuentro) || encuentro.getRonda() != rondaActual) {
	        throw new ReglaNegocioException("El encuentro no pertenece a la ronda actual de este torneo");
	    }
		encuentro.registrarGanador(ganador);
	    List<Encuentro> ronda = getEncuentrosDeRonda(rondaActual);
	    for (Encuentro e : ronda) {
	        if (!e.tieneGanador()) {
	            return;
	        }
	    }
	    if (ronda.size() == 1) {
	        finalizar();
	    } 
	    else {
	        armarSiguienteRonda();
	    }
	}

	public void corregirGanador(Encuentro encuentro, InscripcionTorneo nuevoGanador) throws ReglaNegocioException {
		if (estado == EstadoTorneo.FINALIZADO) {
	        throw new ReglaNegocioException("El resultado ya no se puede cambiar");
	    }
	    if (!encuentro.tieneGanador()) {
	        throw new ReglaNegocioException("El encuentro todavía no tiene ganador");
	    }

	    InscripcionTorneo ganadorActual = encuentro.getGanador();
	    for (Encuentro siguiente : encuentros) {
	        if (siguiente.getRonda() == encuentro.getRonda() + 1 && siguiente.incluye(ganadorActual)) {
	            if (siguiente.tieneGanador()) {
	                throw new ReglaNegocioException("El resultado ya no se puede cambiar: la ronda siguiente ya se jugó");
	            }
	            siguiente.reemplazarParticipante(ganadorActual, nuevoGanador);
	        }
	    }
	    encuentro.cambiarGanador(nuevoGanador);
	}

	private void armarSiguienteRonda() {
		List<Encuentro> anterior = getEncuentrosDeRonda(rondaActual);
	    rondaActual++; 
	    for (int k = 0; k < anterior.size(); k += 2) {
	        InscripcionTorneo a = anterior.get(k).getGanador();
	        InscripcionTorneo b = anterior.get(k + 1).getGanador();
	        encuentros.add(new Encuentro(rondaActual, a, b));
	    }
	}

	private void finalizar() {
		estado = EstadoTorneo.FINALIZADO;
		Encuentro laFinal = getEncuentrosDeRonda(rondaActual).get(0);
	    InscripcionTorneo primero = laFinal.getGanador();
	    InscripcionTorneo segundo = laFinal.getPerdedor();
		premios = premiacion.entregarPremios(primero, segundo, getRecaudado(), nombre);
	}

	public double getRecaudado() {
		double total = 0;
	    for (InscripcionTorneo i : inscripciones) {
	        total += i.getMontoPagado();
	    }
	    return total;
	}

	public List<Encuentro> getEncuentrosPendientes() {
		List<Encuentro> pendientes = new ArrayList<>();
	    for (Encuentro e : encuentros) {
	        if (!e.tieneGanador()) {
	            pendientes.add(e);
	        }
	    }
	    return pendientes;
	}

	public List<Encuentro> getEncuentrosJugados() {
		List<Encuentro> jugados = new ArrayList<>();
		for (Encuentro e : encuentros) {
			if (e.tieneGanador()) {
				jugados.add(e);
			}
		}
		return jugados;
	}

	public List<Encuentro> getEncuentrosDeRonda(int ronda) {
		List<Encuentro> partidosRonda = new ArrayList<>();
		for (Encuentro e : encuentros) {
			if(e.getRonda() == ronda) {
				partidosRonda.add(e);
			}
		}
		return partidosRonda;
	}

	public int getRondaEliminacion(InscripcionTorneo inscripcion) {
		for (Encuentro e : encuentros) {
	        if (e.tieneGanador() && e.getPerdedor() == inscripcion) {
	            return e.getRonda();
	        }
	    }
	    return 0; 
	}

	/**
	 * HU-TINT-10 CA-3. Solo tiene sentido con el torneo FINALIZADO.
	 * TODO: lista con el campeón primero, luego el subcampeón, y después los demás ordenados de la ronda de
	 * eliminación más alta a la más baja (los que perdieron en semifinal antes que los de cuartos, etc.).
	 */
	public List<InscripcionTorneo> getPosiciones() {
		List<InscripcionTorneo> posiciones = new ArrayList<>();
	    Encuentro laFinal = getEncuentrosDeRonda(rondaActual).get(0);
	    posiciones.add(laFinal.getGanador());
	    for (int r = rondaActual; r >= 1; r--) {
	        for (InscripcionTorneo i : inscripciones) {
	            if (getRondaEliminacion(i) == r) {
	                posiciones.add(i);
	            }
	        }
	    }
	    return posiciones;
	}

	public abstract String getDescripcion();


	public boolean esCompetitivo() {
		return premiacion instanceof PremiacionCompetitiva;
	}

	public String getNombre() {
		return nombre;
	}

	public int getCupos() {
		return cupos;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public LocalDate getFechaLimitePreferencial() {
		return fechaLimitePreferencial;
	}

	public EstadoTorneo getEstado() {
		return estado;
	}

	public int getRondaActual() {
		return rondaActual;
	}

	public Premiacion getPremiacion() {
		return premiacion;
	}

	public List<InscripcionTorneo> getInscripciones() {
		return Collections.unmodifiableList(inscripciones);
	}

	public List<Encuentro> getEncuentros() {
		return Collections.unmodifiableList(encuentros);
	}

	public List<PremioEntregado> getPremios() {
		return Collections.unmodifiableList(premios);
	}
	
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("cupos", cupos);
	    json.put("fechaInicio", fechaInicio.toString());
	    json.put("fechaLimitePreferencial", fechaLimitePreferencial.toString());
	    json.put("competitivo", esCompetitivo());
	    if (premiacion instanceof PremiacionCompetitiva) {
	        json.put("tarifa", ((PremiacionCompetitiva) premiacion).getTarifaBase());
	    }
	    json.put("estado", estado.name());
	    json.put("rondaActual", rondaActual);

	    JSONArray jsonInscripciones = new JSONArray();
	    for (InscripcionTorneo inscripcion : inscripciones) {
	        jsonInscripciones.put(inscripcion.toJSON());
	    }
	    json.put("inscripciones", jsonInscripciones);

	    JSONArray jsonEncuentros = new JSONArray();
	    for (Encuentro encuentro : encuentros) {
	        jsonEncuentros.put(encuentro.toJSON(inscripciones));
	    }
	    json.put("encuentros", jsonEncuentros);

	    JSONArray jsonPremios = new JSONArray();
	    for (PremioEntregado premio : premios) {
	        jsonPremios.put(premio.toJSON());
	    }
	    json.put("premios", jsonPremios);
	    return json;
	}

	public static Torneo fromJSON(JSONObject json, Map<String, DeporteConjunto> mapaDeportes,
	        Map<String, DisciplinaIndividual> mapaDisciplinas, Map<String, Usuario> mapaUsuarios,
	        Map<String, Equipo> mapaEquipos) {
	    try {
	        String nombre = json.getString("nombre");
	        int cupos = json.getInt("cupos");
	        LocalDate fechaInicio = LocalDate.parse(json.getString("fechaInicio"));
	        LocalDate fechaLimite = LocalDate.parse(json.getString("fechaLimitePreferencial"));
	        Premiacion premiacion;
	        if (json.getBoolean("competitivo")) {
	            premiacion = new PremiacionCompetitiva(json.getDouble("tarifa"));
	        } else {
	            premiacion = new PremiacionRecreativa();
	        }

	        Torneo torneo;
	        String tipo = json.getString("tipo");
	        if (tipo.equals("CONJUNTO")) {
	            DeporteConjunto deporte = mapaDeportes.get(json.getString("deporte"));
	            if (deporte == null) {
	                throw new IllegalStateException("Deporte inexistente en el torneo: " + json.getString("deporte"));
	            }
	            torneo = new TorneoConjunto(nombre, cupos, fechaInicio, fechaLimite, premiacion, deporte,
	                    Categoria.valueOf(json.getString("categoria")));
	        } else if (tipo.equals("INDIVIDUAL")) {
	            DisciplinaIndividual disciplina = mapaDisciplinas.get(json.getString("disciplina"));
	            if (disciplina == null) {
	                throw new IllegalStateException("Disciplina inexistente en el torneo: " + json.getString("disciplina"));
	            }
	            torneo = new TorneoIndividual(nombre, cupos, fechaInicio, fechaLimite, premiacion, disciplina,
	                    NivelJugador.valueOf(json.getString("nivel")), Modalidad.valueOf(json.getString("modalidad")));
	        } else {
	            throw new IllegalArgumentException("Tipo de torneo desconocido: " + tipo);
	        }

	        torneo.estado = EstadoTorneo.valueOf(json.getString("estado"));
	        torneo.rondaActual = json.getInt("rondaActual");

	        JSONArray jsonInscripciones = json.optJSONArray("inscripciones");
	        if (jsonInscripciones != null) {
	            for (int indice = 0; indice < jsonInscripciones.length(); indice++) {
	                torneo.inscripciones.add(InscripcionTorneo.fromJSON(jsonInscripciones.getJSONObject(indice),
	                        mapaUsuarios, mapaEquipos));
	            }
	        }
	        JSONArray jsonEncuentros = json.optJSONArray("encuentros");
	        if (jsonEncuentros != null) {
	            for (int indice = 0; indice < jsonEncuentros.length(); indice++) {
	                torneo.encuentros.add(Encuentro.fromJSON(jsonEncuentros.getJSONObject(indice), torneo.inscripciones));
	            }
	        }
	        JSONArray jsonPremios = json.optJSONArray("premios");
	        if (jsonPremios != null) {
	            for (int indice = 0; indice < jsonPremios.length(); indice++) {
	                torneo.premios.add(PremioEntregado.fromJSON(jsonPremios.getJSONObject(indice), mapaUsuarios));
	            }
	        }
	        return torneo;
	    } catch (ReglaNegocioException excepcion) {
	        throw new IllegalStateException("Torneo inválido en los datos: " + excepcion.getMessage(), excepcion);
	    }
	}

	@Override
	public String toString() {
		return nombre + " (" + getDescripcion() + ", " + (esCompetitivo() ? "competitivo" : "recreativo") + ", " + estado + ")";
	}
	
	
}
