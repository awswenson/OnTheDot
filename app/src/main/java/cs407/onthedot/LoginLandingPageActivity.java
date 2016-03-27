package cs407.onthedot;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

public class LoginLandingPageActivity extends AppCompatActivity implements FacebookLoginFragment.OnFragmentInteractionListener{

    public static final String PREFS_NAME = "MyPrefsFile";
    public com.facebook.CallbackManager callbackManager;
    public AccessTokenTracker mAccessTokenTracker;
    public AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //link up the facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //initialize view
        setContentView(R.layout.activity_login_landing_page);



        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //set the access token using currentAccessToken when its loaded or set
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        //handle whether we need to make the user log in, or if they are already logged in
        if (AccessToken.getCurrentAccessToken() == null){
            //sdk takes a little time so we must wait for it
            waitForFacebookSdk();
        }
        else{
            initFacebookLogin();
        }
    }

    //for some period of time, let the sdk become ready, and then init facebook login
    private void waitForFacebookSdk() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                int tries = 0;
                while (tries < 3) {
                    if (AccessToken.getCurrentAccessToken() == null) {
                        tries++;
                        try {
                            Thread.sleep(400);
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
        //LoginManager.getInstance().logOut();
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
            //TODO use intent to start new activity
        }
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
