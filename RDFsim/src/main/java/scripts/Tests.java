package scripts;

import embeddings.Word2VecEmbeddingCreator;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import sparql.SPARQLTripleRetriever;
import utils.CommonUtils;

/**
 * Class to provide basic examples about the triple retrieval and the embeddings creation.
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
public class Tests {

    public static void getMaxTripleNumber() throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String totalTriples = "";
        String currTriples;
        int limit = 10000;
        int offset = 0;
        int i = 0;
        //String query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>}  OFFSET " + limit + " LIMIT " + limit;
        String query = "select  * where {?s ?p ?o FILTER(isURI(?o))} OFFSET " + offset + " LIMIT " + limit;

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        while (!(currTriples = tr.getTriples(endpoint, query, false)).equals("")) {
            System.out.println("Offset: " + offset + " Limit: " + limit);
            offset += tr.getTriples().size();
            limit += 10000;
            currTriples += "\n";
            totalTriples += currTriples;
            if (limit >= 30000) {
                break;
            }
            query = "select  * where {?s ?p ?o FILTER(isURI(?o))} OFFSET " + offset + " LIMIT " + limit;
        }

        CommonUtils.writeStringToFile(totalTriples, "DBpedia.rdf");
    }

    public static void simpleExample() throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String query = "";

        query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>} limit 10000";
        //query = "select  * where {?s ?p ?o }";
        //query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
        //query = "?dataset dataid:group <https://databus.dbpedia.org/dbpedia/generic> .";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(endpoint, query, false);

        //System.out.println(triples);
        System.out.println("Total triples: " + tr.getTriples().size());

        String path = CommonUtils.writeStringToFile(triples, "triples.rdf");
    }

    public static void createEmbeddingsW2v() throws IOException {
        String endpoint = "https://dbpedia.org/sparql";
        String query = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>} limit 1000 offset 0";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(endpoint, query, false);

        String path = CommonUtils.writeStringToFile(triples, "triples.rdf");

        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();

        vects.saveVectorSpace("vectors.vec");

        Collection<String> similars = vects.getSimilarEntities("Damascius", 10);
        System.out.println(similars);
    }

    public static void createEmbeddingsw2v(String filepath) {
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, filepath);
        vects.train();

        Collection<String> similars = vects.getSimilarEntities("http://dbpedia.org/resource/Ammonius_Saccas", 10);
        System.out.println(similars);

        System.out.println("Training complete");
    }

    public static void APIPresentationExample() throws IOException {
        /*STEP A: Select a SPARQL endpoint*/
        String SPARQLendpoint = "https://dbpedia.org/sparql";

        /*
        STEP B: Create a model base query (Base queries: Queries without offset and limit.
        Note: Use simple "getTriple" method for complete queries with limits and offsets. 
        Example: Select All triples of philosophers, excluding literals.
         */
        String baseQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";

        /*STEP C: Retrieve the data, selecting the first 30.000 triples of philosophers. Note that triple formatting is tested for DBpedia*/
        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        /*Iterative method that collects queries in sets of 10k*/
        String triples = tr.getTriples(SPARQLendpoint, baseQuery, true, 0, 100, "s", "p", "o");


        /*STEP D: Save the data to a file and get the absoulte file path*/
        String path = CommonUtils.writeStringToFile(triples, "triples/exampleTriples.rdf");

        /*STEP E: Use the saved file as a vocabulary for word2vec after instanciation, and train the model*/
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();

        /*STEP F: Save the produce embeddings for future use*/
        vects.saveVectorSpace("embeddings/vectors.vec");

        /*STEP G: Apply operations using the vector space of embeddings*/
        //Find the 5 most similar entities of Damascius
        String entity = "Damascius";
        Collection<String> similars = vects.getSimilarEntities(entity, 5);
        System.out.println("Similars of " + entity + " " + similars);

        //Find the similarity number of two Philosopher
        double sim = vects.calculateCosineSimilarity("Philip_of_Opus", "Proklos");
        System.out.println("Similarity (cosine): " + sim);

        HashMap<String, Double> topEntitiesOfPhilosopher = vects.getSimilarEntitiesWithValues("Aristotle", 20);
        CommonUtils.printEntityMap(topEntitiesOfPhilosopher);
        
        sim = vects.calculateCosineSimilarity("Socrates", "Proklos");
        System.out.println("Similarity (cosine): " + sim);
        
        /*NOTE: The vector space could be reloaded using the following line*/
        //Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator("vectors.vec");
    }

    public static void crashTest() throws IOException {
        /*STEP A: Select a SPARQL endpoint*/
        String SPARQLendpoint = "https://dbpedia.org/sparql";

        /*
        STEP B: Create a model base query (Base queries: Queries without offset and limit.
        Note: Use simple "getTriple" method for complete queries with limits and offsets. 
        Example: Select All triples of philosophers, excluding literals.
         */
        String baseQuery = "select  * where {?s ?p ?o .}";

        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getAllTriples(SPARQLendpoint, baseQuery, true, "s", "p", "o");

        /*STEP D: Save the data to a file and get the absoulte file path*/
        String path = CommonUtils.writeStringToFile(triples, "triples/exampleTriples.rdf");

        /*STEP E: Use the saved file as a vocabulary for word2vec after instanciation, and train the model*/
        Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator(5, 100, 42, 5, path);
        vects.train();

        /*STEP F: Save the produce embeddings for future use*/
        vects.saveVectorSpace("embeddings/vectors.vec");

        /*STEP G: Apply operations using the vector space of embeddings*/
        //Find the 5 most similar entities of Damascius
        String entity = "Damascius";
        Collection<String> similars = vects.getSimilarEntities(entity, 5);
        System.out.println("Similars of " + entity + " " + similars);

        //Find the similarity number of two Philosopher
        double sim = vects.calculateCosineSimilarity("Philip_of_Opus", "Proklos");
        System.out.println("Similarity (cosine): " + sim);

        HashMap<String, Double> topEntitiesOfPhilosopher = vects.getSimilarEntitiesWithValues("Proklos", 12);
        CommonUtils.printEntityMap(topEntitiesOfPhilosopher);

        /*NOTE: The vector space could be reloaded using the following line*/
        //Word2VecEmbeddingCreator vects = new Word2VecEmbeddingCreator("vectors.vec");
    }

    public static void SPARQLexamples() throws IOException {
        String SPARQLendpoint = "https://dbpedia.org/sparql";

        String baseQuery = "select  * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}";
        /*baseQuery = "SELECT *\n"
                + "WHERE\n"
                + "{\n"
                + "  ?s  rdfs:label      \"Cristiano Ronaldo\"@en ;\n"
                + "            dbo:birthPlace  ?p .\n"
                + " ?p     a               dbo:City ;\n"
                + "            rdfs:label      ?o .\n"
                + "}";*/
 /*baseQuery = "SELECT * WHERE \n"
                + "{\n"
                + "         ?s a :Album ;\n"
                + "          :artist ?p ;\n"
                + "          :date ?o\n"
                + "}\n"
                + "";*/
 
        baseQuery = "select * where { ?s ?p ?o. ?s rdf:type <http://dbpedia.org/class/yago/WikicatCharltonAthleticF.C.Players>} ";
        SPARQLTripleRetriever tr = new SPARQLTripleRetriever();
        String triples = tr.getTriples(SPARQLendpoint, baseQuery, false, 0, 100, "s", "p", "o");

        System.out.println(triples);
    }

    public static void EnvironmentBuildUpTest(){
        
    }
    
    public static void main(String[] args) throws IOException {
        //simpleExample();
        //createEmbeddingsW2v();
        //getMaxTripleNumber();
        //createEmbeddingsw2v("DBpedia.rdf");

        APIPresentationExample();
        SPARQLexamples();
        
    }
}
