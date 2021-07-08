/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import embeddings.Word2VecEmbeddingCreator;
import java.io.File;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/SearchServlet"})
public class SearchServlet extends HttpServlet {

    Word2VecEmbeddingCreator vec;

    public SearchServlet() {
        super();
        //loadDataAndInit("conf.json");
    }

    private void initVectorSpace() {
        vec = new Word2VecEmbeddingCreator("C:\\Users\\manos\\Documents\\GitHub\\RDFsim\\RDFsim\\embeddings\\vectors.vec");
    }

    private void loadPreSavedData() {

    }

    private void loadDataAndInit(String confFilePath) throws IOException {
        System.out.println("Search servlet initializing...");

        JSONObject obj = new JSONObject(CommonUtils.getFileContent(confFilePath));
        System.out.println("Loaded conf file: " + obj.toString());
        
        String endpoint = obj.getString("endpoint");
        String query = obj.getString("query");
        
        int limit = obj.getInt("limit");
        int offset = obj.getInt("offset");
        
        
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DoGet -- Search");
        loadDataAndInit("C:\\xampp\\tomcat\\bin\\confs.json");
        request.getRequestDispatcher("/search.jsp").forward(request, response);
        return;
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
        case 3:
            System.out.println("---------------------------------------------------------------------------------------------------------dd");
            String mainEntity = request.getParameter("entity");
            int depth = Integer.parseInt(request.getParameter("depth"));
            count = Integer.parseInt(request.getParameter("count"));
            data2sent = getBigGraphData(mainEntity, depth, count);
            break;
        }

        System.out.println("Server->Sending: " + data2sent.toString(2));

        out.print(data2sent);
        out.flush();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Search Servlet";
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
        //System.out.println("Blah");
        //System.out.println(entities2add);
        //System.out.println(entities2sub);
        Collection<String> result = vec.getExpressionResult(entities2add, entities2sub, count);

        String resultAsString = "";
        for (String s : result) {
            resultAsString += s + "   ";
        }

        data.put("expr_result", resultAsString);
        return data;
    }

    public JSONObject getBigGraphData(String mainEntity, int depth, int count) {
        JSONObject graph = new JSONObject();
        int cD = 0, counter = 0;

        //apply BFS?
        Queue<String> queue = new LinkedList<>();
        queue.add(mainEntity);

        while (!queue.isEmpty() && cD < depth * count) { //just for testing, need to add levels there

            //System.out.println("A: " + counter + "M:" + depth*count);
            String currEn = queue.remove();

            JSONObject nodeInfo = new JSONObject();
            JSONArray links = new JSONArray();

            nodeInfo.put("label", counter);
            System.out.println(currEn);
            HashMap<String, Double> sims = vec.getSimilarEntitiesWithValues(currEn, count);
            for (String adj : sims.keySet()) {

                JSONObject linkInfo = new JSONObject();
                linkInfo.put("label", counter++);
                linkInfo.put("name", adj);
                linkInfo.put("weight", sims.get(adj)); //cosSim

                links.put(linkInfo);
                //from currEn to ajd link!
                queue.add(adj);
            }

            nodeInfo.put("links", links);
            graph.put(currEn, nodeInfo);
            cD++;
        }

        return graph;
    }

}
