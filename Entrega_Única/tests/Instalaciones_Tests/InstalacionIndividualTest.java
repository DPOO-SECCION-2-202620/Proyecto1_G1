package Instalaciones_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Tenis;
import DeportesIndividuales.TenisDeMesa;
import Instalaciones.CanchaPadel;
import Instalaciones.CanchaTenis;
import Instalaciones.InstalacionIndividual;
import Instalaciones.MesaTenisDeMesa;
import Usuarios.Socio;

public class InstalacionIndividualTest
{
    private Tenis tenis;
    private Padel padel;
    private TenisDeMesa tenisDeMesa;

    private InstalacionIndividual canchaTenis;
    private InstalacionIndividual canchaPadel;
    private InstalacionIndividual mesa;

    private Socio socioSinBebida;
    private Socio socioConBebida;
    private LocalDateTime momento;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        padel = new Padel(1, "Padel");
        tenisDeMesa = new TenisDeMesa(3, "TenisDeMesa");

        canchaTenis = new CanchaTenis("Tenis 1", true, 5, tenis);
        canchaPadel = new CanchaPadel("Padel 1", false, 2, padel);
        mesa = new MesaTenisDeMesa("Mesa 1", true, 7, tenisDeMesa);

        momento = LocalDateTime.of(2026, 11, 4, 10, 0);

        socioSinBebida = new Socio(20, "Ana Torres", "ana", "1234");

        socioConBebida = new Socio(21, "Pedro Gil", "pgil", "5678");
        socioConBebida.registrarBebidaCaliente(momento);
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testGetters()
    {
        assertEquals("Tenis 1", canchaTenis.getNombre(), "El nombre de la instalación no es el esperado.");
        assertEquals(4, canchaTenis.getCapacidadMaxima(), "La capacidad de la instalación no es la esperada.");
        assertTrue(canchaTenis.isTechado(), "La cancha de tenis debe estar techada.");
        assertFalse(canchaPadel.isTechado(), "La cancha de pádel no debe estar techada.");
        assertEquals(5, canchaTenis.getSalon(), "El salón de la instalación no es el esperado.");
        assertSame(tenis, canchaTenis.getDisciplina(), "La disciplina de la instalación no es la esperada.");
    }

    @Test
    void testseRegistraEnLaDisciplina()
    {
        assertEquals(1, tenis.getInstalaciones().size(), "La disciplina debe tener la instalación registrada.");
        assertSame(canchaTenis, tenis.getInstalaciones().get(0), "La instalación registrada no es la esperada.");
    }

    @Test
    void testAdmiteModalidadSencillos()
    {
        assertTrue(canchaTenis.admiteModalidad(Modalidad.SENCILLOS, 2), "Sencillos con dos jugadores debe admitirse.");
        assertFalse(canchaTenis.admiteModalidad(Modalidad.SENCILLOS, 4), "Sencillos con cuatro jugadores no debe admitirse.");
        assertFalse(canchaTenis.admiteModalidad(Modalidad.SENCILLOS, 3), "Sencillos con tres jugadores no debe admitirse.");
    }

    @Test
    void testAdmiteModalidadDobles()
    {
        assertTrue(canchaTenis.admiteModalidad(Modalidad.DOBLES, 4), "Dobles con cuatro jugadores debe admitirse.");
        assertFalse(canchaTenis.admiteModalidad(Modalidad.DOBLES, 2), "Dobles con dos jugadores no debe admitirse.");
    }

    @Test
    void testPuedeUsarSinBebidaCaliente()
    {
        assertTrue(canchaPadel.puedeUsar(socioSinBebida, momento), "Un socio sin bebida caliente puede usar cualquier instalación.");
        assertTrue(canchaTenis.puedeUsar(socioSinBebida, momento), "Un socio sin bebida caliente puede usar cualquier instalación.");
    }

    @Test
    void testPuedeUsarConBebidaCalienteEnInstalacionQueNoLaPermite()
    {
        assertFalse(canchaPadel.puedeUsar(socioConBebida, momento.plusMinutes(10)), "Un socio con bebida caliente no puede usar la cancha de pádel.");
        assertFalse(mesa.puedeUsar(socioConBebida, momento.plusMinutes(10)), "Un socio con bebida caliente no puede usar una mesa techada.");
    }

    @Test
    void testPuedeUsarConBebidaCalienteEnInstalacionQueLaPermite()
    {
        assertTrue(canchaTenis.puedeUsar(socioConBebida, momento.plusMinutes(10)), "Un socio con bebida caliente puede usar la cancha de tenis.");
    }

    @Test
    void testPuedeUsarDespuesDeQueSeAcabeLaRestriccion()
    {
        assertTrue(canchaPadel.puedeUsar(socioConBebida, momento.plusMinutes(30)), "Pasados los 30 minutos el socio puede usar la cancha de pádel.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = canchaTenis.toJSON();

        assertEquals("Tenis 1", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(4, json.getInt("capacidadMaxima"), "La capacidad en el JSON no es la esperada.");
        assertTrue(json.getBoolean("techado"), "El valor techado en el JSON no es el esperado.");
        assertEquals(5, json.getInt("salon"), "El salón en el JSON no es el esperado.");
    }

    @Test
    void testFromJSONTenis()
    {
        InstalacionIndividual creada = InstalacionIndividual.fromJSON(canchaTenis.toJSON(), new Tenis(2, "Tenis"));

        assertTrue(creada instanceof CanchaTenis, "El tipo CanchaTenis debe crear una cancha de tenis.");
        assertEquals(5, creada.getSalon(), "El salón de la instalación creada no es el esperado.");
    }

    @Test
    void testFromJSONPadel()
    {
        InstalacionIndividual creada = InstalacionIndividual.fromJSON(canchaPadel.toJSON(), new Padel(1, "Padel"));

        assertTrue(creada instanceof CanchaPadel, "El tipo CanchaPadel debe crear una cancha de pádel.");
        assertFalse(creada.isTechado(), "La cancha de pádel creada no debe estar techada.");
    }

    @Test
    void testFromJSONTenisDeMesa()
    {
        InstalacionIndividual creada = InstalacionIndividual.fromJSON(mesa.toJSON(), new TenisDeMesa(3, "TenisDeMesa"));

        assertTrue(creada instanceof MesaTenisDeMesa, "El tipo MesaTenisDeMesa debe crear una mesa.");
    }

    @Test
    void testFromJSONTipoDesconocido()
    {
        JSONObject json = canchaTenis.toJSON();
        json.put("tipo", "CanchaFutbol");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> InstalacionIndividual.fromJSON(json, tenis), "Debe lanzarse IllegalArgumentException si el tipo no es individual.");

        assertEquals("Instalación desconocida: CanchaFutbol", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }

    @Test
    void testFromJSONDisciplinaIncorrecta()
    {
        JSONObject json = canchaTenis.toJSON();

        assertThrows(ClassCastException.class, () -> InstalacionIndividual.fromJSON(json, padel), "La disciplina debe corresponder al tipo de instalación.");
    }
}
