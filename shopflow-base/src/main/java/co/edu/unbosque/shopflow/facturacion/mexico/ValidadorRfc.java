package co.edu.unbosque.shopflow.facturacion.mexico;

import co.edu.unbosque.shopflow.facturacion.ValidadorIdentificacion;

/**
 * Producto concreto (México): RFC del cliente. Persona física: 4 letras, fecha AAMMDD y
 * homoclave de 3 caracteres (13 en total); persona moral: 3 letras (12 en total).
 */
class ValidadorRfc implements ValidadorIdentificacion {

    private static final String FORMATO = "[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}";

    @Override
    public void validar(String documento) {
        if (documento == null || !documento.toUpperCase().matches(FORMATO)) {
            throw new IllegalArgumentException(
                    "Documento inválido para México: se espera un RFC (por ejemplo GODE561231GR8) y llegó " + documento);
        }
    }
}
