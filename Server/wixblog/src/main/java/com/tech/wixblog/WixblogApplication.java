package com.tech.wixblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WixblogApplication {
    public static void main (String[] args) {
        SpringApplication.run(WixblogApplication.class, args);
    }

}
