package co.edu.unbosque.shopflow.notificaciones;

import org.springframework.stereotype.Component;

/** Creador concreto del canal WhatsApp (reto C). */
@Component
public class NotificadorWhatsApp extends Notificador {

    private final BandejaNotificaciones bandeja;

    public NotificadorWhatsApp(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public String canal() {
        return "WHATSAPP";
    }

    @Override
    protected Notificacion crearNotificacion() {
        return new NotificacionWhatsApp(bandeja);
    }
}
