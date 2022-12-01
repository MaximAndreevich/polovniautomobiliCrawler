package com.max.autoLookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.max.autoLookup")
public class AutoLookupApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoLookupApplication.class, args);
    }

}
