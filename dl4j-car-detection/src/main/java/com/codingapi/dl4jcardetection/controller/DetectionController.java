package com.codingapi.dl4jcardetection.controller;


import com.codingapi.dl4jcardetection.detection.CarParkingLearning;
import com.codingapi.dl4jcardetection.detection.CarParkingTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetectionController {

    @Autowired
    private CarParkingLearning carParkingLearning;

    @Autowired
    private CarParkingTest carParkingTest;

    @GetMapping("/learning")
    public int learning(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                carParkingLearning.learning();
            }
        }.start();
        return 1;
    }

    @GetMapping("/test")
    public int test(@RequestParam("file") String file){
        carParkingTest.test(file);
        return 1;
    }

}
