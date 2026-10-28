package co.edu.unbosque.shopflow.facturacion;

/**
 * Fábrica abstracta (AbstractFactory). Cada país entrega su propia familia de piezas que
 * deben ir juntas: impuesto, validador de documento y comprobante. Quien recibe una sola
 * fábrica nunca puede mezclar el impuesto de un país con el comprobante de otro.
 */
public interface FabricaFacturacion {

    /** Código ISO del país que atiende esta fábrica, por ejemplo "CO". */
    String pais();

    CalculadoraImpuestos crearCalculadora();

    ValidadorIdentificacion crearValidador();

    GeneradorComprobante crearGenerador();
}
