/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simgraph;

import embeddings.Word2VecEmbeddingCreator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import lombok.Data;
import org.json.JSONObject;

/**
 *
 * @author manos
 */
@Data
public class SimilarityGraph {

    private int depth;
    private int similarCount;

    private String startingNodeURI;
    private Word2VecEmbeddingCreator vec;
    private HashMap<String, SimilarityNode> nodes;

    public SimilarityGraph(int depth, int similarCount, Word2VecEmbeddingCreator vec, String startingNodeURI) {
        this.depth = depth;
        this.similarCount = similarCount;
        this.vec = vec;
        this.startingNodeURI = startingNodeURI;

        nodes = new HashMap<>();
    }

    public void createGraph() {

        int nodeCounter = 0;
        int levelBFS = 0;

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
