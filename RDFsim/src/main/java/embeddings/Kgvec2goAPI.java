/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddings;

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
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import simgraph.SimilarityGraph;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class Kgvec2goAPI {

    String url;
    String dataset;

    public Kgvec2goAPI(String url, String dataset) {
        this.url = url;
        this.dataset = dataset;
    }

    public HashMap<String, Double> getSimilarEntitiesWithValues(String entity, int similarCount) throws MalformedURLException, MalformedURLException, ProtocolException, IOException {
        HashMap<String, Double> neighbours = new HashMap<>();
        String url2sent = url + "/" + dataset + "/" + similarCount + "/" + entity;
        JSONArray data = CommonUtils.retrieveData(url2sent).getJSONArray("result");

        for (int i = 0; i < data.length(); i++) {
            JSONObject n = data.getJSONObject(i);
            String entityName = n.getString("concept");
            Double score = n.getDouble("score");

            neighbours.put(entityName, score);
        }

        return CommonUtils.sortEntityMap(neighbours);
    }

    public String getResouceURI(String entity) {
        if (dataset.equals("dbpedia")) {
            if (!entity.startsWith("dbr:")) {
                return "dbr:" + entity;
            }
        }

        return entity;
    }

    public static void main(String[] args) throws MalformedURLException, MalformedURLException, ProtocolException, IOException {
        //System.out.println(CommonUtils.retrieveData("http://kgvec2go.org//rest/closest-concepts/dbpedia/10/Greece"));
        Kgvec2goAPI kgv2g = new Kgvec2goAPI("http://kgvec2go.org//rest/closest-concepts", "dbpedia");
        //System.out.println(kgv2g.getSimilarEntitiesWithValues("dbr:Aristotle",10));
        SimilarityGraph g = new SimilarityGraph(kgv2g);

        g.createGraphKGVec2go("dbr:Aristotle", 1, 1);

        System.out.println(g.toJSON().toString(2));
    }

}
