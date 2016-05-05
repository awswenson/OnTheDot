package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBeanCollection;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class GetTripsTask extends AsyncTask<TripApi, Void, TripBeanCollection> {

    public GetTripsTask() {
        super();
        // do stuff
    }


    protected TripBeanCollection doInBackground(TripApi... tripApiService) {
        TripBeanCollection k = new TripBeanCollection();
        try{
            k = new EndpointsPortal().tripApiService.getTrips().execute();
        }catch (IOException e){
            Log.e("Async exception", "Error when pushing trips", e);
        }

        return k;
    }

    /*
    protected void onProgressUpdate(Void v) {

    }
    */

    protected void onPostExecute(TripBeanCollection collection) {
        Log.e("Get trips returned", "Yay");
        //maybe put local database stuff in here
        //maybe put a listener reference in here and then handle it elsewhere
    }

}

