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
    
    RafApi raf = null;
    
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
        
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String s;
        /*Connection attributes*/
        String currentEntity = String.valueOf(session.getAttribute("entity"));
        Integer currentCount = ((s = String.valueOf(session.getAttribute("count"))).equals("null")) ? null : Integer.parseInt(s);        // Integer.parseInt(String.valueOf(session.getAttribute("count")));
        Integer currentDepth = ((s = String.valueOf(session.getAttribute("depth"))).equals("null")) ? null : Integer.parseInt(s);
        String currentService = String.valueOf(session.getAttribute("service"));

        /*Attributes sent from the client forms*/
        String entity = request.getParameter("entity");
        String count = request.getParameter("count");
        String depth = request.getParameter("depth");
        String infoService = request.getParameter("info-service");
        
        if (entity != null) {
            String[] conts = (raf.getEntityContents(entity));
            String curEnURI = conts[1];
            currentEntity = curEnURI;
        } else if (currentEntity == null) {
            currentEntity = "";
        }
        
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
            /*if (infoService.equals("wikipedia")) {
                //session.setAttribute("service", "wikipedia");
                currentService = "wikipedia";
            } else if (infoService.equals("dbpedia")) {
                // session.setAttribute("service", "dbpedia");
                currentService = "dbpedia";
            } else if (infoService.equals("triples")) {
                //JSONArray jtriples = SPARQLQuery.getTriplesOfURI((String) session.getAttribute("entity"), endpoint);
                //session.setAttribute("service", jtriples.toString());
                currentService = "triples";
            }*/
            currentService = infoService;
        } else if (currentService == null) {
            //session.setAttribute("service", "wikipedia");
            currentService = "wikipedia";
        }
        
        JSONObject graph2sent = null;
        SimilarityGraph g = new SimilarityGraph(raf);
        g.createGraphRaf(currentEntity, currentDepth, currentCount);
        graph2sent = g.toJSON();
        
        request.setAttribute("graph", graph2sent.toString());
        request.setAttribute("self", currentEntity);
        request.setAttribute("count", currentCount);
        request.setAttribute("depth", currentDepth);
        
        if (currentService.equals("triples")) {
            JSONArray jtriples = SPARQLQuery.getTriplesOfURI(currentEntity, endpoint);
            request.setAttribute("info-service", jtriples.toString());
        } else {
            request.setAttribute("info-service", currentService);
        }
        
        session.setAttribute("entity", currentEntity);
        session.setAttribute("count", currentCount);
        session.setAttribute("depth", currentDepth);
        session.setAttribute("service", currentService);

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
