package scripts;

import java.io.IOException;
import java.net.ProtocolException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import raf.RafApi;
import simgraph.SimilarityGraph;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 * This class automatically does the tests and running times of the algorithmic
 * features of RDFsim. To reproduce these tests as CSV files (able to be imported
 * to spreadsheet software) configure the selection variables and run.
 *
 * The test offered from this class are:
 *          a) Indexing running times, by comparing sequential and random file access
 *          b) Embedding accuracy, by checking the scores, link existence and common entities         
 *          c) Graph time creation with configurable depth and count parameters
 * 
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
public class Statistics {

    /* ========================== Data And Configuration ========================== */
    public static final int SAMPLE_LIMIT = 100;
    public static final TestTypes TEST_TYPE = TestTypes.EMBEDDINGS;
    public static final boolean USING_SELECTED_SAMPLES = false;

    public static String dbpediaEndpoint = "https://dbpedia.org/sparql";
    public static String philosophersFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_philosophers.txt";
    public static String programmingLangsFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_programming_langs.txt";
    public static String videoGamesFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_video_games.txt";
    public static String moviesFilepath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";

    public static String programmingClass = "http://dbpedia.org/ontology/ProgrammingLanguage";
    public static String philosophersClass = "http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers";
    public static String videoGamesClass = "http://dbpedia.org/ontology/VideoGame";
    public static String moviesClass = "http://schema.org/Movie";

    public static ArrayList<String> moviesSamples;
    public static ArrayList<String> philosophersSamples;
    public static ArrayList<String> videoGamesSamples;
    public static ArrayList<String> programmingLangsSamples;

    public static String[] selectedMoviesSamples = {"Avengers:_Endgame", "Spider-Man:_No_Way_Home", "Batman:_The_Dark_Knight_Returns_(film)"};
    public static String[] selectedPhilosophersSamples = {"Aristotle", "Plato", "Socrates", "Homer", "Pythagoras"};
    public static String[] selectedVideoGamesSamples = {"The_Last_of_Us", "Tetris", "Pac-Man", "Uncharted_4:_A_Thief's_End", "Shadow_of_the_Colossus_(2018_video_game)", "Journey_(2012_video_game)"};
    public static String[] selectedProgrammingLangsSamples = {"Java_(programming_language)", "JavaScript", "C++", "Python_(programming_language)", "Scala_(programming_language)"};

    /* ========================== Begin Testing! :)  ========================== */
    public static void main(String[] args) throws ProtocolException, IOException {
        initSamples();

        switch (TEST_TYPE) {
        case INDEXING:
            indexingTimeTests();
            break;
        case GRAPHS:
            graphCreationTimeTests();
            break;
        case EMBEDDINGS:
            embeddingStatisticsTests();
            break;
        case ALL:
            indexingTimeTests();
            graphCreationTimeTests();
            embeddingStatisticsTests();
            break;
        }
    }

    /* ========================== Sample Downloading ========================== */
    private static ArrayList<String> initArrayList(String query, String endpoint, String dataset) throws IOException {
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

    private static String queryCreator(String dbClass, int limit, int countLB) {
        String r1 = "select ?s count(?p) as ?count from <http://dbpedia.org> where {?s a <" + dbClass + "> . ?s ?p ?o} group by(?s) having(count(?p)>" + countLB + ")  order by rand() limit " + limit + "";
        String r2 = "select ?s from <http://dbpedia.org> where {?s a <" + dbClass + ">} ORDER BY RAND() LIMIT " + limit + "";
        return r1;
    }

    public static void initSamples() throws IOException {
        moviesSamples = initArrayList(queryCreator(moviesClass, 250, 500), dbpediaEndpoint, moviesFilepath);
        System.out.println("Movies samples size: " + moviesSamples.size());

        philosophersSamples = initArrayList(queryCreator(philosophersClass, 250, 500), dbpediaEndpoint, philosophersFilepath);
        System.out.println("Philosophers samples size: " + philosophersSamples.size());

        videoGamesSamples = initArrayList(queryCreator(videoGamesClass, 250, 500), dbpediaEndpoint, videoGamesFilepath);
        System.out.println("Video Games samples size: " + videoGamesSamples.size());

        programmingLangsSamples = initArrayList(queryCreator(programmingClass, 350, 500), dbpediaEndpoint, programmingLangsFilepath);
        System.out.println("Programming Langs samples size: " + programmingLangsSamples.size());
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

    private static void indexingTests(String rafTargetPath, ArrayList<String> entities2test, String[] header, int count, CalculationUnit cu, String fileName) throws IOException {
        DecimalFormat df = new DecimalFormat(".##");

        String[][] results = new String[entities2test.size()][3];

        for (int i = 0; i < entities2test.size(); i++) {
            String entity = entities2test.get(i);

            double pointerTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.POINTER, cu);
            double sequentialTime = calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.SEQUENTIAL, cu);
            double speedup = sequentialTime / pointerTime;

            System.out.println(entity + ": [" + pointerTime + "s," + sequentialTime + "s," + speedup + "]");

            results[i][0] = entity;
            results[i][1] = pointerTime + "";
            results[i][2] = sequentialTime + "";
            //results[i][3] = Math.round(speedup) + "x";
        }

        //CommonUtils.generateTeXTable(results, header, fileName + ".tex");
        CommonUtils.generateCSV(results, header, fileName + ".txt");
    }

