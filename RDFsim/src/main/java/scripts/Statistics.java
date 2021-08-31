/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.IOException;
import java.net.ProtocolException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import raf.RafApi;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
enum IndexingMode {
    POINTER,
    SEQUENTIAL
}

enum CalculationUnit {
    MS,
    NS
}

public class Statistics {

    public static final int SAMPLE_LIMIT = 100;

    public static String dbpediaEndpoint = "https://dbpedia.org/sparql";
    public static String philosophersFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_philosophers.txt";
    public static String programmingLangsFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_programming_langs.txt";
    public static String videoGamesFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_video_games.txt";
    public static String moviesFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";

    public static ArrayList<String> moviesSamples;
    public static ArrayList<String> philosophersSamples;
    public static ArrayList<String> videoGamesSamples;
    public static ArrayList<String> programmingLangsSamples;

    public static ArrayList<String> initArrayList(String query, String endpoint, String dataset) throws IOException {
        ArrayList<String> rawEntities = new SPARQLQuery().entityQuery(endpoint, query);
        ArrayList<String> entities = new ArrayList<>();
        RafApi raf = new RafApi(dataset);

        for (String rawEn : rawEntities) {
            if (raf.exists(rawEn)) {
                entities.add(rawEn);
                //entities.add(SPARQLQuery.formatDBpediaURI(rawEn));
            }

            if (entities.size() >= SAMPLE_LIMIT) {
                break;
            }
        }

        return entities;
    }

    public static void initSamples() throws IOException {
        moviesSamples = initArrayList("select ?s from <http://dbpedia.org> where {?s a <http://schema.org/Movie>} ORDER BY RAND() LIMIT 250", dbpediaEndpoint, moviesFilepath);
        System.out.println("Movies samples size: " + moviesSamples.size());

        philosophersSamples = initArrayList("select ?s from <http://dbpedia.org> where {?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>} ORDER BY RAND() LIMIT 250", dbpediaEndpoint, philosophersFilepath);
        System.out.println("Philosophers samples size: " + philosophersSamples.size());

        videoGamesSamples = initArrayList("select ?s from <http://dbpedia.org> where {?s a <http://dbpedia.org/ontology/VideoGame>} ORDER BY RAND() LIMIT 250", dbpediaEndpoint, videoGamesFilepath);
        System.out.println("Video Games samples size: " + videoGamesSamples.size());

        programmingLangsSamples = initArrayList("select ?s from <http://dbpedia.org> where {?s a <http://dbpedia.org/ontology/ProgrammingLanguage> } ORDER BY RAND() LIMIT 350", dbpediaEndpoint, programmingLangsFilepath);
        System.out.println("Programming Langs samples size: " + programmingLangsSamples.size());

    }

    public static void main(String[] args) throws ProtocolException, IOException {
        initSamples();
        //indexingTimeTests();
        embeddingStatisticsTests();

    }

    /* ========================== Indexing ========================== */
    public static void indexingTimeTests() throws IOException {
        CalculationUnit cu = CalculationUnit.MS;
        int count = 10;
        String[] header = {"Entity", "Pointer Time", "Sequential Time"};

        System.out.println("========================== Testing Movies Indexing Time ==========================");
        indexingTests(moviesFilepath, moviesSamples, header, count, cu, "C:\\tmp\\moviesIndexing");

        System.out.println("========================== Testing Philosophers Indexing Time ==========================");
        indexingTests(philosophersFilepath, philosophersSamples, header, count, cu, "C:\\tmp\\philosophersIndexing");

        System.out.println("========================== Testing Video Games Indexing Time ==========================");
        indexingTests(videoGamesFilepath, videoGamesSamples, header, count, cu, "C:\\tmp\\videoGamesIndexing");

        System.out.println("========================== Testing Programming Langs Indexing Time ==========================");
        indexingTests(programmingLangsFilepath, programmingLangsSamples, header, count, cu, "C:\\tmp\\programmingLangsIndexing");
    }

