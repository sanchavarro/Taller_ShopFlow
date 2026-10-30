package co.edu.unbosque.shopflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** El SMS se recorta a 160 caracteres. */
class NotificadorSmsTest {

    @Test
    void recortaElMensajeA160Caracteres() {
        BandejaNotificaciones bandeja = new BandejaNotificaciones();
        NotificacionSms notificacion = new NotificacionSms(bandeja);

        notificacion.enviar("+573001234567", "x".repeat(200));

        BandejaNotificaciones.NotificacionEnviada enviada = bandeja.todas().get(0);
        assertEquals("SMS", enviada.canal());
        assertEquals(160, enviada.mensaje().length());
    }
}
