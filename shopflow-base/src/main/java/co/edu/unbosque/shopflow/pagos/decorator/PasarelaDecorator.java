package co.edu.unbosque.shopflow.pagos.decorator;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class PasarelaDecorator implements PasarelaDePago {

	protected final PasarelaDePago pasarelaEnvuelta;

	protected PasarelaDecorator(PasarelaDePago pasarelaEnvuelta) {
		this.pasarelaEnvuelta = Objects.requireNonNull(pasarelaEnvuelta,
				"La pasarela que se va a decorar es obligatoria");
	}

	@Override
	public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
		return pasarelaEnvuelta.cobrar(montoUsd, referencia);
	}
}