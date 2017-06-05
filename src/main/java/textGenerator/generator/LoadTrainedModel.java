package textGenerator.generator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import textGenerator.training.BRCharacterIterator;
import textGenerator.training.LSTMBRTextModelingTrainer;

/**
 *
 * @author joelpl
 */
public class LoadTrainedModel {

    private BRCharacterIterator characterIterator;
    private String modelFilePath;
    private int nSamplesToGenerate = 1;

    public LoadTrainedModel(BRCharacterIterator characterIterator, String modelFilePath) {
        this.characterIterator = characterIterator;
        this.modelFilePath = modelFilePath;
    }
    
    public String getSample() {
        int nCharactersToSample = 300;

        StringBuilder[] samples = null;
        try {
            MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(new File(modelFilePath));
            network.init();
            network.setListeners(new ScoreIterationListener(1));

            TextSampleGenerator txtGenerator = new TextSampleGenerator(network, characterIterator);

            samples = txtGenerator.getTextSamples(nCharactersToSample, nSamplesToGenerate);
            nSamplesToGenerate++;

        } catch (IOException ex) {
            Logger.getLogger(LoadTrainedModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return samples[0].toString();
    }

    public void buildText() throws IOException {
        int nSamplesToGenerate = 4;
        int nCharactersToSample = 300;

        MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(new File(modelFilePath));
        
        network.init();
        network.setListeners(new ScoreIterationListener(1));

        TextSampleGenerator txtGenerator = new TextSampleGenerator(network, characterIterator);

        StringBuilder[] samples = txtGenerator.getTextSamples(nCharactersToSample, nSamplesToGenerate);
        for( int j=0; j<samples.length; j++ ){
            System.out.println("----- Sample " + j + " -----");
            System.out.println(samples[j].toString());
            System.out.println();
        }
    }
}