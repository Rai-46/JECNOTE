package jp.ac.jec.cm0146.jecnote.models;

import com.google.android.gms.maps.model.LatLng;

public class SchoolInfo {
    private String name;
    private LatLng position;
    private String information;

    public LatLng getPosition() {
        return position;
    }

    public SchoolInfo setPosition(LatLng position) {
        this.position = position;
        return this;
    }

    public String getInformation() {
        return information;
    }

    public SchoolInfo setInformation(String information) {
        this.information = information;
        return this;
    }

    public String getName() {
        return name;
    }

    public SchoolInfo setName(String name) {
        this.name = name;
        return this;
    }
}
