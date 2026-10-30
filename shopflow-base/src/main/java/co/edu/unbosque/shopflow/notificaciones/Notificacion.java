package co.edu.unbosque.shopflow.notificaciones;

/**
 * Producto del Factory Method. Cada canal concreto decide cómo validar el
 * destino y cómo simular el envío, pero el creador solo conoce esta interfaz.
 */
public interface Notificacion {

    void enviar(String destino, String mensaje);
}
