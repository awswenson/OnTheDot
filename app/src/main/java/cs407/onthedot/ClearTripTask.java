package cs407.onthedot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class ClearTripTask extends AsyncTask<TripApi, Void, Void> {

    Context context;
    Trip tripToDelete;

    public interface ClearTripTaskListener {
        public void deleteTripfromListView(Trip trip);
    }

    public ClearTripTask(Context context, Trip tripToDelete) {
        super();

        this.context = context;
        this.tripToDelete = tripToDelete;
    }

    protected Void doInBackground(TripApi... tripApiService) {

        try {
            new EndpointsPortal().tripApiService.clearTripsById(this.tripToDelete.getTripID()).execute();
        } catch (IOException e) {
            Log.e("ClearTripTask", "Error when trying to call clearTripsById", e);
        }

        return null;
    }

    protected void onPostExecute(Void v) {

        for (Friend friend : tripToDelete.getAttendingFBFriendsList()) {
            new EndpointsPortal().clearParticipantByIds(friend.getId(), tripToDelete.getTripID());
        }

        DBHelper.getInstance(context).deleteTripByTripID(tripToDelete.getTripID());

        // If the DashboardActivity called this, then add the trip to the ListView
        if (context instanceof DashboardActivity) {
            ((DashboardActivity) context).deleteTripfromListView(tripToDelete);
        }

        deleteNotificationHandler(tripToDelete);
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

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationHandler.class);
        intent.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, (int) trip.getTripID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

}
