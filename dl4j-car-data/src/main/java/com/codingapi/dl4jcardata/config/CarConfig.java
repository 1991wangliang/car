package com.codingapi.dl4jcardata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "car.config")
public class CarConfig {

    private final String _Annotations = "Annotations";
    private final String _JPEGImages = "JPEGImages";

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        new File(path+"/"+_Annotations).mkdirs();
        new File(path+"/"+_JPEGImages).mkdirs();

    }
}
