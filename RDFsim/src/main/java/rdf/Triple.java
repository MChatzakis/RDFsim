/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import lombok.Data;

/**
 *
 * @author Manos Chatzakis
 */
@Data
public class Triple {

    private String s;
    private String p;
    private String o;

    public Triple(String s, String p, String o) {
        this.s = s;
        this.p = p;
        this.o = o;
    }

    public String toString() {
        return s + " " + p + " " + o;
    }

    public static String produceTripleVocabulary(ArrayList<Triple> triples) {
        String result = "";
        for (Triple t : triples) {
            result += t.toString() + "  .\n";
        }
        return result;
    }

    public static void printTriples(Collection<Triple> triples) {
        for (Triple t : triples) {
            System.out.println(t.toString() + "\n");
        }
    }

    public static ArrayList<Triple> loadTriplesFromFile(String filepath) {
        ArrayList<Triple> triples = new ArrayList<>();
        try {
            File f = new File(filepath);
            Scanner scanner = new Scanner(f);

            while (scanner.hasNext()) {
                String s = scanner.next();
                String p = scanner.next();
                String o = scanner.next();

                triples.add(new Triple(s, p, o));

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return triples;
    }
}
