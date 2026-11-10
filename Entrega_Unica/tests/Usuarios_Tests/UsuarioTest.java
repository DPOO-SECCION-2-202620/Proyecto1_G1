package Usuarios_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import Usuarios.Administrador;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;
import Usuarios.Usuario;

public class UsuarioTest
{
    private Usuario administrador;
    private Usuario socio;
    private Usuario entrenador;
    private Usuario fisioterapeuta;

    private Map<String, DisciplinaIndividual> mapaDisciplinas;

    @BeforeEach
    void setUp() throws Exception
    {
        administrador = new Administrador("Carlos Admin", "cadmin", "clave1");
        socio = new Socio(25, "Ana Torres", "ana", "1234");
        entrenador = new Entrenador(40, "Luis Mora", "lmora", "abc", "E01");
        fisioterapeuta = new Fisioterapeuta(35, "Marta Rios", "mrios", "xyz", "F01");

        mapaDisciplinas = new HashMap<String, DisciplinaIndividual>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetNombre()
    {
        assertEquals("Carlos Admin", administrador.getNombre(), "El nombre del usuario no es el esperado.");
    }

    @Test
    void testGetLogin()
    {
        assertEquals("cadmin", administrador.getLogin(), "El login del usuario no es el esperado.");
    }

    @Test
    void testGetPassword()
    {
        assertEquals("clave1", administrador.getPassword(), "La contraseña del usuario no es la esperada.");
    }

    @Test
    void testVerificarPasswordCorrecta()
    {
        assertTrue(administrador.verificarPassword("clave1"), "La contraseña correcta debe ser aceptada.");
    }

    @Test
    void testVerificarPasswordIncorrecta()
    {
        assertFalse(administrador.verificarPassword("clave2"), "Una contraseña incorrecta no debe ser aceptada.");
        assertFalse(administrador.verificarPassword("CLAVE1"), "La contraseña debe distinguir mayúsculas de minúsculas.");
        assertFalse(administrador.verificarPassword(""), "Una contraseña vacía no debe ser aceptada.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = administrador.toJSON();

        assertEquals("Carlos Admin", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals("cadmin", json.getString("login"), "El login en el JSON no es el esperado.");
        assertEquals("clave1", json.getString("password"), "La contraseña en el JSON no es la esperada.");
        assertEquals("Administrador", json.getString("tipo"), "El tipo en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONAdministrador()
    {
        Usuario creado = Usuario.fromJSON(administrador.toJSON(), mapaDisciplinas);

        assertTrue(creado instanceof Administrador, "El tipo Administrador debe crear un Administrador.");
        assertEquals("cadmin", creado.getLogin(), "El login del usuario creado no es el esperado.");
    }

    @Test
    void testFromJSONSocio()
    {
        Usuario creado = Usuario.fromJSON(socio.toJSON(), mapaDisciplinas);

        assertEquals(Socio.class, creado.getClass(), "El tipo Socio debe crear un Socio.");
        assertEquals(25, ((Socio) creado).getEdad(), "La edad del socio creado no es la esperada.");
    }

    @Test
    void testFromJSONEntrenador()
    {
        Usuario creado = Usuario.fromJSON(entrenador.toJSON(), mapaDisciplinas);

        assertTrue(creado instanceof Entrenador, "El tipo Entrenador debe crear un Entrenador.");
        assertEquals("lmora", creado.getLogin(), "El login del entrenador creado no es el esperado.");
    }

    @Test
    void testFromJSONFisioterapeuta()
    {
        Usuario creado = Usuario.fromJSON(fisioterapeuta.toJSON(), mapaDisciplinas);

        assertTrue(creado instanceof Fisioterapeuta, "El tipo Fisioterapeuta debe crear un Fisioterapeuta.");
        assertEquals("mrios", creado.getLogin(), "El login del fisioterapeuta creado no es el esperado.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = administrador.toJSON();
        json.put("tipo", "Visitante");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> Usuario.fromJSON(json, mapaDisciplinas), "Debe lanzarse IllegalArgumentException si el tipo es desconocido.");

        assertEquals("Tipo de usuario desconocido: Visitante", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONSinTipo()
    {
        JSONObject json = administrador.toJSON();
        json.remove("tipo");

        assertThrows(org.json.JSONException.class, () -> Usuario.fromJSON(json, mapaDisciplinas), "Debe lanzarse JSONException si el JSON no tiene tipo.");
    }
}
