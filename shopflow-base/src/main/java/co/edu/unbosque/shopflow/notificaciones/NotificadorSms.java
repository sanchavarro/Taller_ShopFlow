package co.edu.unbosque.shopflow.notificaciones;

import org.springframework.stereotype.Component;

/** Creador concreto del canal SMS. */
@Component
public class NotificadorSms extends Notificador {

    private final BandejaNotificaciones bandeja;

    public NotificadorSms(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public String canal() {
        return "SMS";
    }

    @Override
    protected Notificacion crearNotificacion() {
        return new NotificacionSms(bandeja);
    }
}
