package NucleoSistema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

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
	
	public ClubSeneca cargar() throws IOException {
		Path archivo = Path.of(rutaCarpeta, "club.json");
		if (!Files.exists(archivo)) {
			return null;   
		}
		JSONObject json = new JSONObject(Files.readString(archivo));
		ClubSeneca club = ClubSeneca.fromJSON(json);
		club.setPersistencia(this);
		return club;
	}
}
