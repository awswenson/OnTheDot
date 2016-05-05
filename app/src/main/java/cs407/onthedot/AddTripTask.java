package cs407.onthedot;

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
public class AddTripTask extends AsyncTask<TripApi, Void, Void> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    TripBean tripToAdd;

    public AddTripTask(Trip trip) {
        super();

        TripBean tripBean = new TripBean();

        tripBean.setId(trip.getTripID()); // I think this should be null since we want to get an ID from the backend
        tripBean.setDate(dateFormat.format(trip.getMeetupTime()));
        tripBean.setDestLat(trip.getDestinationLatitude());
        tripBean.setDestLong(trip.getDestinationLongitude());
        tripBean.setStartLat(trip.getStartingLocationLatitude());
        tripBean.setStartLong(trip.getStartingLocationLongitude());
        tripBean.setTripComplete(trip.isTripComplete());
        tripBean.setFriendsList(Friend.getFriendsStringFromArray(trip.getAttendingFBFriendsList()));

        this.tripToAdd = tripBean;
    }


    protected Void doInBackground(TripApi... tripApiService) {
        try {
            new EndpointsPortal().tripApiService.storeTrip(this.tripToAdd).execute();
        } catch (IOException e){
            Log.e("Async exception", "Error when adding a trip", e);
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        Log.e("Add trips returned", "Yay");
        //maybe put local database stuff in here
        //maybe put a listener reference in here and then handle it elsewhere
    }

}
