package cs407.onthedot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

    public AddTripTask(Context context, Trip trip) {
        super();

        this.context = context;
        this.tripToAdd = trip;

        TripBean tripBean = new TripBean();

        tripBean.setId(trip.getTripID()); // I think this should be null since we want to get an ID from the backend
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
        try {
            new EndpointsPortal().tripApiService.storeTrip(this.tripBeanToAdd).execute();
        } catch (IOException e){
            Log.e("Async exception", "Error when adding a trip", e);
        }
        return Long.valueOf(0); // TODO return the tripID that was returned from the DB
    }

    protected void onPostExecute(Long tripID) {

        this.tripToAdd.setTripID(tripID);
        DBHelper.getInstance(context).addTrip(tripToAdd);



        // TODO
    }

}
