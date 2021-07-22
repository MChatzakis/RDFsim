package utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * Utilities class (Common Methods)
 *
 * @author Manos Chatzakis (chatzakis@ics.forth.gr)
 */
public class CommonUtils {

    public static String writeStringToFile(String data, String filepath) {
        File file = new File(filepath);

        try (FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] bytes = data.getBytes();
            bos.write(bytes);
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public static String getFileContent(String filePath) throws IOException {
        Charset encoding = Charset.defaultCharset();

        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String string = new String(bytes, encoding);

        return string;
    }

    public static void printEntityMap(HashMap<String, Double> map) {
        //map.forEach((key, value) -> System.out.println(key + ":" + value)); //why is Java 7 enabled?????????
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=>" + entry.getValue().toString());
        }
    }

    public static String levenshteinDistance(Collection<String> URIs, String URI) {
        String closestURI = "";
        int dist = Integer.MAX_VALUE;

        for (String curr : URIs) {
            int currDist = StringUtils.getLevenshteinDistance(curr, URI);
            if (currDist < dist) {
                dist = currDist;
                closestURI = curr;
            }
        }

        return closestURI;
    }

    public static JSONObject entityMapToJSON(HashMap<String, Double> map) {
        JSONObject mapObj = new JSONObject();

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + "=>" + entry.getValue().toString());
            mapObj.put(entry.getKey(), entry.getValue().toString());
        }

        return mapObj;
    }

    public static HashMap<String, Double> sortEntityMap(HashMap<String, Double> map) {

        List<Map.Entry<String, Double>> list
                = new LinkedList<Map.Entry<String, Double>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }

}
