package vn.edu.khtn.googlemapsforseminar02;

import io.realm.RealmObject;

/**
 * Created by 10 pro 64bit on 07-Apr-18.
 */

public class TreeObject extends RealmObject {
    private int id;
    private String name;
    private double positionLat;
    private double positionLong;
    private int imageID;
    private int status;

    public TreeObject() {
    }

    public TreeObject(int id, String name, double positionLat, double positionLong, int imageID) {
        this.id = id;
        this.name = name;
        this.positionLat = positionLat;
        this.positionLong = positionLong;
        this.imageID = imageID;
        this.status = 2;
    }

    public TreeObject(int id, String name, double positionLat, double positionLong, int imageID, int status) {
        this.id = id;
        this.name = name;
        this.positionLat = positionLat;
        this.positionLong = positionLong;
        this.imageID = imageID;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(double positionLat) {
        this.positionLat = positionLat;
    }

    public double getPositionLong() {
        return positionLong;
    }

    public void setPositionLong(double positionLong) {
        this.positionLong = positionLong;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
