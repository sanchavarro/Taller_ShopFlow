package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy de caché del catálogo. Implementa la misma interfaz que el sujeto real
 * ({@link ServicioCatalogo}) y guarda los productos consultados en memoria para
 * evitar la latencia de 1,5 s del catálogo remoto.
 *
 * <p>El cliente (PedidoService o ProductoController) no sabe que está hablando
 * con un proxy: recibe un {@link ServicioCatalogo} y lo usa igual que antes.</p>
 */
public class CatalogoCacheProxy implements ServicioCatalogo {

    private final ServicioCatalogo catalogoReal;
    private final Map<String, Producto> cache = new ConcurrentHashMap<>();

    public CatalogoCacheProxy(ServicioCatalogo catalogoReal) {
        this.catalogoReal = catalogoReal;
    }

    @Override
    public Producto buscar(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU es obligatorio");
        }

        String clave = sku.trim();
        Producto enCache = cache.get(clave);
        if (enCache != null) {
            return enCache;
        }

        Producto producto = catalogoReal.buscar(clave);
        cache.put(producto.sku(), producto);
        return producto;
    }

    @Override
    public List<Producto> listar() {
        List<Producto> productos = catalogoReal.listar();
        productos.forEach(producto -> cache.put(producto.sku(), producto));
        return productos;
    }

    @Override
    public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
        Producto actualizado = catalogoReal.actualizarPrecio(sku, nuevoPrecio);
        // Se actualiza la caché en la misma operación para no servir un precio viejo.
        cache.put(actualizado.sku(), actualizado);
        return actualizado;
    }
}
