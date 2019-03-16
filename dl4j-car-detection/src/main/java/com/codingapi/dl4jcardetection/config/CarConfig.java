package com.codingapi.dl4jcardetection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "car.config")
public class CarConfig {

    private final static String _Annotations = "Annotations";
    private final static String _JPEGImages = "JPEGImages";

    private String path;
    private int batchSize = 2;
    private int nEpochs = 50;
    private double detectionThreshold=0.3;

    public double getDetectionThreshold() {
        return detectionThreshold;
    }

    public void setDetectionThreshold(double detectionThreshold) {
        this.detectionThreshold = detectionThreshold;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getnEpochs() {
        return nEpochs;
    }

    public void setnEpochs(int nEpochs) {
        this.nEpochs = nEpochs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        new File(path+"/"+_Annotations).mkdirs();
        new File(path+"/"+_JPEGImages).mkdirs();

    }
}
