package cs407.onthedot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.google.android.gms.maps.model.LatLng;

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

        // TODO replace the LatLng with the initial location of the device
        newTrip = new Trip(new LatLng(43, -89), new Date(), new ArrayList<Friend>(), false);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.newTripContainer_frameLayout,
                        NewTripDetailsFragment.newInstance(newTrip.getMeetupTime(), newTrip.getDestination()))
                .addToBackStack(null)
                .commit();
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
                    NewTripAddFriendsFragment.newInstance(newTrip.getAttendingFBFriendsList()))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onNewTripAddFriendsUpdated(ArrayList<Friend> attendingFBFriendsList) {
        newTrip.setAttendingFBFriendsList(attendingFBFriendsList);
    }

    @Override
    public void onCreateTripButtonPressed() {
        Intent data = new Intent();
        data.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, newTrip);

        // Activity finished OK, return the data so that we can add it to the database
        setResult(RESULT_OK, data);
        finish();
    }

}
