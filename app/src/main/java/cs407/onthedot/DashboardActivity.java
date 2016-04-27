package cs407.onthedot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {

    /*
      This is used to identify a single Trip object when passing it between Activities/Fragments
     */
    public static final String INTENT_TRIP_OBJECT = "TRIP_OBJECT";

    /*
      These are used to indicate which ActivityForResult we are starting
     */
    private final int ADD_NEW_TRIP_REQUEST = 1;
    private final int EDIT_TRIP_REQUEST = 2;

    private DBHelper onTheDotDatabase;

    private ArrayList<Trip> currentTripsList;
    private ArrayList<Trip> pastTripsList;

    private ListView currentTrips_listView;
    private ListView pastTrips_listView;

    private DashboardAdapter currentTripsAdapter;
    private DashboardAdapter pastTripsAdapter;

    private Button deletePastTrips_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the database
        onTheDotDatabase = new DBHelper(this);

        // Initialize the two trip lists
        currentTripsList = onTheDotDatabase.getAllActiveTrips();
        pastTripsList = onTheDotDatabase.getAllPastTrips();

        // Get the two ListViews and setup the DashboardAdapter for each view
        currentTrips_listView = (ListView) findViewById(R.id.currentTrips_listView);
        currentTripsAdapter = new DashboardAdapter(this, currentTripsList);
        currentTrips_listView.setAdapter(currentTripsAdapter);

        pastTrips_listView = (ListView) findViewById(R.id.pastTrips_listView);
        pastTripsAdapter = new DashboardAdapter(this, pastTripsList);
        pastTrips_listView.setAdapter(pastTripsAdapter);

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

                    // TODO replace the LatLng with the initial location of the device
                    Trip newTrip = new Trip(new LatLng(43, -89), new Date(), new ArrayList<Friend>(), false);

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

                ArrayList<Trip> tripsToRemove = new ArrayList<>();

                for (Trip trip : pastTripsList) {

                    // Attempt to delete the trip from the database.  If it was unable to be
                    // deleted from the database, don't remove it from the list.
                    if (onTheDotDatabase.deleteTripByTripID(trip.getTripID())) {
                        tripsToRemove.add(trip);
                    }
                }

                pastTripsList.removeAll(tripsToRemove);

                pastTripsAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(currentTrips_listView);
                ListUtils.setDynamicHeight(pastTrips_listView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to and ensure the result was successful
        if (requestCode == ADD_NEW_TRIP_REQUEST) {

            if (resultCode == RESULT_OK) {

                // Get the trip data from the Intent object
                Trip newTrip = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                // Add the trip to the database and get the ID
                long newTripId = onTheDotDatabase.addTrip(newTrip);

                // Make sure that the trip was added to the database successfully. If not,
                // we do not add the trip to the list.
                if (newTripId <= 0) {

                    new AlertDialog.Builder(this)
                            .setMessage("The trip could not be created. Please try again.")
                            .setCancelable(true)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
                else {

                    // Set the ID of the trip in the Trip object
                    newTrip.setTripID(newTripId);

                    currentTripsList.add(newTrip);

                    currentTripsAdapter.notifyDataSetChanged();
                    ListUtils.setDynamicHeight(currentTrips_listView);
                    ListUtils.setDynamicHeight(pastTrips_listView);
                }
            }
        }
        else if (requestCode == EDIT_TRIP_REQUEST) {

            if (resultCode == TripInfoActivity.RESULT_UPDATE) { // Update the Trip object in the database

                // Get the trip data from the Intent object
                Trip editedTrip = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                // Update the trip and make sure it was successful. If it wasn't, then do not
                // remove it from the list.
                if (!onTheDotDatabase.updateTrip(editedTrip)) {

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
                }
                else {

                    // Update the list. No need to notify the adapter the data set changed because
                    // we are just going back to the trip info page (will happen OnResume)
                    currentTripsList.remove(editedTrip);
                    currentTripsList.add(editedTrip);

                    // Return back to the Trip info page with the updated trip details
                    Intent intent = new Intent(this, TripInfoActivity.class);
                    intent.putExtra(INTENT_TRIP_OBJECT, editedTrip);
                    startActivityForResult(intent, EDIT_TRIP_REQUEST);
                }
            }
            else if (resultCode == TripInfoActivity.RESULT_DELETE) { // Delete the Trip object in the database

                // Get the trip data from the Intent object
                Trip tripToDelete = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                // Delete the trip from the database and ensure that it was actually deleted.
                // If it wasn't, then don't remove it from the list
                if (!onTheDotDatabase.deleteTripByTripID(tripToDelete.getTripID())) {

                    new AlertDialog.Builder(this)
                            .setMessage("The trip could not be deleted. Please try again.")
                            .setCancelable(true)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
                else {

                    // Remove the trip from the current trip list
                    currentTripsList.remove(tripToDelete);

                    currentTripsAdapter.notifyDataSetChanged();
                    ListUtils.setDynamicHeight(currentTrips_listView);
                    ListUtils.setDynamicHeight(pastTrips_listView);
                }
            }
        }
    }

    @Override
    public void onResume() {

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
                onTheDotDatabase.setTripCompleteStatus(trip.getTripID());

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

    public static class ListUtils {

        /**
         * Dynamically sets the height of the list view to accommodate two list views.  This should
         * be called each time after the Adapter.notifyDataSetChanged is called for any ListView.
         * This probably isn't the best way to keep two different scrollable lists on a single
         * view, but it's one that I find works for now.
         */
        public static void setDynamicHeight(ListView listView) {
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