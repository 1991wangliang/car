package com.codingapi.dl4jcardetection.utils;

import com.codingapi.dl4jcardetection.vo.ResPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class XmlUtils {

    private final static  String xml = "<annotation verified=\"no\">\n" +
            "    <folder>car</folder>\n" +
            "    <filename>#{fileName}</filename>\n" +
            "    <path>/JPEGImages/#{fileName}.jpg</path>\n" +
            "    <source>\n" +
            "        <database>Unknown</database>\n" +
            "    </source>\n" +
            "    <size>\n" +
            "        <width>800</width>\n" +
            "        <height>800</height>\n" +
            "        <depth>3</depth>\n" +
            "    </size>\n" +
            "    <segmented>0</segmented>\n" +
            "    <object>\n" +
            "        <name>car</name>\n" +
            "        <pose>Unspecified</pose>\n" +
            "        <truncated>0</truncated>\n" +
            "        <difficult>0</difficult>\n" +
            "        <bndbox>\n" +
            "            <xmin>#{car.xmin}</xmin>\n" +
            "            <ymin>#{car.ymin}</ymin>\n" +
            "            <xmax>#{car.xmax}</xmax>\n" +
            "            <ymax>#{car.ymax}</ymax>\n" +
            "        </bndbox>\n" +
            "    </object>\n" +
            "    <object>\n" +
            "        <name>parking</name>\n" +
            "        <pose>Unspecified</pose>\n" +
            "        <truncated>0</truncated>\n" +
            "        <difficult>0</difficult>\n" +
            "        <bndbox>\n" +
            "            <xmin>#{parking.xmin}</xmin>\n" +
            "            <ymin>#{parking.ymin}</ymin>\n" +
            "            <xmax>#{parking.xmax}</xmax>\n" +
            "            <ymax>#{parking.ymax}</ymax>\n" +
            "        </bndbox>\n" +
            "    </object>\n" +
            "</annotation>\n";

    public static String createXml(String fileName,Point parking,Point car){
        String newXml =xml.replace("#{fileName}",fileName);
        newXml = newXml.replace("#{parking.xmax}",parking.getXmax());
        newXml = newXml.replace("#{parking.xmin}",parking.getXmin());
        newXml = newXml.replace("#{parking.ymax}",parking.getYmax());
        newXml = newXml.replace("#{parking.ymin}",parking.getYmin());
        newXml = newXml.replace("#{car.xmax}",car.getXmax());
        newXml = newXml.replace("#{car.xmin}",car.getXmin());
        newXml = newXml.replace("#{car.ymax}",car.getYmax());
        newXml = newXml.replace("#{car.ymin}",car.getYmin());
        return newXml;
    }

    public static ResPoint xml2ResPoint(String xml) {
        JSONObject xmlObj =  XML.toJSONObject(xml);
        if(xmlObj==null||xmlObj.isNull("annotation")){
            throw new RuntimeException("no data");
        }
        ResPoint resPoint = new ResPoint();
        JSONArray jsonArray =  xmlObj.getJSONObject("annotation").getJSONArray("object");
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            JSONObject bndbox =  jsonObject.getJSONObject("bndbox");
            Point point = new Point();
            point.setLabel(name);
            point.setXmin(String.valueOf(bndbox.getInt("xmin")));
            point.setXmax(String.valueOf(bndbox.getInt("xmax")));
            point.setYmin(String.valueOf(bndbox.getInt("ymin")));
            point.setYmax(String.valueOf(bndbox.getInt("ymax")));
            if(name.equals("car")){
                resPoint.setCar(point);
            }else{
                resPoint.setParking(point);
            }
        }
        return resPoint;
    }


}
