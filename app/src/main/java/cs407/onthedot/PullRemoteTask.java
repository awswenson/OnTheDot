package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class PullRemoteTask extends AsyncTask<TripApi, Void, TripBean> {

    protected TripBean doInBackground(TripApi... tripApiService) {

        TripBean trip = new TripBean();
        try{
            trip = new EndpointsPortal().tripApiService.sayHi("Jason").execute();
        }
        catch (IOException e){
            Log.e("Async exception", "Error when loading trips", e);
        }
        return trip;
    }

    /*
    protected void onProgressUpdate(Void v) {

    }
    */

    protected void onPostExecute(TripBean tripBean) {
        Log.e("SayHiResults", tripBean.getData());
    }
}
