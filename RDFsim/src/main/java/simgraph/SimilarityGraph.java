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
        int levelBFS = 0;

        nodes = new HashMap<>();
        ArrayList<Queue<SimilarityNode>> queuesBFS = new ArrayList<>();

        Queue<SimilarityNode> currQueue = null;
        Queue<SimilarityNode> lvl0q = new LinkedList<>();

        lvl0q.add(addNode(startingNodeURI, nodeCounter++));

        queuesBFS.add(lvl0q);

        while (!(currQueue = queuesBFS.get(levelBFS)).isEmpty() && levelBFS < depth) {
            Queue<SimilarityNode> lvlCq = new LinkedList<>();

            for (SimilarityNode n : currQueue) {

                System.out.println(" ===================== Getting similars of node with URI: " + n.getURI() + " ===================== ");
                HashMap<String, Double> neighbours = raf.getSimilarEntitiesOfEntity(n.getURI(), similarCount);

                for (Map.Entry<String, Double> entry : neighbours.entrySet()) {

                    System.out.println("Similar Process: " + entry.getKey());

                    if (containsNode(entry.getKey())) {

                        SimilarityNode oldNode = nodes.get(entry.getKey());
                        System.out.println("Graph already contains the node " + oldNode.getURI() + " found as neighbour!");

                        /*if (!n.hasLink(oldNode.getId()) && !oldNode.hasLink(n.getId())) {
                            System.out.println("There does not exist a link between " + oldNode.getId() + "and" + n.getId() + ". Adding the link!");
                            n.addLink(entry.getValue(), oldNode.getId());
                        }*/
                        
                        SimilarityLink l;
                        if ((l = oldNode.getLinkToID(n.getId())) != null) {
                            l.setUL(true);
                            System.out.println("There exists a link between " + oldNode.getId() + "and" + n.getId() + ". Made it undirected!");
                        } else if ((l = n.getLinkToID(oldNode.getId())) != null) {
                            l.setUL(true);
                            System.out.println("There exists a link between " + oldNode.getId() + "and" + n.getId() + ". Made it undirected!");
                        } else {
                            System.out.println("There does not exist a link between " + oldNode.getId() + "and" + n.getId() + ". Adding the link!");
                            n.addLink(entry.getValue(), oldNode.getId());
                        }
                        
                    } else {
                        SimilarityNode newNode = addNode(entry.getKey(), nodeCounter++);

                        newNode.addLink(entry.getValue(), n.getId());

                        lvlCq.add(newNode);
                        queuesBFS.add(lvlCq);

                        System.out.println("There did not exist a node with name " + newNode.getURI() + ". Added the node and the link!");
                    }
                }

            }

            levelBFS++;
        }

    }

    public void createGraphW2V(String startingNodeURI, int depth, int similarCount) {

        int nodeCounter = 0;
        int levelBFS = 0;

        nodes = new HashMap<>();
        ArrayList<Queue<SimilarityNode>> queuesBFS = new ArrayList<>();

        Queue<SimilarityNode> currQueue = null;
        Queue<SimilarityNode> lvl0q = new LinkedList<>();

        lvl0q.add(addNode(startingNodeURI, nodeCounter++));

        queuesBFS.add(lvl0q);

        while (!(currQueue = queuesBFS.get(levelBFS)).isEmpty() && levelBFS < depth) {
            Queue<SimilarityNode> lvlCq = new LinkedList<>();

            for (SimilarityNode n : currQueue) {
                HashMap<String, Double> neighbours = vec.getSimilarEntitiesWithValues(n.getURI(), similarCount);

                for (Map.Entry<String, Double> entry : neighbours.entrySet()) {

                    if (containsNode(entry.getKey())) {
                        //do I need to do something here? (the link would be added before, so there also exist a link? maybe wrong thought
                        //for now only source nodes have links
                        SimilarityNode oldNode = nodes.get(entry.getKey());
                        //oldNode.addLink(entry.getValue(), n.getId());
                    } else {
                        SimilarityNode newNode = addNode(entry.getKey(), nodeCounter++);

                        newNode.addLink(entry.getValue(), n.getId()); //not yet needed
                        //n.addLink(entry.getValue(), newNode.getId());

                        lvlCq.add(newNode);
                        queuesBFS.add(lvlCq);
                    }
                }

            }

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