    private static double calculateIndexingTime(String dataset, String entity, int count, IndexingMode mode, CalculationUnit cu) throws IOException {
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

        String dataCSV = "";

        System.out.println("========================== Testing Programming Languages Embeddings ==========================");
        dataCSV += "PROGRAMMING LANGUAGES\n" + embeddingTests(programmingLangsFilepath, (USING_SELECTED_SAMPLES) ? Arrays.asList(selectedProgrammingLangsSamples) : programmingLangsSamples, 10) + "\n\n";

        System.out.println("========================== Testing Philosophers Embeddings ==========================");
        dataCSV += "PHILOSOPHERS\n" + embeddingTests(philosophersFilepath, (USING_SELECTED_SAMPLES) ? Arrays.asList(selectedPhilosophersSamples) : philosophersSamples, 10) + "\n\n";

        System.out.println("========================== Testing Video Games Embeddings ==========================");
        dataCSV += "VIDEO GAMES\n" + embeddingTests(videoGamesFilepath, (USING_SELECTED_SAMPLES) ? Arrays.asList(selectedVideoGamesSamples) : videoGamesSamples, 10) + "\n\n";

        System.out.println("========================== Testing Movies Embeddings ==========================");
        dataCSV += "MOVIES\n" + embeddingTests(moviesFilepath, (USING_SELECTED_SAMPLES) ? Arrays.asList(selectedMoviesSamples) : moviesSamples, 10) + "\n\n";

        CommonUtils.writeStringToFile(dataCSV, "C:\\tmp\\embeddingMeasurements.txt");
    }

    public static String embeddingTests(String rafFilePath, Collection<String> entities2test, int count) throws IOException {
        RafApi raf = new RafApi(rafFilePath);
        String data = "";
        for (String en : entities2test) {
            data += createEmbeddingData(en, raf, count);
        }

        //CommonUtils.writeStringToFile(data, filename + ".txt");
        return data;
    }

    public static String createEmbeddingData(String entity, RafApi raf, int count) throws IOException {
        SPARQLQuery sq = new SPARQLQuery();
        DecimalFormat df = new DecimalFormat(".##");
        String[] header = {raf.getEntityURI(entity), "hasLink", "similars", "score"};
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

            results[index][0] = raf.getEntityURI(entry.getKey());//entry.getKey();
            results[index][1] = (sq.askQuery(dbpediaEndpoint, askQuery) || sq.askQuery(dbpediaEndpoint, askQueryRR)) ? "yes" : "no";
            results[index][2] = (sq.countQuery(dbpediaEndpoint, countQuery) + sq.countQuery(dbpediaEndpoint, countQueryRR)) + "";
            results[index][3] = df.format(entry.getValue()) + "";
            index++;
        }

