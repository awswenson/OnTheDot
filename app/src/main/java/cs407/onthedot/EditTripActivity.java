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
public class EditTripActivity extends AppCompatActivity implements EditTripDetailsFragment.OnTripDetailsListener, EditTripAddFriendsFragment.OnTripAddFriendsListener {

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        trip = getIntent().getParcelableExtra(DashboardActivity.INTENT_TRIP_OBJECT);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.newTripContainer_frameLayout,
                        EditTripDetailsFragment.newInstance(trip.getMeetupTime(), trip.getDestination()))
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
    public void onTripDetailsUpdated(Date meetupTime, LatLng destination) {
        trip.setMeetupTime(meetupTime);
        trip.setDestination(destination);
    }

    @Override
    public void onAddFriendsButtonPressed() {
        getSupportFragmentManager()
                .beginTransaction()
            .replace(R.id.newTripContainer_frameLayout,
                    EditTripAddFriendsFragment.newInstance(trip.getAttendingFBFriendsList()))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onTripAddFriendsUpdated(ArrayList<Friend> attendingFBFriendsList) {
        trip.setAttendingFBFriendsList(attendingFBFriendsList);
    }

    @Override
    public void onFinishTripButtonPressed() {
        Intent data = new Intent();
        data.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);

        // Activity finished OK, return the data so that we can add it to the database
        setResult(RESULT_OK, data);
        finish();
    }

}
