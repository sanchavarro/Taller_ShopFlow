package co.edu.unbosque.shopflow.pedidos;

import java.math.BigDecimal;
import java.util.List;

/** Cuerpo JSON de respuesta de POST /api/pedidos y GET /api/pedidos/{id}. */
public record PedidoResponse(
        String id,
        EstadoPedido estado,
        String clienteId,
        String pais,
        String contacto,
        List<ItemPedido> items,
        String direccionEnvio,
        boolean recogerEnTienda,
        boolean envioExpress,
        String notaRegalo,
        String canalNotificacion,
        String cupon,
        BigDecimal subtotal,
        BigDecimal descuento,
        String autorizacionPago) {

    public static PedidoResponse de(PedidoRegistrado registrado) {
        Pedido p = registrado.pedido();
        return new PedidoResponse(
                registrado.id(),
                registrado.estado(),
                p.clienteId(),
                p.pais(),
                p.contacto(),
                p.items(),
                p.direccionEnvio(),
                p.recogerEnTienda(),
                p.envioExpress(),
                p.notaRegalo(),
                p.canalNotificacion(),
                p.cupon(),
                p.subtotal(),
                p.descuento(),
                registrado.autorizacionPago());
    }
}
