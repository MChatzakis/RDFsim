package utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
                //return (o1.getValue()).compareTo(o2.getValue());
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void mergeFilesToFile(String directory, ArrayList<String> filenames, String target) throws Exception {

        BufferedWriter targetWriter = new BufferedWriter(new FileWriter(target));

        Collections.sort(filenames);

        System.out.println("File Merging Started. Target Destination: " + target);
        System.out.println("Files to be merged: " + filenames.toString());

        for (String filename : filenames) {
            BufferedReader reader = new BufferedReader(new FileReader(directory + "\\" + filename));
            System.out.println("Current File: " + filename);

            String line;
            while ((line = reader.readLine()) != null) {
                targetWriter.write(line);
            }

            reader.close();
        }

        targetWriter.close();
        System.out.println("File Merging Completed.");
    }

    public static void generateTeXTable(String[][] data, String[] header, String filepath) {
        try {
            BufferedWriter texWriter = new BufferedWriter(new FileWriter(filepath));

            texWriter.write("\\begin{center}\n");
            texWriter.write("\\begin{tabular}");

            String cc = "{|| ";
            String tabHeader = "";
            for (int i = 0; i < header.length; i++) {

                if (i != header.length - 1) {
                    cc += "c ";
                    tabHeader += header[i] + " & ";
                } else {
                    cc += "c ||}\n";
                    tabHeader += header[i] + " \\\\ [0.5ex]\n";
                }

            }

            texWriter.write(cc);
            texWriter.write("\\hline\n");
            texWriter.write(tabHeader);
            texWriter.write("\\hline\\hline\n");

            for (int i = 0; i < data.length; i++) {
                String[] contents = data[i];
                for (int k = 0; k < contents.length; k++) {
                    if (k != contents.length - 1) {
                        texWriter.write(contents[k] + " & ");
                    } else {
                        texWriter.write(contents[k] + " \\\\ \n");
                    }
                }

                texWriter.write("\\hline\n");
            }

            texWriter.write("\\end{tabular}\n");
            texWriter.write("\\end{center}\n");

            texWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void generateCSV(String[][] data, String[] header, String filepath) {
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(filepath));
            for (int i = 0; i < header.length; i++) {
                if (i != header.length - 1) {
                    wr.write(header[i] + ",");
                } else {
                    wr.write(header[i] + "\n");
                }
            }

            for (int i = 0; i < data.length; i++) {
                String[] contents = data[i];
                for (int k = 0; k < contents.length; k++) {
                    if (k != contents.length - 1) {
                        wr.write(contents[k] + ",");
                    } else {
                        wr.write(contents[k] + "\n");
                    }
                }
            }

            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCSVtext(String[][] data, String[] header) {
        String dataCSV = "";
        for (int i = 0; i < header.length; i++) {
            if (i != header.length - 1) {
                dataCSV += header[i] + ",";
            } else {
                dataCSV += header[i] + "\n";
            }
        }

        for (int i = 0; i < data.length; i++) {
            String[] contents = data[i];
            for (int k = 0; k < contents.length; k++) {
                if (k != contents.length - 1) {
                    dataCSV += contents[k] + ",";
                } else {
                    dataCSV += contents[k] + "\n";
                }
            }
        }
        return dataCSV;
    }
}
