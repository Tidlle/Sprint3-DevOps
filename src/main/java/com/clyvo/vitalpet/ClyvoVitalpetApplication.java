package com.clyvo.vitalpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ClyvoVitalpetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClyvoVitalpetApplication.class, args);
    }
}
