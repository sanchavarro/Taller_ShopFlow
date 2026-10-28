package co.edu.unbosque.shopflow.pedidos;

import co.edu.unbosque.shopflow.catalogo.Producto;
import co.edu.unbosque.shopflow.catalogo.ServicioCatalogo;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/**
 * Casos de uso de los pedidos. En el Builder cumple el papel de quien dirige la
 * construcción: traduce el JSON recibido a llamadas a Pedido.Builder.
 */
@Service
public class PedidoService {

    private final ServicioCatalogo catalogo;
    private final Map<String, PedidoRegistrado> pedidos = new ConcurrentHashMap<>();
    private final AtomicInteger consecutivo = new AtomicInteger();

    public PedidoService(ServicioCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    public PedidoRegistrado crear(PedidoRequest request) {
        Pedido.Builder builder = new Pedido.Builder(
                request.clienteId(), request.documentoCliente(), request.pais(), request.contacto());

        if (request.items() != null) {
            for (PedidoRequest.ItemRequest item : request.items()) {
                builder.item(aItemPedido(item));
            }
        }

        Pedido pedido = builder
                .direccionEnvio(request.direccionEnvio())
                .recogerEnTienda(Boolean.TRUE.equals(request.recogerEnTienda()))
                .cupon(request.cupon())
                .notaRegalo(request.notaRegalo())
                .envioExpress(Boolean.TRUE.equals(request.envioExpress()))
                .canalNotificacion(request.canalNotificacion())
                .build();

        PedidoRegistrado registrado = PedidoRegistrado.nuevo("PED-" + consecutivo.incrementAndGet(), pedido);
        pedidos.put(registrado.id(), registrado);
        return registrado;
    }

    public PedidoRegistrado buscar(String id) {
        PedidoRegistrado registrado = pedidos.get(id);
        if (registrado == null) {
            throw new NoSuchElementException("No existe el pedido " + id);
        }
        return registrado;
    }

    /** El precio se toma del catálogo, no del cliente. */
    private ItemPedido aItemPedido(PedidoRequest.ItemRequest item) {
        if (item == null || item.sku() == null || item.sku().isBlank()) {
            throw new IllegalArgumentException("Cada ítem debe indicar un SKU");
        }
        Producto producto = catalogo.buscar(item.sku().trim());
        return new ItemPedido(producto.sku(), producto.nombre(), item.cantidad(), producto.precio());
    }
}
