package utils;

import exceptions.InvalidInputException;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static String validarTextoNoVacio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            String mensaje = "El campo '" + nombreCampo + "' es obligatorio y no puede estar vacío o nulo.";
            Logger.warn(ValidationUtils.class, mensaje);
            throw new InvalidInputException(mensaje);
        }
        return valor.trim();
    }

    public static String validarEmail(String email) {
        String emailLimpio = validarTextoNoVacio(email, "email");
        if (!emailLimpio.contains("@")) {
            String mensaje = "El formato del correo es inválido (falta '@'): " + emailLimpio;
            Logger.warn(ValidationUtils.class, mensaje);
            throw new InvalidInputException(mensaje);
        }
        return emailLimpio;
    }

    public static void validarMontoPositivo(double monto) {
        if (monto <= 0) {
            String mensaje = "El monto debe ser mayor a cero. Valor recibido: " + monto;
            Logger.warn(ValidationUtils.class, mensaje);
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static String obtenerElementoSeguro(String[] lista, int indice) {
        if (lista == null) {
            String mensaje = "La lista no puede ser nula (NullPointerException prevenido).";
            Logger.warn(ValidationUtils.class, mensaje);
            throw new InvalidInputException(mensaje);
        }

        try {
            return lista[indice];
        } catch (ArrayIndexOutOfBoundsException e) {
            String mensaje = "Índice fuera de rango (" + indice + ") para un arreglo de tamaño " + lista.length;
            Logger.warn(ValidationUtils.class, mensaje);
            // Conserva la excepción original para no perder el stack trace al agregar el mensaje
            ArrayIndexOutOfBoundsException errorConDetalle = new ArrayIndexOutOfBoundsException(mensaje);
            errorConDetalle.initCause(e);
            throw errorConDetalle;
        }
    }
}
