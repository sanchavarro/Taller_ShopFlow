package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import co.edu.unbosque.shopflow.facturacion.FabricaFacturacion;
import co.edu.unbosque.shopflow.facturacion.GeneradorComprobante;
import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/**
 * Reto A · Fábrica concreta de Perú: IGV 18 %, RUC de 11 dígitos y factura electrónica de
 * la SUNAT. Se agrega sin modificar ninguna clase existente: Spring la encuentra por ser
 * @Component y ServicioFacturacion la recibe en su List<FabricaFacturacion>.
 */
@Component
public class FabricaPeru implements FabricaFacturacion {

    private final AtomicLong consecutivo = new AtomicLong();

    @Override
    public String pais() {
        return "PE";
    }

    @Override
    public CalculadoraImpuestos crearCalculadora() {
        return new IgvPeru();
    }

    @Override
    public ValidadorIdentificacion crearValidador() {
        return new ValidadorRuc();
    }

    @Override
    public GeneradorComprobante crearGenerador() {
        return new FacturaElectronicaSunat(consecutivo);
    }
}
