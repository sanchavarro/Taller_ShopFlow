package co.edu.unbosque.shopflow.facturacion.mexico;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/** Producto concreto (México): comprobante CFDI del SAT, numerado CFDI-MX-n. */
class CfdiSat implements GeneradorComprobante {

    private final AtomicLong consecutivo;

    CfdiSat(AtomicLong consecutivo) {
        this.consecutivo = consecutivo;
    }

    @Override
    public Comprobante generar(BigDecimal base, String nombreImpuesto, BigDecimal impuesto) {
        return new Comprobante("CFDI (SAT)", "CFDI-MX-" + consecutivo.incrementAndGet(),
                base, nombreImpuesto, impuesto, base.add(impuesto));
    }
}
