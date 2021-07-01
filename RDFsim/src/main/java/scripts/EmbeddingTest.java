/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.Word2VecEmbeddingCreator;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import sparql.SPARQLTripleRetriever;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class EmbeddingTest {

    public static void test() {
        SentenceIterator iter = new LineSentenceIterator(new File("triples.rdf"));
        TokenizerFactory t = new DefaultTokenizerFactory();
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        vec.fit();
    }

    public static void main(String[] args) throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>} limit 1000";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(endpoint, query, true);

        String path = CommonUtils.writeStringToFile(triples, "triples.rdf");

        SentenceIterator iter = new LineSentenceIterator(new File(path));
        TokenizerFactory t = new DefaultTokenizerFactory();

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, iter, t);
        vects.train();
        
        vects.saveVectorSpace("vectors.vec");
        
        Collection<String>similars = vects.getSimilarEntities("Damascius", 10);
        System.out.println(similars);
    
        
    }
}
