/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
@Data
public class RafApi {

    private RandomAccessFile raf;
    private HashMap<String, Long> pointerMappings;

    public RafApi(String rafPath, String ptrPath) throws FileNotFoundException, IOException {
        raf = new RandomAccessFile(rafPath, "rw");
        processPrtFile(ptrPath);
    }

    private void processPrtFile(String ptrPath) throws IOException {
        pointerMappings = new HashMap<>();

        File file = new File(ptrPath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            //System.out.println(st);
            String[] parts = st.split(",");
            pointerMappings.put(parts[0], Long.parseLong(parts[1]));
        }

    }

    public HashMap<String, Double> getSimilarEntitiesOfEntity(String entity, int count) throws IOException {
        HashMap<String, Double> similars = new HashMap<>();

        String[] contents = getEntityContents(entity);

        if (contents != null) {
            System.out.println("Entity " + entity + " found.");

            String entityURI = contents[1];
            String rawSimilars = contents[2];

            //System.out.println("AA: " + rawSimilars);
            String[] similarPairs = rawSimilars.split("@");

            //System.out.println("BB: " + similarPairs[0]);
            //System.out.println("CC: " + similarPairs[1]);
            for (String sim : similarPairs) {
                String[] info = sim.split("%%%");
                //System.out.println(info[0] + "+++" + info[1]);
                if (info.length == 2) {
                    similars.put(info[0], Double.parseDouble(info[1]));
                }

                if (similars.size() >= count) {
                    break;
                }

                //System.out.println("CC: " + sim);
            }

        }

        return CommonUtils.sortEntityMap(similars);
    }

    public String[] getEntityContents(String entity) throws IOException {

        char startingChar = entity.charAt(0);
        long startingIndex = pointerMappings.get(startingChar + "");

        raf.seek(startingIndex);
        String line = "";
        while ((line = raf.readUTF()) != null) {

            if (line.equals("#end") || line.charAt(0) != startingChar) {
                break;
            }

            String[] contents = line.split(" ");
            String curEn = contents[0];
            String currEnURI = contents[1];

            if (curEn.equals(entity) || currEnURI.equals(entity)) {
                resetPtr();
                return contents;
            }

        }

        resetPtr();
        return getLevenshteinEntity(entity);
    }

    private void resetPtr() throws IOException {
        raf.seek(0);
    }

    public String toUTF() throws IOException {
        raf.seek(0);
        String res = "";
        String line = "";
        while ((line = raf.readUTF()) != null) {
            res += line + "";
            //System.out.println(line);

            if (line.equals("#end")) {
                break;
            }

        }

        return res;
    }

    public String[] getLevenshteinEntity(String entity) throws IOException {

        String[] closestURIContents = null;
        String line = "";
        int dist = Integer.MAX_VALUE;
        char startingChar = entity.charAt(0);
        long startingIndex = pointerMappings.get(startingChar + "");

        resetPtr();
        while ((line = raf.readUTF()) != null) {

            if (line.equals("#end")) {
                break;
            }

            String[] contents = line.split(" ");
            String curEn = contents[0];

            int currDist = StringUtils.getLevenshteinDistance(curEn, entity);
            if (currDist < dist) {
                dist = currDist;
                closestURIContents = contents;
            }

        }

        return closestURIContents;
    }

}
