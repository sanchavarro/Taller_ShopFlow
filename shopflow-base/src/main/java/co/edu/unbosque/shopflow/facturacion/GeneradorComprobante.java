package co.edu.unbosque.shopflow.facturacion;

import java.math.BigDecimal;

/** Producto abstracto 3 de la familia de facturación: el comprobante electrónico. */
public interface GeneradorComprobante {

    Comprobante generar(BigDecimal base, String nombreImpuesto, BigDecimal impuesto);
}
