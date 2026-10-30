package co.edu.unbosque.shopflow.catalogo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Entrega el ServicioCatalogo de toda la aplicación. El resto de clases recibe
 * esta interfaz y no sabe que por dentro hay un proxy de caché sobre el
 * catálogo remoto.
 */
@Configuration
public class ConfiguracionCatalogo {

    @Bean
    public ServicioCatalogo servicioCatalogo() {
        return new CatalogoCacheProxy(new CatalogoRemoto());
    }
}
