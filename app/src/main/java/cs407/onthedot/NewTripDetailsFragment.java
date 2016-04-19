package cs407.onthedot;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTripDetailsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_MEETUP_TIME = "MEETUP_TIME";
    private static final String ARG_DESTINATION = "DESTINATION";

    private final int GOOGLE_MAPS_ZOOM_LEVEL = 16;
    private final int NUMBER_OF_ADDR_SEARCH_RESULTS = 1;

    private EditText date_editText;
    private EditText time_editText;
    private EditText search;
    private Button cancel_button;
    private Button addFriends_button;
    private Button search_button;

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private static boolean canAccessLocation;

    private LatLng destination;
    private GoogleMap destination_googleMaps;

    private Calendar meetupTime_calendar;

    OnNewTripDetailsListener onNewTripDetailsListenerCallback;

    public interface OnNewTripDetailsListener {

        /**
         * Update the meetup time and destination in a Trip object
         */
        public void onNewTripDetailsUpdated(Date meetupTime, LatLng destination);

        /**
         * Determines what happens after the "Add Friends" button is clicked.  Ideally,
         * the class that implements this go the the "Add Friends" screen so that the user
         * can add friends to participate in the trip with
         */
        public void onAddFriendsButtonPressed();

    }

    public NewTripDetailsFragment() {
        // Required empty public constructor
    }

    public static NewTripDetailsFragment newInstance(Date meetupTime, LatLng destination) {
        NewTripDetailsFragment fragment = new NewTripDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEETUP_TIME, meetupTime);
        args.putParcelable(ARG_DESTINATION, destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            meetupTime_calendar = Calendar.getInstance();
            meetupTime_calendar.setTime((Date) getArguments().getSerializable(ARG_MEETUP_TIME));

            destination = getArguments().getParcelable(ARG_DESTINATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_trip_details, container, false);

        date_editText = (EditText) view.findViewById(R.id.date_editText);
        time_editText = (EditText) view.findViewById(R.id.time_editText);
        search = (EditText) view.findViewById(R.id.searchView1);

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        addFriends_button = (Button) view.findViewById(R.id.addFriends_button);
        search_button = (Button) view.findViewById(R.id.searchButton);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.destination_googleMaps);
        mapFragment.getMapAsync(this);

        setDateOnEditText();
        setTimeOnEditText();

        date_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(view.getContext(), date,
                        meetupTime_calendar.get(Calendar.YEAR),
                        meetupTime_calendar.get(Calendar.MONTH),
                        meetupTime_calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new TimePickerDialog(view.getContext(), time,
                        meetupTime_calendar.get(Calendar.HOUR_OF_DAY),
                        meetupTime_calendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        addFriends_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onNewTripDetailsListenerCallback
                        .onNewTripDetailsUpdated(meetupTime_calendar.getTime(), destination);

                onNewTripDetailsListenerCallback
                        .onAddFriendsButtonPressed();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String searchText = search.getText().toString();

                try {
                    Geocoder geocoder = new Geocoder(getActivity().getBaseContext());

                    onSearchButtonPressed(geocoder.getFromLocationName(searchText, NUMBER_OF_ADDR_SEARCH_RESULTS));
                } catch (IOException e) {
                    // TODO Display popup saying no location could be found
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This ensures that the activity that created this fragment has implemented
        // the embedded listener class.
        if (context instanceof Activity) {
            try {
                onNewTripDetailsListenerCallback = (OnNewTripDetailsListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnNewTripDetailsListener");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        destination_googleMaps = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            canAccessLocation = true;
            destination_googleMaps.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            String question = "Allow OnTheDot to access current location?";
            new AlertDialog.Builder(getActivity())
                    .setCancelable(true)
                    .setMessage(question)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canAccessLocation = true;
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canAccessLocation = false;
                        }
                    })
                    .show();
        }

        if (canAccessLocation) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);

        } else {
            getActivity().finish();
            System.exit(0);
        }

        displayMarker(destination);
        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, GOOGLE_MAPS_ZOOM_LEVEL));

        destination_googleMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                if (!point.equals(destination)) {
                    destination = point;
                    displayMarker(point);
                    destination_googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(point, GOOGLE_MAPS_ZOOM_LEVEL));
                }
            }
        });

        destination_googleMaps.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (!cameraPosition.target.equals(destination)) {
                    destination = cameraPosition.target;
                    displayMarker(cameraPosition.target);
                }
            }
        });


        destination_googleMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {


                // Return false so the default behavior occurs
                return false;
            }
        });

    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            meetupTime_calendar.set(Calendar.YEAR, year);
            meetupTime_calendar.set(Calendar.MONTH, monthOfYear);
            meetupTime_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setDateOnEditText();
        }
    };

    private TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            meetupTime_calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            meetupTime_calendar.set(Calendar.MINUTE, minute);

            setTimeOnEditText();
        }
    };

    private void setDateOnEditText() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        date_editText.setText(sdf.format(meetupTime_calendar.getTime()));
    }

    private void setTimeOnEditText() {
        String timeFormat = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);

        time_editText.setText(sdf.format(meetupTime_calendar.getTime()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    destination_googleMaps.setMyLocationEnabled(true);
                }
            } else {
                // Permission was denied. Display an error message.

            }
        }
    }

    public void onSearchButtonPressed(List<Address> addresses) {

        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        Address address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        displayMarker(latLng);
        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, GOOGLE_MAPS_ZOOM_LEVEL));
    }

    public void displayMarker(LatLng location) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.latitude,
                    location.longitude, NUMBER_OF_ADDR_SEARCH_RESULTS);

        } catch (IOException e) {
            // TODO Handle exception?
        }

        destination_googleMaps.clear();

        if (addresses != null && !addresses.isEmpty()) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();

            Marker newMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(location)
                    .title(address + " " + city + ", " + state));

            newMarker.showInfoWindow();
        }
        else {
            Marker newMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(location));

            newMarker.showInfoWindow();
        }
    }
}
