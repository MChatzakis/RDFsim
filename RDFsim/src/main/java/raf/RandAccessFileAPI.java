/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import sparql.SPARQLQuery;
import utils.CommonUtils;

/**
 *
 * @author manos
 */
@Data
public class RandAccessFileAPI {
    
    private String path;
    private RandomAccessFile raf;
    private HashMap<String, Long> pointerMappings;
    
    public RandAccessFileAPI(String rafPath, String ptrPath) throws FileNotFoundException, IOException {
        path = rafPath;
        raf = new RandomAccessFile(rafPath, "rw");
        processPrtFile(ptrPath);
    }
    
    public RandAccessFileAPI(String rafPath) throws FileNotFoundException, IOException {
        path = rafPath;
        raf = new RandomAccessFile(rafPath, "rw");
        String ptrPath = rafPath.replace(".txt", "PTR.txt");
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
        String[] contents = getEntityContents(entity);
        HashMap<String, Double> similars = new HashMap<>();
        
        if (contents != null) {
            String rawSimilars = contents[2];
            String[] similarPairs = rawSimilars.split("@");
            
            for (String sim : similarPairs) {
                String[] info = sim.split("%%%");
                if (info.length == 2) {
                    similars.put(info[0], Double.parseDouble(info[1]));
                }
                
                if (similars.size() >= count) {
                    break;
                }
            }
            
        }
        return CommonUtils.sortEntityMap(similars);
    }
    
    public HashMap<String, Double> getSimilarEntitiesOfEntitySequential(String entity, int count) throws IOException {
        String[] contents = getEntityContentsSequential(entity);
        HashMap<String, Double> similars = new HashMap<>();
        
        if (contents != null) {
            String rawSimilars = contents[2];
            String[] similarPairs = rawSimilars.split("@");
            
            for (String sim : similarPairs) {
                String[] info = sim.split("%%%");
                if (info.length == 2) {
                    similars.put(info[0], Double.parseDouble(info[1]));
                }
                
                if (similars.size() >= count) {
                    break;
                }
            }
            
        }
        return CommonUtils.sortEntityMap(similars);
    }
    
    public String getEntityURI(String en) throws IOException {
        return getEntityContents(en)[1];
    }
    
    public String[] getEntityContents(String en) throws IOException {
        
        String entity = SPARQLQuery.formatDBpediaURI(en);
        
        char startingChar = entity.charAt(0);
        
        long startingIndex = 0;
        
        if (pointerMappings.containsKey(startingChar + "")) {
            startingIndex = pointerMappings.get(startingChar + "");
        } else {
            String otherLower = (startingChar + "").toLowerCase();
            String otherUpper = (startingChar + "").toUpperCase();
            
            if (pointerMappings.containsKey(otherLower)) {
                startingIndex = pointerMappings.get(otherLower);
                
            } else if (pointerMappings.containsKey(otherUpper)) {
                startingIndex = pointerMappings.get(otherUpper);
            }
        }
        
        raf.seek(startingIndex);
        String line = "";
        while ((line = raf.readUTF()) != null) {
            //raf.readLine();

            if (line.equals("#end") || line.charAt(0) != startingChar) {
                break;
            }
            
            String[] contents = line.split(" ");
            String curEn = contents[0];
            String currEnURI = contents[1];
            
            if (curEn.equals(entity)) { // could optimize
                resetPtr();
                return contents;
            }
            
        }
        
        resetPtr();
        return getLevenshteinEntity(entity);
    }
    
