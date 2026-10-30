package co.edu.unbosque.shopflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** El reto C agrega WhatsApp sin modificar los creadores existentes. */
class NotificadorWhatsAppTest {

    @Test
    void numeroSinIndicativoLanzaIllegalArgumentException() {
        BandejaNotificaciones bandeja = new BandejaNotificaciones();
        NotificadorWhatsApp notificador = new NotificadorWhatsApp(bandeja);

        assertThrows(IllegalArgumentException.class,
                () -> notificador.notificarPagoAprobado("PED-1", "3001234567", new BigDecimal("145.66")));
        assertTrue(bandeja.todas().isEmpty());
    }

    @Test
    void enviaWhatsAppConCheckYTotalPagado() {
        BandejaNotificaciones bandeja = new BandejaNotificaciones();
        NotificadorWhatsApp notificador = new NotificadorWhatsApp(bandeja);

        notificador.notificarPagoAprobado("PED-1", "+573001234567", new BigDecimal("145.66"));

        BandejaNotificaciones.NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("WHATSAPP", enviada.canal());
        assertEquals("+573001234567", enviada.destino());
        assertTrue(enviada.mensaje().startsWith("\u2705 "));
        assertTrue(enviada.mensaje().contains("145.66"));
    }
}
