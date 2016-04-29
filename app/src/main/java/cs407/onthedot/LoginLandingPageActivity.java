package cs407.onthedot;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class LoginLandingPageActivity extends AppCompatActivity implements FacebookLoginFragment.OnFragmentInteractionListener{

    //public static final String PREFS_NAME = "MyPrefsFile";
    com.facebook.CallbackManager callbackManager;
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

        //testing out the backend here
        //test putting a task in
        TaskBean task = new TaskBean();
        task.setData("data1");
        task.setId(new Long(1));
        //new EndpointsAsyncTask().insertBean(task);
        //insertBean
        //new EndpointsAsyncTask().doInBackgroundPUT(new Pair<Context, TaskBean>(this, task));
        //new EndpointsAsyncTask().doInBackgroundPUT(this, task);
        //new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Conner"));

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //set the access token using currentAccessToken when its loaded or set
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        //set callback methods
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult){
                Toast.makeText(LoginLandingPageActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                Log.d("LOGINMANAGER", "onSuccess: ");
                Intent intent = new Intent(LoginLandingPageActivity.this, DashboardActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel(){
                Toast.makeText(LoginLandingPageActivity.this,"Cancel!", Toast.LENGTH_SHORT).show();
                Log.d("LOGINMANAGER", "onCancel: ");
            }

            @Override
            public void onError(FacebookException exception){
                Toast.makeText(LoginLandingPageActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                Log.d("LOGINMANAGER", "onError: ");
            }
        });

        //handle whether we need to make the user log in, or if they are already logged in
        if (AccessToken.getCurrentAccessToken() == null){
            //sdk takes a little time so we must wait for it
            waitForFacebookSdk();
        }
        else{
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    //after some time specified below, we will call this function which will bring
                    //us to the DashboardActivity
                    //us to the DashboardActivity
                    initFacebookLogin();
                }
            };
            Handler h = new Handler();
            h.postDelayed(r, 2000);

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
        //DEBUG: uncomment the below line to force a logout
        //LoginManager.getInstance().logOut();

        //get current access token. If it indicates we are logged in then redirect to the main
        //activity, otherwise we need to launch the login fragment
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            // launch fragment that allows user to login
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment, new FacebookLoginFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            // bypass login fragment and continue on with app functionality
            //TODO use intent to start new activity
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
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
