package NucleoSistema_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import NucleoSistema.Autenticador;
import Usuarios.Administrador;
import Usuarios.Entrenador;
import Usuarios.Socio;
import Usuarios.Usuario;

public class AutenticadorTest
{
    private Autenticador autenticador;

    private Socio socio;
    private Administrador administrador;
    private Entrenador entrenador;
    private List<Usuario> usuarios;

    @BeforeEach
    void setUp() throws Exception
    {
        autenticador = new Autenticador();

        socio = new Socio(20, "Ana Torres", "ana", "1234");
        administrador = new Administrador("Carlos Admin", "cadmin", "admin1");
        entrenador = new Entrenador(40, "Jorge Ruiz", "jruiz", "pass", "ENT01");

        usuarios = new ArrayList<Usuario>();
        usuarios.add(socio);
        usuarios.add(administrador);
        usuarios.add(entrenador);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testAutenticarSocio()
    {
        assertSame(socio, autenticador.autenticar("ana", usuarios, "1234"), "Debe autenticarse al socio con sus credenciales.");
    }

    @Test
    void testAutenticarAdministrador()
    {
        assertSame(administrador, autenticador.autenticar("cadmin", usuarios, "admin1"), "Debe autenticarse al administrador con sus credenciales.");
    }

    @Test
    void testAutenticarEntrenador()
    {
        assertSame(entrenador, autenticador.autenticar("jruiz", usuarios, "pass"), "Debe autenticarse al entrenador con sus credenciales.");
    }

    @Test
    void testAutenticarPasswordIncorrecta()
    {
        assertNull(autenticador.autenticar("ana", usuarios, "9999"), "Con una contraseña incorrecta no debe autenticarse.");
        assertNull(autenticador.autenticar("ana", usuarios, "1234 "), "La contraseña debe coincidir exactamente.");
    }

    @Test
    void testAutenticarLoginInexistente()
    {
        assertNull(autenticador.autenticar("nadie", usuarios, "1234"), "Con un login inexistente no debe autenticarse.");
    }

    @Test
    void testAutenticarDistingueMayusculasEnElLogin()
    {
        assertNull(autenticador.autenticar("ANA", usuarios, "1234"), "El login debe distinguir mayúsculas de minúsculas.");
    }

    @Test
    void testAutenticarPasswordDeOtroUsuario()
    {
        assertNull(autenticador.autenticar("ana", usuarios, "admin1"), "La contraseña de otro usuario no debe servir.");
    }

    @Test
    void testAutenticarConParametrosNulos()
    {
        assertNull(autenticador.autenticar(null, usuarios, "1234"), "Con login nulo no debe autenticarse.");
        assertNull(autenticador.autenticar("ana", null, "1234"), "Con lista nula no debe autenticarse.");
        assertNull(autenticador.autenticar("ana", usuarios, null), "Con contraseña nula no debe autenticarse.");
    }

    @Test
    void testAutenticarListaVacia()
    {
        assertNull(autenticador.autenticar("ana", new ArrayList<Usuario>(), "1234"), "Con una lista vacía no debe autenticarse.");
    }
}
