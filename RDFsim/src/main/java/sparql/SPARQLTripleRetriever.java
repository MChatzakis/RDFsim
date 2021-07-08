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
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.CommonUtils;

/**
 * Class offering methods to retrieve RDF triples from any SPARQL endpoint
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
@Data
public class SPARQLTripleRetriever {

    private ArrayList<String> triples = null;

    public JSONObject getRawJSONData(String endpoint, String query) throws UnsupportedEncodingException, MalformedURLException, IOException {

        String prefixedQuery = query;
        String sparqlQueryURL = endpoint + "?query=" + URLEncoder.encode(prefixedQuery, "utf8");
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
            //System.out.println(input);
            resultsString += input;
        }

        in.close();
        isr.close();
        is.close();

        return new JSONObject(resultsString);
    }

    public String getTriplesFromRawJSON(JSONObject raw, boolean formatTriples, String subject, String predicate, String object) {
        JSONObject results = raw.getJSONObject("results");
        JSONArray bindings = results.getJSONArray("bindings");
        triples = new ArrayList<>();
        String s = "";
        String p = "";
        String o = "";
        String triplesText = "";
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

            String triple = s + " " + p + " " + o + " ,\n";
            triplesText += triple;
            triples.add(triple);
        }
        return triplesText;
    }

    public String getTriples(String endpoint, String query, boolean formatTriples) throws MalformedURLException, IOException, IOException {
        //NOTE: for now only select queries are supported
        JSONObject raw = getRawJSONData(endpoint, query);
        String triples2ret = getTriplesFromRawJSON(raw, formatTriples, "s", "p", "o");
        return triples2ret;
    }

    public String getTriples(String endpoint, String query, boolean formatTriples, String s, String p, String o) throws MalformedURLException, IOException, IOException {
        //NOTE: for now only select queries are supported
        JSONObject raw = getRawJSONData(endpoint, query);
        //System.out.println(raw.toString(2));
        String triples = getTriplesFromRawJSON(raw, formatTriples, s, p, o);
        return triples;
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

    public String getAllTriples(String endpoint, String baseQuery, boolean formatTriples, String s, String p, String o) throws IOException, IOException {

        String totalTriples = "";
        String currTriples;

        int limit = 10000;
        int offset = 0;

        String query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;

        while (!(currTriples = getTriples(endpoint, query, formatTriples, s, p, o)).equals("")) {

            System.out.println("Offset: " + offset + " Limit: " + limit);

            offset += getTriples().size();
            limit += 10000;

            //currTriples += ",\n";
            totalTriples += currTriples;

            query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;
        }

        return totalTriples;
    }

    public String getTriples(String endpoint, String baseQuery, boolean formatTriples, int startOffset, int endLimit, String s, String p, String o) throws IOException {
        String totalTriples = "";
        String currTriples;

        int limit = 10000;
        int offset = startOffset;

        String query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;

        while (!(currTriples = getTriples(endpoint, query, formatTriples, s, p, o)).equals("")) {

            System.out.println("Offset: " + offset + " Limit: " + limit);

            offset += getTriples().size();
            limit += 10000;

            //currTriples += "\n";
            totalTriples += currTriples;

            if (limit > endLimit) {
                break;
            }

            query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;
        }

        return totalTriples;
    }
    
    
}
