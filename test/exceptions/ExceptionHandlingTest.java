package exceptions;

import org.junit.jupiter.api.Test;
import utils.ValidationUtils;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlingTest {

    @Test
    void jerarquia_customBusinessExceptionEsChecked() {
        CustomBusinessException checked = new CustomBusinessException("Error de negocio");

        assertInstanceOf(Exception.class, checked);
        assertInstanceOf(Throwable.class, checked);
        assertFalse(RuntimeException.class.isInstance(checked));
    }

    @Test
    void jerarquia_invalidInputExceptionEsUnchecked() {
        InvalidInputException unchecked = new InvalidInputException("Dato inválido");

        assertInstanceOf(RuntimeException.class, unchecked);
        assertInstanceOf(Exception.class, unchecked);
        assertInstanceOf(Throwable.class, unchecked);
    }

    @Test
    void validaciones_previenenNullPointerYValidanArgumentos() {
        assertThrows(
                InvalidInputException.class,
                () -> ValidationUtils.validarTextoNoVacio(null, "nombre")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> ValidationUtils.validarMontoPositivo(0)
        );
    }

    @Test
    void obtenerElementoSeguro_indiceFueraDeRango_lanzaMensajeDescriptivo() {
        String[] metodos = {"TARJETA", "EFECTIVO"};

        ArrayIndexOutOfBoundsException ex = assertThrows(
                ArrayIndexOutOfBoundsException.class,
                () -> ValidationUtils.obtenerElementoSeguro(metodos, 5)
        );

        assertTrue(ex.getMessage().contains("Índice fuera de rango (5)"));
    }

    @Test
    void tryWithResources_cierraRecursoAutomaticamenteInclusoConError() {
        RecursoSimulado recurso = new RecursoSimulado();

        assertThrows(IllegalStateException.class, () -> {
            try (RecursoSimulado r = recurso) {
                r.usarConFallo();
            }
        });

        assertTrue(recurso.isCerrado());
    }

    private static class RecursoSimulado implements AutoCloseable {
        private boolean cerrado = false;

        void usarConFallo() {
            throw new IllegalStateException("Fallo mientras se usaba el recurso");
        }

        @Override
        public void close() {
            cerrado = true;
        }

        boolean isCerrado() {
            return cerrado;
        }
    }
}
