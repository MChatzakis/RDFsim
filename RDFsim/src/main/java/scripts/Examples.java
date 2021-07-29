package scripts;

import embeddings.W2VApi;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Collection;
import java.util.HashMap;
import raf.RafApi;
import simgraph.SimilarityGraph;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 * Class to provide basic backend functionality examples and sample creation
 *
 * @author Manos Chatzakis
 */
public class Examples {

    public static String dbPediaEndpoint = "https://dbpedia.org/sparql";
    public static String ariadneEndpoint = "https://graphdb-test.ariadne.d4science.org/repositories/ariadneplus-ts01";
    public static String simplestQuery = "select * where {?s ?p ?o .}";
    public static String ariadnePeopleQuery = "select ?s ?p ?o ?p1 ?o1 where {?s a <http://www.cidoc-crm.org/cidoc-crm/E21_Person> . ?o ?p ?s . ?o ?p1 ?o1}";
    public static String dbPediaPhilosophers = "select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
    public static String dbPediaQueryBiggerSeqs = "select  ?s ?p ?o ?p1 ?o1 where {?s ?p ?o . ?o ?p1 ?o1 .  ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
    public static String dbPediaProgrammingLanguages = "select ?s ?p ?o where {?s ?p ?o . ?s a <http://dbpedia.org/ontology/ProgrammingLanguage> . filter(isURI(?o)) }";
    public static String dbPediaMovies = "select ?s ?p ?o from <http://dbpedia.org> where { ?s ?p ?o . ?s a <http://schema.org/Movie> . filter(isURI(?o))} ";
    public static String dbPediaGameConsoles = "select ?s ?p ?o where { ?s ?p ?o. ?s a <http://dbpedia.org/class/yago/WikicatVideoGameConsoles> . filter(isURI(?o))} ";

    public static void completeProc(String rdfFilePath, String vecFilePath, String endpoint, String query, int total, int start, int minFreq) throws IOException {
        SPARQLQuery sq = new SPARQLQuery();
        String path = sq.writeDataToFile(endpoint, query, total, start, rdfFilePath, false);

        //Word2VecEmbeddingCreator vects = new W2VApi(minFreq, 100, 42, 5, path);
        //vects.train();
        //vects.saveVectorSpace(vecFilePath);

        /*String entity = "http://dbpedia.org/resource/Cassius_Longinus_(philosopher)";
        Collection<String> similars = vects.getSimilarEntities(entity, 5);
        System.out.println("Similars of " + entity + " " + similars);
        double sim = vects.calculateCosineSimilarity("http://dbpedia.org/resource/Damo_(philosopher)", "http://dbpedia.org/resource/Onasander");
        System.out.println("Similarity (cosine): " + sim);
        HashMap<String, Double> topEntitiesOfPhilosopher = vects.getSimilarEntitiesWithValues("http://dbpedia.org/resource/Democritus", 5);
        CommonUtils.printEntityMap(topEntitiesOfPhilosopher);
        sim = vects.calculateCosineSimilarity("http://dbpedia.org/resource/Democritus", "http://dbpedia.org/resource/Gorgias");
        System.out.println("Similarity (cosine): " + sim);*/
    }

    public static void loadPreSaved(String filepath) {
        W2VApi vects = new W2VApi(filepath);
        Collection<String> strs = vects.getVocab();
        for (String s : strs) {
            System.out.println(s);
        }
        System.out.println("Total words: " + strs.size());
    }

    public static void trainOnly(String filepath, String output, int minFreq) {
        W2VApi vects = new W2VApi(minFreq, 100, 42, 5, filepath);
        vects.train();
        vects.saveVectorSpace(output);
    }

    public static void simGraph(String filepath, int count, int depth) {
        W2VApi vects = new W2VApi(filepath);
        SimilarityGraph g = new SimilarityGraph(vects);

        g.createGraphW2V("http://dbpedia.org/resouce/Aristotle", depth, count);

        System.out.println(g.toJSON().toString(2));
    }

    public static void getTriplesOfEntity(String entity, String endpoint) throws ProtocolException, IOException {
        System.out.println(SPARQLQuery.getTriplesOfURI(entity, endpoint).toString(2));
    }

    public static void createRAF(String filenameRAF, String filenamePTR, String modelName, int count) throws IOException {
        new W2VApi(modelName).createRAF(filenameRAF, filenamePTR, count);

        //System.out.println(new RafApi(filenameRAF, filenamePTR).toUTF());
        RafApi raf = new RafApi(filenameRAF, filenamePTR);
        
        System.out.println(raf.toString());
        
        String en = "Plato";
        HashMap<String, Double> similars = raf.getSimilarEntitiesOfEntity(en, count);

        System.out.println("Similars of " + en);
        CommonUtils.printEntityMap(similars);

    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        String datasetName = "movies";
        String vectorFilePath = "C:\\tmp\\rdfsim\\embeddings\\" + datasetName + ".vec";
        String rdfFilePath = "C:\\tmp\\rdfsim\\" + datasetName + ".rdf";
        completeProc(rdfFilePath, vectorFilePath, dbPediaEndpoint, dbPediaMovies, 1000000, 3349937 , 5);
        //completeProc(rdfFilePath, vectorFilePath, dbPediaEndpoint, dbPediaPhilosophers, 15000000, 0, 3);
        //trainOnly(rdfFilePath, vectorFilePath, 2);
        //loadPreSaved(vectorFilePath);
        //getTriplesOfEntity("http://dbpedia.org/resource/Aristotle", dbPediaEndpoint);
        //createRAF("raf.txt", "t.txt", "C:\\tmp\\rdfsim\\embeddings\\philosophers.vec", 20);

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("Time elapsed: " + (double) timeElapsed / 1000.0 + " seconds");
    }

}
