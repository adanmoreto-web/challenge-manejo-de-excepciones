package services;

import exceptions.CustomBusinessException;
import exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void registrarUsuario_datosValidos_retornaConfirmacion() throws CustomBusinessException {
        String resultado = userService.registrarUsuario("Carlos", "carlos@correo.com", 25);

        assertEquals("Usuario registrado: Carlos (carlos@correo.com)", resultado);
    }

    @Test
    void registrarUsuario_menorDeEdad_lanzaCustomBusinessException() {
        CustomBusinessException ex = assertThrows(
                CustomBusinessException.class,
                () -> userService.registrarUsuario("Lucia", "lucia@correo.com", 16)
        );

        assertTrue(ex.getMessage().contains("mayor de 18"));
    }

    @Test
    void registrarUsuario_emailInvalido_lanzaInvalidInputException() {
        assertThrows(
                InvalidInputException.class,
                () -> userService.registrarUsuario("Carlos", "carlos-sin-arroba.com", 25)
        );
    }

    @Test
    void buscarUsuarioEnBaseDeDatos_errorDeConexion_lanzaSQLException() {
        SQLException ex = assertThrows(
                SQLException.class,
                () -> userService.buscarUsuarioEnBaseDeDatos(999)
        );

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    void obtenerPerfilUsuario_falloSql_encadenaCausaEnCustomBusinessException() {
        CustomBusinessException ex = assertThrows(
                CustomBusinessException.class,
                () -> userService.obtenerPerfilUsuario(999)
        );

        assertNotNull(ex.getCause());
        assertInstanceOf(SQLException.class, ex.getCause());
    }

    @Test
    void leerUsuariosDesdeArchivo_archivoValido_devuelveLineas(@TempDir Path tempDir) throws IOException {
        Path archivo = tempDir.resolve("usuarios.txt");
        Files.write(archivo, List.of("Ana", "Luis", ""));

        List<String> usuarios = userService.leerUsuariosDesdeArchivo(archivo);

        assertEquals(List.of("Ana", "Luis"), usuarios);
    }

    @Test
    void leerUsuariosDesdeArchivo_noExiste_manejaFileNotFoundYNoRompe(@TempDir Path tempDir) {
        Path archivoInexistente = tempDir.resolve("no_existe.txt");

        List<String> usuarios = userService.leerUsuariosDesdeArchivo(archivoInexistente);

        assertTrue(usuarios.isEmpty());
    }
}
