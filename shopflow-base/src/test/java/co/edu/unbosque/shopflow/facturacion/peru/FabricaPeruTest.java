package co.edu.unbosque.shopflow.facturacion.peru;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import co.edu.unbosque.shopflow.facturacion.colombia.FabricaColombia;
import co.edu.unbosque.shopflow.facturacion.mexico.FabricaMexico;
import co.edu.unbosque.shopflow.pedidos.ItemPedido;
import co.edu.unbosque.shopflow.pedidos.Pedido;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Reto A: Perú se factura con solo agregar FabricaPeru, sin tocar ServicioFacturacion. */
class FabricaPeruTest {

    private final ServicioFacturacion servicio =
            new ServicioFacturacion(List.of(new FabricaColombia(), new FabricaMexico(), new FabricaPeru()));

    private Pedido pedidoPeru(String documento) {
        return new Pedido.Builder("CLI-010", documento, "PE", "luis.quispe@correo.pe")
                .item(new ItemPedido("SKU-003", "Monitor 27 pulgadas", 1, new BigDecimal("320.00")))
                .direccionEnvio("Av. Larco 1301, Miraflores, Lima")
                .build();
    }

    @Test
    void pedidoDePeruSaleConFacturaSunatEIgv18() {
        Comprobante c = servicio.facturar(pedidoPeru("20123456789"));
        assertEquals("Factura electrónica (SUNAT)", c.tipo());
        assertEquals("IGV 18%", c.nombreImpuesto());
        assertEquals(new BigDecimal("57.60"), c.impuesto());
        assertEquals(new BigDecimal("377.60"), c.total());
    }

    @Test
    void pedidoDePeruConCedulaColombianaEsRechazado() {
        assertThrows(IllegalArgumentException.class, () -> servicio.facturar(pedidoPeru("1020304050")));
    }
}
