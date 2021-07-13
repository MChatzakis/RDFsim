package embeddings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import utils.CommonUtils;

/**
 * Wrapper class of w2v to provide basic comparison methods
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
@Data
public class Word2VecEmbeddingCreator {

    Word2Vec vec; //Vector space
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

        //topEntities = new HashMap<String, ArrayList<String>>();
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
            WordVectorSerializer.writeWordVectors(vec, filepath);
        } catch (IOException ex) {
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
}
