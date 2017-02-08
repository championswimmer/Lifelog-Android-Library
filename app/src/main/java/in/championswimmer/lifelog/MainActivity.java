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

import com.google.gson.Gson;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.Me;
import com.sonymobile.lifelog.api.models.MeLocation;
import com.sonymobile.lifelog.api.requests.MeLocationRequest;
import com.sonymobile.lifelog.api.requests.MeRequest;
import com.sonymobile.lifelog.utils.SecurePreferences;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "LifeLog:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Gson gson = new Gson();

        final Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifeLog.doLogin(MainActivity.this);
            }
        });

        final Button logout = (Button) findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurePreferences preferences = LifeLog.getSecurePreference(MainActivity.this);
                preferences.clear();
            }
        });

        LifeLog.checkAuthentication(this, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                if (authenticated) {
                    logout.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "authed", Toast.LENGTH_SHORT).show();
                    final MeLocationRequest llLocation = MeLocationRequest.prepareRequest(500);
                    llLocation.get(MainActivity.this, new MeLocationRequest.OnLocationFetched() {

                        private int mPage = 0;

                        @Override
                        public void onLocationFetched(ArrayList<MeLocation> locations) {
                            Log.d(TAG, "Page number: " + mPage + ", " + locations.size() + " points of location data fetched.");

                            if (mPage >= 9) {
                                Log.d(TAG, "10 pages of data fetched, finish fetching.");
                                return;
                            }

                            if (llLocation.getNextPage(MainActivity.this, this)) {
                                mPage++;
                                Log.d(TAG, "Next page of pagination is available, requested the next page data");
                            } else {
                                Log.d(TAG, "Fetching finished until last page");
                            }

                        }
                    });

                    MeRequest meRequest = MeRequest.prepareRequest();
                    meRequest.get(MainActivity.this, new MeRequest.OnMeFetched() {
                        @Override
                        public void onMeFetched(Me meData) {
                            Log.d(TAG, "onMeFetched: " + gson.toJson(meData));
                        }
                    });
                } else {
                    //LifeLog.doLogin(MainActivity.this);
                    login.setVisibility(View.VISIBLE);
                    logout.setVisibility(View.GONE);
                }
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
