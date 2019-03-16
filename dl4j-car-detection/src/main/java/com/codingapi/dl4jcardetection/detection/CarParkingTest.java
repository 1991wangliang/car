package com.codingapi.dl4jcardetection.detection;

import com.codingapi.dl4jcardetection.config.CarConfig;
import org.bytedeco.javacpp.opencv_core;
import org.datavec.api.io.filters.RandomPathFilter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.objdetect.ObjectDetectionRecordReader;
import org.datavec.image.recordreader.objdetect.impl.VocLabelProvider;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.bytedeco.javacpp.opencv_core.CV_8U;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_DUPLEX;
import static org.bytedeco.javacpp.opencv_imgproc.*;

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
//            throw new RuntimeException("no found model");
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
        System.out.println(model.summary());
        NativeImageLoader imageLoader = new NativeImageLoader();
        org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer yout = (org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer) model.getOutputLayer(0);
        List<String> labels = Arrays.asList("car","parking");
        INDArray features = null;
        try {
            features = imageLoader.asMatrix(new File(carConfig.getPath()+"/"+pitcute));
            DataNormalization scaler = new ImagePreProcessingScaler(0,1);
            scaler.transform(features);
        } catch (IOException e) {
            throw new RuntimeException("image load fail ",e);
        }
        INDArray results = model.outputSingle(features);
        List<DetectedObject> objs = yout.getPredictedObjects(results, carConfig.getDetectionThreshold());
//        model.get
        log.info("res->{}",objs);
        opencv_core.Mat mat = imageLoader.asMat(features);
        opencv_core.Mat convertedMat = new opencv_core.Mat();
        mat.convertTo(convertedMat, CV_8U, 255, 0);
        opencv_core.Mat image = new opencv_core.Mat();
        resize(convertedMat, image, new opencv_core.Size(width, height));
        for (DetectedObject obj : objs) {
            double[] xy1 = obj.getTopLeftXY();
            double[] xy2 = obj.getBottomRightXY();
            String label = labels.get(obj.getPredictedClass());
            int x1 = (int) Math.round(width * xy1[0] / gridWidth);
            int y1 = (int) Math.round(height * xy1[1] / gridHeight);
            int x2 = (int) Math.round(width * xy2[0] / gridWidth);
            int y2 = (int) Math.round(height * xy2[1] / gridHeight);
            rectangle(image, new opencv_core.Point(x1, y1), new opencv_core.Point(x2, y2), opencv_core.Scalar.RED);
            putText(image, label, new opencv_core.Point(x1 + 2, y2 - 2), FONT_HERSHEY_DUPLEX, 1, opencv_core.Scalar.GREEN);
        }


    }


}
