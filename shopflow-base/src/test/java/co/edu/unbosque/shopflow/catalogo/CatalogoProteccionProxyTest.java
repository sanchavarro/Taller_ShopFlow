package co.edu.unbosque.shopflow.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.shopflow.comun.AccesoDenegadoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Solo el rol ADMIN puede cambiar precios. */
class CatalogoProteccionProxyTest {

    @Test
    void clienteNoPuedeCambiarPrecio() {
        CatalogoFalso remoto = new CatalogoFalso();
        CatalogoProteccionProxy proxy = new CatalogoProteccionProxy(remoto, "CLIENTE");

        assertThrows(AccesoDenegadoException.class,
                () -> proxy.actualizarPrecio("SKU-003", new BigDecimal("299.90")));
        assertEquals(0, remoto.actualizaciones());
    }

    @Test
    void sinRolNoPuedeCambiarPrecio() {
        CatalogoFalso remoto = new CatalogoFalso();
        CatalogoProteccionProxy proxy = new CatalogoProteccionProxy(remoto, null);

        assertThrows(AccesoDenegadoException.class,
                () -> proxy.actualizarPrecio("SKU-003", new BigDecimal("299.90")));
    }

    @Test
    void adminPuedeCambiarPrecio() {
        CatalogoFalso remoto = new CatalogoFalso();
        CatalogoProteccionProxy proxy = new CatalogoProteccionProxy(remoto, "ADMIN");

        Producto actualizado = proxy.actualizarPrecio("SKU-003", new BigDecimal("299.90"));

        assertEquals(new BigDecimal("299.90"), actualizado.precio());
        assertEquals(1, remoto.actualizaciones());
    }
}
