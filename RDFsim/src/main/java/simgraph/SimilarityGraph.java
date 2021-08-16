package simgraph;

import embeddings.W2VApi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import lombok.Data;
import org.json.JSONObject;
import raf.RafApi;

/**
 *
 * @author manos
 */
@Data
public class SimilarityGraph {

    private W2VApi vec;
    private RafApi raf;

    private HashMap<String, SimilarityNode> nodes;

    public SimilarityGraph(W2VApi vec) {
        this.vec = vec;
    }

    public SimilarityGraph(RafApi raf) {
        this.raf = raf;
    }

    public void createGraphRaf(String startingNodeURI, int depth, int similarCount) throws IOException {
        int nodeCounter = 0;
        nodes = new HashMap<>();

        ArrayList<ArrayList<SimilarityNode>> levelNodes = new ArrayList<>();

        ArrayList<SimilarityNode> firstLevelNode = new ArrayList<>();
        ArrayList<SimilarityNode> currentLevelNodes = null;

        firstLevelNode.add(addNode(startingNodeURI, nodeCounter++));
        levelNodes.add(firstLevelNode);

        int levelBFS = 0;
        while (levelBFS < depth && !(currentLevelNodes = levelNodes.get(levelBFS)).isEmpty()) {
            ArrayList<SimilarityNode> nextLevelNodes = new ArrayList<>();

            for (SimilarityNode currentNode : currentLevelNodes) {
                HashMap<String, Double> neighbours = raf.getSimilarEntitiesOfEntity(currentNode.getURI(), similarCount);

                for (Map.Entry<String, Double> entry : neighbours.entrySet()) {
                    String neighbourNodeURI = entry.getKey();
                    Double neightbourNodeWeight = entry.getValue();

                    if (containsNode(neighbourNodeURI)) {
                        SimilarityNode previousLevelNode = nodes.get(neighbourNodeURI);

                        /* 
                            Repeating case: 'currentNode' has a similar node that already belongs to the graph, which is 'previousLevelNode'. 
                            Actions to be done: Add link between them if not exists!
                            Importat note: The link could exist from previous iteration!
                         */
                        SimilarityLink link;
                        if ((link = previousLevelNode.getLinkToID(currentNode.getId())) != null) {
                            /* Make this link undirected */
                            link.setUL(true);
                        } else if ((link = currentNode.getLinkToID(previousLevelNode.getId())) != null) {
                            /* Make this link undirected */
                            link.setUL(true);
                        } else {
                            /* Insert link */
                            previousLevelNode.addLink(neightbourNodeWeight, currentNode.getId());
                            //currentNode.addLink(neightbourNodeWeight, previousLevelNode.getId());
                        }

                    } else {
                        SimilarityNode newNode = addNode(neighbourNodeURI, nodeCounter++);
                        newNode.addLink(neightbourNodeWeight, currentNode.getId());

                        nextLevelNodes.add(newNode);
                    }
                }

            }

            levelNodes.add(nextLevelNodes);
            levelBFS++;
        }
    }

    public SimilarityNode addNode(String URI, int id) {
        SimilarityNode newNode = new SimilarityNode(URI, id);
        nodes.put(URI, newNode);
        return newNode;
    }

    public boolean containsNode(String URI) {
        return nodes.containsKey(URI);
    }

    public JSONObject toJSON() {
        JSONObject jgraph = new JSONObject();

        for (Map.Entry<String, SimilarityNode> entry : nodes.entrySet()) {
            jgraph.put(entry.getKey(), entry.getValue().toJSON());
        }

        return jgraph;
    }
}
