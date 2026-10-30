package co.edu.unbosque.shopflow.catalogo;

import co.edu.unbosque.shopflow.comun.AccesoDenegadoException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Proxy de protección del catálogo. Envuelve al catálogo real (o a otro proxy)
 * y solo permite cambiar precios cuando el rol del usuario es ADMIN.
 *
 * <p>Los demás métodos se delegan sin cambios: la protección se concentra en
 * una sola operación y el cliente sigue viendo la misma interfaz.</p>
 */
public class CatalogoProteccionProxy implements ServicioCatalogo {

    public static final String ROL_ADMIN = "ADMIN";

    private final ServicioCatalogo catalogo;
    private final String rol;

    public CatalogoProteccionProxy(ServicioCatalogo catalogo, String rol) {
        this.catalogo = catalogo;
        this.rol = rol;
    }

    @Override
    public Producto buscar(String sku) {
        return catalogo.buscar(sku);
    }

    @Override
    public List<Producto> listar() {
        return catalogo.listar();
    }

    @Override
    public Producto actualizarPrecio(String sku, BigDecimal nuevoPrecio) {
        if (!esAdmin()) {
            throw new AccesoDenegadoException("Solo el rol ADMIN puede cambiar precios");
        }
        return catalogo.actualizarPrecio(sku, nuevoPrecio);
    }

    private boolean esAdmin() {
        return rol != null && ROL_ADMIN.equalsIgnoreCase(rol.trim());
    }
}
