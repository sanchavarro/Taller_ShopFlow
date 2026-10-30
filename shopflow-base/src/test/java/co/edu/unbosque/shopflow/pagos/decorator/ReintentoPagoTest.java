package co.edu.unbosque.shopflow.pagos.decorator;

import co.edu.unbosque.shopflow.pagos.PasarelaDePago;
import co.edu.unbosque.shopflow.pagos.PasarelaNoDisponibleException;
import co.edu.unbosque.shopflow.pagos.ResultadoPago;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReintentoPagoTest {

    @Test
    void reintentaHastaQueLaPasarelaResponde() {
        int[] llamadas = {0};

        PasarelaDePago pasarelaInestable = (monto, referencia) -> {
            llamadas[0]++;

            if (llamadas[0] < 3) {
                throw new PasarelaNoDisponibleException(
                        "Pasarela caída", null);
            }

            return ResultadoPago.aprobado("AUT-1");
        };

        ReintentoPago reintento =
                new ReintentoPago(pasarelaInestable, 3);

        ResultadoPago resultado =
                reintento.cobrar(BigDecimal.TEN, "PED-1");

        assertTrue(resultado.aprobado());
        assertEquals(3, llamadas[0]);
    }

    @Test
    void noSuperaElNumeroMaximoDeIntentos() {
        int[] llamadas = {0};

        PasarelaDePago pasarelaCaida = (monto, referencia) -> {
            llamadas[0]++;
            throw new PasarelaNoDisponibleException(
                    "Pasarela caída", null);
        };

        ReintentoPago reintento =
                new ReintentoPago(pasarelaCaida, 2);

        assertThrows(
                PasarelaNoDisponibleException.class,
                () -> reintento.cobrar(
                        BigDecimal.TEN, "PED-2")
        );

        assertEquals(2, llamadas[0]);
    }
}