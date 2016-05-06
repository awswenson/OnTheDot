package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;

import java.io.IOException;

/**
 * Created by connerhuff on 5/5/16.
 */
public class ClearParticipantTask extends AsyncTask<TripApi, Void, Void> {

    Friend friendToClear;
    Long tripID;

    public ClearParticipantTask(Friend friendToClear, Long tripID) {
        super();

        this.friendToClear = friendToClear;
        this.tripID = tripID;
    }

    protected Void doInBackground(TripApi... tripApiService) {
        try {
            new EndpointsPortal().tripApiService.clearParticipantByPartAndTripId(
                    this.friendToClear.getId(), this.tripID).execute();
        } catch (IOException e){
            Log.e("ClearParticipantTask", "Error when clearing a participant from a task from clearParticipantByPartAndTripId()", e);
        }

        return null;
    }

    protected void onPostExecute(Void v) {
        // No need to call anything after this
    }

}
