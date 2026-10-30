package co.edu.unbosque.shopflow.pagos.decorator;

import java.math.BigDecimal;

public record EntradaAuditoria(String proveedor, BigDecimal monto, String resultado, long duracionMs) {
}