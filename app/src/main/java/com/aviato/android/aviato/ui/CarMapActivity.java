package com.aviato.android.aviato.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aviato.android.aviato.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class CarMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = CarMapActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    boolean mapReady=false;

    public double mUserLatitude;
    public double mUserLongitude;

    public double mDestinationLatitude;
    public double mDestinationLongitude;

    private ArrayList<Double> mRouteLegs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_map);

        Intent intent = getIntent();
        mUserLatitude = intent.getDoubleExtra("userLatitude", 0.0);
        mUserLongitude = intent.getDoubleExtra("userLongitude", 0.0);
        mDestinationLatitude = intent.getDoubleExtra("destinationLatitude", 0.0);
        mDestinationLongitude = intent.getDoubleExtra("destinationLongitude", 0.0);

        getTransportation();

//        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        setUpMapIfNeeded();

//        Button btnMap = (Button) findViewById(R.id.btnMap);
//        btnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mapReady)
//                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            }
//        });
//
//        Button btnSatellite = (Button) findViewById(R.id.btnSatellite);
//        btnSatellite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mapReady)
//                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//            }
//        });
//
//        Button btnHybrid = (Button) findViewById(R.id.btnHybrid);
//        btnHybrid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mapReady)
//                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//            }
//        });

    }

    @Override
    public void onMapReady(GoogleMap map){
        mapReady = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm_car_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            Intent intent = new Intent(this, BubblesActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng userLocation = new LatLng(mUserLatitude, mUserLongitude);     // get values based on GPS
        LatLng airportLocation = new LatLng(mDestinationLatitude, mDestinationLongitude);  // get values based on flight information

        // Add icons if we want
        mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("User")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_black_36dp)));
        mMap.addMarker(new MarkerOptions()
                .position(airportLocation)
                .title("Airport")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_local_airport_black_36dp)));

        // Consider adding bearing or tilt
        CameraPosition target = CameraPosition.builder().target(userLocation).zoom(14).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        // If we want animation:
        // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 1000, null);

        for (int i = 0; i < mRouteLegs.size(); i += 4) {
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(mRouteLegs.get(i), mRouteLegs.get(i+1)), new LatLng(mRouteLegs.get(i+2), mRouteLegs.get(i+3)))
                    .width(5)
                    .color(Color.RED));
        }

    }

    private void getTransportation() {

        String origin = Double.toString(mUserLatitude) + "," + Double.toString(mUserLongitude);
        String destination = Double.toString(mDestinationLatitude) + "," + Double.toString(mDestinationLongitude);
        String apiKey = "AIzaSyBAXuIbW7Hn07sggLJZuG3v_Uwu7gQaPcU";
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
                    Toast.makeText(CarMapActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final String transportationValue = getTransportationValue(jsonData);
                            // return getDirections(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(CarMapActivity.this, transportationValue, Toast.LENGTH_LONG).show();
                                    updateDisplay();
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
        String status = response.getString("status");

        String returnString = "";

        JSONArray routes = response.getJSONArray("routes");

        for (int i = 0; i < routes.length(); i++) {
            JSONObject currentRoute = routes.getJSONObject(i);
            JSONArray legs = currentRoute.getJSONArray("legs");

            for (int j = 0; j < legs.length(); j++) {
                JSONObject currentLeg = legs.getJSONObject(j);
                JSONArray steps = currentLeg.getJSONArray("steps");

                for (int x = 0; x < steps.length(); x++) {
                    JSONObject currentStep = steps.getJSONObject(x);
                    JSONObject startLocation = currentStep.getJSONObject("start_location");
                    mRouteLegs.add(startLocation.getDouble("lat"));
                    mRouteLegs.add(startLocation.getDouble("lng"));
                    JSONObject endLocation = currentStep.getJSONObject("end_location");
                    mRouteLegs.add(endLocation.getDouble("lat"));
                    mRouteLegs.add(endLocation.getDouble("lng"));

                    returnString += Double.toString(mRouteLegs.get(mRouteLegs.size() - 4)) + "," +
                            Double.toString(mRouteLegs.get(mRouteLegs.size() - 3)) + "," +
                            Double.toString(mRouteLegs.get(mRouteLegs.size() - 2)) + "," +
                            Double.toString(mRouteLegs.get(mRouteLegs.size() - 1)) + "    ";
                }

            }
        }

        return returnString;

    }

    private ArrayList<Double> getDirections(String jsonData) throws JSONException {

        JSONObject response = new JSONObject(jsonData);
        String status = response.getString("status");

        ArrayList<Double> answer = new ArrayList<>();

        JSONArray routes = response.getJSONArray("routes");

        for (int i = 0; i < routes.length(); i++) {
            JSONObject currentRoute = routes.getJSONObject(i);
            JSONArray legs = currentRoute.getJSONArray("legs");

            for (int j = 0; j < legs.length(); j++) {
                JSONObject currentLeg = legs.getJSONObject(j);
                JSONArray steps = currentLeg.getJSONArray("steps");

                for (int x = 0; x < steps.length(); x++) {
                    JSONObject currentStep = steps.getJSONObject(x);
                    JSONObject startLocation = currentStep.getJSONObject("start_location");
                    answer.add(startLocation.getDouble("lat"));
                    answer.add(startLocation.getDouble("lng"));
                    JSONObject endLocation = currentStep.getJSONObject("end_location");
                    answer.add(endLocation.getDouble("lat"));
                    answer.add(endLocation.getDouble("lng"));
                }

            }
        }

        return answer;

    }

    private void updateDisplay() {
        setUpMapIfNeeded();
    }

}
