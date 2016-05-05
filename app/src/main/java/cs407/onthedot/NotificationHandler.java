package cs407.onthedot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AlexanderSwenson on 5/3/16.
 */
public class NotificationHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        Trip trip = intent.getParcelableExtra(DashboardActivity.INTENT_TRIP_OBJECT);

        int notificationID = (int) trip.getTripID();

        Intent tripInfoIntent = new Intent(context, LoginLandingPageActivity.class);

        //Intent tripInfoIntent = new Intent(context, TripInfoActivity.class);
        //intent.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);

        PendingIntent contentIntent = PendingIntent.getActivity(context, notificationID,
                tripInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentText = "Depart for "
                + trip.getAddressInformation(context).getAddressLine(0) +
                " right now to arrive at exactly " + timeFormat.format(trip.getMeetupTime()) +
                ". Tap for more details.";

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Leave Now")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .build();

        notification.defaults = Notification.DEFAULT_ALL;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notification);
    }
}
