package co.edu.unbosque.shopflow.facturacion.colombia;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/** Producto concreto (Colombia): factura electrónica de la DIAN, numerada FE-CO-n. */
class FacturaElectronicaDian implements GeneradorComprobante {

    private final AtomicLong consecutivo;

    FacturaElectronicaDian(AtomicLong consecutivo) {
        this.consecutivo = consecutivo;
    }

    @Override
    public Comprobante generar(BigDecimal base, String nombreImpuesto, BigDecimal impuesto) {
        return new Comprobante("Factura electrónica (DIAN)", "FE-CO-" + consecutivo.incrementAndGet(),
                base, nombreImpuesto, impuesto, base.add(impuesto));
    }
}
