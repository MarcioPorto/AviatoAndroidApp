package com.aviato.android.aviato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BubblesActivity extends AppCompatActivity {

    private ImageView mTransportationBubble;

    private TextView mTransportValue;
    private TextView mCheckInValue;
    private TextView mImmigrationValue;
    private TextView mSecurityValue;
    private TextView mGateValue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubbles);

        mTransportationBubble = (ImageView)findViewById(R.id.transport_bubble);

        mTransportValue = (TextView)findViewById(R.id.transport_value);
        mCheckInValue = (TextView)findViewById(R.id.checkin_value);
        mImmigrationValue = (TextView)findViewById(R.id.immigration_value);
        mSecurityValue = (TextView)findViewById((R.id.security_value));
        mGateValue = (TextView)findViewById(R.id.gate_value);

        mTransportationBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BubblesActivity.this, TransportationModeActivity.class);
                startActivity(intent);
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
}
