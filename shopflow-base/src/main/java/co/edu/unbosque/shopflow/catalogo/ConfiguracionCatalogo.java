package co.edu.unbosque.shopflow.catalogo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Entrega el ServicioCatalogo de toda la aplicación.
 * PROVISIONAL (rol A): mientras se integra el Proxy, se usa el catálogo remoto directo para
 * que PedidoService pueda arrancar. El rol C reemplaza esta clase por la suya, que entrega
 * new CatalogoCacheProxy(new CatalogoRemoto()). Al integrar, gana la versión del rol C.
 */
@Configuration
public class ConfiguracionCatalogo {

    @Bean
    public ServicioCatalogo servicioCatalogo() {
        return new CatalogoRemoto();
    }
}
