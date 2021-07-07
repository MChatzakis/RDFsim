package sparql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONObject;

/**
 * Class trying to provide functionality for any SPARQL endpoint/query (not only triples)
 * (OnGoing)
 * @author Manos Chatzakis
 */
public class SPARQLQuery {
    public JSONObject getRawJSONData(String endpoint, String query) throws UnsupportedEncodingException, MalformedURLException, IOException {

        String sparqlQueryURL = endpoint + "?query=" + URLEncoder.encode(query, "utf8");
        
        URL url = new URL(sparqlQueryURL);
        URLConnection con = url.openConnection();
        
        String json = "application/sparql-results+json";
        con.setRequestProperty("ACCEPT", json);

        InputStream is = con.getInputStream();
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

    public void parseRawJSONData(JSONObject rawData, String [] keys){
        
    }
}
