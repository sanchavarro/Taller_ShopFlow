package co.edu.unbosque.shopflow.facturacion.mexico;

import co.edu.unbosque.shopflow.facturacion.CalculadoraImpuestos;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Producto concreto (México): IVA del 16 %. */
class IvaMexico implements CalculadoraImpuestos {

    private static final BigDecimal TARIFA = new BigDecimal("0.16");

    @Override
    public String nombre() {
        return "IVA 16%";
    }

    @Override
    public BigDecimal calcular(BigDecimal base) {
        return base.multiply(TARIFA).setScale(2, RoundingMode.HALF_UP);
    }
}
