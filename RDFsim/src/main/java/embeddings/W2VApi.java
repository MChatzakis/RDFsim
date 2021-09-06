package embeddings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 * Wrapper class of w2v to provide basic comparison methods
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
@Data
public class W2VApi {

    Word2Vec vec;
    SentenceIterator iter;
    TokenizerFactory tokenizerFactory;

    List<String> stopWords;

    int minWordFrequency;
    int layerSize;
    int seed;
    int windowSize;
    int iterations;

    public W2VApi(String path) {
        this.loadVectorFile(path);
    }

    public W2VApi(int minWordFrequency, int layerSize, int seed, int windowSize, int iterations, List<String> stopWords, String filepath) {
        this.minWordFrequency = minWordFrequency;
        this.layerSize = layerSize;
        this.seed = seed;
        this.stopWords = stopWords;
        this.windowSize = windowSize;
        this.iterations = iterations;
        this.iter = new LineSentenceIterator(new File(filepath));;
        this.tokenizerFactory = new DefaultTokenizerFactory();
    }

    public void train() {
        vec = new Word2Vec.Builder()
                .minWordFrequency(minWordFrequency)
                .layerSize(layerSize)
                .iterations(iterations)
                .seed(seed)
                .windowSize(windowSize)
                .iterate(iter)
                .tokenizerFactory(tokenizerFactory)
                .stopWords(stopWords)
                .build();

        vec.fit();
    }

    public void saveVectorSpace(String filepath) {
        try {
            WordVectorSerializer.writeWord2VecModel(vec, filepath);
        } catch (Exception ex) {
            Logger.getLogger(W2VApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Collection<String> getSimilarEntities(String entity, int count) {
        Collection<String> similars = vec.wordsNearest(entity, count);
        return similars;
    }

    public double calculateCosineSimilarity(String entity1, String entity2) {
        return vec.similarity(entity1, entity2);
    }

    public void loadVectorFile(String filepath) {
        vec = WordVectorSerializer.readWord2VecModel(filepath);
        assert (vec != null);
    }

    public HashMap<String, Double> getSimilarEntitiesWithValues(String entity, int count) {
        HashMap<String, Double> topEntities = new HashMap<>();
        Collection<String> entities = getSimilarEntities(entity, count);

        for (String neighbour : entities) {
            double cosSim = calculateCosineSimilarity(entity, neighbour);
            topEntities.put(neighbour, cosSim);
        }

        return CommonUtils.sortEntityMap(topEntities);
    }

    public Collection<String> getExpressionResult(Collection<String> ents2add, Collection<String> ents2sub, int count) {
        return vec.wordsNearest(ents2sub, ents2sub, count);
    }

    public Collection<String> getVocab() {
        return vec.getVocab().words();
    }

    /*public void removeWordsFromVocab(Collection<String> words2remove) {
        for (String s : words2remove) {
            vec.vocab().removeElement(s);
        }
    }

    public void removeWord(String word) {
        vec.vocab().removeElement(word);
    }*/
    public void createRAF(String filenameRAF, String filenamePTR, int count) throws FileNotFoundException, IOException {

        RandomAccessFile raf = new RandomAccessFile(filenameRAF, "rw");
        raf.seek(0);

        String characterPointerMappings = "";

        char currentStartChar = ' ';
        long currentOffset = 0;

        Collection<String> words = getVocab();
        TreeMap<String, String> wordsCutted = new TreeMap<>();

        for (String w : words) {
            wordsCutted.put(SPARQLQuery.formatDBpediaURI(w), w);
        }

        for (Map.Entry<String, String> entry : wordsCutted.entrySet()) {
            String currentEntity = entry.getKey();
            String currentEntityURI = entry.getValue();
            System.out.println("Writing entity " + currentEntity + " to Raf.");
            char start = currentEntity.charAt(0);

            currentOffset = raf.getFilePointer();

            if (start > currentStartChar || currentStartChar == ' ') {
                currentStartChar = start;
                characterPointerMappings += currentStartChar + "," + currentOffset + "\n";
            }

            String line2write = currentEntity + " " + currentEntityURI + " ";

            HashMap<String, Double> similars = getSimilarEntitiesWithValues(currentEntityURI, count);
            for (Map.Entry<String, Double> simEntry : similars.entrySet()) {
                line2write += simEntry.getKey() + "%%%" + simEntry.getValue() + "@";
            }

            line2write += " \n";

            //System.out.println(line2write);
            raf.writeUTF(line2write);

        }

        raf.writeUTF("#end");
        raf.close();

        CommonUtils.writeStringToFile(characterPointerMappings, filenamePTR);

    }

    public void filterVocab(Collection<String> toStartWith, Collection<String> toNotStartWith, Collection<String> toNotContain) {
        Collection<String> startingVocab = new ArrayList<>(getVocab());

        for (String s : startingVocab) {
            boolean rem = true;
            for (String c : toStartWith) {
                if (s.startsWith(c)) {
                    rem = false;
                    break;
                }
            }

            if (rem) {
                vec.vocab().removeElement(s);
                //System.out.println("1. Removing " + s);
            }
        }

        startingVocab = new ArrayList<>(getVocab());
        for (String s : startingVocab) {
            for (String c : toNotStartWith) {
                if (s.startsWith(c)) {
                    vec.vocab().removeElement(s);
                    //System.out.println("2. Removing " + s);
                    break;
                }
            }
        }

        startingVocab = new ArrayList<>(getVocab());
        for (String s : startingVocab) {
            for (String c : toNotContain) {
                if (s.contains(c)) {
                    vec.vocab().removeElement(s);
                    //System.out.println("3. Removing " + s);
                    break;
                }
            }
        }

    }

}
