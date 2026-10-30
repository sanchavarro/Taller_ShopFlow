package co.edu.unbosque.shopflow.catalogo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints del catálogo. Para cambiar un precio se envuelve el catálogo de la
 * aplicación en el proxy de protección con el rol que llega en X-Rol; así la
 * regla de acceso vive en el proxy y no se repite en cada controlador.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ServicioCatalogo catalogo;

    public ProductoController(ServicioCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public List<Producto> listar() {
        return catalogo.listar();
    }

    @GetMapping("/{sku}")
    public Producto buscar(@PathVariable String sku) {
        return catalogo.buscar(sku);
    }

    @PutMapping("/{sku}/precio")
    public Producto actualizarPrecio(@PathVariable String sku,
            @RequestHeader(value = "X-Rol", required = false) String rol,
            @RequestBody PrecioRequest request) {
        if (request == null || request.precio() == null) {
            throw new IllegalArgumentException("El precio es obligatorio");
        }
        if (request.precio().signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }

        ServicioCatalogo conProteccion = new CatalogoProteccionProxy(catalogo, rol);
        return conProteccion.actualizarPrecio(sku, request.precio());
    }

    public record PrecioRequest(BigDecimal precio) { }
}
