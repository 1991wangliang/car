package com.codingapi.dl4jcardetection.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.dl4jcardetection.config.CarConfig;
import com.codingapi.dl4jcardetection.service.IndexService;
import com.codingapi.dl4jcardetection.utils.Point;
import com.codingapi.dl4jcardetection.utils.UUIDUtils;
import com.codingapi.dl4jcardetection.utils.XmlUtils;
import com.codingapi.dl4jcardetection.vo.ResPoint;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private CarConfig carConfig;

    @Override
    public int save(String car, String parking, String picture) {
        String name = UUIDUtils.randomId();
        Point parkingObj =  JSONObject.parseObject(parking,Point.class);
        Point carObj =  JSONObject.parseObject(car,Point.class);
        String jpgName = String.format("%s/JPEGImages/%s.jpg",carConfig.getPath(),name);
        String xmlName = String.format("%s/Annotations/%s.xml",carConfig.getPath(),name);
        if(!StringUtils.isEmpty(picture)) {
            picture = picture.replace("data:image/jpeg;base64,", "");
            byte[] bytes = Base64Utils.decodeFromString(picture);
            try {
                FileUtils.writeByteArrayToFile(new File(jpgName), bytes);
                FileUtils.writeStringToFile(new File(xmlName), XmlUtils.createXml(name,parkingObj,carObj), Charset.forName("utf8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }
        return 0;
    }

    @Override
    public ResPoint checkPoint(String file) {
        String xmlName = String.format("%s/Annotations/%s.xml",carConfig.getPath(),file);
        String jpgName = String.format("%s/JPEGImages/%s.jpg",carConfig.getPath(),file);
        try {
            String xmlContent = FileUtils.readFileToString(new File(xmlName),Charset.forName("utf8"));
            ResPoint resPoint=  XmlUtils.xml2ResPoint(xmlContent);
            String base64 = String.format("%s,%s","data:image/jpeg;base64",Base64Utils.encodeToString(FileUtils.readFileToByteArray(new File(jpgName))));
            resPoint.setBase64(base64);
            return resPoint;
        } catch (IOException e) {
            throw new RuntimeException("read file error.",e);
        }
    }

    @Override
    public List<String> list() {
        String xmlpath = String.format("%s/Annotations/",carConfig.getPath());
        return Arrays.asList(new File(xmlpath).list());
    }
}
