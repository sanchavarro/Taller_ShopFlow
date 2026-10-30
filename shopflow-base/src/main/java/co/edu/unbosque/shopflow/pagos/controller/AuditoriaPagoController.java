package co.edu.unbosque.shopflow.pagos.controller;

import co.edu.unbosque.shopflow.pagos.decorator.EntradaAuditoria;
import co.edu.unbosque.shopflow.pagos.decorator.RegistroAuditoria;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pagos/auditoria")
public class AuditoriaPagoController {

    private final RegistroAuditoria registroAuditoria;

    public AuditoriaPagoController(RegistroAuditoria registroAuditoria) {
        this.registroAuditoria = registroAuditoria;
    }

    @GetMapping
    public List<EntradaAuditoria> listar() {
        return registroAuditoria.listar();
    }
}