/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class RafApi {

    private RandomAccessFile raf;

    public RafApi(String filename) throws FileNotFoundException {
        raf = new RandomAccessFile(filename, "rw");
    }

    public String toUTF() throws IOException {
        raf.seek(0);
        String res = "";
        String line = "";
        while ((line = raf.readLine()) != null) {
            res += line;
        }

        return res;
    }

}
