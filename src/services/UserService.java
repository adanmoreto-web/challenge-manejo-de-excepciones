package services;

import exceptions.CustomBusinessException;
import exceptions.InvalidInputException;
import org.springframework.stereotype.Service;
import utils.Logger;
import utils.ValidationUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    public String registrarUsuario(String nombre, String email, int edad) throws CustomBusinessException {
        Logger.debug(UserService.class, "Iniciando registro para email: " + email);

        String nombreLimpio = ValidationUtils.validarTextoNoVacio(nombre, "nombre");
        String emailLimpio = ValidationUtils.validarEmail(email);

        if (edad < 18) {
            String mensaje = "El usuario debe ser mayor de 18 años para registrarse. Edad recibida: " + edad;
            Logger.warn(UserService.class, mensaje);
            throw new CustomBusinessException(mensaje);
        }

        Logger.info(UserService.class, "Usuario registrado correctamente: " + nombreLimpio);
        return "Usuario registrado: " + nombreLimpio + " (" + emailLimpio + ")";
    }

    public String buscarUsuarioEnBaseDeDatos(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser positivo. Valor recibido: " + idUsuario);
        }

        // ID 999 reservado para simular fallo de conexión en pruebas
        if (idUsuario == 999) {
            throw new SQLException("Error de conexión al consultar el usuario con ID: " + idUsuario);
        }

        Logger.debug(UserService.class, "Usuario encontrado en BD con ID: " + idUsuario);
        return "Usuario_" + idUsuario;
    }

    public String obtenerPerfilUsuario(int idUsuario) throws CustomBusinessException {
        try {
            String nombreUsuario = buscarUsuarioEnBaseDeDatos(idUsuario);
            Logger.info(UserService.class, "Perfil obtenido para: " + nombreUsuario);
            return "Perfil[" + nombreUsuario + "]";
        } catch (SQLException e) {
            Logger.error(UserService.class, "Error SQL al obtener el perfil del usuario " + idUsuario, e);
            throw new CustomBusinessException("No se pudo obtener el perfil del usuario " + idUsuario, e);
        }
    }

    public List<String> leerUsuariosDesdeArchivo(Path rutaArchivo) {
        if (rutaArchivo == null) {
            throw new InvalidInputException("La ruta del archivo no puede ser nula.");
        }

        List<String> usuarios = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo.toFile(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    usuarios.add(linea.trim());
                }
            }
            Logger.info(UserService.class, "Se leyeron " + usuarios.size() + " usuarios desde " + rutaArchivo.getFileName());
        } catch (FileNotFoundException e) {
            // Se captura antes que IOException porque FileNotFoundException hereda de ella
            Logger.warn(UserService.class, "Archivo no encontrado en la ruta: " + rutaArchivo + " -> " + e.getMessage());
        } catch (IOException e) {
            Logger.error(UserService.class, "Error de entrada/salida al leer el archivo: " + rutaArchivo, e);
        }

        return usuarios;
    }
}
