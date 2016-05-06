package cs407.onthedot;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cs407.onthedot.AddTripTask.AddTripTaskListener;

public class DashboardActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AddTripTaskListener, ClearTripTask.ClearTripTaskListener {

    /*
      This is used to identify a single Trip object when passing it between Activities/Fragments
     */
    public static final String INTENT_TRIP_OBJECT = "TRIP_OBJECT";

    private static final String INTENT_FACEBOOK_ID = "INTENT_FACEBOOK_ID";

    /*
      These are used to indicate which ActivityForResult we are starting
     */
    private final int ADD_NEW_TRIP_REQUEST = 1;
    private final int EDIT_TRIP_REQUEST = 2;

    private int TIME_TO_POLL_BACKEND = 60000; // This is in milliseconds

    private ArrayList<Trip> currentTripsList;
    private ArrayList<Trip> pastTripsList;

    private ListView currentTrips_listView;
    private ListView pastTrips_listView;

    private DashboardAdapter currentTripsAdapter;
    private DashboardAdapter pastTripsAdapter;

    private Button deletePastTrips_button;

    private Friend me;

    private GoogleApiClient googleApiClient;

    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Initialize the two trip lists
        currentTripsList = DBHelper.getInstance(this).getAllActiveTrips();
        pastTripsList = DBHelper.getInstance(this).getAllPastTrips();

        // Get the two ListViews and setup the DashboardAdapter for each view
        currentTrips_listView = (ListView) findViewById(R.id.currentTrips_listView);
        currentTripsAdapter = new DashboardAdapter(this, currentTripsList);
        currentTrips_listView.setAdapter(currentTripsAdapter);

        pastTrips_listView = (ListView) findViewById(R.id.pastTrips_listView);
        pastTripsAdapter = new DashboardAdapter(this, pastTripsList);
        pastTrips_listView.setAdapter(pastTripsAdapter);

        /*
          Get information about me and use it when creating new trips (we want to add a Friend
          object with out information into the attending list so that it's easier to maintain
          the trip participants on the server
        */
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            String facebookID = object.getString("id");
                            String name = object.getString("name");

                            me = new Friend(name, false, facebookID);

