package cs407.onthedot;

import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TripInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int GOOGLE_MAPS_ZOOM_LEVEL = 14;

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

        editTrip_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        deleteTrip_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        destination_googleMaps = googleMap;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(trip.getDestinationLatitude(),
                    trip.getDestinationLongitude(), 1);

        } catch (IOException e) {
            // TODO Handle exception?
        }

        if (addresses != null && !addresses.isEmpty()) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();

            Marker newMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(trip.getDestination())
                    .title(address + " " + city + ", " + state));

            newMarker.showInfoWindow();
        }
        else {
            Marker newMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(trip.getDestination()));

            newMarker.showInfoWindow();
        }

        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(trip.getDestination(), GOOGLE_MAPS_ZOOM_LEVEL));
    }
}
