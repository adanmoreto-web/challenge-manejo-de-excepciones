package services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utils.Logger;

@SpringBootApplication(scanBasePackages = {"services", "utils", "exceptions"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Logger.info(Application.class, "Aplicación de manejo de excepciones iniciada correctamente.");
    }
}