                            // Since we have data on "me," start the service to poll the backend
                            // for new trips
                            startBackendPollAlarm(facebookID);
                        } catch (JSONException j) {
                            // Error getting the JSON, so do not create the Friend object
                            Log.d("DashboardActivity", "ERROR: Trouble parsing FB JSON about me");
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

        // Set up the behavior that occurs when we click on an item in the currentTrips list
        currentTrips_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DashboardAdapter adapter = (DashboardAdapter) ((ListView) parent).getAdapter();
                Trip trip = adapter.getItem(position);

                Intent intent = new Intent(view.getContext(), TripInfoActivity.class);
                intent.putExtra(INTENT_TRIP_OBJECT, trip);
                startActivityForResult(intent, EDIT_TRIP_REQUEST);
            }
        });

        // Set up the "Add New Trip" button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ArrayList<Friend> participants = new ArrayList<>();

                    // Add me to the participants list
                    if (me != null) {
                        participants.add(me);
                    }

                    LatLng location = new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude());

                    Trip newTrip = new Trip(location, location, new Date(), participants, false);

                    Intent intent = new Intent(view.getContext(), EditTripActivity.class);
                    intent.putExtra(INTENT_TRIP_OBJECT, newTrip);
                    startActivityForResult(intent, ADD_NEW_TRIP_REQUEST);
                }
            });
        }

        // Set up the button to delete the past (i.e. completed) trips
        deletePastTrips_button = (Button) findViewById(R.id.deletePastTrips_button);
        deletePastTrips_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deletePastTrips();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to and ensure the result was successful
        if (requestCode == ADD_NEW_TRIP_REQUEST) {

            if (resultCode == RESULT_OK) {

                // Get the trip data from the Intent object
                Trip tripToAdd = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                addTrip(tripToAdd);
            }
        }
        else if (requestCode == EDIT_TRIP_REQUEST) {

            if (resultCode == TripInfoActivity.RESULT_UPDATE) { // Update the Trip object in the database

                // Get the trip data from the Intent object
                Trip tripToUpdate = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                updateTrip(tripToUpdate);

                // Return back to the Trip info page
                Intent intent = new Intent(this, TripInfoActivity.class);
                intent.putExtra(INTENT_TRIP_OBJECT, tripToUpdate);
                startActivityForResult(intent, EDIT_TRIP_REQUEST);
            }
            else if (resultCode == TripInfoActivity.RESULT_DELETE) { // Delete the Trip object in the database

                // Get the trip data from the Intent object
                Trip tripToDelete = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                deleteTrip(tripToDelete);
            }
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {

        // Check the trips to see if any of the statuses have changed
        checkActiveTripStatusAndUpdateList();

        // Dynamically set the height of the two list views. See ListUtils.setDyamicHeight
        // for documentation
        currentTripsAdapter.notifyDataSetChanged();
        pastTripsAdapter.notifyDataSetChanged();
        ListUtils.setDynamicHeight(currentTrips_listView);
        ListUtils.setDynamicHeight(pastTrips_listView);

        super.onResume();
    }

    /**
     * Inserts a trip into the DB and creates the notification for the trip. Also adds the
     * trip to the ListView.
     *
     * @param tripToAdd The Trip to add
     */
    public void addTrip(Trip tripToAdd) {
        new EndpointsPortal().addTrip(this, tripToAdd);

//        // Add the trip to the database and get the ID
//        long newTripId = DBHelper.getInstance(this).addTrip(tripToAdd);
//
//        // Make sure that the trip was added to the database successfully. If not,
//        // we do not add the trip to the list.
//        if (newTripId <= 0) {
//
//            new AlertDialog.Builder(this)
//                    .setMessage("The trip could not be created. Please try again.")
//                    .setCancelable(true)
//                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    })
//                    .show();
//
//            return false;
//        }
//        else {
//
//            // Set the ID of the trip in the Trip object
//            tripToAdd.setTripID(newTripId);
//
//            currentTripsList.add(tripToAdd);
//
//            // Create a notification to go off at the time the user should leave
//            createNotificationHandler(tripToAdd);
//
//            currentTripsAdapter.notifyDataSetChanged();
//            ListUtils.setDynamicHeight(currentTrips_listView);
//            ListUtils.setDynamicHeight(pastTrips_listView);
//
//            return true;
//        }
    }

    /**
     * Updates an active trip from the DB and updates the notification that was created for
     * the trip. Also updates the trip in the ListView.
     *
     * @param tripToUpdate The Trip to delete
     * @return true if the trip was successful updated; false otherwise.
     */
    public boolean updateTrip(Trip tripToUpdate) {

        // Update the trip and make sure it was successful. If it wasn't, then do not
        // remove it from the list.
        if (!DBHelper.getInstance(this).updateTrip(tripToUpdate)) {

            new AlertDialog.Builder(this)
                    .setMessage("The trip could not be updated. Please try again.")
                    .setCancelable(true)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();

            return false;
        }
        else {

            // Update the list. No need to notify the adapter the data set changed because
            // we are just going back to the trip info page (will happen OnResume)
            currentTripsList.remove(tripToUpdate);
            currentTripsList.add(tripToUpdate);

            // Create a notification to go off at the time the user should leave
            createNotificationHandler(tripToUpdate);

            return true;
        }
    }

    /**
     * Deletes an active trip from the DB and cancels the notification created for the trip.
     * Also deletes the trip from the ListView
     *
     * @param tripToDelete The Trip to delete
     */
    public void deleteTrip(Trip tripToDelete) {
        new EndpointsPortal().clearTrip(this, tripToDelete);

//        // Delete the trip from the database and ensure that it was actually deleted.
//        // If it wasn't, then don't remove it from the list
//        if (!DBHelper.getInstance(this).deleteTripByTripID(tripToDelete.getTripID())) {
//
//            new AlertDialog.Builder(this)
//                    .setMessage("The trip could not be deleted. Please try again.")
//                    .setCancelable(true)
//                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    })
//                    .show();
//
//            return false;
//        }
//        else {
//
//            // Remove the trip from the current trip list
//            currentTripsList.remove(tripToDelete);
//
//            // Delete the notification
//            deleteNotificationHandler(tripToDelete);
//
//            currentTripsAdapter.notifyDataSetChanged();
//            ListUtils.setDynamicHeight(currentTrips_listView);
//            ListUtils.setDynamicHeight(pastTrips_listView);
//
//            return true;
//        }
    }

    /**
     * Delete all the past trips from the local DB and updates the ListView accordingly.
     */
    public void deletePastTrips() {
        ArrayList<Trip> tripsToDelete = new ArrayList<>();

        for (Trip trip : pastTripsList) {

            // Attempt to delete the trip from the database.  If it was unable to be
            // deleted from the database, don't remove it from the list.
            if (DBHelper.getInstance(this).deleteTripByTripID(trip.getTripID())) {
                tripsToDelete.add(trip);
            }
        }

        // Don't spend time redrawing the ListViews if there was nothing to delete
        if (!tripsToDelete.isEmpty()) {
            pastTripsList.removeAll(tripsToDelete);

            pastTripsAdapter.notifyDataSetChanged();
            ListUtils.setDynamicHeight(currentTrips_listView);
            ListUtils.setDynamicHeight(pastTrips_listView);
        }
    }

    /**
     * This method starts an alarm that will repeatedly fire every so often
     * (see TIME_TO_POLL_BACKEND) to poll the backend for new/updated/removed trips.
     *
     * Ideally, polling is bad and can waste system resources. But due to the limited time frame and
     * complexity of trying to implement Push Notifications, this was the next best option.
     *
     * @param facebookID The ID corresponding to the user of this device. This will be used to
     *                   fetch the trips that the user is a participant of.
     */
    public void startBackendPollAlarm(String facebookID) {

        if (facebookID == null) {
            return; // Don't start the alarm if we don't know the user's ID
        }

        // Create an alarm to go off when we want to poll the backend for new data
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BackendPollHandler.class);
        intent.putExtra(INTENT_FACEBOOK_ID, facebookID);

        PendingIntent alarmIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to go off at the time to leave. According to the
        // documentation, if an alarm with same intent is made, the previous
        // one is canceled (i.e. no need to call cancel method)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), TIME_TO_POLL_BACKEND, alarmIntent);
    }

    /**
     * When a trip is created or updated, a notification will be created or delayed and created
     * when the user should leave based on distance, best route, and traffic conditions.
     *
     * @param trip The trip to create a notification
     */
    public void createNotificationHandler(final Trip trip) {

        if (trip == null) {
            return; // Don't create a notification for a null trip
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + trip.getStartingLocationLatitude() + "," + trip.getStartingLocationLongitude() +
                "&destination=" + trip.getDestinationLatitude() + "," + trip.getDestinationLongitude() +
                "&departure_time=" + trip.getMeetupTime().getTime() +
                "&traffic_model=best_guess&mode=walking";

        RequestQueue queue = Volley.newRequestQueue(this);

        final Context context = this;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // routesArray contains ALL routes to get to the destination
                            JSONArray routesArray = response.getJSONArray("routes");

                            // Grab the first route
                            JSONObject route = routesArray.getJSONObject(0);

                            // Take all legs from the route (typically there should only be one
                            JSONArray legs = route.getJSONArray("legs");

                            // Grab first leg
                            JSONObject leg = legs.getJSONObject(0);

                            int duration = leg.getJSONObject("duration").getInt("value");

                            Calendar leaveAtCalendar = Calendar.getInstance();
                            leaveAtCalendar.setTime(trip.getMeetupTime());

                            leaveAtCalendar.add(Calendar.SECOND, -duration);

                            // Set the seconds to 0 so that the timer fires immediately at the clock change
                            leaveAtCalendar.set(Calendar.SECOND, 0);
                            leaveAtCalendar.set(Calendar.MILLISECOND, 0);

                            // Create an alarm to go off when the user needs to leave. When the
                            // alarm goes off, it will create a notification that the user will see
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                            Intent intent = new Intent(context, NotificationHandler.class);
                            intent.putExtra(INTENT_TRIP_OBJECT, trip);

                            PendingIntent alarmIntent =
                                    PendingIntent.getBroadcast(context, (int) trip.getTripID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            // Set the alarm to go off at the time to leave. According to the
                            // documentation, if an alarm with same intent is made, the previous
                            // one is canceled (i.e. no need to call cancel method)
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, leaveAtCalendar.getTimeInMillis(), alarmIntent);

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(jsObjRequest);
    }

    /**
     * Deletes a notification handler that may still exist for a trip
     *
     * @param trip The trip to delete a planned notification
     */
    public void deleteNotificationHandler(Trip trip) {

        if (trip == null) {
            return; // Return since we don't know which notification to cancel
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationHandler.class);
        intent.putExtra(INTENT_TRIP_OBJECT, trip);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, (int) trip.getTripID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * Checks to if any of the active trips' have been completed. That is, change the status
     * of the trip to COMPLETE if the active trip's meet-up time has passed. Update the
     * currentTripsList if any of the status' changed.
     */
    public void checkActiveTripStatusAndUpdateList() {

        Date currentTime = new Date();

        ArrayList<Trip> tripsCompleted = new ArrayList<>();

        for (Trip trip : currentTripsList) {
            if (currentTime.after(trip.getMeetupTime())) {
                trip.setTripComplete(true);

                // Update the status in the database
                DBHelper.getInstance(this).setTripCompleteStatus(trip.getTripID());

                // Add the trip to the list to be removed from the
                tripsCompleted.add(trip);
            }
        }

        currentTripsList.removeAll(tripsCompleted);
        pastTripsList.addAll(tripsCompleted);
    }

    public static Bitmap getFacebookProfilePicture(String URL) throws SocketException,
            SocketTimeoutException, MalformedURLException, IOException, Exception {

        Bitmap bitmap = null;
        InputStream in = (InputStream) new URL(URL).getContent();
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
        } catch(SecurityException e) {
            // TODO for now, assume that the user has given the app permission
        }

        if (lastKnownLocation == null) {
            lastKnownLocation = new Location("");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
    }

    @Override
    public void addTripToListView(Trip trip) {

        // Only add the trip to the list if it currently exists (i.e. the application is running)
        if (currentTripsList != null) {
            currentTripsList.add(trip);

            if (currentTripsAdapter != null) {
                currentTripsAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(currentTrips_listView);
                ListUtils.setDynamicHeight(pastTrips_listView);
            }
        }
    }

    @Override
    public void deleteTripfromListView(Trip trip) {

        // Only remove the trip to the list if it currently exists (i.e. the application is running)
        if (currentTripsList != null) {
            currentTripsList.remove(trip);

            if (currentTripsAdapter != null) {
                currentTripsAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(currentTrips_listView);
                ListUtils.setDynamicHeight(pastTrips_listView);
            }
        }
    }

    public static class ListUtils {

        /**
         * Dynamically sets the height of the list view to accommodate two list views.  This should
         * be called each time after the Adapter.notifyDataSetChanged is called for any ListView.
         * This probably isn't the best way to keep two different scrollable lists on a single
         * view, but it's one that I find works for now.
         */
        public static void setDynamicHeight(ListView listView) {

            if (listView == null) {
                return; // We can't do anything with a ListView that hasn't been created
            }

            DashboardAdapter dashboardAdapter = (DashboardAdapter) listView.getAdapter();

            // Check to make sure the adapter is not null
            if (dashboardAdapter == null) {
                return;
            }

            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);

            for (int i = 0; i < dashboardAdapter.getCount(); i++) {
                View listItem = dashboardAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = height + (listView.getDividerHeight() * (dashboardAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }
}