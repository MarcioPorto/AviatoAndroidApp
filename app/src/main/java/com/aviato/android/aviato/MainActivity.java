package com.aviato.android.aviato;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private EditText mFlightNumberField;
    private Button mGoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlightNumberField = (EditText)findViewById(R.id.flight_number_input_text);
        mGoButton = (Button)findViewById(R.id.go_button);

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flightNumber = mFlightNumberField.getText().toString();
                if (flightNumber.equals("")) {
                    // String is empty. Show warning.
                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            getString(R.string.flght_number_check_message),
                            Toast.LENGTH_LONG).show();
                } else {
                    startStory(flightNumber);
                }
            }
        });

    }

    private void startStory(String name) {
        Intent intent = new Intent(this, FlightInfoActivity.class);
        intent.putExtra(getString(R.string.flight_number_intent_extra), name);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFlightNumberField.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
