/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Data;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author manos
 */
@Data
public class EmbeddingCreator_w2v {

    private Word2Vec vec;

    public void createEmbeddings(String textFile) {
        // Split on white spaces in the line to get words
        SentenceIterator iter = new LineSentenceIterator(new File(textFile));

        //Tokenize inputSS
        TokenizerFactory t = new DefaultTokenizerFactory();
        //t.setTokenPreProcessor(new CommonPreprocessor());

        System.out.println("Starting building the model...");
        vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        System.out.println("Fitting Word2Vec model....");
        vec.fit();
    }
    
    

}
