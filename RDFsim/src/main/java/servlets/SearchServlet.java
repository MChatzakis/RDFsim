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
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import raf.RafApi;
import simgraph.SimilarityGraph;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author Manos Chatzakis
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/SearchServet"})
public class SearchServlet extends HttpServlet {

    String currentPrefix = "http://dbpedia.org/resource/";
    String endpoint = "https://dbpedia.org/sparql";
    String currentInformationService = "wikipedia";
    String currentEntity = "";

    int similarsNum = 10;
    int graphDepth = 1;

    final int DEFAULT_SIMILARS = 10;
    final int DEFAULT_DEPTH = 1;

    RafApi raf = null;
    SimilarityGraph simg = null;

    public SearchServlet() {
        super();

        try {
            initRaf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRaf() throws FileNotFoundException, IOException {
        String[] samples = {"philosophers", "movies", "programming_langs", "game_consoles"};
        String name = samples[0];

        String linuxPath = "/var/lib/tomcat9/work/rdfsim/rafs/" + name + ".txt";
        String windowsPath = "C:\\tmp\\rdfsim\\rafs\\" + name + ".txt";

        File lin = new File(linuxPath);
        File win = new File(windowsPath);

        if (lin.exists()) {
            raf = new RafApi(linuxPath, linuxPath.replace(".txt", "PTR.txt"));
        } else if (win.exists()) {
            raf = new RafApi(windowsPath, windowsPath.replace(".txt", "PTR.txt"));
        } else {
            throw new FileNotFoundException("Could not locate the raf in current file system");
        }

        simg = new SimilarityGraph(raf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        
        String entity = request.getParameter("entity");
        String count = request.getParameter("count");
        String depth = request.getParameter("depth");
        String infoService = request.getParameter("info-service");
        String rafFilePath = request.getParameter("raf-filepath");

        if (rafFilePath != null) { //to compile..
            raf = new RafApi(rafFilePath, rafFilePath.replace(".txt", "PTR.txt"));
            simg = new SimilarityGraph(raf);
        }

        //Consider entity as URI (with prefix :) )
        if (entity != null) {
            //currentEntity = entity;
            String[] conts = raf.getEntityContents(entity);
            String curEn = conts[0];
            String curEnURI = conts[1];

            //currentEntity = curEnURI;
            session.setAttribute("entity", curEnURI);
        }

        if (count != null) {
            if (CommonUtils.isNumeric(count)) {
                //similarsNum = (int) Double.parseDouble(count);
                //System.out.println("Count set: " + similarsNum);
                session.setAttribute("count", count);
            }
        }

        if (depth != null) {
            if (CommonUtils.isNumeric(depth)) {
                // graphDepth = (int) Double.parseDouble(depth);
                //System.out.println("Depth set: " + graphDepth);
                session.setAttribute("depth", depth);
            }
        }

        if (infoService != null) {
            if (infoService.equals("wikipedia")) {
                //currentInformationService = "wikipedia";
                //System.out.println("Wikipedia service selected.");
                session.setAttribute("service", "wikipedia");
            } else if (infoService.equals("dbpedia")) {
                //currentInformationService = "dbpedia";
                //System.out.println("DBpedia service selected.");
                session.setAttribute("service", "dbpedia");
            } else if (infoService.equals("triples")) {
                JSONArray jtriples = SPARQLQuery.getTriplesOfURI(currentEntity, endpoint);
                //currentInformationService = jtriples.toString();
                //System.out.println("Triple Services Selected");
                session.setAttribute("service", jtriples.toString());
            }
        }

        JSONObject graph2sent = new JSONObject();
        if (simg != null) {
            simg.createGraphRaf(currentEntity, graphDepth, similarsNum);
            graph2sent = simg.toJSON();
        }

        request.setAttribute("graph", graph2sent.toString());
        request.setAttribute("self", currentEntity);
        request.setAttribute("count", similarsNum);
        request.setAttribute("depth", graphDepth);
        request.setAttribute("info-service", currentInformationService);

        //System.out.println("Server connection attribute--graph: " + graph2sent.toString(2));
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

        System.out.println("Server->Sending: " + data2sent.toString(2));

        out.print(data2sent);
        out.flush();
    }

}
