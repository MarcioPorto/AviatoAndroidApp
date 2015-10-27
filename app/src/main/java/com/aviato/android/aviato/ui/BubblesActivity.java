package com.aviato.android.aviato.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aviato.android.aviato.R;
import com.aviato.android.aviato.beacons.BeaconData;
import com.aviato.android.aviato.beacons.BeaconHelper;
import com.aviato.android.aviato.beacons.BeaconScannerApp;
import com.aviato.android.aviato.beacons.LocationManager;
import com.aviato.android.aviato.beacons.WaitingTimeEstimator;
import com.aviato.android.aviato.models.Constants;
import com.aviato.android.aviato.models.GeofenceErrorMessages;
import com.aviato.android.aviato.models.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BubblesActivity extends Activity implements BeaconConsumer, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    public static final String TAG = BubblesActivity.class.getSimpleName();

    // Constant Declaration
    private static final String PREFERENCE_SCANINTERVAL = "scanInterval";
    private static final String PREFERENCE_TIMESTAMP = "timestamp";
    private static final String PREFERENCE_POWER = "power";
    private static final String PREFERENCE_PROXIMITY = "proximity";
    private static final String PREFERENCE_RSSI = "rssi";
    private static final String PREFERENCE_MAJORMINOR = "majorMinor";
    private static final String PREFERENCE_UUID = "uuid";
    private static final String PREFERENCE_INDEX = "index";
    private static final String PREFERENCE_LOCATION = "location";
    private static final String PREFERENCE_REALTIME = "realTimeLog";
    private static final String MODE_SCANNING = "Stop";
    private static final String MODE_STOPPED = "Scan";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private BeaconManager beaconManager;
    private Region region;
    private int eventNum = 1;

    // This StringBuffer will hold the scan data for any given scan.
    private StringBuffer logString;

    // Preferences - will actually have a boolean value when loaded.
    private Boolean index;
    private Boolean location;
    private Boolean uuid;
    private Boolean majorMinor;
    private Boolean rssi;
    private Boolean proximity;
    private Boolean power;
    private Boolean timestamp;
    private String scanInterval;
    // Added following a feature request from D.Schmid.
    private Boolean realTimeLog;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public double mUserLatitude;
    public double mUserLongitude;

    public double mDestinationLatitude;
    public double mDestinationLongitude;

    private ImageView mTransportationBubble;
    private ImageView mCheckInBubble;
    private ImageView mSecurityBubble;
    private ImageView mLoungeBubble;
    private ImageView mGateBubble;
    private ImageView mBufferBubble;

    private TextView mTransportValue;
    private TextView mCheckInValue;
    private TextView mSecurityValue;
    private TextView mGateValue;
    private TextView mLoungeValue;
    private TextView mBufferValue;

    private TextView mLeavingTime;

    private Button mScanButton;

    protected ArrayList<Geofence> mGeofenceList;

    public int mUsersInTransit = 0;

    private boolean mIsTransit = false;

    public LocationManager locationManager = new LocationManager();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubbles);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mTransportationBubble = (ImageView)findViewById(R.id.transport_bubble);
        mCheckInBubble = (ImageView)findViewById(R.id.checkin_bubble);
        mSecurityBubble = (ImageView)findViewById(R.id.immigration_bubble);
        mLoungeBubble = (ImageView)findViewById(R.id.security_bubble);
        mGateBubble = (ImageView)findViewById(R.id.gate_bubble);
        mBufferBubble = (ImageView)findViewById(R.id.buffer_bubble);

        mTransportValue = (TextView)findViewById(R.id.transport_value);
        mCheckInValue = (TextView)findViewById(R.id.checkin_value);
        mSecurityValue = (TextView)findViewById(R.id.immigration_value);
        mGateValue = (TextView)findViewById((R.id.security_value));
        mLoungeValue = (TextView)findViewById(R.id.gate_value);
        mBufferValue = (TextView)findViewById(R.id.buffer_value);
        mLeavingTime = (TextView)findViewById(R.id.leavingTime);

        mTransportValue.setText(0 + "");
        mCheckInValue.setText(0 + "");
        mSecurityValue.setText(0 + "");
        mGateValue.setText(0 + "");
        mLoungeValue.setText(0 + "");
        mBufferValue.setText(Constants.BUFFER_VALUE + "");

        mScanButton = (Button)findViewById(R.id.scanButton);
        mScanButton.setText(MODE_STOPPED);

        mGeofenceList = new ArrayList<Geofence>();

        try {
            Intent prevIntent = getIntent();
            mIsTransit = prevIntent.getBooleanExtra("isTransit", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // I will hardcode this for now, but can gather information from flights API
        mDestinationLatitude = 1.3592;
        mDestinationLongitude = 103.9894;
        Constants.CHECKPOINT_LOCATIONS.put("SIN", new LatLng(mDestinationLatitude, mDestinationLongitude));

        verifyBluetooth();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        BeaconScannerApp app = (BeaconScannerApp)this.getApplication();
        beaconManager = app.getBeaconManager();
        // beaconManager.setForegroundScanPeriod(10);
        region = app.getRegion();
        beaconManager.bind(this);

        populateGeofenceList();

        fillBubbleValues(10, Constants.USERS_IN_CHECK_IN, 25, 3, 1, "Check In Desk");
        fillBubbleValues(10, Constants.USERS_IN_SECURITY, 25, 3, 1, "Security Zone");
        fillBubbleValues(10, Constants.USERS_IN_LOUNGE, 25, 3, 1, "Lounge");
        fillBubbleValues(10, Constants.USERS_IN_GATE, 25, 3, 1, "Gate");

        updateBubblesSize("Transit");
        updateBubblesSize("Check In Desk");
        updateBubblesSize("Security Zone");
        updateBubblesSize("Lounge");
        updateBubblesSize("Gate");
        updateBufferSize();

        mTransportationBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BubblesActivity.this, TransportationModeActivity.class);
                intent.putExtra("userLatitude", mUserLatitude);
                intent.putExtra("userLongitude", mUserLongitude);
                intent.putExtra("destinationLatitude", mDestinationLatitude);
                intent.putExtra("destinationLongitude", mDestinationLongitude);
                startActivity(intent);
            }
        });

        mBufferBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.BUFFER_VALUE += 10;
                updateBufferSize();
                mBufferValue.setText(Constants.BUFFER_VALUE + "");
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScanState();
            }
        });
        startScanning(mScanButton);

        Toast.makeText(this, "Please wait while we calibrate your trip time based on your current location.", Toast.LENGTH_SHORT).show();

        updateLocations();

    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    public void updateLocations(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("NewUser");
        query.whereNotEqualTo("location", "Transit");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                int a = 0;
                int b = 0;
                int c = 0;
                int d = 0;
                if (e == null) {
                    for (ParseObject user : list) {
                        switch (user.get("location").toString()) {
                            case "Checkin Desk":
                                a++;
                                break;
                            case "Security Zone":
                                b++;
                                break;
                            case "Lounge":
                                c++;
                                break;
                            case "Gate":
                                d++;
                                break;
                            case "Transit":
                                Log.i("RIHANNA", "Transit should not be updated here");
                        }
                    }
                    fillBubbleValues(10, Constants.USERS_IN_CHECK_IN, 25, 3, 1, "Check In Desk");
                    fillBubbleValues(10, Constants.USERS_IN_SECURITY, 20, 3, 1, "Security Zone");
                    fillBubbleValues(10, Constants.USERS_IN_LOUNGE, 30, 3, 1, "Lounge");
                    fillBubbleValues(10, Constants.USERS_IN_GATE, 15, 3, 1, "Gate");

                    updateBubblesSize("Check In Desk");
                    updateBubblesSize("Security Zone");
                    updateBubblesSize("Lounge");
                    updateBubblesSize("Gate");
                }
                if (a != Constants.USERS_IN_CHECK_IN) {
                    Constants.USERS_IN_CHECK_IN = a;
                } else if (b != Constants.USERS_IN_SECURITY) {
                    Constants.USERS_IN_SECURITY = b;
                } else if (c != Constants.USERS_IN_LOUNGE) {
                    Constants.USERS_IN_LOUNGE = c;
                } else if (d != Constants.USERS_IN_GATE) {
                    Constants.USERS_IN_GATE = d;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bubbles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   // Will probably want to change this
        mLocationRequest.setInterval(1000);                                     // Updates location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        addAllGeofences();
    }

    @Override
    public void onLocationChanged(Location location) {
        mUserLatitude = location.getLatitude();
        mUserLongitude = location.getLongitude();

        getTransportationTime();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

         /* Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(getBaseContext(),
                    "Location services not available, cannot track device location.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fillBubbleValues(int a, int b, int c, int d, int e, String f) {

        switch (f) {
            case "Check In Desk":
                mCheckInValue.setText((int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, 2 * d, e) + "");
                Constants.CHECK_IN_VALUE = (int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, 2 * d, e);
                break;
            case "Security Zone":
                mSecurityValue.setText((int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, 1.5 * d, e) + "");
                Constants.SECURITY_VALUE = (int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, 1.5 * d, e);
                break;
            case "Lounge":
                mLoungeValue.setText((int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, 0.6 * d, e) + "");
                Constants.LOUNGE_VALUE = (int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, 0.6 * d, e);
                break;
            case "Gate":
                mGateValue.setText((int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, d, e) + "");
                Constants.GATE_VALUE = (int) WaitingTimeEstimator.estimateWaitingTime(a, b, c, d, e);
                break;
        }

    }

    private void updateBubblesSize(String place) {

        switch (place) {
            case "Transit":
                if (mIsTransit) {
                    mTransportationBubble.getLayoutParams().height = 86 + 70;
                    mTransportationBubble.getLayoutParams().width = 86 + 70;
                } else {
                    mTransportationBubble.getLayoutParams().height = (Constants.TRANSPORT_VALUE) + 60;
                    mTransportationBubble.getLayoutParams().width = (Constants.TRANSPORT_VALUE) + 60;
                }
                break;
            case "Check In Desk":
                mCheckInBubble.getLayoutParams().height = (Constants.USERS_IN_CHECK_IN * 4) + 60;
                mCheckInBubble.getLayoutParams().width = (Constants.USERS_IN_CHECK_IN * 4) + 60;
                break;
            case "Security Zone":
                mSecurityBubble.getLayoutParams().height = (Constants.USERS_IN_SECURITY * 4) + 60;
                mSecurityBubble.getLayoutParams().width = (Constants.USERS_IN_SECURITY * 4) + 60;
                break;
            case "Lounge":
                mLoungeBubble.getLayoutParams().height = (Constants.USERS_IN_LOUNGE * 4) + 60;
                mLoungeBubble.getLayoutParams().width = (Constants.USERS_IN_LOUNGE * 4) + 60;
                break;
            case "Gate":
                mGateBubble.getLayoutParams().height = (Constants.USERS_IN_GATE * 4) + 60;
                mGateBubble.getLayoutParams().width = (Constants.USERS_IN_GATE * 4) + 60;
                break;
        }

        if (mIsTransit) {
            mLeavingTime.setText(getTimeToFlight(86,
                    Constants.CHECK_IN_VALUE,
                    Constants.SECURITY_VALUE,
                    Constants.LOUNGE_VALUE,
                    Constants.GATE_VALUE,
                    Constants.BUFFER_VALUE));
        } else {
            mLeavingTime.setText(getTimeToFlight(Constants.TRANSPORT_VALUE,
                    Constants.CHECK_IN_VALUE,
                    Constants.SECURITY_VALUE,
                    Constants.LOUNGE_VALUE,
                    Constants.GATE_VALUE,
                    Constants.BUFFER_VALUE));
        }

    }

    private void updateBufferSize() {
        mBufferBubble.getLayoutParams().height = (Constants.BUFFER_VALUE) + 60;
        mBufferBubble.getLayoutParams().width = (Constants.BUFFER_VALUE) + 60;
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.CHECKPOINT_LOCATIONS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_HOURS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addAllGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
//            Toast.makeText(
//                    this,
//                    "Geofences Added",
//                    Toast.LENGTH_SHORT
//            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private void getTransportationTime() {

        String origin = Double.toString(mUserLatitude) + "," + Double.toString(mUserLongitude);
        String destination = Double.toString(mDestinationLatitude) + "," + Double.toString(mDestinationLongitude);
        String apiKey = "@string/google_maps_key";
        String arrivalTime = "1444305043";

        // Mode already defaults to driving
        String apiURL = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin +
                "&destination=" + destination +
                "&arrival_time=" + arrivalTime +
                "&key=" + apiKey;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiURL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // TODO: Handle this later
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final String transportationValue = getTransportationValue(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mIsTransit) {
                                        mTransportValue.setText("86");
                                    } else {
//                                        if (Constants.CURRENT_USER.get("location").equals("Checkin Desk")) {
//                                            mTransportValue.setText(0);
//                                        } else {
                                        mTransportValue.setText(transportationValue);
//                                        }
                                    }
                                    Constants.TRANSPORT_VALUE = Integer.valueOf(transportationValue);
                                    updateBubblesSize("Transit");
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        else {
            Toast.makeText(this, "Network is unavailable",
                    Toast.LENGTH_LONG).show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There was an error processing your request").setTitle("Oops!");
        builder.create();
    }

    private String getTransportationValue(String jsonData) throws JSONException {

        JSONObject response = new JSONObject(jsonData);

        int counter = 0;

        JSONArray routes = response.getJSONArray("routes");

        for (int i = 0; i < routes.length(); i++) {
            JSONObject currentRoute = routes.getJSONObject(i);
            JSONArray legs = currentRoute.getJSONArray("legs");

            for (int j = 0; j < legs.length(); j++) {
                JSONObject currentLeg = legs.getJSONObject(i);
                JSONObject duration = currentLeg.getJSONObject("duration");
                counter += duration.getInt("value");
            }
        }

        return (counter / 60) + "";
    }

    @Override
    public void onBeaconServiceConnect() {}

    /**
     * start looking for beacons.
     */
    private void startScanning(Button scanButton) {

        // Reset event counter
        eventNum = 1;
        // Get current values for logging preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        HashMap<String, Object> prefs = new HashMap<String, Object>();
        prefs.putAll(sharedPrefs.getAll());

        index = (Boolean)prefs.get(PREFERENCE_INDEX);
        location = (Boolean)prefs.get(PREFERENCE_LOCATION);
        uuid = (Boolean)prefs.get(PREFERENCE_UUID);
        majorMinor = (Boolean)prefs.get(PREFERENCE_MAJORMINOR);
        rssi = (Boolean)prefs.get(PREFERENCE_RSSI);
        proximity = (Boolean)prefs.get(PREFERENCE_PROXIMITY);
        power = (Boolean)prefs.get(PREFERENCE_POWER);
        timestamp = (Boolean)prefs.get(PREFERENCE_TIMESTAMP);
        scanInterval = (String)prefs.get(PREFERENCE_SCANINTERVAL);
        realTimeLog = (Boolean)prefs.get(PREFERENCE_REALTIME);

        // Get current background scan interval (if specified)
        if (prefs.get(PREFERENCE_SCANINTERVAL) != null) {
            beaconManager.setBackgroundBetweenScanPeriod(Long.parseLong(scanInterval));
        }

        logToDisplay("Scanning...");

        // Initialise scan log
        logString = new StringBuffer();

        //Start scanning again. This can be seen as one scan
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Iterator<Beacon> beaconIterator = beacons.iterator();
                    while (beaconIterator.hasNext()) {
                        Beacon beacon = beaconIterator.next();
                        BeaconData bD = extractBeaconData(beacon);
                        logToDisplay(bD.printBeaconData());
                        if (bD.getProximity().equals("Immediate")) {

                            try {
                                locationManager.notifyServer(bD.getMajor());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            updateLocations();

                            // if (bD.getMajor().toString().equals("1000")) {}

                            // TODO - update server
                            // TODO - potentially determine location in app based on major value
                        }

                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    /**
     * Stop looking for beacons.
     */
    private void stopScanning(Button scanButton) {
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private BeaconData extractBeaconData(Beacon beacon) { //when is this done?
        BeaconData bD = new BeaconData();
        StringBuilder scanString = new StringBuilder();

        if (index) {
            scanString.append(eventNum++);
        }
        //NO NEED TO LOOK HERE, CONTINUE AT ELSE STATEMENT BELOW
        if (beacon.getServiceUuid() == 0xfeaa) {
            Log.e("ERROR", "This is not a beacon the app can deal with");
        } else {
            // Just an old fashioned iBeacon or AltBeacon...
            return extractGenericBeacon(scanString, beacon); // This is what we're dealing with in case of sticknfind
        }

        logToDisplay(scanString.toString());
        scanString.append("\n");

        logString.append(scanString.toString());
        return bD;
    }

    /**
     * Logs iBeacon & AltBeacon data.
     */
    private BeaconData extractGenericBeacon(StringBuilder scanString, Beacon beacon) {
        BeaconData bD = new BeaconData();
        if (location) {
            scanString.append(" Location: ").append(Double.toString(mUserLatitude) + "," +
                    Double.toString(mUserLongitude) + " ");
        }

        if (uuid) { //This is where we need to start looking!
            bD.setUuid(beacon.getId1().toString());
        }

        if (majorMinor) {
            if (beacon.getId2() != null) {
                bD.setMajor(beacon.getId2().toString());
            }
            scanString.append("-");
            if (beacon.getId3() != null) {
                bD.setMinor(beacon.getId3().toString());
            }
        }

        if (rssi) {
            // No need
        }

        if (proximity) {
            bD.setProximity(BeaconHelper.getProximityString(beacon.getDistance()));
        }

        if (power) {
            // No need
        }

        if (timestamp) {
            bD.setTimestamp(BeaconHelper.getCurrentTimeStamp());
        }
        return bD;
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
//                Toast.makeText(BubblesActivity.this, line, Toast.LENGTH_LONG).show();
//                if (Constants.CURRENT_USER.get("location").equals("Checkin Desk")) {
//                    mTransportValue.setText(0);
//                }
            }
        });
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please enable bluetooth in settings and restart this application.").setTitle("Bluetooth not enabled");
                builder.create();
            }
        }
        catch (RuntimeException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sorry, this device does not support Bluetooth LE.").setTitle("Bluetooth LE not available");
            builder.create();
        }

    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                 /*
                  * TODO - Try the request again
                  */
                        break;
                }
        }
    }

    private void toggleScanState() {
        String currentState = mScanButton.getText().toString();
        if (currentState.equals(MODE_SCANNING)) {
            stopScanning(mScanButton);
        } else {
            startScanning(mScanButton);
        }
    }

    private String getTimeToFlight(int transport, int checkin, int security, int lounge, int gate, int buffer) {

        int hours;
        int minutes;
        String time = " PM";

        int total_min = transport + checkin + security + lounge + gate + buffer;
        int wait_hours = total_min / 60;
        int wait_min = total_min % 60;

        // Get minutes
        if (wait_min > 45) {
            minutes = 105 - wait_min;
            wait_hours += 1;
        } else {
            minutes = 45 - wait_min;
        }

        // Get hours
        if (wait_hours > 2) {
            wait_hours -= 3;
            hours = 12 - wait_hours;
        } else {
            hours = 3 - wait_hours;
        }

        if (wait_hours > 3) {
            time = " AM";
        }

        String hourStr = Integer.toString(hours + 3);
        String minutesStr = Integer.toString(minutes);
        if (minutes < 10) {
            minutesStr = "0" + minutesStr;
        }

        return hourStr  + ":" + minutesStr + time;

    }

}

