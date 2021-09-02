package sparql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class providing methods to retrieve data from REST and Virtuoso endpoints.
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
public class SPARQLQuery {
    
    public JSONObject retrieveData(String endpoint, String query) throws UnsupportedEncodingException, MalformedURLException, ProtocolException, IOException {
        
        System.out.println("Query: " + query);
        
        String sparqlQueryURL = endpoint + "?query=" + URLEncoder.encode(query, "utf8");
        URL url = new URL(sparqlQueryURL);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestProperty("ACCEPT", "application/sparql-results+json");
        conn.setRequestMethod("GET");
        conn.connect();
        
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf8");
        BufferedReader in = new BufferedReader(isr);
        
        String input;
        String resultsString = "";
        while ((input = in.readLine()) != null) {
            resultsString += input;
        }
        
        in.close();
        isr.close();
        is.close();
        
        return new JSONObject(resultsString);
    }
    
    public String parseData(JSONObject rawData, boolean formatURI) {

        //System.out.println(rawData.toString(2));
        JSONArray vars = (rawData.getJSONObject("head")).getJSONArray("vars");
        JSONArray data = rawData.getJSONObject("results").getJSONArray("bindings");
        String res = "";

        //System.out.println(rawData.toString(2));
        for (int i = 0; i < data.length(); i++) {
            for (int k = 0; k < vars.length(); k++) {
                if (formatURI) {
                    res += formatDBpediaURI(data.getJSONObject(i).getJSONObject(vars.getString(k)).getString("value")) + " ";
                } else {
                    res += data.getJSONObject(i).getJSONObject(vars.getString(k)).getString("value") + " ";
                }
            }
            
            res += ".\n";
        }
        return res;
    }
    
    public String getData(String endpoint, String query, boolean formatURI) throws MalformedURLException, ProtocolException, IOException {
        return parseData(retrieveData(endpoint, query), formatURI);
    }
    
    public String writeDataToFile(String endpoint, String baseQuery, int total, int startOffset, String filename, boolean formatURI) throws ProtocolException, IOException {
        String currData = "";
        FileWriter fw = new FileWriter(filename, true);
        
        int dataRetrieved = 0;
        int offset = startOffset;
        int step = (total - dataRetrieved >= 10000) ? 10000 : (total - dataRetrieved);
        
        String query = baseQuery + " offset " + offset + " limit " + step;
        
        while (!(currData = getData(endpoint, query, formatURI)).equals("")) {

            //System.out.println("[O: " + offset + ",E:" + (offset + step) + "]");
            offset += step;
            dataRetrieved += step;
            
            step = (total - dataRetrieved >= 10000) ? 10000 : (total - dataRetrieved);
            
            fw.write(currData);
            
            if (dataRetrieved >= total) {
                break;
            }
            
            query = baseQuery + " offset " + offset + " limit " + step;
        }
        
        fw.close();
        return new File(filename).getAbsolutePath();
    }
    
    public boolean askQuery(String endpoint, String query) throws MalformedURLException, ProtocolException, IOException {
        JSONObject res = retrieveData(endpoint, query);
        return res.getBoolean("boolean");
    }
    
    public ArrayList<String> entityQuery(String endpoint, String query) throws MalformedURLException, ProtocolException, IOException {
        ArrayList<String> entities = new ArrayList<>();
        
        JSONObject res = retrieveData(endpoint, query);
        //System.out.println(res.toString(2));
        
        JSONArray jsonEns = res.getJSONObject("results").getJSONArray("bindings");
        String variableName = res.getJSONObject("head").getJSONArray("vars").getString(0);
        for (int i = 0; i < jsonEns.length(); i++) {
            String currEn = jsonEns.getJSONObject(i).getJSONObject(variableName).getString("value");
            entities.add(currEn);
        }
        
        return entities;
    }
    
    public int countQuery(String endpoint, String query) throws MalformedURLException, ProtocolException, IOException {
        JSONObject res = retrieveData(endpoint, query);
        //System.out.println(res.toString(2));
        return Integer.parseInt(res.getJSONObject("results").getJSONArray("bindings").getJSONObject(0).getJSONObject("count").getString("value"));
    }
    
    public static String formatDBpediaURI(String URI) {
        
        String[] splitters = {"/", "#"};
        /*Removed : as many times it was useful, etc: Star Wars: A new hope, or Thor: Dark World*/
        String[] parts;
        String result = URI;
        
        for (String s : splitters) {
            parts = result.split(s);
            result = parts[parts.length - 1];
        }
        
        return result;
    }
    
    public static JSONObject getAllTriplesOfURI(String s, String endpoint) throws MalformedURLException, ProtocolException, IOException {
        JSONObject allTriples = new JSONObject();
        
        allTriples.put("asSubject", getTriplesOfURIAsSubject(s, endpoint));
        allTriples.put("asObject", getTriplesOfURIAsObject(s, endpoint));
        
        return allTriples;
    }
    
    public static JSONArray getTriplesOfURIAsObject(String o, String endpoint) throws MalformedURLException, ProtocolException, IOException {
        JSONArray jtable = new JSONArray();
        
        String query = "select ?s ?p from <http://dbpedia.org> where { ?s ?p <" + o + ">. }";
        
        JSONObject rawData = new SPARQLQuery().retrieveData(endpoint, query);
        JSONArray data = rawData.getJSONObject("results").getJSONArray("bindings");
        
        String ss = "";
        String ps = "";
        
        for (int i = 0; i < data.length(); i++) {
            JSONObject newIndex = new JSONObject();
            
            ss = data.getJSONObject(i).getJSONObject("s").getString("value") + "";
            ps = data.getJSONObject(i).getJSONObject("p").getString("value") + "";
            
            String subject = "";
            String predicate = "";
            
            predicate = ps.replace("'", "@_@");
            subject = ss.replace("'", "@_@");
            
            newIndex.put("s", subject);
            newIndex.put("p", predicate);
            
            jtable.put(newIndex);
        }
        
        return jtable;
    }
    
    public static JSONArray getTriplesOfURIAsSubject(String s, String endpoint) throws MalformedURLException, ProtocolException, IOException {
        JSONArray jtable = new JSONArray();
        
        String query = "select ?p ?o from <http://dbpedia.org> where { <" + s + "> ?p ?o. filter(isURI(?o)) }";
        
        JSONObject rawData = new SPARQLQuery().retrieveData(endpoint, query);
        JSONArray data = rawData.getJSONObject("results").getJSONArray("bindings");
        
        String ps = "";
        String os = "";
        
        for (int i = 0; i < data.length(); i++) {
            JSONObject newIndex = new JSONObject();
            
            ps = data.getJSONObject(i).getJSONObject("p").getString("value") + "";
            os = data.getJSONObject(i).getJSONObject("o").getString("value") + "";
            
            String subject = "";
            String predicate = "";
            String object = "";
            
            predicate = ps.replace("'", "@_@");
            object = os.replace("'", "@_@");
            
            newIndex.put("p", predicate);
            newIndex.put("o", object);
            
            jtable.put(newIndex);
        }
        
        return jtable;
    }
    
    public static boolean isURI(String str) {
        return str.startsWith("http");
    }
}
