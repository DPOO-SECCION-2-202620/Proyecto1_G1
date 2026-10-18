package PruebasJson;

import NucleoSistema.ClubSeneca;
import NucleoSistema.Persistencia;

public class PruebaGuardar {
	public static void main(String[] args) throws Exception {
		ClubSeneca club = new ClubSeneca("Club Seneca");
		new Persistencia("datos").guardar(club);
		System.out.println("Guardado");
	}
}