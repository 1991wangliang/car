package com.codingapi.dl4jcardata.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
public class IndexController {


    @PostMapping("/save")
    public int save(HttpServletRequest request){
        String content = request.getParameter("content");
        String picture = request.getParameter("picture");
        System.out.println(picture);
        if(!StringUtils.isEmpty(picture)) {
            picture = picture.replace("data:image/jpeg;base64,", "");
            byte[] bytes = Base64Utils.decodeFromString(picture);
            try {
                FileUtils.writeByteArrayToFile(new File("C:\\test\\123.jpg"), bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("content:"+content+",picture:"+picture);
        return 1;
    }

}
