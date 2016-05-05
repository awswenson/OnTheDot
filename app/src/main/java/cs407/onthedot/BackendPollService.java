package cs407.onthedot;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AlexanderSwenson on 5/4/16.
 */
public class BackendPollService extends IntentService {

    private static final String ACTION_SYNCHRONIZE_LOCAL_FROM_BACKEND =
            "cs407.onthedot.action.SYNCHRONIZE_LOCAL_FROM_BACKEND";

    public BackendPollService() {
        super("BackendPollService");
    }

    /**
     * Starts this service to synchronize the local database with the backend database. If
     * the service is already performing a task this action will be queued.
     */
    public static void startSynchronizeLocalFromBackend(Context context) {
        Intent intent = new Intent(context, BackendPollService.class);
        intent.setAction(ACTION_SYNCHRONIZE_LOCAL_FROM_BACKEND);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_SYNCHRONIZE_LOCAL_FROM_BACKEND.equals(action)) {
                handleSynchronizeLocalFromBackend();
            }
        }
    }

    /**
     * Handles the action of synchronizing the local database with the backend database
     * in the provided background thread.
     */
    private void handleSynchronizeLocalFromBackend() {
        // TODO: implement
    }

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
