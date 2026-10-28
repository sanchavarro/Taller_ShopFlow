package co.edu.unbosque.shopflow.pedidos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de ShopFlow (Product del Builder). Es inmutable: atributos final, constructor
 * privado y sin setters. La única forma de obtener uno es Pedido.Builder#build(), que
 * valida todas las reglas; por eso un Pedido siempre es válido.
 */
public final class Pedido {

    public static final int MAX_NOTA_REGALO = 140;
    public static final String CUPON_BOSQUE10 = "BOSQUE10";
    private static final BigDecimal PORCENTAJE_BOSQUE10 = new BigDecimal("0.10");

    // Obligatorios
    private final String clienteId;
    private final String documentoCliente;
    private final String pais;
    private final String contacto;
    private final List<ItemPedido> items;

    // Opcionales
    private final String direccionEnvio;
    private final boolean recogerEnTienda;
    private final String cupon;
    private final String notaRegalo;
    private final boolean envioExpress;
    private final String canalNotificacion;

    // Calculados al construir
    private final BigDecimal subtotal;
    private final BigDecimal descuento;

    private Pedido(Builder b) {
        this.clienteId = b.clienteId;
        this.documentoCliente = b.documentoCliente;
        this.pais = b.pais;
        this.contacto = b.contacto;
        this.items = List.copyOf(b.items);
        this.direccionEnvio = b.direccionEnvio;
        this.recogerEnTienda = b.recogerEnTienda;
        this.cupon = b.cupon;
        this.notaRegalo = b.notaRegalo;
        this.envioExpress = b.envioExpress;
        this.canalNotificacion = b.canalNotificacion;
        this.subtotal = items.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.descuento = CUPON_BOSQUE10.equals(cupon)
                ? subtotal.multiply(PORCENTAJE_BOSQUE10).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
    }

    public String clienteId() { return clienteId; }
    public String documentoCliente() { return documentoCliente; }
    public String pais() { return pais; }
    public String contacto() { return contacto; }
    public List<ItemPedido> items() { return items; }
    public String direccionEnvio() { return direccionEnvio; }
    public boolean recogerEnTienda() { return recogerEnTienda; }
    public String cupon() { return cupon; }
    public String notaRegalo() { return notaRegalo; }
    public boolean envioExpress() { return envioExpress; }
    public String canalNotificacion() { return canalNotificacion; }
    public BigDecimal subtotal() { return subtotal; }
    public BigDecimal descuento() { return descuento; }

    /** Base gravable: subtotal menos el descuento del cupón (antes de impuestos). */
    public BigDecimal subtotalConDescuento() {
        return subtotal.subtract(descuento);
    }

    /**
     * Builder (versión de Bloch, Effective Java ítem 2). Los datos obligatorios van en el
     * constructor; cada opción devuelve el propio builder (interfaz fluida) y build()
     * valida las reglas que cruzan varias opciones en un solo lugar.
     */
    public static final class Builder {

        private final String clienteId;
        private final String documentoCliente;
        private final String pais;
        private final String contacto;
        private final List<ItemPedido> items = new ArrayList<>();

        private String direccionEnvio;
        private boolean recogerEnTienda = false;
        private String cupon;
        private String notaRegalo;
        private boolean envioExpress = false;
        private String canalNotificacion = "EMAIL";

        public Builder(String clienteId, String documentoCliente, String pais, String contacto) {
            this.clienteId = limpiar(clienteId);
            this.documentoCliente = limpiar(documentoCliente);
            this.pais = pais == null ? null : pais.trim().toUpperCase();
            this.contacto = limpiar(contacto);
        }

        public Builder item(ItemPedido item) {
            this.items.add(item);
            return this;
        }

        public Builder direccionEnvio(String direccionEnvio) {
            this.direccionEnvio = limpiar(direccionEnvio);
            return this;
        }

        public Builder recogerEnTienda(boolean recogerEnTienda) {
            this.recogerEnTienda = recogerEnTienda;
            return this;
        }

        public Builder cupon(String cupon) {
            this.cupon = cupon == null ? null : cupon.trim().toUpperCase();
            return this;
        }

        public Builder notaRegalo(String notaRegalo) {
            this.notaRegalo = notaRegalo;
            return this;
        }

        public Builder envioExpress(boolean envioExpress) {
            this.envioExpress = envioExpress;
            return this;
        }

        public Builder canalNotificacion(String canalNotificacion) {
            if (canalNotificacion != null && !canalNotificacion.isBlank()) {
                this.canalNotificacion = canalNotificacion.trim().toUpperCase();
            }
            return this;
        }

        /** Valida todas las reglas y entrega un Pedido válido; si alguna falla, lanza IllegalArgumentException (400). */
        public Pedido build() {
            requerido(clienteId, "El cliente es obligatorio");
            requerido(documentoCliente, "El documento del cliente es obligatorio");
            requerido(pais, "El país es obligatorio");
            requerido(contacto, "El contacto es obligatorio");
            if (items.isEmpty()) {
                throw new IllegalArgumentException("El pedido debe tener al menos un ítem");
            }
            for (ItemPedido item : items) {
                if (item.cantidad() <= 0) {
                    throw new IllegalArgumentException("La cantidad del ítem " + item.sku() + " debe ser mayor que cero");
                }
            }
            if (!recogerEnTienda && direccionEnvio == null) {
                throw new IllegalArgumentException("La dirección de envío es obligatoria si el pedido no se recoge en tienda");
            }
            if (recogerEnTienda && envioExpress) {
                throw new IllegalArgumentException("Un pedido para recoger en tienda no puede tener envío express");
            }
            if (notaRegalo != null && notaRegalo.length() > MAX_NOTA_REGALO) {
                throw new IllegalArgumentException("La nota de regalo admite máximo " + MAX_NOTA_REGALO + " caracteres");
            }
            if (cupon != null && !cupon.isBlank() && !CUPON_BOSQUE10.equals(cupon)) {
                throw new IllegalArgumentException("El cupón " + cupon + " no es válido");
            }
            return new Pedido(this);
        }

        private static void requerido(String valor, String mensaje) {
            if (valor == null) {
                throw new IllegalArgumentException(mensaje);
            }
        }

        private static String limpiar(String valor) {
            return valor == null || valor.isBlank() ? null : valor.trim();
        }
    }
}
