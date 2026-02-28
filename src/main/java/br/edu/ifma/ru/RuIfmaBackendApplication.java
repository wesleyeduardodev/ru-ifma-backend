package br.edu.ifma.ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RuIfmaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuIfmaBackendApplication.class, args);
    }
}
