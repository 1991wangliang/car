package com.codingapi.dl4jcardetection;

import com.codingapi.dl4jcardetection.config.CarConfig;
import com.codingapi.dl4jcardetection.detection.CarParkingDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties(CarConfig.class)
public class DetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetectionApplication.class,args);
    }

    @Autowired
    private CarParkingDetection carParkingDetection;

    @PostConstruct
    public void start(){
        String path = null;
        try {
            path = carParkingDetection.detection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(path);
    }
}