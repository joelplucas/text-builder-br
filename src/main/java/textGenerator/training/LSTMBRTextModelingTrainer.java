package textGenerator.training;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import java.io.File;
import java.io.IOException;
import textGenerator.generator.TextSampleGenerator;


public class LSTMBRTextModelingTrainer {

    public static int MIN_BATCH_SIZE = 32;
    public static int EXAMPLE_LENGTH = 1000;

    private int lstmLayerSize = 200;
    private int tbpttLength = 50;
    private int numEpochs = 5;
    private int generateSamplesEveryNMinibatches = 10;
    private int numSamplesToGenerate = 4;
    private int sampleNumChars = 300;

    private BRCharacterIterator charIterator;

    private MultiLayerNetwork network;
    
    public static void main( String[] args ) throws Exception {	
        String trainingFilePath = args[0];
        int miniBatchSize = (args.length > 1) ? Integer.valueOf(args[1]) : MIN_BATCH_SIZE;
        int exampleLength = (args.length > 2) ? Integer.valueOf(args[2]) : EXAMPLE_LENGTH;

        BRCharacterIterator charIterator =  new BRCharacterIterator(trainingFilePath, miniBatchSize, exampleLength);
        LSTMBRTextModelingTrainer lstm = new LSTMBRTextModelingTrainer(charIterator);
        lstm.train(trainingFilePath, miniBatchSize, exampleLength);
    }

    public LSTMBRTextModelingTrainer(BRCharacterIterator charIterator) throws Exception {
        this.charIterator = charIterator;

        network = initializeNetwork(charIterator);
        network.init();
        network.setListeners(new ScoreIterationListener(1));

        printLayers();
    }

    private void printLayers() {
        Layer[] layers = network.getLayers();
        int totalNumParams = 0;
        for( int i=0; i<layers.length; i++ ){
            int nParams = layers[i].numParams();
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams);
    }

    private MultiLayerNetwork initializeNetwork(BRCharacterIterator charIterator) {
        int nOut = charIterator.totalOutcomes();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
                .learningRate(0.1)
                .rmsDecay(0.95)
                .seed(12345)
                .regularization(true)
                .l2(0.001)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.RMSPROP)
                .list()
                .layer(0, new GravesLSTM.Builder().nIn(charIterator.inputColumns()).nOut(lstmLayerSize).activation("tanh").build())
                .layer(1, new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize).activation("tanh").build())
                .layer(2, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation("softmax").nIn(lstmLayerSize).nOut(nOut).build())
                .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
                .pretrain(false).backprop(true)
                .build();
        return new MultiLayerNetwork(conf);
    }
    
    public void train(String trainingFilePath, int miniBatchSize, int exampleLength) throws Exception {
        TextSampleGenerator txtGenerator = new TextSampleGenerator(network, charIterator);

        int miniBatchNumber = 0;
        for(int i=0; i<numEpochs; i++) {
            while(charIterator.hasNext()) {
                DataSet ds = charIterator.next();
                network.fit(ds);
                if(++miniBatchNumber % generateSamplesEveryNMinibatches == 0) {
                    System.out.println("--------------------");
                    System.out.println("Completed " + miniBatchNumber + " minibatches of size " + miniBatchSize + "x" + exampleLength + " characters" );
                    StringBuilder[] samples = txtGenerator.getTextSamples(sampleNumChars, numSamplesToGenerate);
                    for(int j=0; j<samples.length; j++) {
                        System.out.println("----- Text Sample " + j + " -----");
                        System.out.println(samples[j].toString());
                        System.out.println();
                    }
                    saveModel(trainingFilePath);
                }
            }

            charIterator.reset();
        }
        System.out.println("\n\nExample complete");
    }
    
    private void saveModel(String filePath) throws IOException {
        String modelFilePath = filePath.substring(0, filePath.length()-3);
        File modelFile = new File(modelFilePath.concat("model"));
        ModelSerializer.writeModel(network, modelFile, true);
    }

    public BRCharacterIterator getCharIterator() {
        return charIterator;
    }
}