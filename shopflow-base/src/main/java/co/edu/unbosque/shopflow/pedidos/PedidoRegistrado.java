package co.edu.unbosque.shopflow.pedidos;

/**
 * Pedido guardado por PedidoService: el Pedido que entregó el Builder más los datos que
 * aparecen después de construirlo (id y estado). Es inmutable: pagar devuelve una copia.
 */
public record PedidoRegistrado(String id, Pedido pedido, EstadoPedido estado, String autorizacionPago) {

    public static PedidoRegistrado nuevo(String id, Pedido pedido) {
        return new PedidoRegistrado(id, pedido, EstadoPedido.CREADO, null);
    }

    /** Copia del pedido en estado PAGADO con la autorización de la pasarela. */
    public PedidoRegistrado pagado(String autorizacion) {
        return new PedidoRegistrado(id, pedido, EstadoPedido.PAGADO, autorizacion);
    }
}
