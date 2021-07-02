/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.Word2VecEmbeddingCreator;
import java.io.IOException;
import java.util.Collection;
import sparql.SPARQLTripleRetriever;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class Tests {

    public static void getMaxTripleNumber() throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String totalTriples = "";
        String currTriples;
        int limit = 10000;
        int offset = 0;
        int i = 0;
        String query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>}  OFFSET " + limit + " LIMIT " + limit;
        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        while (!(currTriples = tr.getTriples(endpoint, query, true)).equals("")) {
            System.out.println("Offset: " + offset + " Limit: " + limit);
            offset += tr.getTriples().size();
            limit += 10000;
            currTriples += "\n";
            totalTriples += currTriples;
            if (limit >= 1000000) {
                break;
            }
        }

        CommonUtils.writeStringToFile(totalTriples, "triples.rdf");
    }

    public static void simpleExample() throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String query = "";

        query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>} limit 1000";
        //query = "select  * where {?s ?p ?o }";
        //query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
        //query = "?dataset dataid:group <https://databus.dbpedia.org/dbpedia/generic> .";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(endpoint, query, true);

        //System.out.println(triples);
        System.out.println("Total triples: " + tr.getTriples().size());

        String path = CommonUtils.writeStringToFile(triples, "triples.rdf");
    }

    public static void createEmbeddingsW2v() throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>} limit 1000 offset 0";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(endpoint, query, true);

        String path = CommonUtils.writeStringToFile(triples, "triples.rdf");

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();

        vects.saveVectorSpace("vectors.vec");

        Collection<String> similars = vects.getSimilarEntities("Damascius", 10);
        System.out.println(similars);
    }

    public static void main(String[] args) throws IOException {
        //simpleExample();
        createEmbeddingsW2v();
    }
}
