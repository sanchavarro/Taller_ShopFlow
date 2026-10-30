package co.edu.unbosque.shopflow.facturacion;

import co.edu.unbosque.shopflow.pedidos.Pedido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Cliente de la Abstract Factory. Recibe todas las fábricas que Spring encuentra
 * (@Component), las indexa por país y factura sin conocer ninguna clase concreta.
 * No tiene ni un if ni un switch por país: un país nuevo es una fábrica nueva.
 */
@Service
public class ServicioFacturacion {

    private final Map<String, FabricaFacturacion> fabricasPorPais;

    public ServicioFacturacion(List<FabricaFacturacion> fabricas) {
        this.fabricasPorPais = fabricas.stream()
                .collect(Collectors.toMap(FabricaFacturacion::pais, Function.identity()));
    }

    public Comprobante facturar(Pedido pedido) {
        FabricaFacturacion fabrica = Optional.ofNullable(fabricasPorPais.get(pedido.pais()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se factura en el país " + pedido.pais() + ". Países disponibles: " + paisesDisponibles()));

        // Las tres piezas salen de la misma fábrica: siempre son de la misma familia.
        ValidadorIdentificacion validador = fabrica.crearValidador();
        CalculadoraImpuestos calculadora = fabrica.crearCalculadora();
        GeneradorComprobante generador = fabrica.crearGenerador();

        validador.validar(pedido.documentoCliente());
        BigDecimal base = pedido.subtotalConDescuento().setScale(2, RoundingMode.HALF_UP);
        BigDecimal impuesto = calculadora.calcular(base);
        return generador.generar(base, calculadora.nombre(), impuesto);
    }

    public List<String> paisesDisponibles() {
        return fabricasPorPais.keySet().stream().sorted().toList();
    }
}
