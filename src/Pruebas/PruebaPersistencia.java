package Pruebas;

import NucleoSistema.ClubSeneca;
import NucleoSistema.Persistencia;
import Usuarios.Socio;

public class PruebaPersistencia {

	public static void main(String[] args) throws Exception {

		Persistencia p = new Persistencia("datos");

		// 1. Crear un club con algunos datos
		ClubSeneca club = new ClubSeneca("Club Seneca");
		Socio socio = new Socio("Ana", 20, "ana", "1234");
		club.getUsuarios().add(socio);

		// 2. Guardar
		p.guardar(club);
		System.out.println("Guardado en: datos/club.json");

		// 3. Cargar
		ClubSeneca cargado = p.cargar();
		System.out.println("Cargado: " + (cargado != null));

		// 4. Verificar datos
		Socio socioCargado = (Socio) cargado.getUsuarios().get(0);
		System.out.println("Nombre: " + socioCargado.getNombre());
		System.out.println("Edad: " + socioCargado.getEdad());

		// 5. Verificar que no es el mismo objeto original (se reconstruyó)
		System.out.println("Es otro objeto: " + (socioCargado != socio));
	}
}