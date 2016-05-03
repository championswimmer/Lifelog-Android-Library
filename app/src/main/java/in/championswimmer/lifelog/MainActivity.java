package in.championswimmer.lifelog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.LifeLogLocationAPI;
import com.sonymobile.lifelog.utils.SecurePreferences;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "LifeLog:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifeLog.doLogin(MainActivity.this);
            }
        });

        Button logout = (Button) findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurePreferences preferences = LifeLog.getSecurePreference(MainActivity.this);
                preferences.clear();
            }
        });

        Button history = (Button) findViewById(R.id.location_history_button);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifeLogLocationAPI locationAPI = LifeLogLocationAPI.prepareRequest(null, Calendar.getInstance(), 10);
                locationAPI.get(MainActivity.this, new LifeLogLocationAPI.OnLocationFetched() {
                    @Override
                    public void onLocationFetched(ArrayList<LifeLogLocationAPI.LifeLogLocation> locations) {
                        for (LifeLogLocationAPI.LifeLogLocation location : locations) {
                            Log.d(TAG, "ID: " + location.getId() + " lat: " + location.getLatitude()
                                    + " lon: " + location.getLongitude());
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LifeLog.LOGINACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "User authenticated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User authentication failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
