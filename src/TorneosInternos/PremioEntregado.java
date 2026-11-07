package TorneosInternos;

import org.json.JSONObject;
import java.util.Map;
import Usuarios.Usuario;

public class PremioEntregado {
	 
	private int puesto;
	private JugadorTorneo jugador;
	private double montoEfectivo;
	private BonoDescuento bono;
 
	public PremioEntregado(int puesto, JugadorTorneo jugador, double montoEfectivo, BonoDescuento bono) {
		this.puesto = puesto;
		this.jugador = jugador;
		this.montoEfectivo = montoEfectivo;
		this.bono = bono;
	}
 
	public int getPuesto() {
		return puesto;
	}
 
	public JugadorTorneo getJugador() {
		return jugador;
	}
 
	public double getMontoEfectivo() {
		return montoEfectivo;
	}
 
	public BonoDescuento getBono() {
		return bono;
	}
	
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    json.put("puesto", puesto);
	    json.put("jugador", jugador.getLogin());
	    json.put("montoEfectivo", montoEfectivo);
	    if (bono != null) {
	        json.put("indiceBono", jugador.getBonos().indexOf(bono));
	    }
	    return json;
	}

	public static PremioEntregado fromJSON(JSONObject json, Map<String, Usuario> mapaUsuarios) {
	    JugadorTorneo jugador = InscripcionTorneo.jugadorPorLogin(json.getString("jugador"), mapaUsuarios);
	    BonoDescuento bono = null;
	    if (json.has("indiceBono")) {
	        int indice = json.getInt("indiceBono");
	        if (indice >= 0 && indice < jugador.getBonos().size()) {
	            bono = jugador.getBonos().get(indice);
	        }
	    }
	    return new PremioEntregado(json.getInt("puesto"), jugador, json.getDouble("montoEfectivo"), bono);
	}
}