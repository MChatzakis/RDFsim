package servlets;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
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

    final int DEFAULT_SIMILARS = 10;
    final int DEFAULT_DEPTH = 1;

    private RafApi initRaf(String name) throws IOException {

        String linuxPath = "/var/lib/tomcat9/work/rdfsim/rafs/" + name + ".txt";
        String windowsPath = "C:\\tmp\\rdfsim\\rafs\\" + name + ".txt";

        File lin = new File(linuxPath);
        File win = new File(windowsPath);

        if (lin.exists()) {
            return new RafApi(linuxPath, linuxPath.replace(".txt", "PTR.txt"));
        } else if (win.exists()) {
            return new RafApi(windowsPath, windowsPath.replace(".txt", "PTR.txt"));
        }

        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*Connection attributes*/
        HttpSession session = request.getSession();
        String s;

        String currentEntity = ((s = String.valueOf(session.getAttribute("entity"))).equals("null")) ? null : s;
        Integer currentCount = ((s = String.valueOf(session.getAttribute("count"))).equals("null")) ? null : Integer.parseInt(s);
        Integer currentDepth = ((s = String.valueOf(session.getAttribute("depth"))).equals("null")) ? null : Integer.parseInt(s);
        String currentService = ((s = String.valueOf(session.getAttribute("service"))).equals("null")) ? null : s;
        String currentVisMode = ((s = String.valueOf(session.getAttribute("visMode"))).equals("null")) ? null : s;
        RafApi currentRaf = ((s = String.valueOf(session.getAttribute("raf"))).equals("null")) ? null : (RafApi) session.getAttribute("raf");

        /*Attributes sent from the client forms*/
        String entity = request.getParameter("entity");
        String count = request.getParameter("count");
        String depth = request.getParameter("depth");
        String infoService = request.getParameter("info-service");
        String dataset = request.getParameter("dataset");
        String visMode = request.getParameter("vis-mode");

        /*Crucial Thing: Check current raf*/
        if (dataset != null) {
            currentRaf = initRaf(dataset);
        }

        if (currentRaf == null) {
            System.out.println("NULL RAF!");
            return;
        }

        if (entity != null) {
            String[] conts = (currentRaf.getEntityContents(entity));
            String curEnURI = conts[1];
            currentEntity = curEnURI;
        } else if (currentEntity == null) {
            currentEntity = "";
        }

        //
        
        if (count != null) {
            if (CommonUtils.isNumeric(count)) {
                currentCount = Integer.parseInt(count);
            }
        } else if (currentCount == null) {
            currentCount = DEFAULT_SIMILARS;
        }

        if (depth != null) {
            if (CommonUtils.isNumeric(depth)) {
                currentDepth = Integer.parseInt(depth);
            }
        } else if (currentDepth == null) {
            currentDepth = DEFAULT_DEPTH;
        }

        if (infoService != null) {
            currentService = infoService;
        } else if (currentService == null) {
            currentService = "wikipedia";
        }

        if (visMode != null) {
            currentVisMode = visMode;
            if (visMode.equals("simcloud")) {
                currentDepth = DEFAULT_DEPTH;
            }
        } else if (currentVisMode == null) {
            currentVisMode = "simgraph";
        }

        JSONObject graph2sent = null;

        SimilarityGraph g = new SimilarityGraph(currentRaf);
        g.createGraphRaf(currentEntity, currentDepth, currentCount);
        graph2sent = g.toJSON();

        request.setAttribute("graph", graph2sent.toString());
        request.setAttribute("self", currentEntity);
        request.setAttribute("count", currentCount);
        request.setAttribute("depth", currentDepth);
        request.setAttribute("visMode", currentVisMode);

        if (currentService.equals("triples")) {
            JSONObject allTriples = SPARQLQuery.getAllTriplesOfURI(currentEntity, endpoint);
            request.setAttribute("info-service", allTriples.toString());
        } else {
            request.setAttribute("info-service", currentService);
        }

        session.setAttribute("entity", currentEntity);
        session.setAttribute("count", currentCount);
        session.setAttribute("depth", currentDepth);
        session.setAttribute("service", currentService);
        session.setAttribute("raf", currentRaf);
        session.setAttribute("visMode", currentVisMode);

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
