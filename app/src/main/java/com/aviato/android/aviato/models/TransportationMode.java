package com.aviato.android.aviato.models;

import com.aviato.android.aviato.R;

public class TransportationMode {

    private String mTransportType;
    private int mTripTime;

    public TransportationMode() {}

    public TransportationMode(String transportType, int tripTime) {
        mTransportType = transportType;
        mTripTime = tripTime;
    }

    public String getTransportType() {
        return mTransportType;
    }

    public void setTransportType(String transportType) {
        mTransportType = transportType;
    }

    public int getTripTime() {
        return mTripTime;
    }

    public void setTripTime(int tripTime) {
        mTripTime = tripTime;
    }

    /*
        This method finds the right icon for each different type of transport
     */
    public int getIconId(String transportType) {

        // TODO: Complete this by adding the appropriate transport types

        // This is a default value
        int iconId = R.mipmap.ic_directions_car_white_48dp;

        if (transportType.equals("Car")) {
            iconId = R.mipmap.ic_directions_car_white_48dp;
        }
        else if (transportType.equals("Transit")) {
            iconId = R.mipmap.ic_directions_transit_white_48dp;
        }
        else if (transportType.equals("Walk")) {
            iconId = R.mipmap.ic_directions_walk_white_48dp;
        }
        else if (transportType.equals("Bicycle")) {
            iconId = R.mipmap.ic_directions_bike_white_48dp;
        }

        return iconId;
    }

}
