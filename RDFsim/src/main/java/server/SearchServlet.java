package server;

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

    private String currentPrefix = "http://dbpedia.org/resource/";
    private String endpoint = "https://dbpedia.org/sparql";

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
        SessionData currentData = ((s = String.valueOf(session.getAttribute("sessionData"))).equals("null")) ? new SessionData() : (SessionData) session.getAttribute("sessionData");

        /*Attributes sent from the client forms*/
        String entity = request.getParameter("entity");
        String count = request.getParameter("count");
        String depth = request.getParameter("depth");
        String infoService = request.getParameter("info-service");
        String dataset = request.getParameter("dataset");
        String visMode = request.getParameter("vis-mode");

        /*Crucial Thing: Check current raf*/
        if (dataset != null) {
            currentData.setRaf(initRaf(dataset));
        }

        if (currentData.getRaf() == null) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
            requestDispatcher.forward(request, response);
            return;
        }

        if (entity != null) {
            String[] conts = (currentData.getRaf().getEntityContents(entity));
            currentData.setEntityURI(conts[1]);
        }

        if (currentData.getEntityURI() == null) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
            requestDispatcher.forward(request, response);
            return;
        }

        if (count != null) {
            if (CommonUtils.isNumeric(count)) {
                currentData.setCount(Integer.parseInt(count));
            }
        }

        if (depth != null) {
            if (CommonUtils.isNumeric(depth)) {
                currentData.setDepth(Integer.parseInt(depth));
            }
        }

        if (infoService != null) {
            currentData.setInfoService(infoService);
        }

        if (visMode != null) {
            currentData.setVisMode(visMode);
        }

        request.setAttribute("graph", currentData.getJSONGraph().toString());
        request.setAttribute("self", currentData.getEntityURI());
        request.setAttribute("count", currentData.getCount());
        request.setAttribute("depth", currentData.getDepth());
        request.setAttribute("visMode", currentData.getVisMode());
        request.setAttribute("info-service", currentData.getInfoService());

        if (currentData.getInfoService().equals("triples") || currentData.getVisMode().equals("triplegraph")) {
            if (currentData.getTriples() == null) {
                currentData.setTriples(SPARQLQuery.getAllTriplesOfURI(currentData.getEntityURI(), endpoint));
            }
            request.setAttribute("triples", currentData.getTriples().toString());
        } else {
            request.setAttribute("triples", new JSONObject().toString());
        }

        session.setAttribute("sessionData", currentData);

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
