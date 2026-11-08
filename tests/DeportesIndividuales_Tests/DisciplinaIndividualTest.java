package DeportesIndividuales_Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeportesIndividuales.DisciplinaIndividual;
import DeportesIndividuales.Modalidad;
import DeportesIndividuales.Padel;
import DeportesIndividuales.Reserva;
import DeportesIndividuales.Tenis;
import DeportesIndividuales.TenisDeMesa;
import Instalaciones.CanchaTenis;
import Usuarios.Socio;

public class DisciplinaIndividualTest
{
    private DisciplinaIndividual tenis;
    private DisciplinaIndividual disciplinaUnaCancha;

    private CanchaTenis cancha1;
    private CanchaTenis cancha2;
    private CanchaTenis cancha3;
    private CanchaTenis canchaUnica;

    private Socio socio;
    private LocalDate fecha;
    private List<Reserva> reservas;

    @BeforeEach
    void setUp() throws Exception
    {
        tenis = new Tenis(2, "Tenis");
        disciplinaUnaCancha = new Tenis(1, "Tenis");

        cancha1 = new CanchaTenis("Cancha 1", true, 1, (Tenis) tenis);
        cancha2 = new CanchaTenis("Cancha 2", true, 1, (Tenis) tenis);
        cancha3 = new CanchaTenis("Cancha 3", true, 1, (Tenis) tenis);
        canchaUnica = new CanchaTenis("Cancha Unica", true, 1, (Tenis) disciplinaUnaCancha);

        socio = new Socio(20, "Ana Torres", "ana", "1234");
        fecha = LocalDate.of(2026, 11, 10);

        reservas = new ArrayList<Reserva>();
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    private Reserva crearReserva(CanchaTenis cancha, LocalDate dia, int horaInicio, int horaFinal)
    {
        return new Reserva(2, LocalTime.of(horaInicio, 0), LocalTime.of(horaFinal, 0), dia, Modalidad.SENCILLOS, socio, cancha);
    }

    @Test
    void testGetters()
    {
        assertEquals("Tenis", tenis.getNombre(), "El nombre de la disciplina no es el esperado.");
        assertEquals(2, tenis.getMaxInstalacionesSimultaneas(), "El máximo de instalaciones simultáneas no es el esperado.");
    }

    @Test
    void testToString()
    {
        assertEquals("Tenis", tenis.toString(), "El texto de la disciplina debe ser su nombre.");
    }

    @Test
    void testInstalacionesRegistradas()
    {
        assertEquals(3, tenis.getInstalaciones().size(), "La disciplina debe tener dos instalaciones.");
        assertSame(cancha1, tenis.getInstalaciones().get(0), "La primera instalación no es la esperada.");
    }

    @Test
    void testAgregarInstalacionNoDuplica()
    {
        tenis.agregarInstalacion(cancha1);

        assertEquals(3, tenis.getInstalaciones().size(), "Una instalación ya registrada no debe duplicarse.");
    }

    @Test
    void testHayCupoSinReservas()
    {
        assertTrue(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "Sin reservas debe haber cupo.");
    }

    @Test
    void testHayCupoConUnaReserva()
    {
        reservas.add(crearReserva(cancha1, fecha, 10, 11));

        assertTrue(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "Con una reserva y un máximo de dos aún debe haber cupo.");
    }

    @Test
    void testHayCupoSinCuposDisponibles()
    {
        reservas.add(crearReserva(cancha1, fecha, 10, 11));
        reservas.add(crearReserva(cancha2, fecha, 10, 12));

        assertFalse(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 30), LocalTime.of(11, 30)), "Con dos reservas solapadas y un máximo de dos no debe haber cupo.");
    }

    @Test
    void testHayCupoReservasNoSolapadas()
    {
        reservas.add(crearReserva(cancha2, fecha, 8, 9));
        reservas.add(crearReserva(cancha3, fecha, 12, 13));

        assertTrue(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "Las reservas en otros horarios no cuentan.");
    }

    @Test
    void testHayCupoReservasContiguas()
    {
        reservas.add(crearReserva(cancha1, fecha, 9, 10));
        reservas.add(crearReserva(cancha2, fecha, 11, 12));

        assertTrue(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "Las reservas contiguas no cuentan como ocupadas.");
    }

    @Test
    void testHayCupoReservasOtroDia()
    {
        reservas.add(crearReserva(cancha1, fecha.plusDays(1), 10, 11));
        reservas.add(crearReserva(cancha2, fecha.plusDays(1), 10, 11));

        assertTrue(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "Las reservas de otro día no cuentan.");
    }

    @Test
    void testHayCupoIgnoraInstalacionesDeOtraDisciplina()
    {
        reservas.add(crearReserva(canchaUnica, fecha, 10, 11));

        assertTrue(tenis.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "Las reservas de instalaciones de otra disciplina no cuentan.");
        assertFalse(disciplinaUnaCancha.hayCupo(reservas, fecha, LocalTime.of(10, 0), LocalTime.of(11, 0)), "La disciplina con un máximo de uno no debe tener cupo.");
    }

    @Test
    void testToJSON()
    {
        JSONObject json = tenis.toJSON();

        assertEquals("Tenis", json.getString("nombre"), "El nombre en el JSON no es el esperado.");
        assertEquals(2, json.getInt("maxInstalacionesSimultaneas"), "El máximo en el JSON no es el esperado.");
        assertEquals(3, json.getJSONArray("instalaciones").length(), "El JSON debe tener tres instalaciones.");
        assertEquals("Cancha 1", json.getJSONArray("instalaciones").getJSONObject(0).getString("nombre"), "La primera instalación en el JSON no es la esperada.");
    }

    @Test
    void testFromJSONTenis()
    {
        DisciplinaIndividual restaurada = DisciplinaIndividual.fromJSON(tenis.toJSON());

        assertTrue(restaurada instanceof Tenis, "El nombre Tenis debe crear una disciplina de tenis.");
        assertEquals(2, restaurada.getMaxInstalacionesSimultaneas(), "El máximo restaurado no es el esperado.");
        assertEquals(3, restaurada.getInstalaciones().size(), "Deben restaurarse las tres instalaciones.");
    }

    @Test
    void testFromJSONPadel()
    {
        JSONObject json = new Padel(3, "Padel").toJSON();

        DisciplinaIndividual restaurada = DisciplinaIndividual.fromJSON(json);

        assertTrue(restaurada instanceof Padel, "El nombre Padel debe crear una disciplina de pádel.");
        assertEquals(3, restaurada.getMaxInstalacionesSimultaneas(), "El máximo restaurado no es el esperado.");
    }

    @Test
    void testFromJSONTenisDeMesa()
    {
        JSONObject json = new TenisDeMesa(4, "TenisDeMesa").toJSON();

        DisciplinaIndividual restaurada = DisciplinaIndividual.fromJSON(json);

        assertTrue(restaurada instanceof TenisDeMesa, "El nombre TenisDeMesa debe crear una disciplina de tenis de mesa.");
    }

    @Test
    void testFromJSONNombreDesconocido()
    {
        JSONObject json = tenis.toJSON();
        json.put("nombre", "Golf");

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> DisciplinaIndividual.fromJSON(json), "Debe lanzarse IllegalArgumentException si la disciplina es desconocida.");

        assertEquals("Tipo de usuario desconocido: Golf", excepcion.getMessage(), "El mensaje de la excepción no es el esperado.");
    }
}
