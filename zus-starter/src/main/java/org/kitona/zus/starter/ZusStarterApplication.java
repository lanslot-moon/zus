package org.kitona.zus.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.kitona.zus.*")
public class ZusStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZusStarterApplication.class, args);
    }

}
