package TorneosInternos;

import java.time.LocalDate;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.NivelJugador;
import Excepciones.ReglaNegocioException;
import Usuarios.Empleado;

public class TorneoIndividual extends Torneo{

	private DisciplinaIndividual disciplina;
	private NivelJugador nivel;
	private Modalidad modalidad;

	public TorneoIndividual(String nombre, int cupos, LocalDate fechaInicio, LocalDate fechaLimitePreferencial,
			Premiacion premiacion, DisciplinaIndividual disciplina, NivelJugador nivel, Modalidad modalidad)
			throws ReglaNegocioException {
		super(nombre, cupos, fechaInicio, fechaLimitePreferencial, premiacion);
		this.disciplina = disciplina;
		this.nivel = nivel;
		this.modalidad = modalidad;
}

	@Override
	protected void validarParticipante(InscripcionTorneo inscripcion) throws ReglaNegocioException {
		if (!(inscripcion instanceof InscripcionTorneoIndividual)) {
	        throw new ReglaNegocioException("En este torneo se inscriben jugadores");
	    }
	    InscripcionTorneoIndividual individual = (InscripcionTorneoIndividual) inscripcion;

	    if (esDobles() && !individual.esPareja()) {
	        throw new ReglaNegocioException("Este torneo es de dobles: debe indicar un compañero");
	    }
	    if (!esDobles() && individual.esPareja()) {
	        throw new ReglaNegocioException("Este torneo es de sencillos: no se admite compañero");
	    }

	    for (JugadorTorneo jugador : individual.getJugadores()) {
	        if (jugador.getNivelEn(disciplina) != nivel) {
	            throw new ReglaNegocioException(jugador.getNombre() + " no tiene el nivel del torneo. Nivel requerido: " + nivel);
	        }
	        if (jugador.esEmpleado() && ((Empleado) jugador).tieneTurnoEl(getFechaInicio())) {
	            throw new ReglaNegocioException("El empleado está de turno el día del torneo");
	        }
	    }
		
	}

	@Override
	public String getDescripcion() {
		return disciplina.getNombre() + " " + nivel + " " + modalidad;
	}
	
	public boolean esDobles() {
		return modalidad == Modalidad.DOBLES;
	}

	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}

	public NivelJugador getNivel() {
		return nivel;
	}

	public Modalidad getModalidad() {
		return modalidad;
	}
}
