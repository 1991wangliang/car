package com.codingapi.dl4jcardetection;

import com.codingapi.dl4jcardetection.detection.CarParkingLearning;
import com.codingapi.dl4jcardetection.detection.CarParkingTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Dl4jCarDataApplicationTests {

    @Autowired
    private CarParkingTest carParkingTest;

    @Autowired
    private CarParkingLearning carParkingLearning;


    @Test
    public void test() {
        carParkingTest.test("123.jpg");
    }

    @Test
    public void running(){
        carParkingLearning.learning();
    }

}
