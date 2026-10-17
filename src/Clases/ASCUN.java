package Clases;

import java.util.ArrayList;
import java.util.List;

public class ASCUN extends Competencia{

	private FaseASCUN fase;

	public ASCUN(String nombre, String entidadOrganizadora, int anio, String formatoJuego, int nominaMinima,
			Categoria categoria, DeporteConjunto deporte, FaseASCUN fase) {
		super("ASCUN", "ASCUN", anio, "Grupos y eliminación directa", nominaMinima, categoria, deporte);
		this.fase = FaseASCUN.GRUPOS;
	}
	
	public FaseASCUN getFase() {
		return fase;
	}
	
	public void avanzarFase() {
		 if (fase == FaseASCUN.GRUPOS) {
		        fase = FaseASCUN.ELIMINACION_DIRECTA;
		    }
	}
	public List<Equipo> determinarClasificados() {
		List<Equipo> clasificados = new ArrayList<>();
		for (InscripcionCompetencia inscripcion : getInscripciones()) {
	        Equipo equipo = inscripcion.getEquipo();
	        int ganados = 0;
	        int perdidos = 0;
	        for (PartidoExterno partido : getListaPartidos()) {
	            if (partido.getEquipo() == equipo) {
	                if (partido.getMarcadorPropio() > partido.getMarcadorRival()) {
	                    ganados++;
	                } else if (partido.getMarcadorPropio() < partido.getMarcadorRival()) {
	                    perdidos++;
	                }
	            }
	        }
	        if (ganados > perdidos) {
	            clasificados.add(equipo);
	        }
	    }
	    return clasificados;
	}

	@Override
	public void registrarPartido(PartidoExterno partido) {
		agregarALista(partido);
	}
}
