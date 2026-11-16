package services;

import exceptions.CustomBusinessException;
import exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    @Test
    void procesarPago_exitoso_cierraTransaccionEnFinally() throws CustomBusinessException {
        String resultado = paymentService.procesarPago("Ana", 50.0, 100.0, "TARJETA-3");

        assertEquals("PAGO_EXITOSO:Ana:50.0", resultado);
        assertFalse(paymentService.isTransaccionEnCurso());
        assertEquals(1, paymentService.getTransaccionesCerradas());
    }

    @Test
    void procesarPago_saldoInsuficiente_lanzaExcepcionYEjecutaFinally() {
        CustomBusinessException ex = assertThrows(
                CustomBusinessException.class,
                () -> paymentService.procesarPago("Ana", 200.0, 100.0, "TARJETA")
        );

        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        assertFalse(paymentService.isTransaccionEnCurso());
        assertEquals(1, paymentService.getTransaccionesCerradas());
    }

    @Test
    void procesarPago_montoNegativo_lanzaIllegalArgumentYEjecutaFinally() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.procesarPago("Ana", -15.0, 100.0, "EFECTIVO")
        );

        assertFalse(paymentService.isTransaccionEnCurso());
        assertEquals(1, paymentService.getTransaccionesCerradas());
    }

    @Test
    void procesarPago_cuotasInvalidas_capturaEnTryAnidadoYEjecutaFinally() {
        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> paymentService.procesarPago("Ana", 25.0, 100.0, "TARJETA-abc")
        );

        assertInstanceOf(NumberFormatException.class, ex.getCause());
        assertFalse(paymentService.isTransaccionEnCurso());
        assertEquals(1, paymentService.getTransaccionesCerradas());
    }

    @Test
    void guardarReciboEnArchivo_rutaValida_escribeContenido(@TempDir Path tempDir) throws Exception {
        Path recibo = tempDir.resolve("recibo.txt");

        paymentService.guardarReciboEnArchivo(recibo, "PAGO_EXITOSO:Ana:50.0");

        String contenido = Files.readString(recibo).trim();
        assertEquals("PAGO_EXITOSO:Ana:50.0", contenido);
    }

    @Test
    void guardarReciboEnArchivo_rutaEsDirectorio_lanzaCustomBusinessExceptionConCausaIO(@TempDir Path tempDir) {
        // Pasar un directorio fuerza IOException
        CustomBusinessException ex = assertThrows(
                CustomBusinessException.class,
                () -> paymentService.guardarReciboEnArchivo(tempDir, "PAGO_EXITOSO:Ana:50.0")
        );

        assertInstanceOf(IOException.class, ex.getCause());
    }
}
