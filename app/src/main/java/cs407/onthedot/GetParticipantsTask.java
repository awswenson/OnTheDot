package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.ParticipantBeanCollection;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBeanCollection;

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
            Log.e("GetParticipantsTask", "Error when getting participants from backend from getParticipants()", e);
        }

        return partBeans;
    }

    protected void onPostExecute(TripBeanCollection collection) {

    }

}


