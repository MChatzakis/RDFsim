/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.impl.StaticLoggerBinder;

/**
 *
 * @author Manos Chatzakis
 */
@Data
public class Word2VecEmbeddingCreator {

    Word2Vec vec; //Vector space
    SentenceIterator iter;
    TokenizerFactory tokenizerFactory;

    HashMap<String, ArrayList<String>> topEntities;

    int minWordFrequency;
    int layerSize;
    int seed;
    int windowSize;

    public Word2VecEmbeddingCreator(String path){
        this.loadVectorFile(path);
    }
    
    public Word2VecEmbeddingCreator(int minWordFrequency, int layerSize, int seed, int windowSize,
            SentenceIterator iter, TokenizerFactory tokenizerFactory) {
        this.minWordFrequency = minWordFrequency;
        this.layerSize = layerSize;
        this.seed = seed;
        this.windowSize = windowSize;
        this.iter = iter;
        this.tokenizerFactory = tokenizerFactory;

        topEntities = new HashMap<String, ArrayList<String>>();
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
    }

}
