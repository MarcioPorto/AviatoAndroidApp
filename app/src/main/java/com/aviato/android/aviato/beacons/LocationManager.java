package com.aviato.android.aviato.beacons;

import android.util.Log;

import com.aviato.android.aviato.models.Constants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by Fisher on 08/10/15.
 */
public class LocationManager {

    public static final String TAG = LocationManager.class.getSimpleName();

    private String currentLocation;
    private String timestamp;

    public LocationManager(){
        this.currentLocation = "Transit";
        this.timestamp = "None";
    }

    public void notifyServer(String major) throws ParseException {
        String tempLoc = parseLocation(major);
        if(!currentLocation.equalsIgnoreCase(tempLoc)){
            ParseQuery query = new ParseQuery("NewUser");
            ParseObject user = query.get(Constants.CURRENT_USER.getObjectId());
            user.put("location", tempLoc);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    // Toast.makeText(BubblesActivity.this, "", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "CAAAAAAAAAAAAAAALLLLLLLLBACK!");
                }
            });
        }
    }

    public String parseLocation(String major){
        if(major.equals("1000")) {
            return "Checkin Desk"; // could also be the sample beacon!
        }
        if(major.equals("1600")) {
            return "Security Zone";
        }
        if(major.equals("1500")) {
            return "Lounge";
        }
        if(major.equals("1200")) {
            return "Gate";
        }
        if(major.equals("2300")) {
            return "Arrivals Hall";
        }
        return "Transit";
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
