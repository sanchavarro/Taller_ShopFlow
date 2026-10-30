package co.edu.unbosque.shopflow.notificaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Creador del Factory Method. Mantiene el proceso fijo de notificar un pago
 * aprobado y delega en cada subclase la creación de la notificación concreta.
 *
 * <p>El método {@link #notificarPagoAprobado(String, String, BigDecimal)} es
 * final: el proceso (armar el mensaje, crear el producto y enviarlo) no cambia
 * cuando aparece un canal nuevo; solo cambia {@link #crearNotificacion()}.</p>
 */
public abstract class Notificador {

    /** Canal que atiende este creador, por ejemplo EMAIL o SMS. */
    public abstract String canal();

    /** Método fábrica: cada creador concreto decide qué producto instanciar. */
    protected abstract Notificacion crearNotificacion();

    public final void notificarPagoAprobado(String pedidoId, String destino, BigDecimal total) {
        String monto = total == null
                ? "0.00"
                : total.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String mensaje = "Pago aprobado del pedido " + pedidoId + " por USD " + monto;
        crearNotificacion().enviar(destino, mensaje);
    }
}
