package simgraph;

import lombok.Data;
import org.json.JSONObject;

/**
 *
 * @author manos
 */
@Data
public class SimilarityLink {
    private double weight;
    private int toID;
   
    public SimilarityLink(double weight, int toID){
        this.weight = weight;
        this.toID = toID;
    }
    
    public JSONObject toJSON(){
        JSONObject jlink = new JSONObject();
        
        jlink.put("weight", weight);
        jlink.put("toID", toID);
        
        return jlink;
    }
    
}
