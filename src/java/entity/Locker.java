/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author Jesper/Jerome
 */
public class Locker {

    private int id;
    private String cluster;
    private String locker_no;
    private String taken_by;
    private String neighbourhood;

    public Locker(int id, String cluster, String locker_no, String taken_by, String neighbourhood) {
        this.id = id;
        this.cluster = cluster;
        this.locker_no = locker_no;
        this.taken_by = taken_by;
        this.neighbourhood = neighbourhood;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getLocker_no() {
        return locker_no;
    }

    public void setLocker_no(String locker_no) {
        this.locker_no = locker_no;
    }

    public String getTaken_by() {
        return taken_by;
    }

    public void setTaken_by(String taken_by) {
        this.taken_by = taken_by;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

}
