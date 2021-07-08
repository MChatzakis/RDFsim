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
public class Entity {

    private String URI;
    private double[] vector;

    ArrayList<Triple> triples;

    public Entity(String URI) {
        this.URI = URI;
    }

    public String toString() {
        return this.getURI();
    }

    public static HashMap<String, Entity> loadEntitiesFromFile(String filepath) {
        HashMap<String, Entity> entities = new HashMap<>();
        try {
            File f = new File(filepath);
            Scanner scanner = new Scanner(f);

            while (scanner.hasNext()) {
                String ent = scanner.next();
                entities.put(ent, new Entity(ent));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return entities;
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
