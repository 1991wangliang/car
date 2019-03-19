package com.codingapi.dl4jcardetection.detection;

import com.codingapi.dl4jcardetection.config.CarConfig;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CarParkingTest {

    private static final Logger log = LoggerFactory.getLogger(CarParkingTest.class);

    private ComputationGraph model;

    private final CarConfig carConfig;

    // parameters matching the pretrained TinyYOLO model
    int width = 416;
    int height = 416;

    int gridWidth = width / 32;
    int gridHeight = height / 32;

    @Autowired
    public CarParkingTest(CarConfig carConfig) {
        this.carConfig = carConfig;
        String modelFilename = carConfig.getPath() + "/car_parking_model.zip";
        if (!new File(modelFilename).exists()) {
            log.warn("no found model");
            return;
        }
        log.info("Load model...");
        try {
            model = ModelSerializer.restoreComputationGraph(modelFilename);
        } catch (IOException e) {
            throw new RuntimeException("restore error",e);
        }
    }

    public void test(String pitcute) {
        NativeImageLoader imageLoader = new NativeImageLoader();
        ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0,1);
        org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer yout = (org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer) model.getOutputLayer(0);
        List<String> labels = Arrays.asList("parking","car");
        INDArray features = null;
        try {
            features = imageLoader.asMatrix(new File(carConfig.getPath()+"/"+pitcute));
            scaler.transform(features);
        } catch (IOException e) {
            throw new RuntimeException("image load fail ",e);
        }
        INDArray results = model.outputSingle(features);
        List<DetectedObject> objs = yout.getPredictedObjects(results, carConfig.getDetectionThreshold());
        for (DetectedObject obj : objs) {
            log.info("obj-->{}",obj);
            double[] xy1 = obj.getTopLeftXY();
            double[] xy2 = obj.getBottomRightXY();
            String label = labels.get(obj.getPredictedClass());
            int x1 = (int) Math.round(width * xy1[0] / gridWidth);
            int y1 = (int) Math.round(height * xy1[1] / gridHeight);
            int x2 = (int) Math.round(width * xy2[0] / gridWidth);
            int y2 = (int) Math.round(height * xy2[1] / gridHeight);

            System.out.println("label:"+label+",x1:"+x1+",y1:"+y1+",x2:"+x2+",y2:"+y2);
        }


    }


}
