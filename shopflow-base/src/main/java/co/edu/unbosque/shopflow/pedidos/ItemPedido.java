package co.edu.unbosque.shopflow.pedidos;

import java.math.BigDecimal;

/** Línea de un pedido. El precio unitario se toma del catálogo, nunca del cliente. */
public record ItemPedido(String sku, String nombre, int cantidad, BigDecimal precioUnitario) {

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
