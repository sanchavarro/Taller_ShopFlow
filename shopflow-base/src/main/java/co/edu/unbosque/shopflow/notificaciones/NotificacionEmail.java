package co.edu.unbosque.shopflow.notificaciones;

/**
 * Producto concreto para correo. Un correo exige un contacto con arroba y
 * lleva asunto; el envío real se simula con {@link BandejaNotificaciones}.
 */
public class NotificacionEmail implements Notificacion {

    private final BandejaNotificaciones bandeja;

    public NotificacionEmail(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public void enviar(String destino, String mensaje) {
        if (destino == null || !destino.contains("@")) {
            throw new IllegalArgumentException("El contacto de correo debe contener @");
        }
        bandeja.registrar("EMAIL", destino, "Asunto: Pago aprobado | " + mensaje);
    }
}
