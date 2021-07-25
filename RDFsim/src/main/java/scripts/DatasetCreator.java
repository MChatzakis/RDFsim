/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.W2VApi;
import embeddings.W2VApi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import raf.RafApi;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class DatasetCreator {

    public static void createPhilosophersDataset() throws IOException {

        System.out.println(" \n================ Creating Philosophers Dataset ================\n ");

        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\philosophers.txt";
        String rdfSourcePath = "C:\\tmp\\rdfsim\\philosophers.rdf";
        String vecTargetPath = "C:\\tmp\\rdfsim\\embeddings\\philosophers.vec";

        int count = 30;

        boolean usePretrainedFile = false;
        W2VApi vec = null;
        if (usePretrainedFile) {
            vec = new W2VApi(vecTargetPath);
        } else {
            vec = new W2VApi(3, 100, 42, 5, rdfSourcePath);
            vec.train();
            //vec.saveVectorSpace(vecTargetPath);
        }

        Collection<String> keepWordsStartingWith = new ArrayList<>();
        keepWordsStartingWith.add("http://dbpedia.org/resource/");

        Collection<String> keepWordsNotStartingWith = new ArrayList<>();
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/Template");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/Category");

        Collection<String> removeWordsContaining = new ArrayList<>();
        removeWordsContaining.add("???");

        vec.filterVocab(keepWordsStartingWith, keepWordsNotStartingWith, removeWordsContaining);

        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);

        RafApi raf = new RafApi(rafTargetPath, ptrTargetPath);

        System.out.println(" \n================ Raf file contents ================\n ");
        System.out.println(raf.toString());

        System.out.println(" \n================ Available Philosophers ================\n ");
        System.out.println(raf.getVocabInfo());

        System.out.println(" \n================ Doing Comparison testing ================\n ");
        String entity = "Aristotle";
        HashMap<String, Double> similars = raf.getSimilarEntitiesOfEntity(entity, 4);
        System.out.println("Similars of Aristotle [RAF]:");
        CommonUtils.printEntityMap(similars);

        similars = vec.getSimilarEntitiesWithValues("http://dbpedia.org/resource/" + entity, 4);
        System.out.println("Similars of Aristotle [VEC]:");
        CommonUtils.printEntityMap(similars);

    }

    public static void createProgrammingLanguagesDataset() throws IOException {

    }

    public static void main(String[] args) throws IOException {
        createPhilosophersDataset();
    }

}
