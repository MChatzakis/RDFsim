/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf;

import java.util.ArrayList;
import java.util.Collection;
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

    public static void printTriples(Collection<Triple>triples){
        for(Triple t : triples){
            System.out.println(t.toString() + "\n");
        }
    }
}
