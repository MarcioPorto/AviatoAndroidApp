package com.aviato.android.aviato.beacons;

public class BeaconData {
    private String uuid;
    private String major;
    private String minor;
    private String proximity;

    public String printBeaconData() {
        return "timestamp: " + getTimestamp() + " proximity: " + getProximity() + " uuid: " + getUuid()
                + " major - minor: " + getMajor() + " - " + getMinor();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getProximity() {
        return proximity;
    }

    public void setProximity(String proximity) {
        this.proximity = proximity;
    }
}
