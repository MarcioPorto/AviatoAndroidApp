package com.aviato.android.aviato.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import java.util.HashMap;

public class Constants {

    public static ParseObject CURRENT_USER;

    public static int BUFFER_VALUE = 20;

    public static int TRANSPORT_VALUE = 0;

    public static int USERS_IN_CHECK_IN = 0;
    public static int USERS_IN_SECURITY = 0;
    public static int USERS_IN_LOUNGE = 0;
    public static int USERS_IN_GATE = 0;

    public static int CHECK_IN_VALUE = 0;
    public static int SECURITY_VALUE = 0;
    public static int LOUNGE_VALUE = 0;
    public static int GATE_VALUE = 0;

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    /**
     * Map for storing information about checkpoint location.
     */
    public static HashMap<String, LatLng> CHECKPOINT_LOCATIONS = new HashMap<>();

//    static {
//        // Changi International Airport.
//        CHECKPOINT_LOCATIONS.put("SIN", new LatLng(1.3592, 103.9894));
//    }

}
