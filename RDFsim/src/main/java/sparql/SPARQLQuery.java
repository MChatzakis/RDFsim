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
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import rdf.Triple;

/**
 * Class trying to provide functionality for any SPARQL endpoint/query (not only
 * triples) (OnGoing)
 *
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

    public ArrayList<Triple> getTriplesFromRawData(JSONObject raw, boolean formatTriples, String subject, String predicate, String object) {
        ArrayList<Triple> triples = new ArrayList<>();

        JSONObject results = raw.getJSONObject("results");
        JSONArray bindings = results.getJSONArray("bindings");

        String s = "";
        String p = "";
        String o = "";

        //String triplesText = "";
        for (int i = 0; i < bindings.length(); i++) {
            JSONObject jsonTriple = bindings.getJSONObject(i);

            if (formatTriples) {
                s = formatDBpediaURI(jsonTriple.getJSONObject(subject).getString("value"));
                p = formatDBpediaURI(jsonTriple.getJSONObject(predicate).getString("value"));
                o = formatDBpediaURI(jsonTriple.getJSONObject(object).getString("value"));
            } else {
                s = jsonTriple.getJSONObject(subject).getString("value");
                p = jsonTriple.getJSONObject(predicate).getString("value");
                o = jsonTriple.getJSONObject(object).getString("value");
            }

            //String triple = s + " " + p + " " + o + " ,\n";
            //triplesText += triple;
            triples.add(new Triple(s, p, o));
        }

        return triples;
    }

    public ArrayList<Triple> getTriples(String endpoint, String query, boolean formatTriples, String s, String p, String o) throws MalformedURLException, IOException {
        JSONObject raw = getRawJSONData(endpoint, query);
        return getTriplesFromRawData(raw, formatTriples, s, p, o);
    }

    public ArrayList<Triple> getTriples(String endpoint, String baseQuery, boolean formatTriples, int startOffset, int endLimit, String s, String p, String o) throws IOException {
        ArrayList<Triple> totalTriples = new ArrayList<>();
        ArrayList<Triple> currTriples = null;
        
        int limit = (endLimit >= 10000) ? 10000 : endLimit ;//10000;
        int offset = startOffset;

        String query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;

        while (!(currTriples = getTriples(endpoint, query, formatTriples, s, p, o)).isEmpty()) {
            
            System.out.println("Offset: " + offset + " Limit: " + limit);
            
            offset += currTriples.size();
            limit += 10000;

            totalTriples.addAll(currTriples);
            
            if (limit >= endLimit) {
                break;
            }

            query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;
        }

        return totalTriples;
    }

    public String formatDBpediaURI(String URI) {

        String[] splitters = {"/", "#", ":"}; //Possible improvement: Use regexes!
        String[] parts;
        String result = URI;

        for (String s : splitters) {
            parts = result.split(s);
            result = parts[parts.length - 1];
        }

        return result;
    }
}
