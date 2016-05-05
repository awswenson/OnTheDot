package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;

import java.io.IOException;

/**
 * Created by connerhuff on 5/5/16.
 */
public class ClearParticipantByIdsTask extends AsyncTask<TripApi, Void, Long> {

    String partIdToClear;
    Long tripIdToClear;

    public ClearParticipantByIdsTask(String partId, Long tripId) {
        super();

        this.partIdToClear = partId;
        this.tripIdToClear = tripId;
    }

    protected Long doInBackground(TripApi... tripApiService) {
        try {
            new EndpointsPortal().tripApiService.clearParticipantByPartAndTripId(
                    this.partIdToClear, this.tripIdToClear).execute();
        } catch (IOException e){
            Log.e("Async exception", "Error when pushing trips", e);
        }

        return null;
        //return trip;
    }

    protected void onPostExecute(Long id) {
        //Log.e("SayHiResults", tripBean.getData());
        //maybe put local database stuff in here
    }

}
