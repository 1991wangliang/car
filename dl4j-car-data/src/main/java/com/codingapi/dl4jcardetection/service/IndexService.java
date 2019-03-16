package com.codingapi.dl4jcardetection.service;

import com.codingapi.dl4jcardetection.vo.ResPoint;

import java.util.List;

public interface IndexService {

    int save(String car, String parking, String picture);

    ResPoint checkPoint(String file);

    List<String> list();
}
