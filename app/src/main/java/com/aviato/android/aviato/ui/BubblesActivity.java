package com.aviato.android.aviato.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aviato.android.aviato.R;

public class BubblesActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubbles);

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

        drawBubbles(100, 50, 70, 50, 80);

        fillBubbleValues(50, 30, 20, 10, 30);

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

    private void fillBubbleValues(int a, int b, int c, int d, int e) {
        mTransportValue.setText(a + "");
        mCheckInValue.setText(b + "");
        mImmigrationValue.setText(c + "");
        mGateValue.setText(d + "");
        mSecurityValue.setText(e + "");
    }

    private void drawBubbles(int t, int c, int i, int s, int g) {

        RelativeLayout.LayoutParams transportParams = new RelativeLayout.LayoutParams(t, t);
        RelativeLayout.LayoutParams checkInParams = new RelativeLayout.LayoutParams(c, c);
        RelativeLayout.LayoutParams immigrationParams = new RelativeLayout.LayoutParams(i, i);
        RelativeLayout.LayoutParams securityParams = new RelativeLayout.LayoutParams(s, s);
        RelativeLayout.LayoutParams gateParams = new RelativeLayout.LayoutParams(g, g);

        mTransportationBubble.setLayoutParams(transportParams);
        mCheckInBubble.setLayoutParams(checkInParams);
        mImmigrationBubble.setLayoutParams(immigrationParams);
        mSecurityBubble.setLayoutParams(securityParams);
        mGateBubble.setLayoutParams(gateParams);

        // ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(250, 250);
        // mTransportationBubble.setLayoutParams(params);

    }

}
