package co.edu.unbosque.shopflow.pagos.adapter;

import co.edu.unbosque.shopflow.externos.legacypay.LegacyPayClient;
import co.edu.unbosque.shopflow.externos.legacypay.LegacyPayTimeoutException;
import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class LegacyPayAdapter implements PasarelaDePago {

	private final LegacyPayClient cliente;

	public LegacyPayAdapter(double probabilidadTimeout) {
		this(new LegacyPayClient(probabilidadTimeout));
	}

	public LegacyPayAdapter(LegacyPayClient cliente) {
		this.cliente = Objects.requireNonNull(cliente, "El cliente LegacyPay es obligatorio");
	}

	@Override
	public ResultadoPago cobrar(BigDecimal montoUsd, String referencia) {
		if (montoUsd == null || montoUsd.signum() <= 0) {
			throw new IllegalArgumentException("El monto del pago debe ser mayor que cero");
		}

		if (referencia == null || referencia.isBlank()) {
			throw new IllegalArgumentException("La referencia del pago es obligatoria");
		}

		try {
			long centavos = montoUsd.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();

			String respuesta = cliente.makeCharge(centavos, "USD", referencia);

			if (respuesta == null) {
				throw new PasarelaNoDisponibleException("LegacyPay devolvió una respuesta vacía", null);
			}

			String[] partes = respuesta.split("\\|", -1);
			String codigo = partes[0];
			String autorizacion = partes.length > 1 ? partes[1] : "";

			return switch (codigo) {
			case "00" -> ResultadoPago.aprobado(autorizacion);
			case "51" -> ResultadoPago.rechazado("Fondos insuficientes");
			case "05" -> ResultadoPago.rechazado("Transacción no permitida");
			default -> ResultadoPago.rechazado("Código de respuesta LegacyPay desconocido: " + codigo);
			};

		} catch (LegacyPayTimeoutException e) {
			throw new PasarelaNoDisponibleException("LegacyPay no está disponible temporalmente", e);
		}
	}
}