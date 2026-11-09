package NucleoSistema_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import NucleoSistema.ClubSeneca;
import NucleoSistema.Persistencia;
import Usuarios.Usuario;

public class PersistenciaTest
{
    @TempDir
    File carpetaTemporal;

    private Persistencia persistencia;
    private ClubSeneca club;
    private Path archivoClub;

    @BeforeEach
    void setUp() throws Exception
    {
        persistencia = new Persistencia(carpetaTemporal.getAbsolutePath());
        club = new ClubSeneca("Club Seneca");
        club.setPersistencia(persistencia);
        archivoClub = carpetaTemporal.toPath().resolve("club.json");
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGuardarCreaElArchivo() throws Exception
    {
        persistencia.guardar(club);

        assertTrue(Files.exists(archivoClub), "Debe crearse el archivo club.json.");
    }

    @Test
    void testGuardarEscribeElContenidoDelClub() throws Exception
    {
        persistencia.guardar(club);

        JSONObject json = new JSONObject(Files.readString(archivoClub));

        assertEquals("Club Seneca", json.getString("nombre"), "El nombre guardado no es el esperado.");
        assertEquals(0, json.getJSONArray("usuarios").length(), "El club vacío no debe tener usuarios guardados.");
    }

    @Test
    void testGuardarCreaLaCarpetaSiNoExiste() throws Exception
    {
        Path subcarpeta = carpetaTemporal.toPath().resolve("nueva").resolve("anidada");
        Persistencia otraPersistencia = new Persistencia(subcarpeta.toString());

        otraPersistencia.guardar(club);

        assertTrue(Files.exists(subcarpeta.resolve("club.json")), "Debe crearse la carpeta y el archivo.");
    }

    @Test
    void testGuardarSobrescribeElArchivo() throws Exception
    {
        persistencia.guardar(club);
        club.crearSocio(20, "Ana Torres", "ana", "1234");

        persistencia.guardar(club);

        JSONObject json = new JSONObject(Files.readString(archivoClub));
        assertEquals(1, json.getJSONArray("usuarios").length(), "El archivo debe reflejar el último estado del club.");
    }

    @Test
    void testCargarSinArchivo() throws Exception
    {
        assertNull(persistencia.cargar(), "Sin archivo guardado no debe cargarse ningún club.");
    }

    @Test
    void testCargar() throws Exception
    {
        club.crearSocio(20, "Ana Torres", "ana", "1234");
        persistencia.guardar(club);

        ClubSeneca cargado = persistencia.cargar();

        assertNotNull(cargado, "Debe cargarse el club guardado.");
        Usuario usuario = cargado.iniciarSesion("ana", "1234");
        assertNotNull(usuario, "El socio guardado debe poder iniciar sesión.");
        assertEquals("Ana Torres", usuario.getNombre(), "El nombre del socio cargado no es el esperado.");
    }

    @Test
    void testCargarAsignaLaMismaPersistencia() throws Exception
    {
        persistencia.guardar(club);
        ClubSeneca cargado = persistencia.cargar();

        cargado.crearSocio(30, "Pedro Gil", "pgil", "5678");

        JSONObject json = new JSONObject(Files.readString(archivoClub));
        assertEquals(1, json.getJSONArray("usuarios").length(), "El club cargado debe seguir guardando en la misma carpeta.");
    }

    @Test
    void testCargarArchivoInvalido() throws Exception
    {
        Files.writeString(archivoClub, "esto no es json");

        assertThrows(org.json.JSONException.class, () -> persistencia.cargar(), "Un archivo con contenido inválido debe lanzar JSONException.");
    }

    @Test
    void testCargarArchivoSinNombre() throws Exception
    {
        Files.writeString(archivoClub, "{}");

        assertThrows(org.json.JSONException.class, () -> persistencia.cargar(), "Un archivo sin nombre de club debe lanzar JSONException.");
    }
}
