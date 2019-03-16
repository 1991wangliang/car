package com.codingapi.dl4jcardetection.vo;

import com.codingapi.dl4jcardetection.utils.Point;

public class ResPoint {

    private String base64;

    private Point car;

    private Point parking;

    public Point getCar() {
        return car;
    }

    public void setCar(Point car) {
        this.car = car;
    }

    public Point getParking() {
        return parking;
    }

    public void setParking(Point parking) {
        this.parking = parking;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
