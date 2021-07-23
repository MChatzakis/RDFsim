package embeddings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class Word2VecEmbeddingCreator {

    Word2Vec vec;
    SentenceIterator iter;
    TokenizerFactory tokenizerFactory;

    int minWordFrequency;
    int layerSize;
    int seed;
    int windowSize;

    public Word2VecEmbeddingCreator(String path) {
        this.loadVectorFile(path);
    }

    public Word2VecEmbeddingCreator(int minWordFrequency, int layerSize, int seed, int windowSize, String filepath) {
        this.minWordFrequency = minWordFrequency;
        this.layerSize = layerSize;
        this.seed = seed;
        this.windowSize = windowSize;
        this.iter = new LineSentenceIterator(new File(filepath));;
        this.tokenizerFactory = new DefaultTokenizerFactory();
    }

    public void train() {
        vec = new Word2Vec.Builder()
                .minWordFrequency(minWordFrequency)
                .layerSize(layerSize)
                .seed(seed)
                .windowSize(windowSize)
                .iterate(iter)
                .tokenizerFactory(tokenizerFactory)
                .build();

        vec.fit();
    }

    public void saveVectorSpace(String filepath) {
        try {
            WordVectorSerializer.writeWord2VecModel(vec, filepath);
        } catch (Exception ex) {
            Logger.getLogger(Word2VecEmbeddingCreator.class.getName()).log(Level.SEVERE, null, ex);
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

    public void removeWordsFromVocab(Collection<String> words2remove) {
        for (String s : words2remove) {
            vec.vocab().removeElement(s);
        }
    }

    public void createRAF(String filenameRAF, String filenamePTR, Collection<String> words2remove, int count) throws FileNotFoundException, IOException {

        RandomAccessFile raf = new RandomAccessFile(filenameRAF, "rw");
        raf.seek(0);

        long currentOffset = 0;

        Collection<String> words = getVocab();
        TreeMap<String, String> wordsCutted = new TreeMap<>();

        for (String w : words) {
            wordsCutted.put(SPARQLQuery.formatDBpediaURI(w), w);
        }

        for (Map.Entry<String, String> entry : wordsCutted.entrySet()) {
            String currentEntity = entry.getKey();
            String currentEntityURI = entry.getValue();

            //System.out.println("Doing staff for entity: " + currentEntity);

            String line2write = currentEntity + " " + currentEntityURI + " ";

            HashMap<String, Double> similars = getSimilarEntitiesWithValues(currentEntityURI, count);
            for (Map.Entry<String, Double> simEntry : similars.entrySet()) {
                line2write += simEntry.getKey() + "_" + simEntry.getValue() + " ";
            }

            line2write += "\n";
            
            //System.out.println(line2write);
            
            raf.writeUTF(line2write);
            
            currentOffset = raf.getFilePointer();
            //raf.seek(currentOffset);

        }
        
        //raf.seek(0);
        //System.out.println(raf.readUTF());
        raf.close();
    }
}
