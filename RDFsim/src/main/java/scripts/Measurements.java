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

    public static void main(String[] args) throws IOException {
        //indexingTests();
        //graphTests();
        askQueryTests();
    }

    public static void indexingTests() throws IOException {
        CalculationUnit cu = CalculationUnit.MS;
        int count = 10;
        String[] header = {"Entity", "Pointer Time", "Sequential Time", "Speedup"};

        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";
        String texFileName = "C:\\tmp\\moviesIndexingTable.tex";
        String[] entities2testMovies = {"Avengers:_Infinity_War", "Captain_America:_The_First_Avenger", "Guardians_of_the_Galaxy_(film)", "Spider-Man:_Far_From_Home", "Thor:_Ragnarok"};
        //indexingTests(rafTargetPath, entities2testMovies, header, count, cu, texFileName);

        rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_video_games.txt";
        texFileName = "C:\\tmp\\videoGamesIndexingTable.tex";
        String[] entities2testVideoGames = {"Batman:_Arkham_Asylum", "Gotham_Knights_(video_game)", "Minecraft", "Pac-Man", "Tetris", "Winter_Olympics_(video_game)"};
        //indexingTests(rafTargetPath, entities2testVideoGames, header, count, cu, texFileName);

        rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_programming_langs.txt";
        texFileName = "C:\\tmp\\programmingIndexingTable.tex";
        String[] entities2testProgramming = {"Apache_Maven", "C++", "Java", "Python_(language)", "Z++"};
        //indexingTests(rafTargetPath, entities2testProgramming, header, count, cu, texFileName);

        rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_philosophers.txt";
        texFileName = "C:\\tmp\\philosophersIndexingTable.tex";
        String[] entities2testPhilosophers = {"Aristotle", "Plato", "Socrates", "Zeno_Of_Tarsus"};
        indexingTests(rafTargetPath, entities2testPhilosophers, header, count, cu, texFileName);
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

    public static void indexingTests(String rafTargetPath, String[] entities2test, String[] header, int count, CalculationUnit cu, String texFileName) throws IOException {
        DecimalFormat df = new DecimalFormat(".##");

        String[][] results = new String[entities2test.length][4];

        for (int i = 0; i < entities2test.length; i++) {
            String entity = entities2test[i];

            double pointerTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.POINTER, cu);
            double sequentialTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.SEQUENTIAL, cu);
            double speedup = sequentialTime / pointerTime;

            System.out.println(entity + ": [" + pointerTime + "s," + sequentialTime + "s," + speedup + "]");

            results[i][0] = entity.replace("_", " ");
            results[i][1] = pointerTime + "s";
            results[i][2] = sequentialTime + "s";
            results[i][3] = Math.round(speedup) + "x";
        }

        CommonUtils.generateTeXTable(results, header, texFileName);
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

enum IndexingMode {
    POINTER,
    SEQUENTIAL
}

enum CalculationUnit {
    MS,
    NS
}
