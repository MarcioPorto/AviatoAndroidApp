package com.aviato.android.aviato;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FlightInfoActivity extends AppCompatActivity {

    public static final String TAG = FlightInfoActivity.class.getSimpleName();

    private TextView mDepartureAirportCode;
    private TextView mDepartureAirportCity;
    private TextView mArrivalAirportCode;
    private TextView mArrivalAirportCity;
    private TextView mFlightNumberLabel;

    private TextView mFlightStatusValue;
    private TextView mFlightBoardingValue;
    private TextView mFlightGateValue;

    private Button mGetMeToTheAirport;

    private String mFlightNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);

        Intent intent = getIntent();
        mFlightNumber = intent.getStringExtra(getString(R.string.flight_number_intent_extra));

        if (mFlightNumber == null) {
            //TODO: handle this later
        }
        Log.d(TAG, mFlightNumber);

        mDepartureAirportCode = (TextView)findViewById(R.id.departure_airport_code);
        mDepartureAirportCity = (TextView)findViewById(R.id.departure_airport_city);
        mArrivalAirportCode = (TextView)findViewById(R.id.arrival_airport_code);
        mArrivalAirportCity = (TextView)findViewById(R.id.arrival_airport_city);
        mFlightNumberLabel = (TextView)findViewById(R.id.flight_number_label);

        mFlightStatusValue = (TextView)findViewById(R.id.status_value);
        mFlightBoardingValue = (TextView)findViewById(R.id.boarding_value);
        mFlightGateValue = (TextView)findViewById(R.id.gate_value);

        mGetMeToTheAirport = (Button)findViewById(R.id.get_me_to_airport_button);

        mGetMeToTheAirport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextPageIntent = new Intent(FlightInfoActivity.this, BubblesActivity.class);
                // add any info we want to send to next activity
                startActivity(nextPageIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flight_info, menu);
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
}
