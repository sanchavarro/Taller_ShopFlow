package co.edu.unbosque.shopflow.pedidos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** API REST de pedidos. */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@RequestBody PedidoRequest request) {
        return PedidoResponse.de(pedidoService.crear(request));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable String id) {
        return PedidoResponse.de(pedidoService.buscar(id));
    }
}
