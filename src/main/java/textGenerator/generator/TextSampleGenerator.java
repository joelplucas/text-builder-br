package textGenerator.generator;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import textGenerator.training.BRCharacterIterator;

import java.util.Random;

public class TextSampleGenerator {

    private MultiLayerNetwork network;
    private BRCharacterIterator iterator;
    private Random random;

    public TextSampleGenerator(MultiLayerNetwork network, BRCharacterIterator iter) {
        this.network = network;
        this.iterator = iter;
        this.random = new Random(12345);
    }

    public StringBuilder[] getTextSamples(int numCharsToSample, int numSamples ){
        String txtInitialization = String.valueOf(iterator.getRandomCharacter());
        INDArray initializationInput = initialiatizeNDArray(txtInitialization, numSamples);

        StringBuilder[] sampleBuilders = new StringBuilder[numSamples];
        for(int i=0; i<numSamples; i++) {
            sampleBuilders[i] = new StringBuilder(txtInitialization);
        }

        network.rnnClearPreviousState();
        INDArray output = network.rnnTimeStep(initializationInput);
        output = output.tensorAlongDimension(output.size(2)-1,1,0);

        for(int i=0; i<numCharsToSample; i++) {
            INDArray nextInput = Nd4j.zeros(numSamples, iterator.inputColumns());
            for(int j=0; j<numSamples; j++) {
                sampleBuilders[j].append(generateCharSample(output, nextInput, j));
            }
            output = network.rnnTimeStep(nextInput);
        }

        return sampleBuilders;
    }

    private INDArray initialiatizeNDArray(String txtInitialization, int numSamples) {
        INDArray initializationInput = Nd4j.zeros(numSamples, iterator.inputColumns(), txtInitialization.length());
        char[] init = txtInitialization.toCharArray();
        for(int i=0; i<init.length; i++) {
            int idx = iterator.convertCharacterToIndex(init[i]);
            for(int j=0; j<numSamples; j++) {
                initializationInput.putScalar(new int[]{j,idx,i}, 1.0f);
            }
        }
        return initializationInput;
    }

    private char generateCharSample(INDArray output, INDArray nextInput, int sampleIndex) {
        double[] outputProbDistribution = new double[iterator.totalOutcomes()];
        for(int j=0; j<outputProbDistribution.length; j++) {
            outputProbDistribution[j] = output.getDouble(sampleIndex,j);
        }
        int sampledCharacterIdx = sampleFromDistribution(outputProbDistribution,random);

        nextInput.putScalar(new int[]{sampleIndex,sampledCharacterIdx}, 1.0f);
        return iterator.convertIndexToCharacter(sampledCharacterIdx);
    }

    public int sampleFromDistribution( double[] distribution, Random random){
        double d = random.nextDouble();
        double sum = 0.0;
        for(int i=0; i<distribution.length; i++ ){
            sum += distribution[i];
            if(d <= sum) {
                return i;
            }
        }
        throw new IllegalArgumentException("Distribution is invalid? d="+d+", sum="+sum);
    }

}
