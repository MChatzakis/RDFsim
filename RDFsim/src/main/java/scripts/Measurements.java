/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ProtocolException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import raf.RafApi;
import simgraph.SimilarityGraph;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class Measurements {

    public static void calculateGraphTime(String dataset, String entity, int count, int depth) throws IOException {
        RafApi raf = new RafApi(dataset);
        SimilarityGraph g = new SimilarityGraph(raf);

        long start;
        long end;
        double elapsedTime;

        start = System.currentTimeMillis();
        g.createGraphRaf(entity, depth, count);
        end = System.currentTimeMillis();
        elapsedTime = end - start;

        System.out.println("Graph creation - Time Passed: " + elapsedTime / 1000.0 + " seconds");
    }

    public static void graphTests() throws IOException {
        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";
        String entity = "Inception";
        int count = 20;
        int depth = 20;
        calculateGraphTime(rafTargetPath, entity, count, depth);

    }

    public static void askQueryTests() throws ProtocolException, IOException {

        String dataset = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";

        String[] entities2test = {"Batman", "Riddler"};
        String[] header = {"Entity", "Percentage"};
        String[][] results = new String[entities2test.length][header.length];

        int count = 20;

        for (int i = 0; i < entities2test.length; i++) {
            String en = entities2test[i];
            double perc = calculateTripleLinkPerc(en, count, dataset);
            results[i][0] = en;
            results[i][1] = perc + "%";
        }

        CommonUtils.generateTeXTable(results, header, "C:\\tmp\\percentageTable.tex");

        //System.out.println(new SPARQLQuery().countQuery("https://dbpedia.org/sparql", "select count(*) as ?count from <http://dbpedia.org> where {?s ?p dbr:Plato . ?s ?p dbr:Sextus_Empiricus}"));
    }

    public static double calculateTripleLinkPerc(String entity, int count, String dataset) throws IOException {
        RafApi raf = new RafApi(dataset);
        HashMap<String, Double> sims = raf.getSimilarEntitiesOfEntity(entity, count);
        int existingLinksCount = 0;

        for (Map.Entry<String, Double> entry : sims.entrySet()) {

            String entityURI = raf.getEntityURI(entity);
            String simEntityURI = raf.getEntityURI(entry.getKey());

            String askQuery = "ASK FROM <http://dbpedia.org> WHERE { <" + entityURI + "> ?p <" + simEntityURI + "> }";
            String askQueryRR = "ASK FROM <http://dbpedia.org> WHERE { <" + simEntityURI + "> ?p <" + entityURI + "> }";

            
            SPARQLQuery sq = new SPARQLQuery();
            if (sq.askQuery("https://dbpedia.org/sparql", askQuery) || sq.askQuery("https://dbpedia.org/sparql", askQueryRR)) {
                existingLinksCount++;
            } else {
                //print something?
            }
        }

        return (existingLinksCount * 100.0) / count;
    }
}
