package co.edu.unbosque.shopflow.pagos.adapter;

import co.edu.unbosque.shopflow.externos.quickpay.QuickPayRequest;
import co.edu.unbosque.shopflow.externos.quickpay.QuickPayResult;
import co.edu.unbosque.shopflow.externos.quickpay.QuickPaySdk;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import java.math.BigDecimal;
import java.util.Objects;

public class QuickPayAdapter implements PasarelaDePago {

	private final QuickPaySdk sdk;

	public QuickPayAdapter() {
		this(new QuickPaySdk());
	}

	public QuickPayAdapter(QuickPaySdk sdk) {
		this.sdk = Objects.requireNonNull(sdk, "El SDK QuickPay es obligatorio");
	}

	@Override
	public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
		if (montoUsd == null || montoUsd.signum() <= 0) {
			throw new IllegalArgumentException("El monto del pago debe ser mayor que cero");
		}

		if (referencia == null || referencia.isBlank()) {
			throw new IllegalArgumentException("La referencia del pago es obligatoria");
		}

		QuickPayRequest solicitud = new QuickPayRequest(referencia, montoUsd.doubleValue(), "USD");

		QuickPayResult respuesta = sdk.pay(solicitud);

		if (respuesta == null || respuesta.status() == null) {
			return ResultadoPago.rechazado("QuickPay devolvió una respuesta inválida");
		}

		return switch (respuesta.status()) {
		case OK -> ResultadoPago.aprobado(respuesta.transactionId());

		case REJECTED -> ResultadoPago.rechazado(mensaje(respuesta, "Pago rechazado por QuickPay"));

		case ERROR -> ResultadoPago.rechazado(mensaje(respuesta, "QuickPay no pudo procesar el pago"));
		};
	}

	private String mensaje(QuickPayResult respuesta, String predeterminado) {
		return respuesta.detail() == null || respuesta.detail().isBlank() ? predeterminado : respuesta.detail();
	}
}
