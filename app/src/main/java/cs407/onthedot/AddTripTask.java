package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class AddTripTask extends AsyncTask<TripApi, Void, Void> {

    TripBean tripToAdd;

    public AddTripTask(TripBean trip) {
        super();
        // do stuff
        tripToAdd = trip;
    }


    protected Void doInBackground(TripApi... tripApiService) {
        try{
            new EndpointsPortal().tripApiService.storeTrip(this.tripToAdd).execute();
        }catch (IOException e){
            Log.e("Async exception", "Error when adding a trip", e);
        }
        return null;
    }

    /*
    protected void onProgressUpdate(Void v) {

    }
    */

    protected void onPostExecute(Void v) {
        Log.e("Add trips returned", "Yay");
        //maybe put local database stuff in here
        //maybe put a listener reference in here and then handle it elsewhere
    }

}
