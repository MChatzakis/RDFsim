/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        ArrayList<Triple>triples = sq.getTriples(SPARQLendpoint, baseQuery, false, 0, 15000, "s", "p", "o");
        
        String vocab = Triple.produceTripleVocabulary(triples);
        //System.out.println(vocab);
        
        HashMap<String, Entity> entityMap = CommonUtils.harvestEntitiesFromTriples(triples);
        
    }

    public static void main(String[] args) throws IOException {
        basicTest();
    }
}
