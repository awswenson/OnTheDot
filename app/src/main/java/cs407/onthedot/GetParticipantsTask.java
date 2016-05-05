package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.ParticipantBeanCollection;

import java.io.IOException;

/**
 * Created by connerhuff on 5/5/16.
 */
public class GetParticipantsTask extends AsyncTask<TripApi, Void, ParticipantBeanCollection> {

    private String facebookId;

    public GetParticipantsTask(String facebookId) {
        super();

        this.facebookId = facebookId;
    }

    protected ParticipantBeanCollection doInBackground(TripApi... tripApiService) {
        ParticipantBeanCollection partBeans = new ParticipantBeanCollection();

        try {
            partBeans = new EndpointsPortal().tripApiService.getParticipants(this.facebookId).execute();
        } catch (IOException e){
            Log.e("Async exception", "Error when pushing participants", e);
        }

        return partBeans;
    }

    /*
    protected void onPostExecute(TripBeanCollection collection) {
        ArrayList<Trip> trips = new ArrayList<>();

        for (TripBean tripBean : collection.getItems()) {

            try {

                long tripID = tripBean.getId();

                LatLng destination = new LatLng(Double.parseDouble(tripBean.getDestLat()),
                        Double.parseDouble(tripBean.getDestLong()));

                LatLng startingLocation = new LatLng(Double.parseDouble(tripBean.getStartLat()),
                        Double.parseDouble(tripBean.getStartLong()));

                Date meetupTime = dateFormat.parse(tripBean.getDate());

                boolean tripComplete = "1".equals(tripBean.getTripComplete());

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
    */

}


