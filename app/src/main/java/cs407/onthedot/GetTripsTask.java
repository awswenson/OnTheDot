package cs407.onthedot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBeanCollection;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by connerhuff on 5/4/16.
 */
public class GetTripsTask extends AsyncTask<TripApi, Void, TripBeanCollection> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private Context context;
    private String facebookId;

    public GetTripsTask(Context context, String facebookId) {
        super();

        this.context = context;
        this.facebookId = facebookId;
    }


    protected TripBeanCollection doInBackground(TripApi... tripApiService) {
        TripBeanCollection tripBeans = new TripBeanCollection();

        try {
            tripBeans = new EndpointsPortal().tripApiService.getTrips(this.facebookId).execute();
        } catch (IOException e){
            Log.e("Async exception", "Error when pushing trips", e);
        }

        return tripBeans;
    }

    protected void onPostExecute(TripBeanCollection collection) {
        ArrayList<Trip> trips = new ArrayList<>();

        for (TripBean tripBean : collection.getItems()) {

            try {

                long tripID = tripBean.getId();

                LatLng destination = new LatLng(tripBean.getDestLat(), tripBean.getDestLong());

                LatLng startingLocation = new LatLng(tripBean.getStartLat(), tripBean.getStartLong());

                Date meetupTime = dateFormat.parse(tripBean.getDate());

                boolean tripComplete = tripBean.getTripComplete();

                ArrayList<Friend> friends =
                        Friend.getFriendsListFromString(tripBean.getFriendsList());

                trips.add(new Trip(tripID, destination, startingLocation,
                        meetupTime, friends, tripComplete));
            } catch (Exception e) {
              // Just don't add the trip to the list
            }
        }

        BackendPollService.startSynchronizeLocalDB(context, trips);
    }

}

