package com.codingapi.dl4jcardetection.controller;

import com.codingapi.dl4jcardetection.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @PostMapping("/save")
    public int save(HttpServletRequest request){
        String car = request.getParameter("car");
        String parking = request.getParameter("parking");
        String picture = request.getParameter("picture");
        return indexService.save(car,parking,picture);
    }

}
