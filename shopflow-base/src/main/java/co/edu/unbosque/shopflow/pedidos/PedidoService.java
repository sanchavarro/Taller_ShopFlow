package co.edu.unbosque.shopflow.pedidos;

import co.edu.unbosque.shopflow.catalogo.Producto;
import co.edu.unbosque.shopflow.catalogo.ServicioCatalogo;
import co.edu.unbosque.shopflow.facturacion.Comprobante;
import co.edu.unbosque.shopflow.facturacion.ServicioFacturacion;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;
import co.edu.unbosque.shopflow.pagos.configuracion.ConfiguracionPagos;

/**
 * Casos de uso de los pedidos. En el Builder cumple el papel de quien dirige la
 * construcción: traduce el JSON recibido a llamadas a Pedido.Builder. Luego
 * factura el pedido con ServicioFacturacion (Abstract Factory) sin saber de qué
 * país es.
 */
@Service
public class PedidoService {

	private final ServicioCatalogo catalogo;
	private final ServicioFacturacion facturacion;
	private final Map<String, PedidoRegistrado> pedidos = new ConcurrentHashMap<>();
	private final AtomicInteger consecutivo = new AtomicInteger();
	private final ConfiguracionPagos configuracionPagos;

	public PedidoService(ServicioCatalogo catalogo, ServicioFacturacion facturacion,
			ConfiguracionPagos configuracionPagos) {
		this.catalogo = catalogo;
		this.facturacion = facturacion;
		this.configuracionPagos = configuracionPagos;
	}

	public PedidoRegistrado crear(PedidoRequest request) {
		Pedido.Builder builder = new Pedido.Builder(request.clienteId(), request.documentoCliente(), request.pais(),
				request.contacto());

		if (request.items() != null) {
			for (PedidoRequest.ItemRequest item : request.items()) {
				builder.item(aItemPedido(item));
			}
		}

		Pedido pedido = builder.direccionEnvio(request.direccionEnvio())
				.recogerEnTienda(Boolean.TRUE.equals(request.recogerEnTienda())).cupon(request.cupon())
				.notaRegalo(request.notaRegalo()).envioExpress(Boolean.TRUE.equals(request.envioExpress()))
				.canalNotificacion(request.canalNotificacion()).build();

		Comprobante comprobante = facturacion.facturar(pedido);

		PedidoRegistrado registrado = PedidoRegistrado.nuevo("PED-" + consecutivo.incrementAndGet(), pedido,
				comprobante);
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

	public PedidoRegistrado pagar(String id, String proveedor) {
		PedidoRegistrado registrado = buscar(id);

		if (registrado.estado() != EstadoPedido.CREADO) {
			throw new IllegalStateException("El pedido " + id + " ya fue pagado");
		}

		PasarelaDePago pasarela = configuracionPagos.obtener(proveedor);

		ResultadoPago resultado = pasarela.cobrar(registrado.comprobante().total(), registrado.id());

		if (!resultado.aprobado()) {
			throw new IllegalStateException(
					resultado.mensaje() == null ? "El pago fue rechazado" : resultado.mensaje());
		}

		PedidoRegistrado pedidoPagado = registrado.pagado(resultado.autorizacion());

		pedidos.put(id, pedidoPagado);

		return pedidoPagado;
	}
}
