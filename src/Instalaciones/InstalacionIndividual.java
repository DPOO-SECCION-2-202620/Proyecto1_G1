package Instalaciones;

import java.time.LocalDateTime;

import org.json.JSONObject;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;
import DeportesIndividuales.TenisDeMesa;
import Usuarios.Socio;

public abstract class InstalacionIndividual extends Instalacion{

	private boolean techado;
	private int salon;
	private DisciplinaIndividual disciplina;
	
	public InstalacionIndividual(String nombre, int capacidadMaxima, boolean techado, int salon,
			DisciplinaIndividual disciplina) {
		super(nombre, capacidadMaxima);
		this.techado = techado;
		this.salon = salon;
		this.disciplina = disciplina;
		disciplina.agregarInstalacion(this);
	}
	
	public boolean admiteModalidad(Modalidad modalidad, int numeroJugadores) {
		if ((modalidad == Modalidad.SENCILLOS && numeroJugadores == 2) || (modalidad == Modalidad.DOBLES && numeroJugadores == 4)) {
			return true;
		}
		return false;
	}
	
	public abstract boolean permiteBebidaCaliente();
	
	public boolean puedeUsar(Socio socio, LocalDateTime momento) {
		if (!socio.llevaBebidaCaliente(momento) || permiteBebidaCaliente()==true) {
			return true;
		}
		return false;
	}
	
	public boolean isTechado() {
		return techado;
	}

	public DisciplinaIndividual getDisciplina() {
		return disciplina;
	}

	public int getSalon() {
		return salon;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();  
		json.put("techado", techado);
		json.put("salon", salon);
		return json;
	}
	
	public static InstalacionIndividual fromJSON(JSONObject json, DisciplinaIndividual disciplina) {
		String tipo = json.getString("tipo");

		switch (tipo) {
			case "CanchaTenis":
				return CanchaTenis.fromJSON(json, (Tenis)disciplina);
			case "CanchaPadel":
				return CanchaPadel.fromJSON(json, (Padel)disciplina);
			case "MesaTenisDeMesa":
				return MesaTenisDeMesa.fromJSON(json, (TenisDeMesa)disciplina);
			default:
				throw new IllegalArgumentException("Instalación desconocida: " + tipo);
		}
	}
	
}
