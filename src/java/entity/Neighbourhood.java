/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author Jesper
 */
public class Neighbourhood {
    String neighbourhoodName;
    String locker;

    public Neighbourhood(String neighbourhoodName, String locker) {
        this.neighbourhoodName = neighbourhoodName;
        this.locker = locker;
    }

    public String getNeighbourhoodName() {
        return neighbourhoodName;
    }

    public void setNeighbourhoodName(String neighbourhoodName) {
        this.neighbourhoodName = neighbourhoodName;
    }

    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        this.locker = locker;
    }
    
    
}
