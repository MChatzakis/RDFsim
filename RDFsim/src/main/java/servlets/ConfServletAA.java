package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utils.CommonUtils;

/**
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
@WebServlet(name = "ConfServlet", urlPatterns = {"/ConfServlet"})
public class ConfServletAA extends HttpServlet {
    
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

        String url2go = "http://localhost:8080/RDFsim/ConfServlet";
        
        boolean sample = Boolean.parseBoolean(request.getParameter("sample"));
        String endpoint = request.getParameter("endpoint");
        String query = request.getParameter("query");
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        JSONObject conf = new JSONObject();
        conf.put("endpoint", endpoint);
        conf.put("query", query);
        conf.put("offset", offset);
        conf.put("limit", limit);
        conf.put("sample", sample);

        String path = CommonUtils.writeStringToFile(conf.toString(2), "confs.json");

        System.out.println("Conf Servlet recieved saved the configurations below: " + conf.toString(2) + "to path: " + path);

        PrintWriter out = response.getWriter();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject data2sent = new JSONObject();
        data2sent.put("url", url2go);

        out.print(data2sent);
        out.flush();
    }

}
