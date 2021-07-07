/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf;

import lombok.Data;

/**
 *
 * @author Manos Chatzakis
 */
@Data
public class Entity {

    private String URI;
    
    public Entity(String URI){
        this.URI = URI;
    }
    
    public String toString() {
        return this.getURI();
    }
    
}
