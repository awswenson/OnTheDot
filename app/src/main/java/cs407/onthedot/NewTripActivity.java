package cs407.onthedot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tanner on 3/24/2016.
 */
public class NewTripActivity extends AppCompatActivity implements NewTripDetailsFragment.OnNewTripDetailsUpdatedListener,
        NewTripAddFriendsFragment.OnNewTripAddFriendsUpdatedListener {

    private Trip newTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        newTrip = new Trip(new LatLng(43, -89), new Date(), new ArrayList<String>(), false);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.newTripContainer_frameLayout,
                        NewTripDetailsFragment.newInstance(newTrip.getMeetupTime(), newTrip.getDestination()))
                .addToBackStack(null)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.newTripContainer_frameLayout,
                        NewTripAddFriendsFragment.newInstance(newTrip.getFacebookFriendsIdList()))
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
    public void onNewTripAddFriendsUpdated(ArrayList<String> facebookFriendsIdList) {
        newTrip.setFacebookFriendsIdList(facebookFriendsIdList);
    }
}
