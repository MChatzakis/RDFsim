/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.IOException;
import sparql.SPARQLQuery;

/**
 *
 * @author manos
 */
public class VocabCreator {

    public static String dbPediaEndpoint = "https://dbpedia.org/sparql";
    public static String ariadneEndpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";
    public static String simplestQuery = "select * where {?s ?p ?o .}";
    public static String ariadnePeopleQuery = "select ?s ?p ?o ?p1 ?o1 where {?s a <http://www.cidoc-crm.org/cidoc-crm/E21_Person> . ?o ?p ?s . ?o ?p1 ?o1}";
    public static String dbPediaPhilosophers = "select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
    public static String dbPediaQueryBiggerSeqs = "select  ?s ?p ?o ?p1 ?o1 where {?s ?p ?o . ?o ?p1 ?o1 .  ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
    public static String dbPediaProgrammingLanguages = "select ?s ?p ?o where {?s ?p ?o . ?s a <http://dbpedia.org/ontology/ProgrammingLanguage> . filter(isURI(?o)) }";
    public static String dbPediaMovies = "select ?s ?p ?o from <http://dbpedia.org> where { ?s ?p ?o . ?s a <http://schema.org/Movie> . filter(isURI(?o))} ";
    public static String dbPediaGameConsoles = "select ?s ?p ?o where { ?s ?p ?o. ?s a <http://dbpedia.org/class/yago/WikicatVideoGameConsoles> . filter(isURI(?o))} ";
    public static String dbPediaGetAll = "select ?s ?p ?o from <http://dbpedia.org> where  { ?s ?p ?o . filter(isURI(?o))} ";
    public static String dbPediaVideoGames = "select * from <http://dbpedia.org> where { ?s ?p ?o . ?s a <http://dbpedia.org/ontology/VideoGame> . filter(isURI(?o))}";

    public static void createVocab(String rdfFilePath, String vecFilePath, String endpoint, String query, int total, int start, boolean formatURI) throws IOException {
        SPARQLQuery sq = new SPARQLQuery();
        sq.writeDataToFile(endpoint, query, total, start, rdfFilePath, formatURI);
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        String datasetName = "video_games";
        String vectorFilePath = "C:\\tmp\\rdfsim\\embeddings\\" + datasetName + ".vec";
        String rdfFilePath = "C:\\tmp\\rdfsim\\" + datasetName + ".rdf";

        createVocab(rdfFilePath, vectorFilePath, dbPediaEndpoint, dbPediaVideoGames, 5000000, 0, false);

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + (double) timeElapsed / 1000.0 + " seconds");
    }

}
