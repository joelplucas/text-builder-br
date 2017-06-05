package textGenerator.training;

import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;


public class BRCharacterIterator implements DataSetIterator {
    private char[] fileCharacters, validCharacters;
    private Map<Character,Integer> charToIdxMap;

    private int exampleLength, miniBatchSize;
    private Random random;
    private LinkedList<Integer> exampleStartOffsets = new LinkedList<>();

    public BRCharacterIterator(String textFilePath, int miniBatchSize, int exampleLength) throws Exception {

        char[] validCharacters = BRCharacterIterator.getMinimalCharacterSet();
        this.validCharacters = BRCharacterIterator.addPtValidCharacters(validCharacters);;

        this.exampleLength = exampleLength;
        this.miniBatchSize = miniBatchSize;
        this.random = new Random(12345);

        charToIdxMap = new HashMap<>();
        for(int i=0; i<this.validCharacters.length; i++) {
            charToIdxMap.put(this.validCharacters[i], i);
        }

        List<String> lines = Files.readAllLines(new File(textFilePath).toPath(), Charset.forName("UTF-8"));
        int maxSize = lines.size();
        for(String s : lines) {
            maxSize += s.length();
        }

        this.fileCharacters = fileContentToChars(lines, maxSize);

        checkTextExample();
        System.out.println("Loaded and converted file: " + fileCharacters.length + " valid characters of "+ maxSize + " total characters (" + (maxSize - fileCharacters.length) + " removed)");
        defineMiniBatches();
    }

    private char[] fileContentToChars(List<String> lines, int maxSize) throws IOException {
        boolean newLineValid = charToIdxMap.containsKey('\n');
        char[] characters = new char[maxSize];
        int currIdx = 0;

        for(String s : lines) {
            char[] thisLine = s.toCharArray();
            for (char aThisLine : thisLine) {
                if (!charToIdxMap.containsKey(aThisLine)) {
                    continue;
                }
                characters[currIdx++] = aThisLine;
            }
            if(newLineValid) {
                characters[currIdx++] = '\n';
            }
        }

        if( currIdx == characters.length) {
            return characters;
        } else {
            return Arrays.copyOfRange(characters, 0, currIdx);
        }
    }

    private void checkTextExample() {
        if(exampleLength >= fileCharacters.length ) {
            throw new IllegalArgumentException("exampleLength="+exampleLength
                    +" cannot exceed number of valid characters in file ("+fileCharacters.length+")");
        }
    }

    private void defineMiniBatches() {
        int nMinibatchesPerEpoch = (fileCharacters.length-1) / exampleLength - 2;
        for( int i=0; i<nMinibatchesPerEpoch; i++ ){
            exampleStartOffsets.add(i * exampleLength);
        }
        Collections.shuffle(exampleStartOffsets, random);
    }

    public static char[] getMinimalCharacterSet(){
        List<Character> validChars = new LinkedList<>();
        for(char c='a'; c<='z'; c++) validChars.add(c);
        for(char c='A'; c<='Z'; c++) validChars.add(c);
        for(char c='0'; c<='9'; c++) validChars.add(c);
        char[] temp = {'!', '&', '(', ')', '?', '-', '\'', '"', ',', '.', ':', ';', ' ', '\n', '\t'};
        for( char c : temp ) validChars.add(c);
        return getArrayFromCharList(validChars);
    }

    public static char[] addPtValidCharacters(char[] validCharacters) {
        List<Character> validChars = new LinkedList<>();
        for(char c : validCharacters) {
            validChars.add(c);
        }
        char[] BRChars = {'ç', 'á', 'à', 'â', 'ã', 'é', 'ê', 'í', 'ó', 'ô', 'õ', 'ú'};
        for(char c : BRChars) {
            validChars.add(c);
        }
        return getArrayFromCharList(validChars);
    }

    public static char[] getArrayFromCharList(List<Character> validChars) {
        char[] out = new char[validChars.size()];
        int i=0;
        for(Character c : validChars) {
            out[i++] = c;
        }
        return out;
    }

    public char convertIndexToCharacter( int idx ){
        return validCharacters[idx];
    }

    public int convertCharacterToIndex( char c ){
        return charToIdxMap.get(c);
    }

    public char getRandomCharacter(){
        return validCharacters[(int) (random.nextDouble()*validCharacters.length)];
    }

    public boolean hasNext() {
        return exampleStartOffsets.size() > 0;
    }

    public DataSet next() {
        return next(miniBatchSize);
    }

    public DataSet next(int num) {
        int currMinibatchSize = Math.min(num, exampleStartOffsets.size());
        INDArray input = Nd4j.create(new int[]{currMinibatchSize,validCharacters.length,exampleLength}, 'f');
        INDArray labels = Nd4j.create(new int[]{currMinibatchSize,validCharacters.length,exampleLength}, 'f');

        for( int i=0; i<currMinibatchSize; i++ ){
            int startIdx = exampleStartOffsets.removeFirst();
            int endIdx = startIdx + exampleLength;
            if(endIdx >= fileCharacters.length) {
                endIdx = fileCharacters.length - 1;
            }
            int currCharIdx = charToIdxMap.get(fileCharacters[startIdx]);
            int c=0;
            for(int j=startIdx+1; j<endIdx; j++, c++ ){
                char fileChar = fileCharacters[j];
                int nextCharIdx = charToIdxMap.get(fileChar);
                input.putScalar(new int[]{i,currCharIdx,c}, 1.0);
                labels.putScalar(new int[]{i,nextCharIdx,c}, 1.0);
                currCharIdx = nextCharIdx;
            }
        }

        return new DataSet(input,labels);
    }

    public int totalExamples() {
        return (fileCharacters.length-1) / miniBatchSize - 2;
    }

    public int inputColumns() {
        return validCharacters.length;
    }

    public int totalOutcomes() {
        return validCharacters.length;
    }

    public void reset() {
        exampleStartOffsets.clear();
	    int nMinibatchesPerEpoch = totalExamples();
        for(int i=0; i<nMinibatchesPerEpoch; i++){
            exampleStartOffsets.add(i * miniBatchSize);
        }
        Collections.shuffle(exampleStartOffsets, random);
    }

    public int batch() {
        return miniBatchSize;
    }

    public int cursor() {
        return totalExamples() - exampleStartOffsets.size();
    }

    public int numExamples() {
        return totalExamples();
    }

    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<String> getLabels() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}