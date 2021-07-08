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
        String SPARQLendpoint = "https://dbpedia.org/sparql";
        String baseQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        SPARQLQuery sq = new SPARQLQuery();
        ArrayList<Triple> triples = sq.getTriples(SPARQLendpoint, baseQuery, false, 0, 10000, "s", "p", "o");

        /*for (Triple t : triples) {
            System.out.println(t.toString());
        }*/

        String vocab = Triple.produceTripleVocabulary(triples);
        String path = CommonUtils.writeStringToFile(vocab, "triples/vocab.rdf");
        
        HashMap<String, Entity> entityMap = CommonUtils.harvestEntitiesFromTriples(triples);
        /*for (Map.Entry<String, Entity> set : entityMap.entrySet()) {

            System.out.println(set.getValue());
        }*/
        
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();
        vects.saveVectorSpace("embeddings/vectors.vec");
        
    }

    public static void main(String[] args) throws IOException {
        basicTest();
    }
}
