package org.example.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        String dir = new java.io.File(".env").exists() ? "." : "server";
        Dotenv.configure().directory(dir).load()
                .entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        SpringApplication.run(ServerApplication.class, args);
    }

}
