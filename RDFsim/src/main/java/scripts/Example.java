/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.Word2VecEmbeddingCreator;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author Manos Chatzakis
 */
public class Example {

    public static void classicExample() throws IOException {
        String dbPediaEndpoint = "https://dbpedia.org/sparql";
        String ariadneEndpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";

        String dbPediaQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
        String ariadneQuery = "select  * where {?s ?p ?o .}";

        SPARQLQuery sq = new SPARQLQuery();
        String vocab = sq.getData(dbPediaEndpoint, dbPediaQuery, 20000, 0);

        String path = CommonUtils.writeStringToFile(vocab, "triples/example.rdf");

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();
        
        String entity = "http://dbpedia.org/resource/Cassius_Longinus_(philosopher)";
        Collection<String> similars = vects.getSimilarEntities(entity, 5);
        System.out.println("Similars of " + entity + " " + similars);

        double sim = vects.calculateCosineSimilarity("http://dbpedia.org/resource/Damo_(philosopher)", "http://dbpedia.org/resource/Onasander");
        System.out.println("Similarity (cosine): " + sim);

        HashMap<String, Double> topEntitiesOfPhilosopher = vects.getSimilarEntitiesWithValues("http://dbpedia.org/resource/Democritus", 5);
        CommonUtils.printEntityMap(topEntitiesOfPhilosopher);

        sim = vects.calculateCosineSimilarity("http://dbpedia.org/resource/Democritus", "http://dbpedia.org/resource/Gorgias");
        System.out.println("Similarity (cosine): " + sim);
    }

    public static void loadPreSaved(){
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator("embeddings/VectorSample_Philosophers.vec");
        System.out.println(vects.getVocab());
    }
    
    public static void main(String[] args) throws IOException {
        classicExample();
        //loadPreSaved();
    }
}