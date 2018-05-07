package vn.edu.khtn.googlemapsforseminar02.directions;

/**
 * Created by 10 pro 64bit on 06-Apr-18.
 */

public class Position {
    private String desLat;
    private String desIng;

    public Position(String desLat, String desIng) {
        this.desLat = desLat;
        this.desIng = desIng;
    }

    public Position() {
    }

    public String getDesLat() {
        return desLat;
    }

    public void setDesLat(String desLat) {
        this.desLat = desLat;
    }

    public String getDesIng() {
        return desIng;
    }

    public void setDesIng(String desIng) {
        this.desIng = desIng;
    }
}
