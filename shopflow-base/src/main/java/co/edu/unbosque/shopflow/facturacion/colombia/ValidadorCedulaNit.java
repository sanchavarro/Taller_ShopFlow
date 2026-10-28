package co.edu.unbosque.shopflow.facturacion.colombia;

import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;

/** Producto concreto (Colombia): cédula o NIT del cliente, de 6 a 10 dígitos. */
class ValidadorCedulaNit implements ValidadorIdentificacion {

    private static final String FORMATO = "\\d{6,10}";

    @Override
    public void validar(String documento) {
        if (documento == null || !documento.matches(FORMATO)) {
            throw new IllegalArgumentException(
                    "Documento inválido para Colombia: se espera cédula o NIT de 6 a 10 dígitos y llegó " + documento);
        }
    }
}
