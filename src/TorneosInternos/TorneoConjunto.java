package TorneosInternos;

import java.time.LocalDate;
import org.json.JSONObject;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import DeportesConjunto_Competencias.InscripcionEquipo;
import Excepciones.ReglaNegocioException;

public class TorneoConjunto extends Torneo{
	
	private DeporteConjunto deporte;
	private Categoria categoria;

	public TorneoConjunto(String nombre, int cupos, LocalDate fechaInicio, LocalDate fechaLimitePreferencial,
			Premiacion premiacion, DeporteConjunto deporte, Categoria categoria) throws ReglaNegocioException {
		super(nombre, cupos, fechaInicio, fechaLimitePreferencial, premiacion);
		this.deporte = deporte;
		this.categoria = categoria;
	}
	
	@Override
	protected void validarParticipante(InscripcionTorneo inscripcion) throws ReglaNegocioException {
		if (!(inscripcion instanceof InscripcionTorneoEquipo)) {
	        for (JugadorTorneo j : inscripcion.getJugadores()) {
	            if (j.esEmpleado()) {
	                throw new ReglaNegocioException("Los empleados solo pueden participar en torneos individuales");
	            }
	        }
	        throw new ReglaNegocioException("En este torneo se inscriben equipos completos");
	    }

	    Equipo equipo = ((InscripcionTorneoEquipo) inscripcion).getEquipo();
	    int minimo = deporte.getMinimoJugadores();

	    if (!equipo.getDeporte().getNombre().equals(deporte.getNombre()) || equipo.getCategoria() != categoria) {
	        throw new ReglaNegocioException("Incompatibilidad: el torneo es de " + deporte.getNombre() + " " + categoria);
	    }
	    int inscritos = equipo.getInscripciones().size();
	    if (inscritos < minimo) {
	        throw new ReglaNegocioException("Le faltan " + (minimo-inscritos) + " jugadores para el mínimo de " + minimo);
	    }
	    int habilitados = equipo.contarJugadoresHabilitados();
	    if (habilitados < minimo) {
	        String mensaje = "Solo hay " + habilitados + " de " + minimo + " jugadores habilitados. No disponibles: ";
	        for (InscripcionEquipo suspendido : equipo.getInscripcionesSuspendidas()) {
	            mensaje += suspendido.getJugador().getNombre() + " (suspendido), ";
	        }
	        throw new ReglaNegocioException(mensaje);
	    }
		
	}

	public DeporteConjunto getDeporte() {
		return deporte;
	}

	public Categoria getCategoria() {
		return categoria;
	}
	
	@Override
	public JSONObject toJSON() {
	    JSONObject json = super.toJSON();
	    json.put("tipo", "CONJUNTO");
	    json.put("deporte", deporte.getNombre());
	    json.put("categoria", categoria.name());
	    return json;
	}

	@Override
	public String getDescripcion() {
		return deporte.getNombre() + " " + categoria;
	}

}
