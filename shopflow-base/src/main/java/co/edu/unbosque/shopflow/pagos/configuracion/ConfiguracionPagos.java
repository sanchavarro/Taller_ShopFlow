package co.edu.unbosque.shopflow.pagos.configuracion;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.adapter.LegacyPayAdapter;
import co.edu.unbosque.shopflow.pagos.adapter.QuickPayAdapter;
import co.edu.unbosque.shopflow.pagos.decorator.AuditoriaPago;
import co.edu.unbosque.shopflow.pagos.decorator.RegistroAuditoria;
import co.edu.unbosque.shopflow.pagos.decorator.ReintentoPago;
import co.edu.unbosque.shopflow.pagos.decorator.MetricasPago;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class ConfiguracionPagos {

	private final Map<String, PasarelaDePago> pasarelas;

	public ConfiguracionPagos(@Value("${shopflow.legacypay.probabilidad-timeout:0.3}") double probabilidadTimeout,

			@Value("${shopflow.pagos.auditoria:true}") boolean auditoriaActiva,

			@Value("${shopflow.pagos.reintentos:true}") boolean reintentosActivos,

			@Value("${shopflow.pagos.max-intentos:3}") int maxIntentos,

			RegistroAuditoria registroAuditoria) {
		Map<String, PasarelaDePago> configuradas = new LinkedHashMap<>();

		configuradas.put("LEGACYPAY", construir(new LegacyPayAdapter(probabilidadTimeout), "LEGACYPAY", auditoriaActiva,
				reintentosActivos, maxIntentos, registroAuditoria));

		configuradas.put("QUICKPAY", construir(new QuickPayAdapter(), "QUICKPAY", auditoriaActiva, reintentosActivos,
				maxIntentos, registroAuditoria));

		this.pasarelas = Collections.unmodifiableMap(configuradas);
	}

	private PasarelaDePago construir(PasarelaDePago adapter, String proveedor, boolean auditoriaActiva,
			boolean reintentosActivos, int maxIntentos, RegistroAuditoria registroAuditoria) {
		PasarelaDePago configurada = adapter;

		if (reintentosActivos) {
			configurada = new ReintentoPago(configurada, maxIntentos);
		}

		if (auditoriaActiva) {
			configurada = new AuditoriaPago(configurada, proveedor, registroAuditoria);
		}

		configurada = new MetricasPago(configurada, proveedor);
		
		return configurada;
	}

	public PasarelaDePago obtener(String proveedor) {
		if (proveedor == null || proveedor.isBlank()) {
			throw new IllegalArgumentException("Debe indicar el proveedor de pago");
		}

		PasarelaDePago pasarela = pasarelas.get(proveedor.trim().toUpperCase(Locale.ROOT));

		if (pasarela == null) {
			throw new IllegalArgumentException("Proveedor de pago no válido: " + proveedor);
		}

		return pasarela;
	}

	public Map<String, PasarelaDePago> todas() {
		return pasarelas;
	}
}