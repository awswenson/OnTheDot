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

    public AddParticipantTask(ParticipantBean partBean) {
        super();
        // do stuff
        partToAdd = partBean;
    }


    protected Void doInBackground(TripApi... tripApiService) {
        try{
            new EndpointsPortal().tripApiService.storeParticipant(this.partToAdd).execute();
        }catch (IOException e){
            Log.e("Async exception", "Error when adding a participant", e);
        }
        return null;
    }

    /*
    protected void onProgressUpdate(Void v) {

    }
    */

    protected void onPostExecute(Void v) {
        Log.e("Add partic returned", "Yay");
        //maybe put local database stuff in here
        //maybe put a listener reference in here and then handle it elsewhere
    }

}
