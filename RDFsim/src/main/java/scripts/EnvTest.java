/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.Word2VecEmbeddingCreator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import rdf.Entity;
import rdf.Triple;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class EnvTest {

    public static void basicTest() throws IOException {
        String SPARQLendpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01l";
        String baseQuery = "select  * where {?s ?p ?o . }";

        SPARQLQuery sq = new SPARQLQuery();
        ArrayList<Triple> triples = sq.getTriples(SPARQLendpoint, baseQuery, false, 0, 10, "s", "p", "o");

        for (Triple t : triples) {
            System.out.println(t.toString());
        }
        /*
        String vocab = Triple.produceTripleVocabulary(triples);
        String path = CommonUtils.writeStringToFile(vocab, "triples/vocab.rdf");
        
        HashMap<String, Entity> entityMap = CommonUtils.harvestEntitiesFromTriples(triples);
        /*for (Map.Entry<String, Entity> set : entityMap.entrySet()) {

            System.out.println(set.getValue());
        }
        
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();
        vects.saveVectorSpace("embeddings/vectors.vec");*/

    }

    public static void createSamples() throws IOException {
        String defTriplesFilePath = "C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\triples\\TripleSample_Philosophers.rdf";
        String defEntitiesFilePath = "C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\entities\\EntitySample_Philosophers.rdf";
        String defVectorFilePath = "C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\embeddings\\VectorSample_Philosophers.rdf";

        String SPARQLendpoint = "https://dbpedia.org/sparql";
        String baseQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        System.out.println("Started creating samples...");

        SPARQLQuery sq = new SPARQLQuery();
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
        //basicTest();
        createSamples();
    }
}
