/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilsCommon;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author manos
 */
public class Utils {

    public static void writeStringToFile(String data, String filepath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            bw.write(data);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
