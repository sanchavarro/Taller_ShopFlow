package co.edu.unbosque.shopflow.pagos.decorator;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import java.math.BigDecimal;

public class ReintentoPago extends PasarelaDecorator {

	private final int maxIntentos;

	public ReintentoPago(PasarelaDePago pasarela, int maxIntentos) {
		super(pasarela);

		if (maxIntentos < 1) {
			throw new IllegalArgumentException("maxIntentos debe ser al menos 1");
		}

		this.maxIntentos = maxIntentos;
	}

	@Override
	public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
		PasarelaNoDisponibleException ultimoError = null;

		for (int intento = 1; intento <= maxIntentos; intento++) {
			try {
				return pasarelaEnvuelta.cobrar(montoUsd, referencia);
			} catch (PasarelaNoDisponibleException e) {
				ultimoError = e;
			}
		}

		throw ultimoError;
	}
}