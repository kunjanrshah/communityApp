package com.yadav.samaj.model;

import io.realm.RealmList;
import io.realm.RealmObject;


public class ListProfiles extends RealmObject {
    public RealmList<ListProfileData> realmlist;

    public ListProfiles() {
    }
    public ListProfiles(RealmList<ListProfileData> realmlist) {
        this.realmlist = realmlist;
    }
}
