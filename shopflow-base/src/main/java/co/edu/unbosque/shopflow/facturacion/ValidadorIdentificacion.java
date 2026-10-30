package co.edu.unbosque.shopflow.facturacion;

/** Producto abstracto 2 de la familia de facturación: el documento del cliente. */
public interface ValidadorIdentificacion {

    /** Lanza IllegalArgumentException (400) si el documento no cumple el formato del país. */
    void validar(String documento);
}
