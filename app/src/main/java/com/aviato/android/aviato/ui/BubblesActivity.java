package com.aviato.android.aviato.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aviato.android.aviato.R;
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

import java.util.ArrayList;
import java.util.Map;

public class BubblesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    public static final String TAG = BubblesActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView mTestLat;
    private TextView mTestLong;
    public double mUserLatitude;
    public double mUserLongitude;

    public double mDestinationLatitude;
    public double mDestinationLongitude;

    private ImageView mTransportationBubble;
    private ImageView mCheckInBubble;
    private ImageView mImmigrationBubble;
    private ImageView mSecurityBubble;
    private ImageView mGateBubble;

    private TextView mTransportValue;
    private TextView mCheckInValue;
    private TextView mImmigrationValue;
    private TextView mSecurityValue;
    private TextView mGateValue;

    protected ArrayList<Geofence> mGeofenceList;

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
        mImmigrationBubble = (ImageView)findViewById(R.id.immigration_bubble);
        mSecurityBubble = (ImageView)findViewById(R.id.security_bubble);
        mGateBubble = (ImageView)findViewById(R.id.gate_bubble);

        mTransportValue = (TextView)findViewById(R.id.transport_value);
        mCheckInValue = (TextView)findViewById(R.id.checkin_value);
        mImmigrationValue = (TextView)findViewById(R.id.immigration_value);
        mSecurityValue = (TextView)findViewById((R.id.security_value));
        mGateValue = (TextView)findViewById(R.id.gate_value);

        mGeofenceList = new ArrayList<Geofence>();

        mTestLat = (TextView)findViewById(R.id.test_lat);
        mTestLong = (TextView)findViewById(R.id.test_long);

        // I will hardcode this for now, but can gather information from flights API
        mDestinationLatitude = 1.3592;
        mDestinationLongitude = 103.9894;
        Constants.CHECKPOINT_LOCATIONS.put("SIN", new LatLng(1.3592, 103.9894));

        // Maybe substitute this locally at a later point
        populateGeofenceList();

        drawBubbles(100, 50, 70, 50, 80);

        fillBubbleValues(50, 30, 20, 10, 30);

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

        Toast.makeText(this, "Please wait while we calibrate your trip time based on your current location.", Toast.LENGTH_SHORT).show();

        // test_sendNotification("Testing if notifications worked!");

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

        mTestLat.setText(Double.toString(mUserLatitude));
        mTestLong.setText(Double.toString(mUserLongitude));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    private void fillBubbleValues(int a, int b, int c, int d, int e) {
        mTransportValue.setText(a + "");
        mCheckInValue.setText(b + "");
        mImmigrationValue.setText(c + "");
        mGateValue.setText(d + "");
        mSecurityValue.setText(e + "");
    }

    private void drawBubbles(int t, int c, int i, int s, int g) {

        RelativeLayout.LayoutParams transportParams = new RelativeLayout.LayoutParams(t, t);
//        RelativeLayout.LayoutParams checkInParams = new RelativeLayout.LayoutParams(c, c);
//        RelativeLayout.LayoutParams immigrationParams = new RelativeLayout.LayoutParams(i, i);
//        RelativeLayout.LayoutParams securityParams = new RelativeLayout.LayoutParams(s, s);
//        RelativeLayout.LayoutParams gateParams = new RelativeLayout.LayoutParams(g, g);

        mTransportationBubble.setLayoutParams(transportParams);
//        mCheckInBubble.setLayoutParams(checkInParams);
//        mImmigrationBubble.setLayoutParams(immigrationParams);
//        mSecurityBubble.setLayoutParams(securityParams);
//        mGateBubble.setLayoutParams(gateParams);

        // ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(250, 250);
        // mTransportationBubble.setLayoutParams(params);

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
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private void test_sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

}
