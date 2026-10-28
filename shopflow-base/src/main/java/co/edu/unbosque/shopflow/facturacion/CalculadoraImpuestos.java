package co.edu.unbosque.shopflow.facturacion;

import java.math.BigDecimal;

/** Producto abstracto 1 de la familia de facturación: el impuesto del país. */
public interface CalculadoraImpuestos {

    /** Nombre del impuesto tal como se imprime en el comprobante, por ejemplo "IVA 19%". */
    String nombre();

    /** Impuesto sobre la base, redondeado a dos decimales. */
    BigDecimal calcular(BigDecimal base);
}
