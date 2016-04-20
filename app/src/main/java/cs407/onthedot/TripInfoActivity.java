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

        // TODO Show the pictures of the attending friends

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

        deleteTrip_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Return to the DashboardActivity with the Trip object
                Intent data = new Intent();
                data.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);

                // (RESULT_FIRST_USER + 1) means to delete the Trip in the database
                setResult((RESULT_FIRST_USER + 1), data);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        destination_googleMaps = googleMap;

        Address address = trip.getAddressInformation(this);

        Marker mapMarker;

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

        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(trip.getDestination(), GOOGLE_MAPS_ZOOM_LEVEL));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to and ensure the result was successful
        if (requestCode == EDIT_TRIP_REQUEST && resultCode == RESULT_OK) {

            // RESULT_FIRST_USER means to update the Trip in the database
            setResult(RESULT_FIRST_USER, data);
            finish();
        }
    }
}
