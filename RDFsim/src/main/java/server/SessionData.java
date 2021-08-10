/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import lombok.Data;
import org.json.JSONObject;
import raf.RafApi;
import simgraph.SimilarityGraph;

/**
 *
 * @author manos
 */
@Data
public class SessionData {

    /* Default RDFsim values. Any change here should be supported at front!*/
    public final int DEFAULT_COUNT = 10;
    public final int DEFAULT_DEPTH = 1;
    public final int DEFAULT_INFO_SERVICE = 0; 
    public final int DEFAULT_VIS_MODE = 0;

    /* Session Attributes: */
    private Integer count = DEFAULT_COUNT;
    private Integer depth = DEFAULT_DEPTH;

    private int infoService = DEFAULT_INFO_SERVICE;
    private int visMode = DEFAULT_VIS_MODE;

    private String entityURI = null;

    private RafApi raf = null;
    private SimilarityGraph simGraph = null;
    private JSONObject triples = null;

    public void setVisMode(int visMode) {
        this.visMode = visMode;

        if (visMode == 1) { //1 stands for simcloud
            this.depth = 1;
        }
    }

    public void setEntityURI(String entityURI) {
        resetEntityData();
        this.entityURI = entityURI;
    }

    public void setRaf(RafApi raf) {
        this.raf = raf;
        resetEntityData();
    }

    public void setCount(Integer count) {
        this.simGraph = null;
        this.count = count;
    }

    public void setDepth(Integer depth) {
        this.simGraph = null;
        this.depth = depth;
    }

    public JSONObject getJSONGraph() throws IOException {
        if (this.simGraph == null) {
            this.simGraph = new SimilarityGraph(raf);
            this.simGraph.createGraphRaf(entityURI, depth, count);
        }

        return this.simGraph.toJSON();
    }

    public void resetEntityData() {
        this.entityURI = null;
        this.simGraph = null;
        this.triples = null;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("URI", this.getEntityURI());
        obj.put("count", this.getCount());
        obj.put("depth", this.getDepth());
        obj.put("infoService", this.getInfoService());
        obj.put("visMode", this.getVisMode());
        obj.put("graph", this.getSimGraph().toJSON());
        obj.put("triples", this.getTriples());
        obj.put("raf", this.getRaf().getPath());

        return obj;
    }
}
