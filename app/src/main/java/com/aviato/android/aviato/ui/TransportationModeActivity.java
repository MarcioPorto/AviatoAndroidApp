package com.aviato.android.aviato.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aviato.android.aviato.R;
import com.aviato.android.aviato.adapters.TransportationModeAdapter;
import com.aviato.android.aviato.models.TransportationMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransportationModeActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView mEmptyText;

    // These are just examples
    String[] typesOfTransportation = { "Car", "Bus", "Bike" };
    private TransportationMode[] mTripOptions = new TransportationMode[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation_mode);

        mListView = (ListView) findViewById(R.id.list_view);

        // This is the function that will get data from the API
        // getTripOptionsInformation();

        // TODO: Call this after setting up getting json data
        // mTripOptions = getTripOptions(jsonData)

        if (typesOfTransportation == null) {
            mEmptyText = (TextView)findViewById(R.id.empty);
            mEmptyText.setVisibility(View.VISIBLE);
        }

        TransportationMode dummyMode1 = new TransportationMode("car", 10);
        TransportationMode dummyMode2 = new TransportationMode("bus", 20);

        mTripOptions[0] = dummyMode1;
        mTripOptions[1] = dummyMode2;

        TransportationModeAdapter adapter = new TransportationModeAdapter(this, mTripOptions);
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typesOfTransportation);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Handle the case when an item is clicked
            }
        });

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

    private TransportationMode[] getTripOptions(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        TransportationMode[] tripOptions = new TransportationMode[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonInfo = data.getJSONObject(i);
            TransportationMode mode = new TransportationMode();

            // This is an example of how these will look like. Change to exact names matching JSON data
            mode.setTransportType(jsonInfo.getString("transport_type"));
            mode.setTripTime(jsonInfo.getInt("trip_time"));

            tripOptions[i] = mode;
        }

        return tripOptions;
    }

}
