package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/** Producto concreto (Perú): factura electrónica de la SUNAT, numerada FE-PE-n. */
class FacturaElectronicaSunat implements GeneradorComprobante {

    private final AtomicLong consecutivo;

    FacturaElectronicaSunat(AtomicLong consecutivo) {
        this.consecutivo = consecutivo;
    }

    @Override
    public Comprobante generar(BigDecimal base, String nombreImpuesto, BigDecimal impuesto) {
        return new Comprobante("Factura electrónica (SUNAT)", "FE-PE-" + consecutivo.incrementAndGet(),
                base, nombreImpuesto, impuesto, base.add(impuesto));
    }
}
