package Consola;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Excepciones.ReglaNegocioException;
import NucleoSistema.ClubSeneca;
import Tienda_Cafeteria_Inventario.BonoDescuento;
import Tienda_Cafeteria_Inventario.Producto;
import Tienda_Cafeteria_Inventario.Rubro;
import Tienda_Cafeteria_Inventario.Venta;
import Usuarios.Socio;

public class OpcionComprarConBono implements OpcionMenu {

    private final ClubSeneca club;
    private final Socio socio;
    private final LectorConsola lector;

    public OpcionComprarConBono(ClubSeneca club, Socio socio, LectorConsola lector) {
        this.club = club;
        this.socio = socio;
        this.lector = lector;
    }

    @Override
    public String getTitulo() {
        return "Comprar usando un bono de descuento";
    }

    @Override
    public void ejecutar() throws ReglaNegocioException {
        List<BonoDescuento> disponibles = new ArrayList<>();
        for (BonoDescuento bono : socio.getBonos()) {
            if (!bono.estaUsado()) {
                disponibles.add(bono);
            }
        }
        if (disponibles.isEmpty()) {
            System.out.println("No tiene bonos de descuento disponibles.");
            return;
        }

        System.out.println("\nBonos disponibles (no acumulables con otros descuentos):");
        for (int i = 0; i < disponibles.size(); i++) {
            BonoDescuento bono = disponibles.get(i);
            System.out.println((i + 1) + ". " + porcentaje(bono) + "% de descuento - torneo \""
                    + bono.getTorneoOrigen() + "\"");
        }
        BonoDescuento bono = disponibles.get(lector.leerIndice("Elija el bono", disponibles.size()));

        int lugar = lector.leerEntero("Dónde compra (1 = tienda, 2 = cafetería, 0 = cancelar)", 0, 2);
        if (lugar == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        Rubro rubro = Rubro.CAFETERIA;
        if (lugar == 1) {
            rubro = Rubro.TIENDA;
        }

        List<Producto> catalogo = club.getProductosDisponibles(rubro);
        if (catalogo.isEmpty()) {
            System.out.println("No hay productos disponibles en este momento.");
            return;
        }

        Map<Producto, Integer> carrito = new LinkedHashMap<>();
        while (true) {
            System.out.println("\nProductos (precio sin impuestos):");
            for (int i = 0; i < catalogo.size(); i++) {
                Producto producto = catalogo.get(i);
                System.out.println((i + 1) + ". " + producto.getNombre() + " - "
                        + FormatoTorneo.dinero(producto.getPrecio()) + " - disponibles: " + restantes(producto, carrito));
            }
            int eleccion = lector.leerEntero("Producto a agregar (0 = terminar)", 0, catalogo.size());
            if (eleccion == 0) {
                break;
            }
            Producto producto = catalogo.get(eleccion - 1);
            int restantes = restantes(producto, carrito);
            if (restantes <= 0) {
                System.out.println("Ya agregó todas las unidades disponibles de ese producto.");
                continue;
            }
            int cantidad = lector.leerEntero("Cantidad", 1, restantes);
            if (carrito.containsKey(producto)) {
                carrito.put(producto, carrito.get(producto) + cantidad);
            } else {
                carrito.put(producto, cantidad);
            }
        }
        if (carrito.isEmpty()) {
            System.out.println("No eligió ningún producto. Operación cancelada.");
            return;
        }

        double propina = 0;
        if (rubro == Rubro.CAFETERIA) {
            propina = lector.leerDecimal("Propina en pesos (0 si no desea dejar)", 0, 1_000_000);
        }

        System.out.println("\nResumen de la compra:");
        carrito.forEach((producto, cantidad) -> System.out.println("  " + cantidad + " x " + producto.getNombre()));
        if (!lector.confirmar("¿Confirma la compra con el bono del " + porcentaje(bono) + "%?")) {
            System.out.println("Operación cancelada.");
            return;
        }

        Venta venta = club.registrarVentaConBono(socio, carrito, rubro, null, bono, propina);
        System.out.println("Compra registrada. El bono del " + porcentaje(bono) + "% quedó usado.");
        System.out.println("Total pagado (con impuestos" + (propina > 0 ? " y propina" : "") + "): "
                + FormatoTorneo.dinero(venta.getTotal()));
        System.out.println("Puntos de fidelidad ganados: " + venta.getPuntosGenerados());
    }

    private int restantes(Producto producto, Map<Producto, Integer> carrito) {
        int enElCarrito = 0;
        if (carrito.containsKey(producto)) {
            enElCarrito = carrito.get(producto);
        }
        return producto.getCantidad() - enElCarrito;
    }

    private long porcentaje(BonoDescuento bono) {
        return Math.round(bono.getPorcentaje() * 100);
    }
}