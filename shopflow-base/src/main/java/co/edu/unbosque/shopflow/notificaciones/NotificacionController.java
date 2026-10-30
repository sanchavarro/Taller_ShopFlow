package co.edu.unbosque.shopflow.notificaciones;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint que evidencia el Factory Method: las notificaciones enviadas. */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final BandejaNotificaciones bandeja;

    public NotificacionController(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @GetMapping
    public List<BandejaNotificaciones.NotificacionEnviada> listar() {
        return bandeja.todas();
    }
}
