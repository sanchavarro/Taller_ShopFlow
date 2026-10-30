package co.edu.unbosque.shopflow.facturacion.peru;

import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;

/** Producto concreto (Perú): RUC del cliente, de 11 dígitos. */
class ValidadorRuc implements ValidadorIdentificacion {

    private static final String FORMATO = "\\d{11}";

    @Override
    public void validar(String documento) {
        if (documento == null || !documento.matches(FORMATO)) {
            throw new IllegalArgumentException(
                    "Documento inválido para Perú: se espera un RUC de 11 dígitos y llegó " + documento);
        }
    }
}
