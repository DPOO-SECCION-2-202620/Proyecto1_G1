package Usuarios;

import org.json.JSONObject;

import DeportesConjunto_Competencias.Categoria;
import DeportesConjunto_Competencias.Competencia;
import DeportesConjunto_Competencias.DeporteConjunto;
import DeportesConjunto_Competencias.Equipo;
import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Tienda_Cafeteria_Inventario.Articulo;
import Tienda_Cafeteria_Inventario.SugerenciaArticulo;
import Tienda_Cafeteria_Inventario.Ubicacion;
import TurnosEmpleados.SolicitudCambioTurno;

public class Administrador extends Usuario{

	private ClubSeneca club;
	
	public Administrador(String nombre, String login, String password) {
		super(nombre,login,password);
	}
	
	public void setClub(ClubSeneca club) {
		this.club = club;
	}

	public void resolverSugerencia(SugerenciaArticulo sugerencia, boolean aprobada) {
		club.resolverSugerencia(sugerencia, aprobada);
	}

	public boolean resolverSolicitudTurno(SolicitudCambioTurno solicitud, boolean aprobada) {
		return club.resolverSolicitudTurno(solicitud, aprobada);
	}

	public Equipo crearEquipo(String nombre, int cupoMaximo, DeporteConjunto deporte, Categoria categoria)
			throws ReglaNegocioException {
		return club.crearEquipo(nombre, cupoMaximo, deporte, categoria);
	}

	public boolean darDeBajaEquipo(Equipo equipo) {
		return club.darDeBajaEquipo(equipo);
	}

	public boolean darDeBajaSocio(Socio socio) {
		return club.darDeBajaSocio(socio);
	}

	public boolean inscribirEquipoEnCompetencia(Equipo equipo, Competencia competencia) {
		return club.inscribirEquipoEnCompetencia(equipo, competencia);
	}

	public boolean moverArticulo(Articulo articulo, Ubicacion origen, Ubicacion destino) {
		return club.moverArticulo(articulo, origen, destino);
	}

	public void reabastecerTienda(Articulo articulo, int cantidad) throws ReglaNegocioException {
		club.reabastecerTienda(articulo, cantidad);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("tipo", "Administrador");
		return json;
	}
	
}
