package services;

import exceptions.CustomBusinessException;
import exceptions.InvalidInputException;
import org.springframework.stereotype.Service;
import utils.Logger;
import utils.ValidationUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PaymentService {

    private boolean transaccionEnCurso = false;
    private int transaccionesCerradas = 0;

    public boolean isTransaccionEnCurso() {
        return transaccionEnCurso;
    }

    public int getTransaccionesCerradas() {
        return transaccionesCerradas;
    }

    public String procesarPago(String usuario, double monto, double saldoDisponible, String metodoPago)
            throws CustomBusinessException {

        try {
            transaccionEnCurso = true;
            Logger.info(PaymentService.class, "Iniciando transacción de pago para usuario: " + usuario);

            String usuarioLimpio = ValidationUtils.validarTextoNoVacio(usuario, "usuario");
            ValidationUtils.validarMontoPositivo(monto);
            String metodoLimpio = ValidationUtils.validarTextoNoVacio(metodoPago, "metodoPago");

            // Formato esperado cuando incluye cuotas: METODO-N (ej. TARJETA-3)
            if (metodoLimpio.contains("-")) {
                try {
                    String[] partes = metodoLimpio.split("-");
                    int cuotas = Integer.parseInt(partes[1]);
                    if (cuotas <= 0) {
                        throw new InvalidInputException("El número de cuotas debe ser mayor a 0.");
                    }
                    Logger.debug(PaymentService.class, "Pago configurado en " + cuotas + " cuotas.");
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    Logger.warn(PaymentService.class, "Formato de cuotas inválido en método de pago: " + metodoLimpio);
                    throw new InvalidInputException(
                            "Formato de método de pago inválido (use por ejemplo TARJETA-3): " + metodoLimpio, e
                    );
                }
            }

            if (monto > saldoDisponible) {
                throw new CustomBusinessException(
                        "Saldo insuficiente. Intentó pagar " + monto + " pero el saldo es " + saldoDisponible
                );
            }

            Logger.info(PaymentService.class, "Pago aprobado para " + usuarioLimpio + " por " + monto);
            return "PAGO_EXITOSO:" + usuarioLimpio + ":" + monto;

        } catch (InvalidInputException | IllegalArgumentException e) {
            Logger.warn(PaymentService.class, "Pago rechazado por datos inválidos: " + e.getMessage());
            throw e;
        } catch (CustomBusinessException e) {
            Logger.error(PaymentService.class, "Error de negocio al procesar el pago", e);
            throw e;
        } finally {
            transaccionEnCurso = false;
            transaccionesCerradas++;
            Logger.info(PaymentService.class, "Bloque finally ejecutado: recursos de transacción liberados.");
        }
    }

    public void guardarReciboEnArchivo(Path rutaArchivo, String detallePago) throws CustomBusinessException {
        if (rutaArchivo == null) {
            throw new InvalidInputException("La ruta del recibo no puede ser nula.");
        }
        String detalleLimpio = ValidationUtils.validarTextoNoVacio(detallePago, "detallePago");

        try (BufferedWriter writer = Files.newBufferedWriter(rutaArchivo, StandardCharsets.UTF_8)) {
            writer.write(detalleLimpio);
            writer.newLine();
            Logger.info(PaymentService.class, "Recibo guardado correctamente en: " + rutaArchivo.getFileName());
        } catch (IOException e) {
            Logger.error(PaymentService.class, "No se pudo guardar el recibo en: " + rutaArchivo, e);
            throw new CustomBusinessException("Error de E/S al guardar el recibo del pago", e);
        }
    }
}
