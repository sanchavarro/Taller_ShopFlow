package co.edu.unbosque.shopflow.notificaciones;

/**
 * Producto concreto para SMS. El mensaje se recorta a 160 caracteres, que es
 * el límite de un SMS tradicional.
 */
public class NotificacionSms implements Notificacion {

    private static final int MAX_CARACTERES = 160;

    private final BandejaNotificaciones bandeja;

    public NotificacionSms(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public void enviar(String destino, String mensaje) {
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("El contacto SMS es obligatorio");
        }

        String texto = mensaje == null ? "" : mensaje;
        if (texto.length() > MAX_CARACTERES) {
            texto = texto.substring(0, MAX_CARACTERES);
        }
        bandeja.registrar("SMS", destino, texto);
    }
}
