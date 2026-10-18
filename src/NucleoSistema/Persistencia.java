package NucleoSistema;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Persistencia {

	private String rutaCarpeta;
	
	public Persistencia(String rutaCarpeta) {
		super();
		this.rutaCarpeta = rutaCarpeta;
	}

	public void guardar(ClubSeneca club) throws IOException {
		Path carpeta = Path.of(rutaCarpeta);
		Files.createDirectories(carpeta);

		Files.writeString(carpeta.resolve("club.json"), club.toJSON().toString(2));
	}
	
	public void cargar(ClubSeneca club) {
		
	}
	
}
