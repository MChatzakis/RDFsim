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

    public static String dbPediaEndpoint = "https://dbpedia.org/sparql";
    public static String ariadneEndpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";
    public static String simplestQuery = "select * where {?s ?p ?o .}";
    
    public static String ariadneQuery = "select ?s ?p ?o ?p1 ?o1 where {?s a <http://www.cidoc-crm.org/cidoc-crm/E21_Person> . ?o ?p ?s . ?o ?p1 ?o1}";
    public static String dbPediaPhilosophersQuery = "select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
    public static String dbPediaQueryBiggerSeqs = "select  ?s ?p ?o ?p1 ?o1 where {?s ?p ?o . ?o ?p1 ?o1 .  ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

    public static void completeExample(String rdfFilePath, String vecFilePath, String endpoint, String query, int total, int start) throws IOException {
        SPARQLQuery sq = new SPARQLQuery();
        String path = sq.writeDataToFile(endpoint, query, total, start, rdfFilePath, false);

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();
        vects.saveVectorSpace(vecFilePath);

        /*String entity = "http://dbpedia.org/resource/Cassius_Longinus_(philosopher)";
        Collection<String> similars = vects.getSimilarEntities(entity, 5);
        System.out.println("Similars of " + entity + " " + similars);
        double sim = vects.calculateCosineSimilarity("http://dbpedia.org/resource/Damo_(philosopher)", "http://dbpedia.org/resource/Onasander");
        System.out.println("Similarity (cosine): " + sim);
        HashMap<String, Double> topEntitiesOfPhilosopher = vects.getSimilarEntitiesWithValues("http://dbpedia.org/resource/Democritus", 5);
        CommonUtils.printEntityMap(topEntitiesOfPhilosopher);
        sim = vects.calculateCosineSimilarity("http://dbpedia.org/resource/Democritus", "http://dbpedia.org/resource/Gorgias");
        System.out.println("Similarity (cosine): " + sim);*/
    }

    public static void loadPreSaved(String filepath) {
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(filepath);
        Collection<String> strs = vects.getVocab();
        for (String s : strs) {
            System.out.println(s);
        }
        System.out.println("Total words: " + strs.size());
    }

    public static void trainOnly(String filepath, String output) {
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(4, 100, 42, 5, filepath);
        vects.train();
        vects.saveVectorSpace(output);
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        //trainOnly("C:\\tmp\\rdfsim\\crash.rdf", "C:\\tmp\\rdfsim\\embeddings\\c.vec");
        //loadPreSaved("C:\\tmp\\rdfsim\\embeddings\\c.vec")

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + (double) timeElapsed / 1000.0 + " seconds");
    }

}
