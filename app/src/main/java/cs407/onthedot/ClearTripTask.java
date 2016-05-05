package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class ClearTripTask extends AsyncTask<TripApi, Void, Long> {

    Long idToClear;

    public ClearTripTask(Long id) {
        super();
        // do stuff
        idToClear = id;
    }


    protected Long doInBackground(TripApi... tripApiService) {

        /*
        TripBean trip = new TripBean();
        trip.setData("Hello");
        trip.setId(new Long(4));
        TripBean trip2 = new TripBean();
        trip2.setId(new Long(2));
        trip2.setData("Conner");
        */
        try{
            //new EndpointsPortal().tripApiService.storeTrip(trip).execute();
            //new EndpointsPortal().tripApiService.storeTrip(trip2).execute();
            new EndpointsPortal().tripApiService.clearTripsById(this.idToClear).execute();
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

    protected void onPostExecute(Long id) {
        //Log.e("SayHiResults", tripBean.getData());
        //maybe put local database stuff in here
    }

}
