package com.aviato.android.aviato.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aviato.android.aviato.R;
import com.aviato.android.aviato.adapters.TransportationModeAdapter;
import com.aviato.android.aviato.models.TransportationMode;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TransportationModeActivity extends Activity {

    public static final String TAG = TransportationModeActivity.class.getSimpleName();

    private ListView mListView;
    private TextView mEmptyText;

    private TransportationMode[] mTripOptions = new TransportationMode[2];

    public double mUserLatitude;
    public double mUserLongitude;

    public double mDestinationLatitude;
    public double mDestinationLongitude;

    public TransportationMode carMode = new TransportationMode();
    public TransportationMode transitMode = new TransportationMode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation_mode);

        Intent intent = getIntent();
        mUserLatitude = intent.getDoubleExtra("userLatitude", 0.0);
        mUserLongitude = intent.getDoubleExtra("userLongitude", 0.0);
        mDestinationLatitude = intent.getDoubleExtra("destinationLatitude", 0.0);
        mDestinationLongitude = intent.getDoubleExtra("destinationLongitude", 0.0);

        mListView = (ListView) findViewById(R.id.list_view);

        carMode.setTransportType("Car");
        transitMode.setTransportType("Transit");

        mTripOptions[0] = carMode;
        mTripOptions[1] = transitMode;

        if (mTripOptions == null) {
            mEmptyText = (TextView)findViewById(R.id.empty);
            mEmptyText.setVisibility(View.VISIBLE);
        }

        getTripOptionsInformation("car");
        getTripOptionsInformation("transit");

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transportation_mode, menu);
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

    private void getTripOptionsInformation(final String transportMode) {

        String origin = Double.toString(mUserLatitude) + "," + Double.toString(mUserLongitude);
        String destination = Double.toString(mDestinationLatitude) + "," + Double.toString(mDestinationLongitude);
        String apiKey = "AIzaSyBAXuIbW7Hn07sggLJZuG3v_Uwu7gQaPcU";
        String arrivalTime = "1444305043";

        String apiURL = "";

        int i = 0;
        while (i < 4) {
            if (i == 0) {
                apiURL = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + origin +
                        "&destination=" + destination +
                        "&arrival_time=" + arrivalTime +
                        "&mode=driving" +
                        "&key=" + apiKey;
            } else if (i == 1) {
                apiURL = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + origin +
                        "&destination=" + destination +
                        "&arrival_time=" + arrivalTime +
                        "&mode=transit" +
                        "&key=" + apiKey;
            }

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
                        Toast.makeText(TransportationModeActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            Log.v(TAG, jsonData);
                            if (response.isSuccessful()) {
                                // final TransportationMode mode = getTripOptions(jsonData);
                                final String test = getTripOptionsTest(jsonData, transportMode);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Toast.makeText(TransportationModeActivity.this,
//                                                test,
//                                                Toast.LENGTH_LONG).show();
                                        // updateDisplay(mode, transportMode);
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

            i++;
        }

        TransportationModeAdapter adapter = new TransportationModeAdapter(this, mTripOptions);
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typesOfTransportation);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Handle the case when an item is clicked

                //Get the String value of the item where the user clicked
                TransportationMode item = (TransportationMode) mListView.getItemAtPosition(position);

                if (item.getTransportType().equals("Car")) {
                    Intent intent = new Intent(getApplicationContext(), CarMapActivity.class);
                    intent.putExtra("userLatitude", mUserLatitude);
                    intent.putExtra("userLongitude", mUserLongitude);
                    intent.putExtra("destinationLatitude", mDestinationLatitude);
                    intent.putExtra("destinationLongitude", mDestinationLongitude);
                    startActivity(intent);
                }
            }
        });

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

    private void updateDisplay(TransportationMode mode, String type) {

        switch (type) {
            case "car":
                carMode.setTripTime(mode.getTripTime());
                break;
            case "transit":
                transitMode.setTripTime(mode.getTripTime());
                break;
        }

    }

    private TransportationMode getTripOptions(String jsonData) throws JSONException {

        JSONObject response = new JSONObject(jsonData);
        String status = response.getString("status");

        int counter = 0;
        TransportationMode mode = new TransportationMode();

        JSONArray routes = response.getJSONArray("routes");

        for (int i = 0; i < routes.length(); i++) {
            JSONObject currentRoute = routes.getJSONObject(i);
            JSONArray legs = currentRoute.getJSONArray("legs");

            for (int j = 0; j < legs.length(); j++) {
                JSONObject currentLeg = legs.getJSONObject(j);
                JSONObject duration = currentLeg.getJSONObject("duration");
                counter += duration.getInt("value");
            }
        }

        mode.setTripTime(counter/60);
        return mode;
    }

    private String getTripOptionsTest(String jsonData, String type) throws JSONException {

        JSONObject response = new JSONObject(jsonData);
        String status = response.getString("status");

        if (!status.equals("OK")) {
            switch (type) {
                case "car":
                    mTripOptions[0].setTripTime(000);
                    break;
                case "transit":
                    mTripOptions[1].setTripTime(000);
                    break;
            }
            return "Problem.";
        }

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

        counter /= 60;

        switch (type) {
            case "car":
                mTripOptions[0].setTripTime(counter);
                break;
            case "transit":
                mTripOptions[1].setTripTime(counter);
                break;
        }

        return String.valueOf(counter);

    }

}
