/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author manos
 */
public class SPARQLTripleRetriever {

    public JSONObject getRawJSONTriples(String endpoint, String query) throws UnsupportedEncodingException, MalformedURLException, IOException {

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
            //System.out.println(input);
            resultsString += input;
        }

        in.close();
        isr.close();
        is.close();

        return new JSONObject(resultsString);
    }

    public String getTriplesFromRawJSON(JSONObject raw, boolean formatTriples) {
        JSONObject results = raw.getJSONObject("results");
        JSONArray bindings = results.getJSONArray("bindings");
        String s = "";
        String p = "";
        String o = "";
        String triples = "";
        for (int i = 0; i < bindings.length(); i++) {
            JSONObject jsonTriple = bindings.getJSONObject(i);

            if (formatTriples) {
                s = formatURI(jsonTriple.getJSONObject("s").getString("value"));
                p = formatURI(jsonTriple.getJSONObject("p").getString("value"));
                o = formatURI(jsonTriple.getJSONObject("o").getString("value"));
            } else {
                s = jsonTriple.getJSONObject("s").getString("value");
                p = jsonTriple.getJSONObject("p").getString("value");
                o = jsonTriple.getJSONObject("o").getString("value");
            }

            triples += s + " " + p + " " + o + "\n";
        }
        return triples;
    }

    public String getTriples(String endpoint, String query, boolean formatTriples) throws MalformedURLException, IOException, IOException {
        JSONObject raw = getRawJSONTriples(endpoint, query);
        //System.out.println(raw.toString(2));
        String triples = getTriplesFromRawJSON(raw, formatTriples);
        return triples;
    }

    public String formatURI(String URI) {
        String[] parts = URI.split("/");
        String remainingStr = parts[parts.length - 1];
        String[] str = remainingStr.split("#");
        return str[str.length - 1];
    }
}
