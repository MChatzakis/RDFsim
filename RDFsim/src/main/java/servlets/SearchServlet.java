package servlets;

import embeddings.W2VApi;
import java.io.File;
import java.io.FileNotFoundException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import simgraph.SimilarityGraph;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author Manos Chatzakis
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/SearchServet"})
public class SearchServlet extends HttpServlet {

    W2VApi vec = null;
    Collection<String> availableVocabulary = null;

    String[] samples = {"philosophers", "programming_langs", "game_consoles", "movies"};

    String currentEntity = "";
    String endpoint = "https://dbpedia.org/sparql";
    String currentInformationService = "wikipedia";
    String currentPrefix = "http://dbpedia.org/resource/";

    int similarsNum = 10;
    int graphDepth = 1;
    boolean gatherTriples = false;

    public SearchServlet() {
        super();
        try {
            String sample2load = samples[0];
            initDBpediaSample(sample2load);
            printInfo();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initDBpediaSample(String sampleName) throws FileNotFoundException {
        String linuxPath = "/tmp/rdfsim/embeddings/" + sampleName + ".vec";
        String windowsPath = "C:\\tmp\\rdfsim\\embeddings\\" + sampleName + ".vec";

        File lin = new File(linuxPath);
        File win = new File(windowsPath);

        if (lin.exists()) {
            //server side version
            vec = new W2VApi(linuxPath);
        } else if (win.exists()) {
            //windows local version
            vec = new W2VApi(windowsPath);
        } else {
            throw new FileNotFoundException("Could not locate the vector file in current file system");
        }

        String[] w2remove = {"."};
        vec.removeWordsFromVocab(Arrays.asList(w2remove));
        availableVocabulary = vec.getVocab();
    }

    private void printInfo() {

        System.out.println("Available words to search:");
        for (String s : availableVocabulary) {
            System.out.println(s);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String entity = request.getParameter("entity");
        String count = request.getParameter("count");
        String depth = request.getParameter("depth");
        String infoService = request.getParameter("info-service");

        if (entity != null) {

            if (!entity.startsWith("http")) {
                entity = currentPrefix + entity;
            }

            if (vec.getVec().hasWord(entity)) {
                currentEntity = entity;
            } else {
                String closestEntity = CommonUtils.levenshteinDistance(availableVocabulary, entity);
                System.out.println("Close: " + closestEntity);
                currentEntity = closestEntity;
            }

            System.out.println("Entity Set: " + currentEntity);
        }

        if (count != null) {
            similarsNum = Integer.parseInt(count);
            System.out.println("Count set: " + similarsNum);
        }

        if (depth != null) {
            graphDepth = Integer.parseInt(depth);
            System.out.println("Depth set: " + graphDepth);
        }

        if (infoService != null) {
            if (infoService.equals("wikipedia")) {
                currentInformationService = "wikipedia";
                System.out.println("Wikipedia service selected.");
            } else if (infoService.equals("dbpedia")) {
                currentInformationService = "dbpedia";
                System.out.println("DBpedia service selected.");
            } else if (infoService.equals("triples")) {
                JSONArray jtriples = SPARQLQuery.getTriplesOfURI(currentEntity, endpoint);
                currentInformationService = jtriples.toString();
                System.out.println("Triple Services Selected");
            }
        }

        SimilarityGraph simg = new SimilarityGraph(graphDepth, similarsNum, vec, currentEntity);
        simg.createGraphW2V();

        JSONObject graph2sent = simg.toJSON();

        request.setAttribute("graph", graph2sent.toString());
        request.setAttribute("self", currentEntity);
        request.setAttribute("count", similarsNum);
        request.setAttribute("depth", graphDepth);
        request.setAttribute("info-service", currentInformationService);

        System.out.println("Server connection attribute--graph: " + graph2sent.toString(2));
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/search.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int type = Integer.parseInt(request.getParameter("type"));

        PrintWriter out = response.getWriter();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int count = 0;
        JSONObject data2sent = null;

        switch (type) {
        /*0 stands for TOP_K similars*/
        case 0:
            String entity = request.getParameter("entity");
            count = Integer.parseInt(request.getParameter("count"));
            data2sent = getSimilarEntities(entity, count);
            break;
        /*0 stands for COS_SIM*/
        case 1:
            String en1 = request.getParameter("en1");
            String en2 = request.getParameter("en2");
            data2sent = getCosineSimilarity(en1, en2);
            break;
        /*2 stands for w2v EXPR*/
        case 2:
            String positives = request.getParameter("positives");
            String negatives = request.getParameter("negatives");
            count = Integer.parseInt(request.getParameter("count"));
            data2sent = getExpressionEntities(positives, negatives, count);
            break;
        }

        System.out.println("Server->Sending: " + data2sent.toString(2));

        out.print(data2sent);
        out.flush();
    }

    public JSONObject getSimilarEntities(String entity, int count) {
        JSONObject data = null;
        HashMap<String, Double> similarsOfEntity = vec.getSimilarEntitiesWithValues(entity, count);
        data = CommonUtils.entityMapToJSON(similarsOfEntity);
        return data;
    }

    public JSONObject getCosineSimilarity(String en1, String en2) {
        JSONObject data = new JSONObject();
        double sim = vec.calculateCosineSimilarity(en1, en2);
        data.put("cosSim", sim);
        return data;
    }

    public JSONObject getExpressionEntities(String positives, String negatives, int count) {
        JSONObject data = new JSONObject();
        String[] posEnts = positives.split(",");
        String[] negEnts = negatives.split(",");

        Collection<String> entities2add = Arrays.asList(posEnts);
        Collection<String> entities2sub = Arrays.asList(negEnts);

        Collection<String> result = vec.getExpressionResult(entities2add, entities2sub, count);

        String resultAsString = "";
        for (String s : result) {
            resultAsString += s + "   ";
        }

        data.put("expr_result", resultAsString);
        return data;
    }

}
