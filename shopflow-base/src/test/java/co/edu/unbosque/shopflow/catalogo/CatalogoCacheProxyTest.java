package co.edu.unbosque.shopflow.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** El proxy evita llamadas repetidas al catálogo remoto. */
class CatalogoCacheProxyTest {

    @Test
    void dosBusquedasDelMismoSkuSoloLlamanUnaVezAlRemoto() {
        CatalogoFalso remoto = new CatalogoFalso();
        CatalogoCacheProxy proxy = new CatalogoCacheProxy(remoto);

        Producto primera = proxy.buscar("SKU-003");
        Producto segunda = proxy.buscar("SKU-003");

        assertEquals(1, remoto.busquedas());
        assertSame(primera, segunda);
    }

    @Test
    void actualizarPrecioDejaLaCacheConElPrecioNuevo() {
        CatalogoFalso remoto = new CatalogoFalso();
        CatalogoCacheProxy proxy = new CatalogoCacheProxy(remoto);

        proxy.buscar("SKU-003");
        proxy.actualizarPrecio("SKU-003", new BigDecimal("299.90"));
        Producto actualizado = proxy.buscar("SKU-003");

        assertEquals(new BigDecimal("299.90"), actualizado.precio());
        assertEquals(1, remoto.busquedas());
        assertEquals(1, remoto.actualizaciones());
    }
}
