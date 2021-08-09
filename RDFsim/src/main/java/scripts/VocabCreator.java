/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import sparql.SPARQLQuery;
import utils.CommonUtils;

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

    public static void createVocab(String rdfFilePath, String endpoint, String query, int total, int start, boolean formatURI) throws IOException {
        SPARQLQuery sq = new SPARQLQuery();
        sq.writeDataToFile(endpoint, query, total, start, rdfFilePath, formatURI);
    }

    public static void main(String[] args) throws IOException, Exception {
        long start = System.currentTimeMillis();

        String datasetName = "all_triples";
        String rdfFilePath = "C:\\tmp\\rdfsim\\" + datasetName;

        //createVocabOnSingleFile(rdfFilePath, dbPediaEndpoint, dbPediaGetAll, 3, 0, false);
        createVocabOnFilePartitions(rdfFilePath, dbPediaEndpoint, dbPediaGetAll, 100000000, 59, 1000000, false);
        //createFileFromParitions("C:\\tmp\\rdfsim\\all_triples\\", "C:\\tmp\\rdfsim\\all_triples_0_38.rdf");

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + (double) timeElapsed / 1000.0 + " seconds");
    }

    public static void createVocabOnSingleFile(String rdfFilePath, String endpoint, String query, int total, int start, boolean formatURI) throws IOException {
        createVocab(rdfFilePath + ".rdf", endpoint, query, total, start, formatURI);
    }

    public static void createVocabOnFilePartitions(String rdfFilePath, String endpoint, String query, int total, int startFileOffset, int step, boolean formatURIs) throws IOException {
        int count = 1 * startFileOffset;
        int totalTriples = count * step;

        while (totalTriples < total) {
            String currentFileName = rdfFilePath + "" + count + ".rdf";
            System.out.println("Current File: " + currentFileName);

            createVocab(currentFileName, endpoint, query, step, totalTriples, formatURIs);

            totalTriples += step;
            count++;
        }
    }

    public static void createFileFromParitions(String directory, String target) throws Exception {
        File directoryPath = new File(directory);
        String contents[] = directoryPath.list();

        ArrayList<String> vocabs = new ArrayList<>();
        for (String f : contents) {
            if (f.endsWith(".rdf")) {
                vocabs.add(f);
            }
        }

        CommonUtils.mergeFilesToFile(directory, vocabs, target);
    }

}
