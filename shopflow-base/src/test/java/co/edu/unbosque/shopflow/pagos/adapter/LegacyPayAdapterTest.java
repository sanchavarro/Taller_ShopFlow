package co.edu.unbosque.shopflow.pagos.adapter;

import co.edu.unbosque.shopflow.externos.legacypay.LegacyPayClient;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyPayAdapterTest {

    @Test
    void traduceElTimeoutDelSdkAExcepcionDelDominio() {
        LegacyPayAdapter adaptador = new LegacyPayAdapter(new LegacyPayClient(1.0));

        assertThrows(
                PasarelaNoDisponibleException.class,
                () -> adaptador.cobrar(BigDecimal.TEN, "PED-1")
        );
    }

    @Test
    void apruebaUnPagoCuandoLegacyPayRespondeCorrectamente() {
        LegacyPayAdapter adaptador = new LegacyPayAdapter(new LegacyPayClient(0.0));

        ResultadoPago resultado = adaptador.cobrar(BigDecimal.TEN, "PED-2");

        assertTrue(resultado.aprobado());
    }
}
