package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class PushRemoteTask extends AsyncTask<TripApi, Void, Void> {

    protected Void doInBackground(TripApi... tripApiService) {

        TripBean trip = new TripBean();
        trip.setData("Hello");
        trip.setId(new Long(1));
        //try{
            //trip = new EndpointsPortal().tripApiService.sayHi("Jason").execute();
        try{
            new EndpointsPortal().tripApiService.storeTrip(trip).execute();
        }catch (IOException e){
            Log.e("Async exception", "Error when pushing trips", e);
        }

        /*
        }
        catch (IOException e){
            Log.e("Async exception", "Error when loading trips", e);
        }
        */
        return null;
        //return trip;
    }

    /*
    protected void onProgressUpdate(Void v) {

    }
    */

    protected void onPostExecute(Void v) {
        //Log.e("SayHiResults", tripBean.getData());
    }

}