    public String[] getEntityContentsSequential(String en) throws IOException {
        String entity = SPARQLQuery.formatDBpediaURI(en);
        char startingChar = entity.charAt(0);
        long startingIndex = 0;
        
        raf.seek(startingIndex);
        
        String line = "";
        while ((line = raf.readUTF()) != null) {
            
            if (line.equals("#end")) {
                break;
            }
            
            String[] contents = line.split(" ");
            String curEn = contents[0];
            
            if (curEn.equals(entity)) {
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
    
    public void print() {
        
        try {
            raf.seek(0);
            String line = "";
            while ((line = raf.readUTF()) != null) {
                System.out.println(line);
                if (line.equals("#end")) {
                    break;
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String[] getLevenshteinEntity(String entity) throws IOException {
        
        String[] closestURIContents = null;
        String line = "";
        int dist = Integer.MAX_VALUE;
        char startingChar = entity.charAt(0);

        // long startingIndex = pointerMappings.get(startingChar + "");
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
    
    public void printVocabInfo() throws IOException {
        
        resetPtr();
        
        int count = 0;
        String res = "";
        String line = "";
        while ((line = raf.readUTF()) != null) {
            
            if (line.equals("#end")) {
                break;
            }
            
            String[] contents = line.split(" ");
            String curEn = contents[0];
            String currEnURI = contents[1];
            
            res = "[" + count + "] " + curEn + ",(" + currEnURI + ")\n";
            System.out.println(res);
            count++;
        }
        
        resetPtr();
    }
    
    public void vocabInfoToFile(String path) throws IOException {
        resetPtr();
        BufferedWriter targetWriter = new BufferedWriter(new FileWriter(path));
        
        int count = 0;
        String res = "";
        String line = "";
        while ((line = raf.readUTF()) != null) {
            
            if (line.equals("#end")) {
                break;
            }
            
            String[] contents = line.split(" ");
            String curEn = contents[0];
            String currEnURI = contents[1];
            
            res = "[" + count + "] " + curEn + ",(" + currEnURI + ")\n";
            //System.out.println(res);
            targetWriter.write(res);
            count++;
        }
        
        targetWriter.close();
        resetPtr();
    }
    
    public JSONArray getWordRecomendations(String prefix) throws IOException {
        JSONArray words2recomend = new JSONArray();
        char startingChar = prefix.charAt(0);
        long startingIndex = 0;
        
        if (pointerMappings.containsKey(startingChar + "")) {
            startingIndex = pointerMappings.get(startingChar + "");
        } else {
            String otherLower = (startingChar + "").toLowerCase();
            String otherUpper = (startingChar + "").toUpperCase();
            
            if (pointerMappings.containsKey(otherLower)) {
                startingIndex = pointerMappings.get(otherLower);
                
            } else if (pointerMappings.containsKey(otherUpper)) {
                startingIndex = pointerMappings.get(otherUpper);
            }
        }
        
        raf.seek(startingIndex);
        String line = "";
        while ((line = raf.readUTF()) != null) {
            
            if (line.equals("#end") || line.charAt(0) != startingChar) { //could optimize
                break;
            }
            
            String[] contents = line.split(" ");
            String curEn = contents[0];
            
            if (curEn.startsWith(prefix)) { // could optimize
                words2recomend.put(curEn);
            }
            
        }
        
        resetPtr();
        return words2recomend;
    }
    
    public boolean exists(String en) throws IOException {
        String entity = SPARQLQuery.formatDBpediaURI(en);
        
        char startingChar = entity.charAt(0);
        long startingIndex = 0;
        
        if (pointerMappings.containsKey(startingChar + "")) {
            startingIndex = pointerMappings.get(startingChar + "");
        } else {
            String otherLower = (startingChar + "").toLowerCase();
            String otherUpper = (startingChar + "").toUpperCase();
            
            if (pointerMappings.containsKey(otherLower)) {
                startingIndex = pointerMappings.get(otherLower);
                
            } else if (pointerMappings.containsKey(otherUpper)) {
                startingIndex = pointerMappings.get(otherUpper);
            }
        }
        
        raf.seek(startingIndex);
        String line = "";
        while ((line = raf.readUTF()) != null) {
            
            if (line.equals("#end") || line.charAt(0) != startingChar) {
                break;
            }
            
            String[] contents = line.split(" ");
            String curEn = contents[0];
            
            if (curEn.equals(entity)) { // could optimize
                resetPtr();
                return true;
            }
            
        }
        
        resetPtr();
        return false;
    }
    
    public static void createRAFfromCustomDataset(String inputPath, String filenameRAF, String endpoint, String graph, boolean useGraph) throws FileNotFoundException, IOException {
        /*
        FORMAT:
        endpoint //todo
        prefix //todo
        suffix URI similar1&score1_similar2&score2 ...
        ....
        ....
        
        APLHABETICALLY (I wont sort them)
         */
        RandomAccessFile raf = new RandomAccessFile(filenameRAF, "rw");
        raf.seek(0);
        
        String characterPointerMappings = "";
        
        char currentStartChar = ' ';
        long currentOffset = 0;
        
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(inputPath));
        String line = reader.readLine();
        while (line != null) {
            String[] contents = line.split(" ");
            String currentEntity = contents[0];
            String currentEntityURI = contents[1];
            
            System.out.println("Writing entity " + currentEntity + " to Raf.");
            
            char start = currentEntity.charAt(0);
            
            currentOffset = raf.getFilePointer();
            
            if (start > currentStartChar || currentStartChar == ' ') {
                currentStartChar = start;
                characterPointerMappings += currentStartChar + "," + currentOffset + "\n";
            }
            
            String line2write = currentEntity + " " + currentEntityURI + " ";
            
            HashMap<String, Double> similars = CommonUtils.parseCustomDatasetSimilars(contents[2], "_", "&");
            for (Map.Entry<String, Double> simEntry : similars.entrySet()) {
                line2write += simEntry.getKey() + "%%%" + simEntry.getValue() + "@";
            }
            
            line2write += " \n";
            
            System.out.println(line2write);
            raf.writeUTF(line2write);
            
            line = reader.readLine();
        }
        
        reader.close();
        
        raf.writeUTF("#end");
        raf.close();
        
        CommonUtils.writeStringToFile(characterPointerMappings, filenameRAF.replace(".txt", "PTR.txt"));
        CommonUtils.createJSONConfFile(filenameRAF.replace(".txt", "CONFIG.json"), endpoint, graph, useGraph);
    }
}
