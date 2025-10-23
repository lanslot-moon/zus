package org.kitona.zus.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.kitona.zus.infrastructure.mapper")
@SpringBootApplication(scanBasePackages = {"org.kitona.zus"})
public class ZusStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZusStarterApplication.class, args);
    }

}
