package vn.edu.khtn.googlemapsforseminar02.objects;

import io.realm.RealmObject;

/**
 * Created by 10 pro 64bit on 07-Apr-18.
 */

public class HistoryObject extends RealmObject {
    private TreeObject tree;
    private String dateTimeWater;

    public HistoryObject(TreeObject tree, String dateTimeWater) {
        this.tree = tree;
        this.dateTimeWater = dateTimeWater;
    }

    public HistoryObject() {

    }

    public TreeObject getTree() {
        return tree;
    }

    public void setTree(TreeObject tree) {
        this.tree = tree;
    }

    public String getDateTimeWater() {
        return dateTimeWater;
    }

    public void setDateTimeWater(String dateTimeWater) {
        this.dateTimeWater = dateTimeWater;
    }
}
