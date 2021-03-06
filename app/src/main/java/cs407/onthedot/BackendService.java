package cs407.onthedot;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AlexanderSwenson on 5/4/16.
 */
public class BackendService extends IntentService {

    private static final String ACTION_SYNCHRONIZE_LOCAL_DB =
            "cs407.onthedot.action.SYNCHRONIZE_LOCAL_DB";

    private static final String ACTION_GET_TRIPS_FROM_BACKEND =
            "cs407.onthedot.action.GET_TRIPS_FROM_BACKEND";

    private static final String ACTION_ADD_TRIP =
            "cs407.onthedot.action.ADD_TRIP";

    private static final String ACTION_DELETE_TRIP =
            "cs407.onthedot.action.DELETE_TRIP";

    private static final String INTENT_TRIP =
            "INTENT_TRIP";

    private static final String INTENT_TRIPS_ARRAY_LIST =
            "INTENT_TRIPS_ARRAY_LIST";

    private static final String INTENT_FACEBOOK_ID =
            "INTENT_FACEBOOK_ID";

    public BackendService() {
        super("BackendService");
    }

    /**
     * Starts this service to synchronize the local database with the backend database. If
     * the service is already performing a task this action will be queued.
     */
    public static void startSynchronizeLocalDB(Context context, ArrayList<Trip> trips) {
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_SYNCHRONIZE_LOCAL_DB);
        intent.putExtra(INTENT_TRIPS_ARRAY_LIST, trips);
        context.startService(intent);
    }

    public static void startGetTripsFromBackend(Context context, String facebookID) {
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_GET_TRIPS_FROM_BACKEND);
        intent.putExtra(INTENT_FACEBOOK_ID, facebookID);
        context.startService(intent);
    }

    public static void startAddTrip(Context context, Trip trip) {
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_ADD_TRIP);
        intent.putExtra(INTENT_TRIP, trip);
        context.startService(intent);
    }

    public static void startDeleteTrip(Context context, Trip tripToDelete) {
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_DELETE_TRIP);
        intent.putExtra(INTENT_TRIP, tripToDelete);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_SYNCHRONIZE_LOCAL_DB.equals(action)) {
                ArrayList<Trip> trips = intent.getParcelableArrayListExtra(INTENT_TRIPS_ARRAY_LIST);
                handleSynchronizeLocalDB(trips);
            }
            else if (ACTION_GET_TRIPS_FROM_BACKEND.equals(action)) {
                String facebookID = intent.getStringExtra(INTENT_FACEBOOK_ID);
                handleGetTripsFromBackend(facebookID);
            }
            else if (ACTION_ADD_TRIP.equals(action)) {
                Trip trip = intent.getParcelableExtra(INTENT_TRIP);
                handleAddTrip(trip);
            }
            else if (ACTION_DELETE_TRIP.equals(action)) {
                Trip tripToDelete = intent.getParcelableExtra(INTENT_TRIP);
                handleDeleteTrip(tripToDelete);
            }
        }
    }

    /**
     * Handles the action of synchronizing the local DB with the trips list.
     */
    private void handleSynchronizeLocalDB(ArrayList<Trip> trips) {

        // Delete the trips that exist in the local DB but not on the backend DB.
        ArrayList<Trip> tripsToDelete = DBHelper.getInstance(this).getAllActiveTrips();
        tripsToDelete.removeAll(trips);

        for (Trip tripToDelete : tripsToDelete) {
            if (!DBHelper.getInstance(this).deleteTripByTripID(tripToDelete.getTripID())) {
                Log.d("BackendService", "handleSynchronizeLocalDB failed to delete trip with ID " +
                        tripToDelete.getTripID());
            }
        }

        // Update the trips that already exist in the local DB. They may or may not have
        // even changed, but we don't know so we update them anyways.
        ArrayList<Trip> tripsToUpdate = DBHelper.getInstance(this).getAllActiveTrips();
        tripsToUpdate.retainAll(trips);

        for (Trip tripToUpdate : tripsToUpdate) {
            if (!DBHelper.getInstance(this).updateTrip(tripToUpdate)) {
                Log.d("BackendService", "handleSynchronizeLocalDB failed to update trip with ID " +
                        tripToUpdate.getTripID());
            }
        }

        // Add the trips that aren't in the local DB to the DB. Fire a notification informing the
        // user of a newly added trip.
        ArrayList<Trip> tripsToAdd = trips;
        tripsToAdd.removeAll(DBHelper.getInstance(this).getAllActiveTrips());

        for (Trip tripToAdd : tripsToAdd) {
            if (DBHelper.getInstance(this).addTrip(tripToAdd) <= 0) {
                Log.d("BackendService", "handleSynchronizeLocalDB failed to add trip with ID " +
                        tripToAdd.getTripID());
            }
            else {
                newTripAddedNotification(tripToAdd);
            }
        }
    }

    /**
     * Handles the action of getting the list of trips from the backend DB
     */
    public void handleGetTripsFromBackend(String facebookID) {

        // First, set trips that have already passed to completed and delete the
        // trip from the backend
        Date currentTime = new Date();

        for (Trip trip :  DBHelper.getInstance(this).getAllActiveTrips()) {
            if (currentTime.after(trip.getMeetupTime())) {
                trip.setTripComplete(true);

                // Update the status in the database
                DBHelper.getInstance(this).setTripCompleteStatus(trip.getTripID());

                // Remove the trip from the backend
                new EndpointsPortal().clearTrip(this, trip);
            }
        }

        if (facebookID != null) {
            new EndpointsPortal().getTrips(this, facebookID);
        }
    }

    /**
     * Handles the action of getting adding the trip to the local and backend DB
     */
    public void handleAddTrip(Trip trip) {
        if (trip != null) {
            new EndpointsPortal().addTrip(this, trip);
        }
    }

    /**
     * Handles the action of getting deleting the trip to the local and backend DB
     */
    public void handleDeleteTrip(Trip tripToDelete) {
        new EndpointsPortal().clearTrip(this, tripToDelete);
    }

    /**
     * Creates a notification informing the user of a newly added trip.
     *
     * @param trip The newly added trip
     */
    private void newTripAddedNotification(Trip trip) {

        // Make sure a trip was actually added. If the Trip is null, just return
        if (trip == null) {
            return;
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        Intent tripInfoIntent = new Intent(this, LoginLandingPageActivity.class);

        int notificationID = (int) trip.getTripID();

        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationID,
                tripInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentText;

        // Set the contentText based on how many people are attending (if there is only one
        // person in the list, the creator of the trip is the only person attending. Ideally
        // that should never happen at this point.
        if (trip.getAttendingFBFriendsList().size() > 1) {
            contentText = "A meet-up with " + (trip.getAttendingFBFriendsList().size() - 1);

            // Change grammar based on if there is more than one friend attending or not
            if (trip.getAttendingFBFriendsList().size() > 2) {
                contentText += " other friends at ";
            }
            else {
                contentText += " other friend at ";
            }

            contentText += trip.getAddressInformation(this).getAddressLine(0) +
                    " is planned for " + timeFormat.format(trip.getMeetupTime()) +
                    ". Tap for more details.";
        }
        else {
            contentText = "Meet-up at "
                    + trip.getAddressInformation(this).getAddressLine(0) +
                    " planned for " + timeFormat.format(trip.getMeetupTime()) +
                    ". Tap for more details.";
        }

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("New Trip Added")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .build();

        notification.defaults = Notification.DEFAULT_ALL;

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notification);
    }
}
