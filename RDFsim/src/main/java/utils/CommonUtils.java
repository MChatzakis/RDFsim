package utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * Utilities class
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

    public static void printEntityMap(HashMap<String, Double> map) {
        //map.forEach((key, value) -> System.out.println(key + ":" + value)); //why is Java 7 enabled?????????
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=>" + entry.getValue().toString());
        }
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

    public static JSONObject entityMapToJSON(HashMap<String, Double> map) {
        JSONObject mapObj = new JSONObject();

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + "=>" + entry.getValue().toString());
            mapObj.put(entry.getKey(), entry.getValue().toString());
        }

        return mapObj;
    }
}
