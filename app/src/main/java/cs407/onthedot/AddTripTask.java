package cs407.onthedot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by connerhuff on 5/4/16.
 */
public class AddTripTask extends AsyncTask<TripApi, Void, Long> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private Context context;
    private Trip tripToAdd;
    private TripBean tripBeanToAdd;

    public interface AddTripTaskListener {
        public void addTripToListView(Trip trip);
    }

    public AddTripTask(Context context, Trip trip) {
        super();

        this.context = context;
        this.tripToAdd = trip;

        TripBean tripBean = new TripBean();

        tripBean.setId(trip.getTripID()); // I think this should be null since we want to get an ID from the backend
        //We can still set it... it just gets ignored by the backend anyways... I will leave it here just in case for now
        tripBean.setDate(dateFormat.format(trip.getMeetupTime()));
        tripBean.setDestLat(trip.getDestinationLatitude());
        tripBean.setDestLong(trip.getDestinationLongitude());
        tripBean.setStartLat(trip.getStartingLocationLatitude());
        tripBean.setStartLong(trip.getStartingLocationLongitude());
        tripBean.setTripComplete(trip.isTripComplete());
        tripBean.setFriendsList(Friend.getFriendsStringFromArray(trip.getAttendingFBFriendsList()));

        this.tripBeanToAdd = tripBean;
    }


    protected Long doInBackground(TripApi... tripApiService) {
        TripBean tripBean = null;
        try {
            tripBean = new EndpointsPortal().tripApiService.storeTrip(this.tripBeanToAdd).execute();
        } catch (IOException e){
            Log.e("Async exception", "Error when adding a trip", e);
        }
        if (tripBean != null)
            return tripBean.getId(); // TODO return the tripID that was returned from the DB
        return new Long(0);//if you get an id of zero treat it like an error occured
    }

    protected void onPostExecute(Long tripID) {
        this.tripToAdd.setTripID(tripID);
        DBHelper.getInstance(context).addTrip(tripToAdd);

        // If the DashboardActivity called this, then add the trip to the ListView
        if (context instanceof DashboardActivity) {
            ((DashboardActivity) context).addTripToListView(tripToAdd);
        }

        createNotificationHandler(tripToAdd);
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

        RequestQueue queue = Volley.newRequestQueue(context);

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
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            Intent intent = new Intent(context, NotificationHandler.class);
                            intent.putExtra(DashboardActivity.INTENT_TRIP_OBJECT, trip);

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
}
