package cs407.onthedot;

import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TripInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    /*
      These are used to indicate which result we are responding to back in the DashboardActivity.
      We either want to delete the Trip or update the Trip details. Those actions should
      happen in the DashboardActivity. This may not be the best way to handle this, but it's
      the easiest for what we have so far.
     */
    public static final int RESULT_UPDATE = RESULT_FIRST_USER;
    public static final int RESULT_DELETE = RESULT_FIRST_USER + 1;

    private final int EDIT_TRIP_REQUEST = 2;

    private final int GOOGLE_MAPS_ZOOM_LEVEL = 15;

    private Trip trip;

    private GoogleMap destination_googleMaps;

    private TextView date_textView;
    private TextView time_textView;
    private TextView friends_textView;

    private Button editTrip_button;
    private Button deleteTrip_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        // Get the trip object from the Intent
        trip = getIntent().getParcelableExtra(DashboardActivity.INTENT_TRIP_OBJECT);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.destination_googleMaps);
        mapFragment.getMapAsync(this);

        date_textView = (TextView) findViewById(R.id.date_textView);
        time_textView = (TextView) findViewById(R.id.time_textView);
        friends_textView = (TextView) findViewById(R.id.friends_textView);

        editTrip_button = (Button) findViewById(R.id.editTrip_button);
        deleteTrip_button = (Button) findViewById(R.id.deleteTrip_button);

        // Append the date to the Date TextView
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        date_textView.append(" " + sdf.format(trip.getMeetupTime()));

        // Append the time to the Time TextView
        String timeFormat = "h:mm a";
        sdf = new SimpleDateFormat(timeFormat, Locale.US);

        time_textView.append(" " + sdf.format(trip.getMeetupTime()));

        // Display none if no friends are attending (remember that we are added to the list
        // so we need to check if there are more than 1 person in the list
        if (trip.getAttendingFBFriendsList().size() <= 1) {
            friends_textView.append(" You are the only participant");
        }
        else {

            for (int i = 0; i < trip.getAttendingFBFriendsList().size(); i++) {
                Friend attendingFBFriend = trip.getAttendingFBFriendsList().get(i);

                if ((i + 1) == trip.getAttendingFBFriendsList().size()) {
                    friends_textView.append(" " + attendingFBFriend.getName());
                }
                else {
                    friends_textView.append(" " + attendingFBFriend.getName() + ",");
                }
            }
        }

        /* When the edit button is clicked, we want to pass the Trip object to the EditTripActivity.
           We do this as an ActivityForResult. That is, if the user finishes editing the trip,
           we want to send the Trip object back (via OnActivityResult) to the DashboardActivity
           to be added to the database.  There, a new TripInfo activity is spawned with
           the edited trip
         */
        editTrip_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditTripActivity.class);
                intent.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);
                startActivityForResult(intent, EDIT_TRIP_REQUEST);
            }
        });

        /* When the delete button is clicked, we want to pass the Trip object back to the
           DashboardActivity.  Since we got to this Activity via an IntentForResult through
           the DashboardActivity, we can just set the result and finish the activity.
           The DashboardActivity will handle deleting the Trip object from the database/list.
         */
        deleteTrip_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Return to the DashboardActivity with the Trip object
                Intent data = new Intent();
                data.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);

                // Indicate that we want to delete the Trip in the database
                setResult((RESULT_DELETE), data);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        destination_googleMaps = googleMap;

        // Use the method in the Trip object to get the address information
        Address address = trip.getAddressInformation(this);

        Marker mapMarker;

        // Display a title over the marker if an address was able to be found.  Otherwise
        // just display the marker.
        if (address != null) {
            mapMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(trip.getDestination())
                    .title(address.getAddressLine(0))
                    .snippet(address.getLocality() + ", " + address.getAdminArea() + " " + address.getPostalCode()));
        }
        else {
            mapMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(trip.getDestination()));
        }

        mapMarker.showInfoWindow();

        // Move the map to the position marked by the marker. NOTE that the user will not
        // be able to pan the map (it's static), so make sure the zoom level is sufficient
        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(trip.getDestination(), GOOGLE_MAPS_ZOOM_LEVEL));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to and ensure the result was successful
        if (requestCode == EDIT_TRIP_REQUEST && resultCode == RESULT_OK) {

            // Indicate we want to update the Trip in the database. This should redirect to the
            // onActivityResult method in DashboardActivity
            setResult(RESULT_UPDATE, data);
            finish();
        }
    }
}
