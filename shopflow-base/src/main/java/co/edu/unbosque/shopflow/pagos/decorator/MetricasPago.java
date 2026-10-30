package co.edu.unbosque.shopflow.pagos.decorator;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Decorator que registra métricas de los cobros realizados a través de una
 * pasarela de pago.
 */
public class MetricasPago extends PasarelaDecorator {

	private final String proveedor;

	private long totalCobros;
	private long pagosAprobados;

	public MetricasPago(PasarelaDePago pasarelaEnvuelta, String proveedor) {
		super(pasarelaEnvuelta);
		this.proveedor = Objects.requireNonNull(proveedor);
	}

	@Override
	public synchronized ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {

		totalCobros++;

		try {
			ResultadoPago resultado = pasarelaEnvuelta.cobrar(montoUsd, referencia);

			if (resultado.aprobado()) {
				pagosAprobados++;
			}

			imprimirMetricas();
			return resultado;

		} catch (RuntimeException e) {
			imprimirMetricas();
			throw e;
		}
	}

	private void imprimirMetricas() {
		double tasa = totalCobros == 0 ? 0.0 : (double) pagosAprobados / totalCobros * 100;

		System.out.printf("[MÉTRICAS] Pasarela=%s | Cobros=%d | Aprobados=%d | Tasa de aprobación=%.2f%%%n", proveedor,
				totalCobros, pagosAprobados, tasa);
	}

	public synchronized long getTotalCobros() {
		return totalCobros;
	}

	public synchronized long getPagosAprobados() {
		return pagosAprobados;
	}

	public synchronized double getTasaAprobacion() {
		return totalCobros == 0 ? 0.0 : (double) pagosAprobados / totalCobros * 100;
	}
}