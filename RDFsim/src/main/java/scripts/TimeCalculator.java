/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.IOException;
import java.util.HashMap;
import raf.RafApi;
import simgraph.SimilarityGraph;

/**
 *
 * @author manos
 */
public class TimeCalculator {

    public static void main(String[] args) throws IOException {
        //indexingTests();
        graphTests();
    }

    public static void calculateIndexingTime(String dataset, String entity, int count, IndexingMode mode) throws IOException {
        RafApi raf = new RafApi(dataset);

        long start;
        long end;
        double elapsedTime = -1;

        switch (mode) {
        case POINTER:
            start = System.currentTimeMillis();
            raf.getSimilarEntitiesOfEntity(entity, count);
            end = System.currentTimeMillis();
            elapsedTime = end - start;
            break;
        case SEQUENTIAL:
            start = System.currentTimeMillis();
            raf.getSimilarEntitiesOfEntitySequential(entity, count);
            end = System.currentTimeMillis();
            elapsedTime = end - start;
            break;
        }

        System.out.println("Mode " + mode.toString() + ": Time Passed: " + elapsedTime / 1000.0 + " seconds");
    }

    public static void calculateGraphTime(String dataset, String entity, int count, int depth) throws IOException {
        RafApi raf = new RafApi(dataset);
        SimilarityGraph g = new SimilarityGraph(raf);

        long start;
        long end;
        double elapsedTime;

        start = System.currentTimeMillis();
        g.createGraphRaf(entity, depth, count);
        end = System.currentTimeMillis();
        elapsedTime = end - start;

        System.out.println("Graph creation - Time Passed: " + elapsedTime / 1000.0 + " seconds");
    }

    public static void indexingTests() throws IOException {
        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";
        String entity = "Inception";
        int count = 10;

        calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.POINTER);
        calculateIndexingTime(rafTargetPath, entity, count, IndexingMode.SEQUENTIAL);
    }

    public static void graphTests() throws IOException {
        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";
        String entity = "Inception";
        int count = 10;
        int depth = 1;
        calculateGraphTime(rafTargetPath, entity, count, depth);
    }
}

enum IndexingMode {
    POINTER,
    SEQUENTIAL
}
