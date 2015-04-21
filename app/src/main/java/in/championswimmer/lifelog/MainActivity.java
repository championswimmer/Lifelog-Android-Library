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

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "LifeLog:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) findViewById(R.id.login_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifeLog.doLogin(MainActivity.this);
            }
        });

        LifeLog.checkAuthentication(this, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                if (authenticated) {
                    Toast.makeText(MainActivity.this, "authed", Toast.LENGTH_SHORT).show();
                    LifeLogLocationAPI llLocation = LifeLogLocationAPI.prepareRequest(500);
                    llLocation.get(MainActivity.this, new LifeLogLocationAPI.OnLocationFetched() {
                        @Override
                        public void onLocationFetched(ArrayList<LifeLogLocationAPI.LifeLogLocation> locations) {
                            Log.d(TAG, locations.get(0).getId());

                        }
                    });
                } else {
                    //LifeLog.doLogin(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LifeLog.LOGINACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "User authenticated", Toast.LENGTH_SHORT).show();
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
