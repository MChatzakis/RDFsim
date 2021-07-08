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
public class Entity {

    private String URI;
    private double [] vector;
    
    ArrayList<Triple>triples;
    
    public Entity(String URI) {
        this.URI = URI;
    }

    public String toString() {
        return this.getURI();
    }

    public static String getEntitiesAsString(Collection<Entity> entities) {
        String res = "";
        for (Entity t : entities) {
            //System.out.println(t.toString() + "\n");
            res += t.toString() + "\n";
        }

        return res;
    }
}
