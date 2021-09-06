/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import embeddings.W2VApi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import raf.RafApi;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
public class DatasetCreator {

    public static W2VApi trainModel(String vecTargetPath, String rdfSourcePath, boolean usePretrainedFile) {
        W2VApi vec = null;
        if (usePretrainedFile) {
            vec = new W2VApi(vecTargetPath);
        } else {
            List<String> stopWords = new ArrayList<>();
            stopWords.add(".");
            vec = new W2VApi(5, 200, 42, 3, 10, stopWords, rdfSourcePath);
            vec.train();
        }
        return vec;
    }

    public static void defaultDatasetCleanUp(W2VApi vec) {
        Collection< String> keepWordsStartingWith = new ArrayList<>();
        keepWordsStartingWith.add("http://dbpedia.org/resource/");

        Collection<String> keepWordsNotStartingWith = new ArrayList<>();
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/Template");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/Category");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/?");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/*");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/-");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/:");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/%");
        keepWordsNotStartingWith.add("http://dbpedia.org/resource/.");

        Collection<String> removeWordsContaining = new ArrayList<>();
        removeWordsContaining.add("?");

        vec.filterVocab(keepWordsStartingWith, keepWordsNotStartingWith, removeWordsContaining);
    }

    public static void createPhilosophersDataset() throws IOException {

        long start;
        long end;

        System.out.println(" \n================ Creating Philosophers Dataset ================\n ");

        String rafTargetPath = "C:\\temp\\dbpedia_philosophers.txt";
        String rdfSourcePath = "C:\\tmp\\rdfsim\\philosophers.rdf";
        String vecTargetPath = "C:\\tmp\\rdfsim\\embeddings\\philosophers.vec";

        int count = 30;

        start = System.currentTimeMillis();
        W2VApi vec = trainModel(vecTargetPath, rdfSourcePath, false);
        end = System.currentTimeMillis();
        double elapsedTimeTrain = (end - start) * 1.0 / 1000.0;

        start = System.currentTimeMillis();
        defaultDatasetCleanUp(vec);
        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);
        end = System.currentTimeMillis();
        double elapsedTimeRaf = (end - start) * 1.0 / 1000.0;

        System.out.println("Philosophers: ");
        System.out.println("Train: " + elapsedTimeTrain);
        System.out.println("Raf: " + elapsedTimeRaf);
    }

    public static void createProgrammingLanguagesDataset() throws IOException {

        long start;
        long end;

        System.out.println(" \n================ Creating Philosophers Dataset ================\n ");

        String rafTargetPath = "C:\\temp\\dbpedia_programming_langs.txt";
        String rdfSourcePath = "C:\\tmp\\rdfsim\\programming_langs.rdf";
        String vecTargetPath = "C:\\tmp\\rdfsim\\embeddings\\programming_langs.vec";

        int count = 30;

        /*W2VApi vec = trainModel(vecTargetPath, rdfSourcePath, false);
    
        defaultDatasetCleanUp(vec);

        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);*/
        start = System.currentTimeMillis();
        W2VApi vec = trainModel(vecTargetPath, rdfSourcePath, false);
        end = System.currentTimeMillis();
        double elapsedTimeTrain = (end - start) * 1.0 / 1000.0;

        start = System.currentTimeMillis();
        defaultDatasetCleanUp(vec);
        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);
        end = System.currentTimeMillis();
        double elapsedTimeRaf = (end - start) * 1.0 / 1000.0;

        System.out.println("Programming: ");
        System.out.println("Train: " + elapsedTimeTrain);
        System.out.println("Raf: " + elapsedTimeRaf);

    }

    public static void createMoviesDataset() throws IOException {

        long start;
        long end;

        System.out.println(" \n================ Creating Movies Dataset ================\n ");

        String rafTargetPath = "C:\\temp\\dbpedia_movies.txt";
        String rdfSourcePath = "C:\\tmp\\rdfsim\\movies.rdf";
        String vecTargetPath = "C:\\tmp\\rdfsim\\embeddings\\movies.vec";

        int count = 30;

        /*W2VApi vec = trainModel(vecTargetPath, rdfSourcePath, false);

        defaultDatasetCleanUp(vec);

        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);*/
        start = System.currentTimeMillis();
        W2VApi vec = trainModel(vecTargetPath, rdfSourcePath, false);
        end = System.currentTimeMillis();
        double elapsedTimeTrain = (end - start) * 1.0 / 1000.0;

        start = System.currentTimeMillis();
        defaultDatasetCleanUp(vec);
        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);
        end = System.currentTimeMillis();
        double elapsedTimeRaf = (end - start) * 1.0 / 1000.0;

        System.out.println("Movies: ");
        System.out.println("Train: " + elapsedTimeTrain);
        System.out.println("Raf: " + elapsedTimeRaf);
    }

    public static void createVideoGamesDataset() throws IOException {

        long start;
        long end;

        System.out.println(" \n================ Creating Video Games Dataset ================\n ");

        String rafTargetPath = "C:\\temp\\dbpedia_video_games.txt";
        String rdfSourcePath = "C:\\tmp\\rdfsim\\video_games.rdf";
        String vecTargetPath = "C:\\tmp\\rdfsim\\embeddings\\video_games.vec";

        int count = 30;

        start = System.currentTimeMillis();
        W2VApi vec = trainModel(vecTargetPath, rdfSourcePath, false);
        end = System.currentTimeMillis();
        double elapsedTimeTrain = (end - start) * 1.0 / 1000.0;

        start = System.currentTimeMillis();
        defaultDatasetCleanUp(vec);
        String ptrTargetPath = rafTargetPath.replace(".txt", "PTR.txt");
        vec.createRAF(rafTargetPath, ptrTargetPath, count);
        end = System.currentTimeMillis();
        double elapsedTimeRaf = (end - start) * 1.0 / 1000.0;

        System.out.println("Video Games: ");
        System.out.println("Train: " + elapsedTimeTrain);
        System.out.println("Raf: " + elapsedTimeRaf);
    }

    public static void main(String[] args) throws IOException {

        //createPhilosophersDataset();

        //createProgrammingLanguagesDataset();
        createMoviesDataset();
        //createVideoGamesDataset();
    }

}
