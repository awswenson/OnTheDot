package cs407.onthedot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    private final int ADD_NEW_TRIP_REQUEST = 1;

    private DBHelper onTheDotDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the database
        onTheDotDatabase = new DBHelper(this);

        // Set up the "Add New Trip" button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewTripActivity.class);
                startActivityForResult(intent, ADD_NEW_TRIP_REQUEST);
            }
        });

        // Test to get information about "me"
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.d("TEST GRAPH API ME", "onCompleted: " + object.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

        // Test to get information about "friends"
        GraphRequest request2 = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {
                        // Application code
                        Log.d("TEST GRAPH API FRIENDS", "onCompleted: " + object.toString());
                        //parse JSON here and deliver it to
                        //getFacebookProfilePicture(object.);
                    }
                });
        Bundle parameters2 = new Bundle();
        parameters2.putString("fields", "id,name,link,picture");
        request2.setParameters(parameters2);
        request2.executeAsync();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to and ensure the result was successful
        if (requestCode == ADD_NEW_TRIP_REQUEST && resultCode == RESULT_OK) {

            // Get the trip data from the Intent object
            Trip newTrip = data.getParcelableExtra("NEW_TRIP");

            // Add the trip to the database and get the ID
            long newTripId = onTheDotDatabase.addTrip(newTrip);

            // Make sure that the trip was added to the database successfully
            if (newTripId > 0) {

                // Set the ID of the trip in the Trip object
                newTrip.setTripID(newTripId);

                // TODO add the trip to the list
            }
        }
    }

    public static Bitmap getFacebookProfilePicture(String URL) throws SocketException, SocketTimeoutException, MalformedURLException, IOException, Exception {
        Bitmap bitmap = null;
        InputStream in = (InputStream) new URL(URL).getContent();
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
    }
}