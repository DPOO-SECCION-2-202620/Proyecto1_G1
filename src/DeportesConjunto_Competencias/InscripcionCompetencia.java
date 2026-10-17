package DeportesConjunto_Competencias;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Socio;

public class InscripcionCompetencia {

	private Equipo equipo;
	private Competencia competencia;
	private List<RegistroNomina> nomina;
	public InscripcionCompetencia(Equipo equipo, Competencia competencia) {
		this.equipo = equipo;
		this.competencia = competencia;
		this.nomina = new ArrayList<>();
	}
	public Equipo getEquipo() {
		return equipo;
	}
	public Competencia getCompetencia() {
		return competencia;
	}
	public List<RegistroNomina> getNomina() {
		return nomina;
	}
	
	public RegistroNomina getRegistroDe(Socio jugador) {
		for (RegistroNomina registro : nomina) {
			if (registro.getInscripcionEquipo().getJugador() == jugador) {
				return registro;
			}
		}
		return null;
	}
	
	public boolean agregarANomina(Socio jugador) {
		InscripcionEquipo inscripcionEquipo = equipo.getInscripcionDe(jugador);
	    if (inscripcionEquipo == null) {
	        return false;
	    }
	    if (getRegistroDe(jugador) != null) {
	        return false;
	    }
	    nomina.add(new RegistroNomina(inscripcionEquipo));
	    return true;
	}
	
	public boolean puedeAlinear(Socio jugador) {
	    RegistroNomina registro = getRegistroDe(jugador);
	    if (registro == null) {
	        return false;
	    }
	    return registro.estaHabilitado();
	}
	
}
