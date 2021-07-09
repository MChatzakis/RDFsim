/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.Word2VecEmbeddingCreator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import rdf.Entity;
import rdf.Triple;
import sparql.SPARQLVirtuosoClient;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class EnvTest {

    public static void exampleDBpedia() throws IOException {
        /*STEP A: Select a SPARQL endpoint*/
        String SPARQLendpoint = "https://dbpedia.org/sparql";

        /*
        STEP B: Create a model base query (Base queries: Queries without offset and limit.
        Note: Use simple "getTriple" method for complete queries with limits and offsets. 
        Example: Select All triples of philosophers, excluding literals.
         */
        String baseQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        /*STEP C: Retrieve the data, selecting the first $(LIMIT) triples of philosophers. Note that triple formatting is tested for DBpedia*/
        SPARQLVirtuosoClient sq = new SPARQLVirtuosoClient();
        ArrayList<Triple> triples = sq.getTriples(SPARQLendpoint, baseQuery, true, 0, 30000, "s", "p", "o");

        /*STEP D: Save the data to a file and get the absoulte file path*/
        String path = CommonUtils.writeStringToFile(Triple.produceTripleVocabulary(triples), "triples/exampleTriples.rdf");

        /*STEP E: Use the saved file as a vocabulary for word2vec after instanciation, and train the model*/
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();

        /*STEP F: Save the produce embeddings for future use*/
        vects.saveVectorSpace("embeddings/vectors.vec");

        /*STEP G: Apply operations using the vector space of embeddings*/
        //Find the 5 most similar entities of Damascius
        String entity = "Damascius";
        Collection<String> similars = vects.getSimilarEntities(entity, 5);
        System.out.println("Similars of " + entity + " " + similars);

        //Find the similarity number of two Philosopher
        double sim = vects.calculateCosineSimilarity("Philip_of_Opus", "Proklos");
        System.out.println("Similarity (cosine): " + sim);

        HashMap<String, Double> topEntitiesOfPhilosopher = vects.getSimilarEntitiesWithValues("Aristotle", 20);
        CommonUtils.printEntityMap(topEntitiesOfPhilosopher);

        sim = vects.calculateCosineSimilarity("Socrates", "Proklos");
        System.out.println("Similarity (cosine): " + sim);

        /*NOTE: The vector space could be reloaded using the following line*/
        //Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator("vectors.vec");
    }

    public static void createSamples() throws IOException {
        String defTriplesFilePath = "C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\triples\\TripleSample_Philosophers.rdf";
        String defEntitiesFilePath = "C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\entities\\EntitySample_Philosophers.rdf";
        String defVectorFilePath = "C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\embeddings\\VectorSample_Philosophers.rdf";

        String SPARQLendpoint = "https://dbpedia.org/sparql";
        String baseQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        System.out.println("Started creating samples...");

        SPARQLVirtuosoClient sq = new SPARQLVirtuosoClient();
        ArrayList<Triple> triples = sq.getTriples(SPARQLendpoint, baseQuery, false, 0, 30000, "s", "p", "o");
        HashMap<String, Entity> entities = CommonUtils.harvestEntitiesFromTriples(triples);

        System.out.println("Data retrieved, started writing to files...");

        String res = "";

        for (Triple t : triples) {
            res += t.toString() + "\n";
        }
        CommonUtils.writeStringToFile(res, defTriplesFilePath);
        System.out.println("Triples saved...");

        res = "";
        for (Map.Entry<String, Entity> set : entities.entrySet()) {
            res += set.getValue().toString() + "\n";
        }
        CommonUtils.writeStringToFile(res, defEntitiesFilePath);
        System.out.println("Entities saved...");

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, defTriplesFilePath);
        vects.train();
        vects.saveVectorSpace(defVectorFilePath);

        System.out.println("Vectors saved...");
        System.out.println("Sample data saved on default paths...");

    }

    public static void main(String[] args) throws IOException {
        exampleDBpedia();
        //createSamples();
    }
}
