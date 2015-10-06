package com.aviato.android.aviato.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aviato.android.aviato.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CarMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    boolean mapReady=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

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
        LatLng userLocation = new LatLng(1.3000, 103.8000);     // get values based on GPS
        LatLng airportLocation = new LatLng(1.3592, 103.9894);  // get values based on flight information

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

    }
}
