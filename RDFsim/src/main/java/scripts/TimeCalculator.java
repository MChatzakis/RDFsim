/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts;

import java.io.IOException;
import java.util.HashMap;
import raf.RafApi;

/**
 *
 * @author manos
 */
public class TimeCalculator {

    public static void calculateTime(String dataset, String entity, int count, Mode mode) throws IOException {
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
            raf.getSimilarEntitiesOfEntity(entity, count);
            end = System.currentTimeMillis();
            elapsedTime = end - start;
            break;
        }

        System.out.println("Mode " + mode.toString() + ": Time Passed: " + elapsedTime / 1000 + " seconds");
    }

    public static void main(String[] args) throws IOException {
        String rafTargetPath = "C:\\tmp\\rdfsim\\rafs\\dbpedia_movies.txt";
        String entity = "Ariztical_Entertainment";
        int count = 10;
        calculateTime(rafTargetPath, entity, count, Mode.POINTER);
    }
}

enum Mode {
    POINTER,
    SEQUENTIAL
}
