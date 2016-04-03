package cs407.onthedot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize view
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up the plus button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewTripActivity.class);
                startActivity(intent);
            }
        });

        //test to get information about "me"
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

        //test to get information about "friends"
        GraphRequest request2 = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {
                        // Application code
                        Log.d("TEST GRAPH API FRIENDS", "onCompleted: " + object.toString());
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
}

/*
    //Use this instance variable in the activity that you are using the db in
    public static DBHelper mydb;

    //put this in onCreate
    mydb = new DBHelper(this);


    //these below methods would be in in the activity as well. These are just examples.

    public DBHelper getMydb() {
        return mydb;
    }

    public void setMydb(DBHelper mydb) {
        this.mydb = mydb;
    }

    public void setCurrentDate(String date){
        this.currentDate = date;
    }

    public String getCurrentDate(){
        return this.currentDate;
    }

    public void setNewEvent(ScheduleEvent s){
        this.newEvent = s;
    }

    public ArrayList<ScheduleEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<ScheduleEvent> events) {
        this.events = events;
    }
    */
