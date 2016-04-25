package cs407.onthedot;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tanner on 3/24/2016.
 */
public class EditTripActivity extends AppCompatActivity implements EditTripDetailsFragment.OnTripDetailsListener, EditTripAddFriendsFragment.OnTripAddFriendsListener {

    private Trip trip;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        // Get the Trip object from the Intent
        trip = getIntent().getParcelableExtra(DashboardActivity.INTENT_TRIP_OBJECT);

        // Start up the EditTripDetailsFragment first
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.newTripContainer_frameLayout,
                        EditTripDetailsFragment.newInstance(trip.getMeetupTime(), trip.getDestination()))
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
    public void onAddFriendsButtonPressed(LatLng destination, LatLng start_location) {

        ArrayList<LatLng> path = new ArrayList<>();
        path.add(destination);
        path.add(start_location);
        distance = SphericalUtil.computeLength(path);
        Toast.makeText(this, "The markers are " + formatNumber(distance) + " apart.", Toast.LENGTH_LONG).show();

        getSupportFragmentManager()
                .beginTransaction()
            .add(R.id.newTripContainer_frameLayout,
                    EditTripAddFriendsFragment.newInstance(trip.getAttendingFBFriendsList()))
            .addToBackStack("EDIT_TRIP_ADD_FRIENDS")
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

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }

}
