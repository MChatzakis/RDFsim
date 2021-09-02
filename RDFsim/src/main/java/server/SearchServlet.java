package server;

import java.io.File;
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
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 * Basic RDFsim servlet, able to send visualization and information data.
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/SearchServet"})
public class SearchServlet extends HttpServlet {

    private final int WIKIPEDIA_INDEX = 0;
    private final int DBPEDIA_INDEX = 1;
    private final int TRIPLE_ARRAY_INDEX = 2;

    private final int SIMGRAPH_INDEX = 0;
    private final int SIMCLOUD_INDEX = 1;
    private final int TRIPLEGRAPH_INDEX = 2;

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
        SessionData currentData = (String.valueOf(session.getAttribute("sessionData")).equals("null")) ? new SessionData() : (SessionData) session.getAttribute("sessionData");

        /*Attributes sent from the client forms*/
        String entity = request.getParameter("entity");
        String count = request.getParameter("count");
        String depth = request.getParameter("depth");
        String infoService = request.getParameter("info-service");
        String dataset = request.getParameter("dataset");
        String visMode = request.getParameter("vis-mode");

        /*Crucial Thing: Check current raf*/
        if (dataset != null) {
            currentData.processDatasetName(dataset);
            currentData.setRaf(initRaf(dataset));
        }

        if (currentData.getRaf() == null) {
            redirectToPage(request, response, "/error.jsp");
            return;
        }

        if (entity != null) {
            String[] conts = (currentData.getRaf().getEntityContents(entity));
            currentData.setEntityURI(conts[1]);
        }

        if (currentData.getEntityURI() == null) {
            redirectToPage(request, response, "/error.jsp");
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
            if (CommonUtils.isNumeric(infoService)) {
                currentData.setInfoService(Integer.parseInt(infoService));
            }
        }

        if (visMode != null) {
            if (CommonUtils.isNumeric(visMode)) {
                currentData.setVisMode(Integer.parseInt(visMode));
            }
        }

        JSONObject requestAttributes = new JSONObject();
        requestAttributes.put("graph", currentData.getJSONGraph());
        requestAttributes.put("self", currentData.getEntityURI());
        requestAttributes.put("count", currentData.getCount());
        requestAttributes.put("depth", currentData.getDepth());
        requestAttributes.put("visMode", currentData.getVisMode());
        requestAttributes.put("infoService", currentData.getInfoService());

        if (currentData.getInfoService() == TRIPLE_ARRAY_INDEX || currentData.getVisMode() == TRIPLEGRAPH_INDEX) {
            if (currentData.getTriples() == null) {
                currentData.setTriples(SPARQLQuery.getAllTriplesOfURI(currentData.getEntityURI(), currentData.getEndpoint()));
            }
            requestAttributes.put("triples", currentData.getTriples());
        } else {
            requestAttributes.put("triples", new JSONObject());
        }

        String data2sent = requestAttributes.toString().replaceAll("'", "&#39;");

        request.setAttribute("attributes", data2sent);
        session.setAttribute("sessionData", currentData);

        System.out.println("Session Data: " + currentData.toJSON().toString(2));
        System.out.println("Attribute Data" + data2sent);

        redirectToPage(request, response, "/search.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*Connection attributes*/
        HttpSession session = request.getSession();
        SessionData currentData = (String.valueOf(session.getAttribute("sessionData")).equals("null")) ? new SessionData() : (SessionData) session.getAttribute("sessionData");

        int type = Integer.parseInt(request.getParameter("type"));

        PrintWriter out = response.getWriter();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray data2sent = null;

        switch (type) {
        case 0:
            String prefix = request.getParameter("prefix");
            String dataset = request.getParameter("dataset");

            if (dataset.equals("empty")) {
                data2sent = currentData.getRaf().getWordRecomendations(prefix);
            } else {
                data2sent = initRaf(dataset).getWordRecomendations(prefix);
            }

            break;
        }

        System.out.println("Answering post request with: " + data2sent.toString(2));

        out.print(data2sent);
        out.flush();
    }

    protected void redirectToPage(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(page);
        requestDispatcher.forward(request, response);
    }
}
