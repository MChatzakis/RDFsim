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

/**
 *
 * @author manos
 */
public class Measurements {

    public static void main(String[] args) throws IOException {
        indexingTests();
        graphTests();
        //askQueryTests();
    }

    public static double calculateIndexingTime(String dataset, String entity, int count, IndexingMode mode, CalculationUnit cu) throws IOException {
        RafApi raf = new RafApi(dataset);
        //raf.printVocabInfo();

        long start;
        long end;
        double elapsedTime = -1;

        switch (mode) {
        case POINTER:
            start = (cu == CalculationUnit.MS) ? System.currentTimeMillis() : System.nanoTime();
            raf.getSimilarEntitiesOfEntity(entity, count);
            end = (cu == CalculationUnit.MS) ? System.currentTimeMillis() : System.nanoTime();
            elapsedTime = end - start;
            break;
        case SEQUENTIAL:
            start = (cu == CalculationUnit.MS) ? System.currentTimeMillis() : System.nanoTime();
            raf.getSimilarEntitiesOfEntitySequential(entity, count);
            end = (cu == CalculationUnit.MS) ? System.currentTimeMillis() : System.nanoTime();
            elapsedTime = end - start;
            break;
        }

        if (cu == CalculationUnit.MS) {
            return elapsedTime * 1.0 / 1000.0;
        }

        return elapsedTime * 1.0 / 1000000000;
    }

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

    public static void indexingTests() throws IOException {
        DecimalFormat df = new DecimalFormat(".##");
        CalculationUnit cu = CalculationUnit.MS;

        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";

        String[] header = {"Entity", "Pointer Time", "Sequential Time", "Speedup"};
        String[] entitiesToTest = {"Inception", "stay_night_characters"};
        String[][] results = new String[entitiesToTest.length][4];

        int count = 30;

        for (int i = 0; i < entitiesToTest.length; i++) {
            String entity = entitiesToTest[i];

            double pointerTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.POINTER, cu);
            double sequentialTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.SEQUENTIAL, cu);
            double speedup = sequentialTime / pointerTime;

            System.out.println(entity + ": [" + pointerTime + "s," + sequentialTime + "s," + speedup + "]");

            results[i][0] = entity.replace("_", " ");
            results[i][1] = pointerTime + "s";
            results[i][2] = sequentialTime + "s";
            results[i][3] = Math.round(speedup) + "";
        }

        generateTeXTable(results, header, "C:\\tmp\\indexingTable.tex");
    }

    public static void generateTeXTable(String[][] data, String[] header, String filepath) {
        try {
            BufferedWriter texWriter = new BufferedWriter(new FileWriter(filepath));

            texWriter.write("\\begin{center}\n");
            texWriter.write("\\begin{tabular}");

            String cc = "{|| ";
            String tabHeader = "";
            for (int i = 0; i < header.length; i++) {

                if (i != header.length - 1) {
                    cc += "c ";
                    tabHeader += header[i] + " & ";
                } else {
                    cc += "c ||}\n";
                    tabHeader += header[i] + " \\\\ [0.5ex]\n";
                }

            }

            texWriter.write(cc);
            texWriter.write("\\hline\n");
            texWriter.write(tabHeader);
            texWriter.write("\\hline\\hline\n");

            for (int i = 0; i < data.length; i++) {
                String[] contents = data[i];
                for (int k = 0; k < contents.length; k++) {
                    if (k != contents.length - 1) {
                        texWriter.write(contents[k] + " & ");
                    } else {
                        texWriter.write(contents[k] + " \\\\ \n");
                    }
                }

                texWriter.write("\\hline\n");
            }

            texWriter.write("\\end{tabular}\n");
            texWriter.write("\\end{center}\n");

            texWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        generateTeXTable(results, header, "C:\\tmp\\percentageTable.tex");

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
            }else{
                //print something?
            }
        }

        return (existingLinksCount * 100.0) / count;
    }

}

enum IndexingMode {
    POINTER,
    SEQUENTIAL
}

enum CalculationUnit {
    MS,
    NS
}
