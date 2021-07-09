package sparql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class providing methods to retrieve data from REST and Virtuoso endpoints.
 * TODO: Update retrieval methods to also create a list of Entities (rdf.entities) -- ONGOING
 * 
 * @author Manos Chatzakis
 */
public class SPARQLQuery {

    public JSONObject retrieveData(String endpoint, String query) throws UnsupportedEncodingException, MalformedURLException, ProtocolException, IOException {
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

    public String parseData(JSONObject rawData) {
        JSONArray vars = (rawData.getJSONObject("head")).getJSONArray("vars");
        JSONArray data = rawData.getJSONObject("results").getJSONArray("bindings");
        String res = "";

        //System.out.println(rawData.toString(2));
        for (int i = 0; i < data.length(); i++) {
            for (int k = 0; k < vars.length(); k++) {
                res += data.getJSONObject(i).getJSONObject(vars.getString(k)).getString("value") + " ";
            }

            res += ".\n";
        }
        return res;
    }

    public String getData(String endpoint, String query) throws MalformedURLException, ProtocolException, IOException {
        return parseData(retrieveData(endpoint, query));
    }

    public String getData(String endpoint, String baseQuery, int endLimit, int startOffset) throws ProtocolException, IOException {
        String totalData = "";
        String currData = "";

        int step = 10000;
        int limit = (endLimit >= step) ? step : endLimit;
        int offset = startOffset;

        String query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;

        while (!(currData = getData(endpoint, query)).equals("")) {

            System.out.println("Offset: " + offset + " Limit: " + limit);

            totalData += currData;

            offset += step;
            limit += step;

            if (limit > endLimit) {
                break;
            }

            query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;
        }

        return totalData;
    }

    public String getAllData(String endpoint, String baseQuery) throws IOException {
        return getData(endpoint, baseQuery, Integer.MAX_VALUE, 0);
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