        return CommonUtils.getCSVtext(results, header) + "\n";
    }

    /* ========================== Graphs ========================== */
    public static void graphCreationTimeTests() throws IOException {
        int[] count2test = {1, 5, 10, 15};
        int[] depth2test = {1, 2, 3, 4};

        int subListEndIndex = 10;

        CalculationUnit cu = CalculationUnit.MS;
        String dataCSV = "";

        System.out.println("========================== Testing Movies Graphs ==========================");
        dataCSV += "MOVIES GRAPHS\n" + graphCreationTime(moviesFilepath, count2test, depth2test, ((USING_SELECTED_SAMPLES) ? Arrays.asList(selectedMoviesSamples) : moviesSamples).subList(0, subListEndIndex), cu) + "\n\n";

        System.out.println("========================== Testing Philosophers Graphs ==========================");
        dataCSV += "PHILOSOPHERS GRAPHS\n" + graphCreationTime(philosophersFilepath, count2test, depth2test, ((USING_SELECTED_SAMPLES) ? Arrays.asList(selectedPhilosophersSamples) : philosophersSamples).subList(0, subListEndIndex), cu) + "\n\n";

        System.out.println("========================== Testing Video Games Graphs ==========================");
        dataCSV += "VIDEO GAMES GRAPHS\n" + graphCreationTime(videoGamesFilepath, count2test, depth2test, ((USING_SELECTED_SAMPLES) ? Arrays.asList(selectedVideoGamesSamples) : videoGamesSamples).subList(0, subListEndIndex), cu) + "\n\n";

        System.out.println("========================== Testing Programming Languages Graphs ==========================");
        dataCSV += "PROGRAMMING LANGS" + graphCreationTime(programmingLangsFilepath, count2test, depth2test, ((USING_SELECTED_SAMPLES) ? Arrays.asList(selectedProgrammingLangsSamples) : programmingLangsSamples).subList(0, subListEndIndex), cu) + "\n\n";

        CommonUtils.writeStringToFile(dataCSV, "C:\\tmp\\graphMeasurements.txt");
    }

    private static String graphCreationTime(String dataset, int[] count2test, int[] depth2test, Collection<String> entities2test, CalculationUnit cu) throws IOException {
        String dataCSV = "";

        for (String en : entities2test) {
            dataCSV += graphCreationTimeForEntity(dataset, en, count2test, depth2test, cu);
        }

        //CommonUtils.writeStringToFile(dataCSV, filename + ".txt");
        return dataCSV;
    }

    private static String graphCreationTimeForEntity(String dataset, String entity, int[] count2test, int[] depth2test, CalculationUnit cu) throws IOException {
        String[] header = new String[depth2test.length + 1];
        String[][] results = new String[count2test.length][header.length];
        DecimalFormat df = new DecimalFormat(".##");
        header[0] = entity;
        for (int i = 0; i < depth2test.length; i++) {
            header[i + 1] = depth2test[i] + "";
        }

        for (int i = 0; i < count2test.length; i++) {
            results[i][0] = count2test[i] + "";
        }

        for (int i = 0; i < count2test.length; i++) {

            for (int k = 0; k < depth2test.length; k++) {

                String elapsedTime = df.format(calculateGraphCreationTime(dataset, entity, count2test[i], depth2test[k], cu));
                System.out.println(entity + ": [C,D,T] = [" + count2test[i] + "," + depth2test[k] + "," + elapsedTime + "]");

                results[i][k + 1] = elapsedTime;
            }
        }

        return CommonUtils.getCSVtext(results, header) + "\n";
    }

    private static double calculateGraphCreationTime(String dataset, String entity, int count, int depth, CalculationUnit cu) throws IOException {
        RafApi raf = new RafApi(dataset);

        long start;
        long end;
        double elapsedTime;

        SimilarityGraph g = new SimilarityGraph(raf);
        String entityURI = raf.getEntityURI(entity);

        start = (cu == CalculationUnit.MS) ? System.currentTimeMillis() : System.nanoTime();
        g.createGraphRaf(entityURI, depth, count);
        end = (cu == CalculationUnit.MS) ? System.currentTimeMillis() : System.nanoTime();
        elapsedTime = end - start;

        if (cu == CalculationUnit.MS) {
            return elapsedTime * 1.0 / 1000.0;
        }

        return elapsedTime * 1.0 / 1000000000;
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

enum TestTypes {
    INDEXING,
    EMBEDDINGS,
    GRAPHS,
    ALL
}