    public static void indexingTests(String rafTargetPath, ArrayList<String> entities2test, String[] header, int count, CalculationUnit cu, String fileName) throws IOException {
        DecimalFormat df = new DecimalFormat(".##");

        String[][] results = new String[entities2test.size()][3];

        for (int i = 0; i < entities2test.size(); i++) {
            String entity = entities2test.get(i);

            double pointerTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.POINTER, cu);
            double sequentialTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.SEQUENTIAL, cu);
            double speedup = sequentialTime / pointerTime;

            System.out.println(entity + ": [" + pointerTime + "s," + sequentialTime + "s," + speedup + "]");

            results[i][0] = entity;//entity.replace("_", " ");
            results[i][1] = pointerTime + "";
            results[i][2] = sequentialTime + "";
            //results[i][3] = Math.round(speedup) + "x";
        }

        //CommonUtils.generateTeXTable(results, header, fileName + ".tex");
        CommonUtils.generateCSV(results, header, fileName + ".txt");
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

    /* ========================== Embeddings ========================== */
    public static void embeddingStatisticsTests() throws IOException {
        System.out.println("========================== Testing Programming Languages Embeddings ==========================");
        embeddingTests(programmingLangsFilepath, programmingLangsSamples, 10, "C:\\tmp\\programmingLangsTOPK");

        System.out.println("========================== Testing Philosophers Embeddings ==========================");
        embeddingTests(philosophersFilepath, philosophersSamples, 10, "C:\\tmp\\philosophersTOPK");

        System.out.println("========================== Testing Video Games Embeddings ==========================");
        embeddingTests(videoGamesFilepath, videoGamesSamples, 10, "C:\\tmp\\videoGamesTOPK");

        System.out.println("========================== Testing Movies Embeddings ==========================");
        embeddingTests(moviesFilepath, moviesSamples, 10, "C:\\tmp\\moviesTOPK");
    }

    public static void embeddingTests(String rafFilePath, ArrayList<String> entities2test, int count, String filename) throws IOException {
        RafApi raf = new RafApi(rafFilePath);
        String data = "";
        for (String en : entities2test) {
            data += createEmbeddingData(en, raf, count);
        }

        CommonUtils.writeStringToFile(data, filename + ".txt");
    }

    public static String createEmbeddingData(String entity, RafApi raf, int count) throws IOException {
        SPARQLQuery sq = new SPARQLQuery();
        DecimalFormat df = new DecimalFormat(".##");
        String[] header = {entity, "hasLink", "similars", "score"};
        String[][] results = new String[count][header.length];
        int index = 0;
        HashMap<String, Double> similars = raf.getSimilarEntitiesOfEntity(entity, count);

        for (Map.Entry<String, Double> entry : similars.entrySet()) {

            String entityURI = raf.getEntityURI(entity);
            String simEntityURI = raf.getEntityURI(entry.getKey());

            String askQuery = "ASK FROM <http://dbpedia.org> WHERE { <" + entityURI + "> ?p <" + simEntityURI + "> }";
            String askQueryRR = "ASK FROM <http://dbpedia.org> WHERE { <" + simEntityURI + "> ?p <" + entityURI + "> }";

            String countQuery = "select count(*) as ?count from <http://dbpedia.org> where {?s ?p <" + entityURI + "> . ?s ?p <" + simEntityURI + ">}";
            String countQueryRR = "select count(*) as ?count from <http://dbpedia.org> where {?s ?p <" + simEntityURI + "> . ?s ?p <" + entityURI + ">}";

            results[index][0] = entry.getKey();
            results[index][1] = (sq.askQuery(dbpediaEndpoint, askQuery) || sq.askQuery(dbpediaEndpoint, askQueryRR)) ? "yes" : "no";
            results[index][2] = (sq.countQuery(dbpediaEndpoint, countQuery) + sq.countQuery(dbpediaEndpoint, countQueryRR)) + "";
            results[index][3] = df.format(entry.getValue()) + "";
            index++;
        }

        return CommonUtils.getCSVtext(results, header) + "\n";
    }
}
