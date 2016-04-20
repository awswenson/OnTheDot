package cs407.onthedot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {

    private final int ADD_NEW_TRIP_REQUEST = 1;
    private final int EDIT_TRIP_REQUEST = 2;

    public static final String INTENT_TRIP_OBJECT = "TRIP_OBJECT";

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

        pastTrips_listView = (ListView) findViewById(R.id.pastTrips_listView);
        pastTripsAdapter = new DashboardAdapter(this, pastTripsList);
        pastTrips_listView.setAdapter(pastTripsAdapter);

        currentTripsAdapter.notifyDataSetChanged();
        pastTripsAdapter.notifyDataSetChanged();

        // Dynamically set the height of the two list views. See ListUtils.setDyamicHeight
        // for documentation
        ListUtils.setDynamicHeight(currentTrips_listView);
        ListUtils.setDynamicHeight(pastTrips_listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Set up the "Add New Trip" button
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
                for (Trip trip : pastTripsList) {
                    onTheDotDatabase.deleteTripByTripID(trip.getTripID());
                }

                pastTripsList.clear();

                pastTripsAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(pastTrips_listView);
            }
        });

        // Test to get information about "me"
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.d("TEST GRAPH API ME", "onCompleted: " + object.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

        // Test to get information about "friends"
        GraphRequest request2 = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {
                        // Application code
                        Log.d("TEST GRAPH API FRIENDS", "onCompleted: " + object.toString());
                        //parse JSON here and deliver it to
                        //getFacebookProfilePicture(object.);
                    }
                });
        Bundle parameters2 = new Bundle();
        parameters2.putString("fields", "id,name,link,picture");
        request2.setParameters(parameters2);
        request2.executeAsync();
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
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_NEW_TRIP_REQUEST) {

                // Get the trip data from the Intent object
                Trip newTrip = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                // Add the trip to the database and get the ID
                long newTripId = onTheDotDatabase.addTrip(newTrip);

                // Make sure that the trip was added to the database successfully
                if (newTripId > 0) {

                    // Set the ID of the trip in the Trip object
                    newTrip.setTripID(newTripId);

                    currentTripsList.add(newTrip);

                    currentTripsAdapter.notifyDataSetChanged();
                    ListUtils.setDynamicHeight(currentTrips_listView);
                }
            } else if (requestCode == EDIT_TRIP_REQUEST) {

                Trip editedTrip = data.getParcelableExtra(INTENT_TRIP_OBJECT);

                // TODO update the trip object in the database

                // Return back to the Trip info page with the update trip
                Intent intent = new Intent(this, TripInfoActivity.class);
                intent.putExtra(INTENT_TRIP_OBJECT, editedTrip);
                startActivityForResult(intent, EDIT_TRIP_REQUEST);
            }
        }
    }

    public static Bitmap getFacebookProfilePicture(String URL) throws SocketException, SocketTimeoutException, MalformedURLException, IOException, Exception {
        Bitmap bitmap = null;
        InputStream in = (InputStream) new URL(URL).getContent();
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
    }

    public static class ListUtils {

        /**
         * Dynamically sets the height of the list view to accommodate two list views.  This should
         * be called each time after the Adapter.notifyDataSetChanged is called for the
         * given ListView.
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