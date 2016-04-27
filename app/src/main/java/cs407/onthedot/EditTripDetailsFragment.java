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

public class EditTripDetailsFragment extends Fragment implements OnMapReadyCallback {

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

    OnTripDetailsListener onTripDetailsListenerCallback;

    public interface OnTripDetailsListener {

        /**
         * Update the meetup time and destination in a Trip object
         */
        public void onTripDetailsUpdated(Date meetupTime, LatLng destination);

        /**
         * Determines what happens after the "Add Friends" button is clicked.  Ideally,
         * the class that implements this go the the "Add Friends" screen so that the user
         * can add friends to participate in the trip with
         */
        public void onAddFriendsButtonPressed(LatLng destination, LatLng start_location);

    }

    public EditTripDetailsFragment() {
        // Required empty public constructor
    }

    public static EditTripDetailsFragment newInstance(Date meetupTime, LatLng destination) {
        EditTripDetailsFragment fragment = new EditTripDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_edit_trip_details, container, false);

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

                // Today's date
                Calendar today = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), date,
                        meetupTime_calendar.get(Calendar.YEAR),
                        meetupTime_calendar.get(Calendar.MONTH),
                        meetupTime_calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        time_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), time,
                        meetupTime_calendar.get(Calendar.HOUR_OF_DAY),
                        meetupTime_calendar.get(Calendar.MINUTE),
                        false);

                timePickerDialog.show();
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
                onTripDetailsListenerCallback
                        .onTripDetailsUpdated(meetupTime_calendar.getTime(), destination);

                onTripDetailsListenerCallback
                        .onAddFriendsButtonPressed(destination, getStartLocation());
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

                    new AlertDialog.Builder(getActivity())
                            .setMessage("Your location could not be found. Please try again later.")
                            .setCancelable(true)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
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
                onTripDetailsListenerCallback = (OnTripDetailsListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnNewTripDetailsListener");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        destination_googleMaps = googleMap;

        // Get permission from the user to get thier location
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

        // Set up the map with the provided location information
        displayMarker(destination);
        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, GOOGLE_MAPS_ZOOM_LEVEL));

        // This is what happens when the user clicks a location on the map
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

        // This determines what happens when the user pans the map
        destination_googleMaps.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (!cameraPosition.target.equals(destination)) {
                    destination = cameraPosition.target;
                    displayMarker(cameraPosition.target);
                }
            }
        });

    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            Calendar today = Calendar.getInstance();

            meetupTime_calendar.set(Calendar.YEAR, year);
            meetupTime_calendar.set(Calendar.MONTH, monthOfYear);
            meetupTime_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (today.after(meetupTime_calendar)) {
                meetupTime_calendar.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
                meetupTime_calendar.set(Calendar.MINUTE, today.get(Calendar.MINUTE));

                setTimeOnEditText();
            }

            setDateOnEditText();
        }
    };

    private TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar today = Calendar.getInstance();

            meetupTime_calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            meetupTime_calendar.set(Calendar.MINUTE, minute);

            if (today.after(meetupTime_calendar)) {
                meetupTime_calendar.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
                meetupTime_calendar.set(Calendar.MINUTE, today.get(Calendar.MINUTE));

                new AlertDialog.Builder(getActivity())
                        .setMessage("The meet-up time cannot be a time that has already passed. Please select another time.")
                        .setCancelable(true)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }

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
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
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

    /**
     * Method that determines what happens when the search button is pressed
     *
     * @param addresses
     */
    public void onSearchButtonPressed(List<Address> addresses) {

        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        Address address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        displayMarker(latLng);
        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, GOOGLE_MAPS_ZOOM_LEVEL));
    }

    /**
     * Displays a Marker on the GoogleMap fragment for the LatLng location with address
     * information if available.
     *
     * @param location The location to display the marker
     */
    public void displayMarker(LatLng location) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        Marker mapMarker;

        try {
            addresses = geocoder.getFromLocation(location.latitude,
                    location.longitude, NUMBER_OF_ADDR_SEARCH_RESULTS);

        } catch (IOException e) {
            // TODO Handle exception?
        }

        destination_googleMaps.clear();

        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);

            mapMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .title(address.getAddressLine(0))
                    .snippet(address.getLocality() + ", " + address.getAdminArea() + " " + address.getPostalCode())
                    .position(location));
        } else {
            mapMarker = destination_googleMaps.addMarker(new MarkerOptions()
                    .position(location));
        }

        mapMarker.showInfoWindow();
    }

    public LatLng getStartLocation() {
        Location location = destination_googleMaps.getMyLocation();
        LatLng start_location = null;

        if (location != null) {
            start_location = new LatLng(location.getLatitude(), location.getLongitude());
        }

        return start_location;
    }
}
