/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simgraph;

import java.util.ArrayList;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author manos
 */
@Data
public class SimilarityNode {

    private String URI;
    private int id;
    private ArrayList<SimilarityLink> links;

    public SimilarityNode(String URI, int id) {
        this.URI = URI;
        this.id = id;
        links = new ArrayList<>();
    }

    public void addLink(double weight, int toID) {
        links.add(new SimilarityLink(weight, toID));
    }

    public boolean hasLink(int toID) {
        for (SimilarityLink l : links) {
            if (l.getToID() == toID) {
                return true;
            }
        }

        return false;
    }

    public SimilarityLink getLinkToID(int toID) {
        for (SimilarityLink l : links) {
            if (l.getToID() == toID) {
                return l;
            }
        }

        return null;
    }

    public JSONObject toJSON() {
        JSONObject jnode = new JSONObject();

        //jnode.put("URI", URI);
        jnode.put("id", id);

        JSONArray jlinks = new JSONArray();
        for (SimilarityLink l : links) {
            jlinks.put(l.toJSON());
        }

        jnode.put("links", jlinks);

        return jnode;
    }
}
