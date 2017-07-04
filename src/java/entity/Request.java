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
public class Request {
    int id;
    String requester;
    String receiver;
    String status;
    String lockerNo;

    public Request(int id, String requester, String receiver, String status, String lockerNo) {
        this.id = id;
        this.requester = requester;
        this.receiver = receiver;
        this.status = status;
        this.lockerNo = lockerNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLockerNo() {
        return lockerNo;
    }

    public void setLockerNo(String lockerNo) {
        this.lockerNo = lockerNo;
    }
    
    
}
