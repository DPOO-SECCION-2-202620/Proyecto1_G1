package DeportesConjunto_Competencias;

import org.json.JSONObject;

public abstract class DeporteConjunto {
	
	private String nombre;
	protected int minimoJugadores;

	public DeporteConjunto(String nombre, int minimoJugadores) {
		this.nombre = nombre;
		this.minimoJugadores = minimoJugadores;
	}
	
	public abstract int calcularPuntos(int marcadorPropio, int marcadorRival);
	
	public abstract boolean admiteEmpate();
	
	public int getMinimoJugadores() {
		return minimoJugadores;
	}

	public String getNombre() {
		return nombre;
	}
	
	@Override
	public String toString() {
		return nombre;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	    json.put("nombre", nombre);
	    json.put("minimoJugadores", minimoJugadores);
		return json;
	}
	
}
