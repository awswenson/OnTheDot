package cs407.onthedot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tanner on 3/24/2016.
 */
public class NewTripActivity extends AppCompatActivity implements NewTripDetailsFragment.OnNewTripDetailsListener, NewTripAddFriendsFragment.OnNewTripAddFriendsListener
{

    private Trip newTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        //TODO put facebook call here to get friends lists
        //get all the id's that correspond to this users friends list
        GraphRequest request2 = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {
                        // Application code
                        Log.d("TEST GRAPH API FRIENDS", "onCompleted: " + object.toString());

                        ArrayList<Friend> friendIds = new ArrayList<Friend>();
                        //parse JSON here and deliver it to the new ArrayList of friend ID's
                        for (int i=0; i< object.length(); i++) {
                            try{
                                JSONObject friend = object.getJSONObject(i);
                                String id = friend.getString("id");
                                String name = friend.getString("name");
                                //TODO change 'false' argument to be the attending status from the db
                                Friend newFriend = new Friend(name, false, id);
                                friendIds.add(newFriend);
                            }
                            catch(JSONException j){
                                //error getting the json... do not add the name
                                Log.d("TEST GRAPH API FRIENDS", "ERROR: had trouble parsing JSON");
                            }


                        }

                        // TODO replace the LatLng with the initial location of the device
                        newTrip = new Trip(new LatLng(43, -89), new Date(), friendIds, false);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.newTripContainer_frameLayout,
                                        NewTripDetailsFragment.newInstance(newTrip.getMeetupTime(), newTrip.getDestination()))
                                .addToBackStack(null)
                                .commit();
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
    public void onNewTripDetailsUpdated(Date meetupTime, LatLng destination) {
        newTrip.setMeetupTime(meetupTime);
        newTrip.setDestination(destination);
    }

    @Override
    public void onAddFriendsButtonPressed() {
        getSupportFragmentManager()
                .beginTransaction()
            .replace(R.id.newTripContainer_frameLayout,
                    NewTripAddFriendsFragment.newInstance(newTrip.getFacebookFriendsList()))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onNewTripAddFriendsUpdated(ArrayList<Friend> facebookFriendsList) {
        newTrip.setFacebookFriendsList(facebookFriendsList);
    }

    @Override
    public void onCreateTripButtonPressed() {
        Intent data = new Intent();
        data.putExtra("NEW_TRIP", newTrip);

        // Activity finished OK, return the data so that we can add it to the database
        setResult(RESULT_OK, data);
        finish();
    }

}
