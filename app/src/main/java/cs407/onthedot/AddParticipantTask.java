package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.ParticipantBean;

import java.io.IOException;

/**
 * Created by connerhuff on 5/5/16.
 */
public class AddParticipantTask extends AsyncTask<TripApi, Void, Void> {

    ParticipantBean partToAdd;

    public AddParticipantTask(long tripID, Friend friend) {
        super();

        ParticipantBean participantBean = new ParticipantBean();

        participantBean.setParticipantId(friend.getId());
        participantBean.setParticipantName(friend.getName());
        participantBean.setTripId(tripID);

        this.partToAdd = participantBean;
    }


    protected Void doInBackground(TripApi... tripApiService) {
        try {
            new EndpointsPortal().tripApiService.storeParticipant(this.partToAdd).execute();
        } catch (IOException e) {
            Log.e("AddParticipantTask", "Error when adding a participant from calling storeParticipant()", e);
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        // No need to do anything here
    }

}
