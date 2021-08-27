package scripts;

import java.io.IOException;
import raf.RafApi;
import simgraph.SimilarityGraph;

/**
 *
 * @author manos
 */
public class SimGraphTester {

    public static void main(String[] args) throws IOException {
        RafApi raf = new RafApi("C:\\tmp\\rdfsim\\rafs\\dbpedia_programming_langs.txt");
        SimilarityGraph g = new SimilarityGraph(raf);
    
        g.createGraphRaf("http://dbpedia.org/resource/Git", 2, 2);
    }
}
