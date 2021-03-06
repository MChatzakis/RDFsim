package server;

import embeddings.Kgvec2goAPI;
import java.io.IOException;
import lombok.Data;
import org.json.JSONObject;
import raf.RandAccessFileAPI;
import simgraph.SimilarityGraph;

/**
 * Session configuration data class.
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
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
    private String prefix = null;
    private String endpoint = null;
    private String graphURL = null;

    private RandAccessFileAPI raf = null;
    private Kgvec2goAPI kgv2g;

    private SimilarityGraph simGraph = null;
    private JSONObject triples = null;

    public void setVisMode(int visMode) {
        this.visMode = visMode;

        if (visMode == 1) { //1 stands for simcloud
            this.depth = 1;
        }
    }

    public void setEntityURI(String entityURI) {
        if (this.entityURI == null || !this.entityURI.equals(entityURI)) {
            resetEntityData();
            this.entityURI = entityURI;
        }
    }

    public void setKgv2g(Kgvec2goAPI kgv2g) {
        this.kgv2g = kgv2g;

        this.raf = null;//check this again!
        resetEntityData();
    }

    public void setRaf(RandAccessFileAPI raf) {
        this.raf = raf;

        this.kgv2g = null; //check this again!
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
            if (this.raf != null) {
                this.simGraph = new SimilarityGraph(raf);
                this.simGraph.createGraphRaf(entityURI, depth, count);
            } else if (this.kgv2g != null) {
                this.simGraph = new SimilarityGraph(kgv2g);
                this.simGraph.createGraphKGVec2go(entityURI, depth, count);
            }
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
        if (raf != null) {
            obj.put("raf", this.getRaf().getPath());
        }

        return obj;
    }

    /* I should automate that one */
    public void processDatasetName(String dataset) {
        if (dataset.startsWith("dbpedia")) {
            this.prefix = "http://dbpedia.org/resource/";
            this.endpoint = "https://dbpedia.org/sparql";
        } else if (dataset.startsWith("ariadne")) {
            this.prefix = "";
            this.endpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";
        }
    }
}
