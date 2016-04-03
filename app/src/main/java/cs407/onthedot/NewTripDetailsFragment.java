package cs407.onthedot;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTripDetailsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_MEETUP_TIME = "MEETUP_TIME";
    private static final String ARG_DESTINATION = "DESTINATION";

    private final int GOOGLE_MAPS_ZOOM_LEVEL = 15;

    private EditText date_editText;
    private EditText time_editText;
    private Button cancel_button;
    private Button addFriends_button;

    private LatLng destination;
    private GoogleMap destination_googleMaps;

    private Calendar meetupTime_calendar;

    OnNewTripDetailsUpdatedListener onNewTripDetailsUpdatedListenerCallback;

    public interface OnNewTripDetailsUpdatedListener {

        /**
         * Update the meetup time and destination in a Trip object
         */
        public void onNewTripDetailsUpdated(Date meetupTime, LatLng destination);
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

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        addFriends_button = (Button) view.findViewById(R.id.addFriends_button);

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

                onNewTripDetailsUpdatedListenerCallback
                        .onNewTripDetailsUpdated(meetupTime_calendar.getTime(), destination);

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                onNewTripDetailsUpdatedListenerCallback = (OnNewTripDetailsUpdatedListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnNewTripDetailsUpdatedListener");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        destination_googleMaps = googleMap;

        destination_googleMaps.addMarker(new MarkerOptions().position(destination));
        destination_googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, GOOGLE_MAPS_ZOOM_LEVEL));
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
}
