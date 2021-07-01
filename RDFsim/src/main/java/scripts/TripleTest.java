/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.IOException;
import java.net.MalformedURLException;
import sparql.SPARQLTripleRetriever;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class TripleTest {

    public static void main(String[] args) throws MalformedURLException, MalformedURLException, IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String query = "";

        query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>}";
        //query = "select  * where {?s ?p ?o }";
        //query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(endpoint, query, true);
        
        System.out.println(triples);
        System.out.println("Total triples: " + tr.getTriples().size());
        
        String path = CommonUtils.writeStringToFile(triples, "triples.rdf");
    }
}
