package co.edu.unbosque.shopflow.facturacion;

import java.math.BigDecimal;

/** Comprobante electrónico que se entrega con el pedido. Valores en dólares (USD). */
public record Comprobante(
        String tipo,
        String numero,
        BigDecimal base,
        String nombreImpuesto,
        BigDecimal impuesto,
        BigDecimal total) { }
