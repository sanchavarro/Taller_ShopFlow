package co.edu.unbosque.shopflow.pagos.decorator;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import java.math.BigDecimal;
import java.util.Objects;

public class AuditoriaPago extends PasarelaDecorator {

	private final String proveedor;
	private final RegistroAuditoria registro;

	public AuditoriaPago(PasarelaDePago pasarelaEnvuelta, String proveedor, RegistroAuditoria registro) {
		super(pasarelaEnvuelta);

		if (proveedor == null || proveedor.isBlank()) {
			throw new IllegalArgumentException("El proveedor es obligatorio para la auditoría");
		}

		this.proveedor = proveedor;
		this.registro = Objects.requireNonNull(registro, "El registro de auditoría es obligatorio");
	}

	@Override
	public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
		long inicio = System.nanoTime();

		try {
			ResultadoPago resultado = pasarelaEnvuelta.cobrar(montoUsd, referencia);

			String detalle;

			if (resultado == null) {
				detalle = "ERROR: respuesta nula";
			} else if (resultado.aprobado()) {
				detalle = "APROBADO" + (resultado.autorizacion() == null ? "" : " (" + resultado.autorizacion() + ")");
			} else {
				detalle = "RECHAZADO: " + resultado.mensaje();
			}

			registrar(montoUsd, detalle, inicio);

			return resultado;

		} catch (RuntimeException e) {
			registrar(montoUsd, "ERROR: " + String.valueOf(e.getMessage()), inicio);

			throw e;
		}
	}

	private void registrar(BigDecimal montoUsd, String resultado, long inicio) {
		long duracionMs = (System.nanoTime() - inicio) / 1_000_000;

		registro.registrar(new EntradaAuditoria(proveedor, montoUsd, resultado, duracionMs));
	}
}