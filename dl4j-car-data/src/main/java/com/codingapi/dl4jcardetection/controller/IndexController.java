package com.codingapi.dl4jcardetection.controller;

import com.codingapi.dl4jcardetection.service.IndexService;
import com.codingapi.dl4jcardetection.vo.ResPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @PostMapping("/checkPoint")
    public ResPoint checkPoint(@RequestParam("file") String file){
        return indexService.checkPoint(file);
    }

    @GetMapping("/list")
    public List<String> list(){
        return indexService.list();
    }

}
