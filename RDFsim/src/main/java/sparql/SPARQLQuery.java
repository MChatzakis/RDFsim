package sparql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nd4j.shade.guava.io.CharSink;

/**
 * Class providing methods to retrieve data from REST and Virtuoso endpoints.
 * TODO: Update retrieval methods to also create a list of Entities
 * (rdf.entities) -- ONGOING, discuss
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

    public String parseData(JSONObject rawData, boolean formatURI) {
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

    public String writeDataToFile(String endpoint, String baseQuery, int endLimit, int startOffset, String filename, boolean formatURI) throws ProtocolException, IOException {
        String currData = "";
        FileWriter fw = new FileWriter(filename, true);

        int step = 10000;
        int limit = (endLimit >= step) ? step : endLimit;
        int offset = startOffset;

        String query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;

        while (!(currData = getData(endpoint, query, formatURI)).equals("")) {

            System.out.println("Offset: " + offset + " Limit: " + limit);

            offset += step;
            limit += step;

            fw.write(currData);

            if (limit > endLimit) {
                break;
            }

            query = baseQuery + " OFFSET " + offset + " LIMIT " + limit;
        }
        fw.close();
        return new File(filename).getAbsolutePath();
    }

    public String getAllData(String endpoint, String baseQuery, String filepath, boolean formatURI) throws IOException {
        return writeDataToFile(endpoint, baseQuery, Integer.MAX_VALUE, 0, filepath, formatURI);
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
