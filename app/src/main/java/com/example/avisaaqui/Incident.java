package com.example.avisaaqui;

public class Incident {
    private int id;
    private int refUser;
    private int refCategory;
    private String latitude;
    private String longitude;
    private String value;
    private boolean active;
    private String dtRegister;

    public Incident(int id, int refUser, int refCategory, String latitude, String longitude, String value, boolean active, String dtRegister) {
        this.id = id;
        this.refUser = refUser;
        this.refCategory = refCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.value = value;
        this.active = active;
        this.dtRegister = dtRegister;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRefUser() {
        return refUser;
    }

    public int getRefCategory() {
        return refCategory;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getValue() {
        return value;
    }

    public boolean isActive() {
        return active;
    }

    public String getDtRegister() {
        return dtRegister;
    }
}