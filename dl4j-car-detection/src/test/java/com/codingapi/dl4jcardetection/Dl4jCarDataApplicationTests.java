package com.codingapi.dl4jcardetection;

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


    @Test
    public void contextLoads() {
        carParkingTest.test("1234.jpg");
    }

}
