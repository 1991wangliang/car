package com.codingapi.dl4jcardetection;

import com.codingapi.dl4jcardetection.config.CarConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CarConfig.class)
public class Dl4jCarDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(Dl4jCarDataApplication.class, args);
    }

}
