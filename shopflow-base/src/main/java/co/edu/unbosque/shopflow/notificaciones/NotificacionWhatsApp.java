package co.edu.unbosque.shopflow.notificaciones;

import java.util.regex.Pattern;

/**
 * Producto concreto del reto C. WhatsApp exige un número con indicativo
 * internacional (por ejemplo +573001234567) y antepone el check al mensaje.
 */
public class NotificacionWhatsApp implements Notificacion {

    private static final Pattern TELEFONO_CON_INDICATIVO = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    private final BandejaNotificaciones bandeja;

    public NotificacionWhatsApp(BandejaNotificaciones bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public void enviar(String destino, String mensaje) {
        if (destino == null || !TELEFONO_CON_INDICATIVO.matcher(destino.trim()).matches()) {
            throw new IllegalArgumentException(
                    "El contacto de WhatsApp debe incluir indicativo, por ejemplo +573001234567");
        }
        bandeja.registrar("WHATSAPP", destino.trim(), "\u2705 " + (mensaje == null ? "" : mensaje));
    }
}
