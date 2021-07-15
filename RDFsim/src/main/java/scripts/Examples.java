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
 * Class to provide basic backend functionality examples and sample creation
 *
 * @author Manos Chatzakis
 */
public class Examples {

    public static void classicExample() throws IOException {
        String dbPediaEndpoint = "https://dbpedia.org/sparql";
        String ariadneEndpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";
        String dbPediaQuery = "select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
        String simplestQuery = "select * where {?s ?p ?o .}";
        String ariadneQuery = "select ?s ?p ?o ?p1 ?o1 where {?s a <http://www.cidoc-crm.org/cidoc-crm/E21_Person> . ?o ?p ?s . ?o ?p1 ?o1}";

        SPARQLQuery sq = new SPARQLQuery();
        //String vocab = sq.getData(dbPediaEndpoint, dbPediaQuery, 1000, 0);

        String path = sq.writeDataToFile(dbPediaEndpoint, dbPediaQuery, 25000, 0, "./data/triples/example.rdf", true);

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

    public static void loadPreSaved() {
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator("embeddings/VectorSample_Philosophers.vec");
        System.out.println(vects.getVocab());
    }

    public static void createDBpediaSample() throws IOException {
        String dbPediaEndpoint = "https://dbpedia.org/sparql";
        String dbPediaQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        SPARQLQuery sq = new SPARQLQuery();

        String path = sq.writeDataToFile(dbPediaEndpoint, dbPediaQuery, 10, 0, "./data/triples/TripleSample_Philosophers10.rdf", false);
        
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, "./data/triples/TripleSample_Philosophers10.rdf");
        vects.train();

        vects.saveVectorSpace("./data/embeddings/VectorSample_Philosophers10.vec");
    }

    public static void createAriadneSample() throws IOException {
        String ariadneEndpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";
        String ariadneQuery = "select ?s ?p ?o ?p1 ?o1 where {?s a <http://www.cidoc-crm.org/cidoc-crm/E21_Person> . ?o ?p ?s . ?o ?p1 ?o1}";

        SPARQLQuery sq = new SPARQLQuery();
        String path = sq.writeDataToFile(ariadneEndpoint, ariadneQuery, 1000, 0, "./data/triples/AriadneTripleSample_People1000.rdf", false);

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();

        vects.saveVectorSpace("./data/embeddings/AriadneVectorSample_People1000.vec");
    }

    public static void createBiggerSequences() throws IOException {
        String dbPediaEndpoint = "https://dbpedia.org/sparql";

        String dbPediaQuery = "select  ?s ?p ?o ?p1 ?o1 where {?s ?p ?o . ?o ?p1 ?o1 .  ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        SPARQLQuery sq = new SPARQLQuery();
        String path = sq.writeDataToFile(dbPediaEndpoint, dbPediaQuery, 10, 0, "triples/example.rdf", false);

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();
    }

    public static void createMostTriples() throws IOException {
        /*String dbPediaEndpoint = "https://dbpedia.org/sparql";
        String dbPediaQuery = "select  * where {?s ?p ?o .}";

        SPARQLQuery sq = new SPARQLQuery();
        String path = sq.writeDataToFile(dbPediaEndpoint, dbPediaQuery, 10000000, 2219983, "C:\\tmp\\rdfsim\\most.rdf", false);
        */
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(3, 100, 42, 5, "C:\\tmp\\rdfsim\\most.rdf");
        vects.train();

        vects.saveVectorSpace("C:\\tmp\\rdfsim\\embeddings\\most.vec");
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        //classicExample();
        //loadPreSaved();
        //createDBpediaSample();
        //createAriadneSample();
        //createBiggerSequences();
        createMostTriples();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + (double) timeElapsed / 1000.0 + " seconds");
    }

}
