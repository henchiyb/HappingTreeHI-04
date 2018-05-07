package vn.edu.khtn.googlemapsforseminar02.utils;

import java.util.List;

import io.realm.Realm;
import vn.edu.khtn.googlemapsforseminar02.objects.HistoryObject;

/**
 * Created by Nhan on 10/31/2016.
 */

public class RealmHandler {
    private static RealmHandler instance;
    private Realm realm;

    public static RealmHandler getInstance() {
        if (instance == null)
            instance = new RealmHandler();
        return instance;
    }

    private RealmHandler() {
        this.realm = Realm.getDefaultInstance();
    }

    public void addHistoryToRealm(HistoryObject historyObject) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(historyObject);
        realm.commitTransaction();
    }

    public List<HistoryObject> getListHistoryInRealm() {
        return realm.where(HistoryObject.class).findAll();
    }

    public HistoryObject findHistoryByID(int id){
        return realm.where(HistoryObject.class).equalTo("id", id).findFirst();
    }
}