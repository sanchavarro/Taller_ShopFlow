package co.edu.unbosque.shopflow.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** El Builder nunca entrega un pedido inválido. */
class PedidoBuilderTest {

    private static final ItemPedido TECLADO = new ItemPedido("SKU-001", "Teclado mecánico", 1, new BigDecimal("85.00"));
    private static final ItemPedido MOUSE = new ItemPedido("SKU-002", "Mouse inalámbrico", 2, new BigDecimal("25.50"));

    private Pedido.Builder builderColombia() {
        return new Pedido.Builder("CLI-001", "1020304050", "CO", "ana.perez@correo.com");
    }

    @Test
    void sinItemsLanzaIllegalArgumentException() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builderColombia().direccionEnvio("Calle 134 # 9-51").build());
        assertEquals("El pedido debe tener al menos un ítem", e.getMessage());
    }

    @Test
    void domicilioSinDireccionLanzaIllegalArgumentException() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builderColombia().item(TECLADO).build());
        assertTrue(e.getMessage().contains("dirección"));
    }

    @Test
    void recogerEnTiendaYExpressALaVezLanzaIllegalArgumentException() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builderColombia().item(TECLADO).recogerEnTienda(true).envioExpress(true).build());
        assertTrue(e.getMessage().contains("express"));
    }

    @Test
    void notaDeRegaloDeMasDe140CaracteresLanzaIllegalArgumentException() {
        String nota = "x".repeat(141);
        assertThrows(IllegalArgumentException.class,
                () -> builderColombia().item(TECLADO).recogerEnTienda(true).notaRegalo(nota).build());
    }

    @Test
    void recogerEnTiendaNoExigeDireccion() {
        Pedido pedido = builderColombia().item(TECLADO).recogerEnTienda(true).build();
        assertTrue(pedido.recogerEnTienda());
    }

    @Test
    void cuponBosque10Aplica10PorCientoSobreElSubtotal() {
        Pedido pedido = builderColombia()
                .item(TECLADO).item(MOUSE)
                .direccionEnvio("Calle 134 # 9-51, Bogotá")
                .cupon("BOSQUE10")
                .build();
        assertEquals(new BigDecimal("136.00"), pedido.subtotal());
        assertEquals(new BigDecimal("13.60"), pedido.descuento());
        assertEquals(new BigDecimal("122.40"), pedido.subtotalConDescuento());
    }

    @Test
    void pedidoNoTieneConstructorPublicoNiSetters() {
        for (Constructor<?> c : Pedido.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(c.getModifiers()), "El constructor de Pedido debe ser privado");
        }
        boolean haySetters = Arrays.stream(Pedido.class.getMethods())
                .map(Method::getName)
                .anyMatch(n -> n.startsWith("set"));
        assertTrue(!haySetters, "Pedido no debe tener setters");
    }
}
