package cs407.onthedot;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

public class DashboardActivity extends AppCompatActivity implements FacebookLoginFragment.OnFragmentInteractionListener{

    public static final String PREFS_NAME = "MyPrefsFile";
    com.facebook.CallbackManager callbackManager;
    AccessTokenTracker mAccessTokenTracker;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //link up the facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //initialize view
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up the plus button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //set the access token using currentAccessToken when its loaded or set
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        //handle whether we need to make the user log in, or if they are already logged in
        if (AccessToken.getCurrentAccessToken() == null){
            waitForFacebookSdk();
        }
        else{
            initFacebookLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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

    private void waitForFacebookSdk() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                int tries = 0;
                while (tries < 3) {
                    if (AccessToken.getCurrentAccessToken() == null) {
                        tries++;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return null;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                initFacebookLogin();
            }
        };
        asyncTask.execute();
    }

    //here we check again if the user is already logged in. If they are we continue with the app
    //like normal, and if they arent then we launch the login fragment
    private void initFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            // launch fragment that allows user to login
            //launch the calendar fragment
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment, new FacebookLoginFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            // bypass login fragment and continue on with app functionality

        }
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
