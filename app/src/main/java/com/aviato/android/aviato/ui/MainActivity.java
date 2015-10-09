package com.aviato.android.aviato.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aviato.android.aviato.R;
import com.aviato.android.aviato.models.Constants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class MainActivity extends Activity {

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
                final String flightNumber = mFlightNumberField.getText().toString();
                if (flightNumber.equals("")) {
                    // String is empty. Show warning.
                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            getString(R.string.flght_number_check_message),
                            Toast.LENGTH_LONG).show();
                } else {

                    initParse();

                    final ParseObject user = new ParseObject("NewUser");
                    user.put("location", "Transit");
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Success
                                Constants.CURRENT_USER = user;
                                Intent intent = new Intent(MainActivity.this, FlightInfoActivity.class);
                                intent.putExtra("flightNumber", flightNumber);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle("Make sure you have internet access.")
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

//        Parse.initialize(this, "iavGr3kHa2LmreIPoSZQ9P3xxfv38YECGrxmVTC2", "9sT41tNlXcqKp5oeNteITozGLJaTfXwplW6IYhz8");
//        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

//    private void startStory(String name) {}

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

    private void initParse() {
        try {
            Parse.initialize(this, "iavGr3kHa2LmreIPoSZQ9P3xxfv38YECGrxmVTC2", "9sT41tNlXcqKp5oeNteITozGLJaTfXwplW6IYhz8");
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
