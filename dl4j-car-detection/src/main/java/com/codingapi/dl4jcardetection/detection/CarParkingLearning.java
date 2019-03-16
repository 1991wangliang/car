package com.codingapi.dl4jcardetection.detection;

import com.codingapi.dl4jcardetection.config.CarConfig;
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
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.memory.enums.AllocationPolicy;
import org.nd4j.linalg.api.memory.enums.LearningPolicy;
import org.nd4j.linalg.api.memory.enums.MirroringPolicy;
import org.nd4j.linalg.api.memory.enums.SpillPolicy;
import org.nd4j.linalg.api.ndarray.INDArray;
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
import java.util.Random;

@Component
public class CarParkingLearning {

    private static final Logger log = LoggerFactory.getLogger(CarParkingLearning.class);

    @Autowired
    private CarConfig carConfig;

    public void learning() {
        // parameters matching the pretrained TinyYOLO model
        int width = 416;
        int height = 416;
        int nChannels = 3;

        int gridWidth = width / 32;
        int gridHeight = height / 32;

        // number classes for the Car Or Parking
        int nClasses = 2;

        // parameters for the Yolo2OutputLayer

        double lambdaNoObj = 0.5;
        double lambdaCoord = 5.0;
        double[][] priorBoxes = {{2, 1}, {3, 2}, {3, 2}, {1, 2}, {2, 2}, {3, 3}};
        int nBoxes = priorBoxes.length;

        // parameters for the training phase
        int batchSize = carConfig.getBatchSize();
        int nEpochs = carConfig.getnEpochs();
        double learningRate = 1e-3;
        double lrMomentum = 0.9;

        int seed = 123;
        Random rng = new Random(seed);

        String dataDir = carConfig.getPath();
        File imageDir = new File(dataDir, "JPEGImages");

        log.info("Load data...");

        RandomPathFilter pathFilter = new RandomPathFilter(rng) {
            @Override
            protected boolean accept(String name) {
                name = name.replace("/JPEGImages/", "/Annotations/").replace(".jpg", ".xml");
                try {
                    return new File(new URI(name)).exists();
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        InputSplit[] data = new FileSplit(imageDir, NativeImageLoader.ALLOWED_FORMATS, rng).sample(pathFilter, 0.8, 0.2);
        InputSplit trainData = data[0];
        InputSplit testData = data[1];

        ObjectDetectionRecordReader recordReaderTrain = new ObjectDetectionRecordReader(height, width, nChannels, gridHeight, gridWidth,
                new VocLabelProvider(dataDir));

        ObjectDetectionRecordReader recordReaderTest = new ObjectDetectionRecordReader(height, width, nChannels, gridHeight, gridWidth,
                new VocLabelProvider(dataDir));
        try {
            recordReaderTrain.initialize(trainData);
            recordReaderTest.initialize(testData);
        } catch (Exception e) {
            throw new RuntimeException("init fail ..",e);
        }
        // ObjectDetectionRecordReader performs regression, so we need to specify it here
        RecordReaderDataSetIterator train = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 1, 1, true);
        train.setPreProcessor(new ImagePreProcessingScaler(0, 1));

        RecordReaderDataSetIterator test = new RecordReaderDataSetIterator(recordReaderTest, 1, 1, 1, true);
        test.setPreProcessor(new ImagePreProcessingScaler(0, 1));

        ComputationGraph model;
        String modelFilename = carConfig.getPath() + "/car_parking_model.zip";

        log.info("Build model...");
        ComputationGraph pretrained = null;
        try {
            pretrained = (ComputationGraph) TinyYOLO.builder().build().initPretrained();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        INDArray priors = Nd4j.create(priorBoxes);

        FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .gradientNormalizationThreshold(1.0)
                .updater(new Adam.Builder().learningRate(learningRate).build())
                .updater(new Nesterovs.Builder().learningRate(learningRate).momentum(lrMomentum).build())
                .activation(Activation.IDENTITY)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .inferenceWorkspaceMode(WorkspaceMode.ENABLED)
                .build();


        model = new TransferLearning.GraphBuilder(pretrained).fineTuneConfiguration(fineTuneConf).removeVertexKeepConnections("conv2d_9")
                .removeVertexKeepConnections("outputs")
                .addLayer("convolution2d_9",
                        new ConvolutionLayer.Builder(1, 1)
                                .nIn(1024)
                                .nOut(nBoxes * (5 + nClasses))
                                .stride(1, 1)
                                .convolutionMode(ConvolutionMode.Same)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.IDENTITY)
                                .build(),
                        "leaky_re_lu_8")
                .addLayer("outputs",
                        new Yolo2OutputLayer.Builder()
                                .lambbaNoObj(lambdaNoObj)
                                .lambdaCoord(lambdaCoord)
                                .boundingBoxPriors(priors)
                                .build(),
                        "convolution2d_9")
                .setOutputs("outputs")
                .build();

        System.out.println(model.summary(InputType.convolutional(height, width, nChannels)));

        log.info("Train model...");

        model.setListeners(new ScoreIterationListener(1));
        for (int i = 0; i < nEpochs; i++) {
            train.reset();
            while (train.hasNext()) {
                model.fit(train.next());
            }
            log.info("*** Completed epoch {} ***", i);
        }
        log.info("Evaluate model....");
        try {
            ModelSerializer.writeModel(model, modelFilename, true);
        } catch (IOException e) {
            throw new RuntimeException("save model fail",e);
        }
    }

}
