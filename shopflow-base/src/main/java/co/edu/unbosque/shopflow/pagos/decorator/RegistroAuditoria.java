package co.edu.unbosque.shopflow.pagos.decorator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RegistroAuditoria {

	private final ConcurrentLinkedQueue<EntradaAuditoria> entradas = new ConcurrentLinkedQueue<>();

	public void registrar(EntradaAuditoria entrada) {
		entradas.add(entrada);
	}

	public List<EntradaAuditoria> listar() {
		return List.copyOf(entradas);
	}

	public void limpiar() {
		entradas.clear();
	}
}