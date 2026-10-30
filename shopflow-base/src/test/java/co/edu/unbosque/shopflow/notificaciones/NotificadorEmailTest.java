package co.edu.unbosque.shopflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Cada creador concreto produce su propio producto y valida su destino. */
class NotificadorEmailTest {

    @Test
    void contactoSinArrobaLanzaIllegalArgumentException() {
        BandejaNotificaciones bandeja = new BandejaNotificaciones();
        NotificadorEmail notificador = new NotificadorEmail(bandeja);

        assertThrows(IllegalArgumentException.class,
                () -> notificador.notificarPagoAprobado("PED-1", "ana.perez", new BigDecimal("145.66")));
        assertTrue(bandeja.todas().isEmpty());
    }

    @Test
    void enviaCorreoConAsuntoYTotalPagado() {
        BandejaNotificaciones bandeja = new BandejaNotificaciones();
        NotificadorEmail notificador = new NotificadorEmail(bandeja);

        notificador.notificarPagoAprobado("PED-1", "ana.perez@correo.com", new BigDecimal("145.66"));

        BandejaNotificaciones.NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("EMAIL", enviada.canal());
        assertEquals("ana.perez@correo.com", enviada.destino());
        assertTrue(enviada.mensaje().startsWith("Asunto:"));
        assertTrue(enviada.mensaje().contains("145.66"));
    }
}
